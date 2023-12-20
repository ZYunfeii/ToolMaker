package com.yunfei.toolmaker.component;

import com.yunfei.toolmaker.service.param.TimedMsgSubmitParam;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        consumerGroup = "${rocketmq.consumer1.group}",
        topic = "${rocketmq.consumer1.topic}"
)
public class TimedMsgConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        System.out.println(s);
    }
}
