package com.yunfei.toolmaker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunfei.toolmaker.dto.CodeSubmitInfo;
import com.yunfei.toolmaker.service.param.CodeSaveParam;
import com.yunfei.toolmaker.service.response.CodeJudgementResponse;

public interface CodeService{
    CodeJudgementResponse codeJudgement(CodeSubmitInfo codeSubmitInfo) throws Exception;
    void codeResultSave(CodeSaveParam codeSaveParam);
}
