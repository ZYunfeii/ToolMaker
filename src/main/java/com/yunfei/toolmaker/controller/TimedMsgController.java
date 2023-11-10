package com.yunfei.toolmaker.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunfei.toolmaker.dao.TimedMsgDao;
import com.yunfei.toolmaker.dto.TimedMsgSubmitData;
import com.yunfei.toolmaker.dto.UserRegisterInfo;
import com.yunfei.toolmaker.exception.ErrorCodeEnum;
import com.yunfei.toolmaker.po.TimedMsgDo;
import com.yunfei.toolmaker.service.TimedMsgService;
import com.yunfei.toolmaker.service.param.TimedMsgSubmitParam;
import com.yunfei.toolmaker.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.List;

import static com.yunfei.toolmaker.constant.AuthServerConstant.LOGIN_USER;

@RestController
public class TimedMsgController {
    @Autowired
    private TimedMsgService timedMsgService;
    @PostMapping("/timedmsg/submit")
    public ResponseEntity<R> register(@RequestBody TimedMsgSubmitData info, HttpSession session) {
        Object attribute = session.getAttribute(LOGIN_USER);
        if (attribute == null) {
            return null;
        }
        UserRegisterInfo userRegisterInfo = (UserRegisterInfo) attribute;
        TimedMsgSubmitParam timedMsgSubmitParam = new TimedMsgSubmitParam();
        timedMsgSubmitParam.setTimeToSend(info.getDate());
        timedMsgSubmitParam.setEmail(info.getEmail());
        timedMsgSubmitParam.setContent(info.getTextarea());
        timedMsgSubmitParam.setUserName(userRegisterInfo.getId());
        try {
            timedMsgService.submitTimedMsg(timedMsgSubmitParam);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(R.error(ErrorCodeEnum.TIMED_MSG_PARA_ERROR.getCode(), ErrorCodeEnum.TIMED_MSG_PARA_ERROR.getMsg()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(R.ok(), HttpStatus.OK);
    }

    @Autowired
    private TimedMsgDao timedMsgDao;
    @GetMapping("/testlimit")
    public R testLimit(@RequestParam Integer page, @RequestParam Integer size) {
        PageHelper.startPage(page, size);
        List<TimedMsgDo> timedMsgDos = timedMsgDao.selectAll();
        PageInfo pageInfo = new PageInfo<>(timedMsgDos);
        return R.ok().setData(pageInfo);
    }
}
