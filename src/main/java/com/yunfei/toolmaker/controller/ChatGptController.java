package com.yunfei.toolmaker.controller;

import com.yunfei.toolmaker.dto.ChatGptSubmitData;
import com.yunfei.toolmaker.service.ChatGptService;
import com.yunfei.toolmaker.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatGptController {
    @Autowired
    private ChatGptService chatGptService;
    @PostMapping("/chatgpt")
    public R chatGptSubmit(@RequestBody ChatGptSubmitData chatGptSubmitData) {
        String chatGptResponse = chatGptService.chatWithGpt(chatGptSubmitData.getText());
        return R.ok().put("msg", chatGptResponse);
    }

}
