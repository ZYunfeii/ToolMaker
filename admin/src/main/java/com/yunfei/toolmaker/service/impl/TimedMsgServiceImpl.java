package com.yunfei.toolmaker.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yunfei.toolmaker.constant.RedisConstant;
import com.yunfei.toolmaker.dao.TimedMsgDao;
import com.yunfei.toolmaker.po.TimedMsgDo;
import com.yunfei.toolmaker.service.TimedMsgService;
import com.yunfei.toolmaker.service.param.TimedMsgSubmitParam;
import com.yunfei.toolmaker.util.DateUtils;
import com.yunfei.toolmaker.util.EmailSender;
import com.yunfei.toolmaker.util.SnowFlake;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TimedMsgServiceImpl implements TimedMsgService {
    @Autowired
    private TimedMsgDao timedMsgDao;
    @Autowired
    private ZSetOperations<String, Object> zSetOperations;
    @Autowired
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    @Autowired
    private EmailSender emailSender;

    @PostConstruct
    private void startPollingThread() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new PollingThread(), 0, 5, TimeUnit.SECONDS);
    }

    private void updateTimedTaskSendFlag(long requestId, Boolean sent) {
        TimedMsgDo timedMsgDo = new TimedMsgDo();
        timedMsgDo.setRequestId(requestId);
        timedMsgDo.setSent(sent);
        timedMsgDao.updateTimedTask(timedMsgDo);
    }

    class PollingThread implements Runnable {
        private static final int BATCH_SIZE = 10;

        @Override
        public void run() {
            synchronized (PollingThread.class) {
                // 1. 判断有无key
                Boolean hasKey = zSetOperations.getOperations().hasKey(RedisConstant.TIMED_MSG_KEY);
                if (hasKey == null || !hasKey) {
                    // log.info("the key doesn't exist.");
                    return;
                }
                // 2. 拿到key对应zset的第一个元素
                Set<Object> set = zSetOperations.range(RedisConstant.TIMED_MSG_KEY, 0, BATCH_SIZE);
                if (set == null) {
                    log.info("the zset is null!");
                    return;
                }
                for (Object o : set) {
                    String jsonStr = (String) o;
                    if (jsonStr == null) {
                        log.warn("the element is null.");
                        continue;
                    }
                    // 3. 判断对应任务是否在mysql中，并判断状态是否发送过，实现被动的最终一致性
                    // 解释：在提交任务的时候如果写mysql成功，写redis成功，但是事务出现异常回滚，redis没有回滚，会导致数据不一致
                    RedisTaskEntry redisTaskEntry = JSON.parseObject(jsonStr, new TypeReference<RedisTaskEntry>() {
                    });
                    if (Long.parseLong(redisTaskEntry.getPara().getTimeToSend()) > System.currentTimeMillis() / 1000) {
                        continue;
                    }
                    List<TimedMsgDo> query = queryTimedMsgByRequestId(redisTaskEntry.getRequestId());
                    if (CollectionUtils.isEmpty(query)) {
                        log.info("The timed msg doesn't exist in MySQL.");
                        continue;
                    }
                    TimedMsgDo taskDo = query.get(0);
                    if (taskDo.getSent()) {
                        log.info("The timed msg has already sent.");
                        continue;
                    }

                    // 4. 发送消息
                    try {
                        String subject = String.format("%s于%s设置的定时消息", taskDo.getUserName(), taskDo.getGmtCreated());
                        log.info("send timed msg:{} {} {}", taskDo.getEmail(), subject, taskDo.getContent());
                        emailSender.sendEmail(taskDo.getEmail(), subject, taskDo.getContent());
                    } catch (GeneralSecurityException | MessagingException e) {
                        log.warn("send email failed!");
                        throw new RuntimeException(e);
                    }
                    // 5. MySQL设置标志位已发送
                    updateTimedTaskSendFlag(redisTaskEntry.getRequestId(), true);
                    // 6. Redis清除该task
                    zSetOperations.remove(RedisConstant.TIMED_MSG_KEY, jsonStr);
                }


            }
        }
    }
    @Data
    static class RedisTaskEntry {
        private TimedMsgSubmitParam para;
        private Long requestId;
    }

    @Transactional
    @Override
    public void submitTimedMsg(TimedMsgSubmitParam timedMsgSubmitParam) throws IllegalArgumentException {
        // 0. 校验时间参数
        long expiredTime = Long.parseLong(timedMsgSubmitParam.getTimeToSend());
        if (expiredTime < System.currentTimeMillis() / 1000) {
            throw new IllegalArgumentException("期望发送的时间已经过去，请输入未来一个时间！");
        }
        // 生成requestId
        long requestId = SnowFlake.getInstance().nextId();
        // 1. 写MySQL
        TimedMsgDo timedMsgDo = new TimedMsgDo();
        BeanUtils.copyProperties(timedMsgSubmitParam, timedMsgDo);
        timedMsgDo.setRequestId(requestId);
        timedMsgDo.setTimeToSend(DateUtils.transformSecondTimestampToYYYYMMDDHHMMSS(expiredTime));
        timedMsgDao.insert(timedMsgDo);
        // 2. 双写至Redis
        RedisTaskEntry redisTaskEntry = new RedisTaskEntry();
        redisTaskEntry.setPara(timedMsgSubmitParam);
        redisTaskEntry.setRequestId(requestId);
        zSetOperations.add(RedisConstant.TIMED_MSG_KEY, JSON.toJSONString(redisTaskEntry), expiredTime);
    }

    public List<TimedMsgDo> queryTimedMsgByRequestId(long requestId) {
        TimedMsgDo timedMsgDo = new TimedMsgDo();
        timedMsgDo.setRequestId(requestId);
        return timedMsgDao.query(timedMsgDo);
    }
}
