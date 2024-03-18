package com.yunfei.toolmaker.controller;

import com.yunfei.toolmaker.dto.CodeSubmitInfo;
import com.yunfei.toolmaker.dto.UserRegisterInfo;
import com.yunfei.toolmaker.service.CodeService;
import com.yunfei.toolmaker.service.param.CodeSaveParam;
import com.yunfei.toolmaker.service.response.CodeJudgementResponse;
import com.yunfei.toolmaker.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import static com.yunfei.toolmaker.constant.AuthServerConstant.LOGIN_USER;

@RestController
public class CodeController extends BaseController{
    @Autowired
    private CodeService codeService;

    @PostMapping("/code/submit")
    public R codeSubmit(@RequestBody CodeSubmitInfo info, HttpSession session) {
        CodeJudgementResponse codeJudgementResponse;
        try {
            codeJudgementResponse = codeService.codeJudgement(info);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }

        CodeSaveParam param = new CodeSaveParam();
        param.setUserName(getUserName(session));
        param.setCode(info.getEditor());
        param.setRunResult(codeJudgementResponse.getRunResult());
        codeService.codeResultSave(param);
        return R.ok().put("msg", codeJudgementResponse.getRunResult());
    }
}
