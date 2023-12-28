package com.yunfei.toolmaker.service.impl;

import com.yunfei.toolmaker.service.ChatGptService;
import com.yunfei.toolmaker.util.PythonCaller;
import org.springframework.stereotype.Service;

@Service
public class ChatGptServiceImpl implements ChatGptService {
    private String scriptName = "chat8-crawler/req.py";
    @Override
    public String chatWithGpt(String msg) {
        return PythonCaller.call(scriptName, new String[]{msg});
    }
}
