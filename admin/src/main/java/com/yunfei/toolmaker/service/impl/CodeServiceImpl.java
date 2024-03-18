package com.yunfei.toolmaker.service.impl;

import com.yunfei.toolmaker.dao.CodeDao;
import com.yunfei.toolmaker.dto.CodeSubmitInfo;
import com.yunfei.toolmaker.po.CodeDo;
import com.yunfei.toolmaker.service.CodeService;
import com.yunfei.toolmaker.service.param.CodeSaveParam;
import com.yunfei.toolmaker.service.response.CodeJudgementResponse;
import com.yunfei.toolmaker.util.CodeRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
public class CodeServiceImpl implements CodeService {
    @Autowired
    private CodeDao codeDao;
    @Override
    public CodeJudgementResponse codeJudgement(CodeSubmitInfo codeSubmitInfo) throws Exception {
        String result = CodeRunner.runJavaCode(codeSubmitInfo.getEditor());
        CodeJudgementResponse codeJudgementResponse = new CodeJudgementResponse();
        codeJudgementResponse.setUserName(codeSubmitInfo.getUserName());
        codeJudgementResponse.setRunResult(result);
        return codeJudgementResponse;
    }

    @Transactional
    @Override
    public void codeResultSave(CodeSaveParam codeSaveParam) {
        CodeDo codeDo = new CodeDo();
        codeDo.setCode(codeSaveParam.getCode());
        codeDo.setRunResult(codeSaveParam.getRunResult());
        codeDo.setUserName(codeSaveParam.getUserName());
        codeDao.insert(codeDo);
        log.info("insert data:{}", codeSaveParam);
    }
}
