package com.yunfei.toolmaker;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootTest
public class RocketMqTests {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Value("${rocketmq.consumer1.topic}")
    private String smsTopic;
    @Test
    public void testRocketMQ() throws InterruptedException {
//        send("test rocketmq~", null);
        Thread.sleep(2000);
    }

    public SendResult send(String messageContent, String tags) {
        String destination = StringUtils.isBlank(tags) ? smsTopic : smsTopic + ":" + tags;
        SendResult sendResult =
                rocketMQTemplate.syncSend(
                        destination,
                        MessageBuilder.withPayload(messageContent).
                                setHeader(MessageConst.PROPERTY_KEYS, "your_unique_key").
                                build()
                );
        if (sendResult != null) {
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                // send message success ，do something
                System.out.println("send successfully!");
            }
        }
        return sendResult;
    }
}
