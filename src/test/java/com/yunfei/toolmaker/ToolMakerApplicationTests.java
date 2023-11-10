package com.yunfei.toolmaker;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.TypeReference;
import com.yunfei.toolmaker.dto.WeatherData;
import com.yunfei.toolmaker.service.WeatherService;
import com.yunfei.toolmaker.util.CodeRunner;

import com.yunfei.toolmaker.util.EmailSender;
import lombok.Data;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ZSetOperations;

import javax.mail.MessagingException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class ToolMakerApplicationTests {
    @Autowired
    private WeatherService weatherService;
    @Test
    void weatherApiTest() throws Exception {
        List<WeatherData> data = weatherService.getWeatherData("北京");
        System.out.println(data);
    }

    @Test
    public void testOther() {
        String input = "8/20℃";

        // 定义一个正则表达式，用于匹配数字
        Pattern pattern = Pattern.compile("\\d+");

        // 创建 Matcher 对象，用于匹配字符串
        Matcher matcher = pattern.matcher(input);

        // 用于存储匹配到的数字
        int[] numbers = new int[2];
        int index = 0;

        // 遍历匹配结果
        while (matcher.find() && index < 2) {
            String match = matcher.group();
            int number = Integer.parseInt(match);
            numbers[index] = number;
            index++;
        }

        if (index == 2) {
            int firstNumber = numbers[0];
            int secondNumber = numbers[1];

            System.out.println("First Number: " + firstNumber);
            System.out.println("Second Number: " + secondNumber);
        } else {
            System.out.println("Not enough numbers found in the input string.");
        }
    }

    @Test
    public void testCodeRunner() throws Exception {
        String userCode = "public class UserSolution {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}\n";
        String s = CodeRunner.runJavaCode(userCode);
        System.out.println(s);

    }

    @Autowired
    private ZSetOperations<String, Object> zSetOperations;
    private String testZSetKey = "testZSetKey";
    @Data
    static class RedisEntry {
        private Integer val;
        private String content;
    }
    @Test
    public void testZSet() {
        deleteRedisKey();

        RedisEntry redisEntry1 = new RedisEntry();
        redisEntry1.val = 1;
        redisEntry1.content = "hello";
        RedisEntry redisEntry2 = new RedisEntry();
        redisEntry2.val = 2;
        redisEntry2.content = "nihao";
        RedisEntry redisEntry3 = new RedisEntry();
        redisEntry3.val = 0;
        redisEntry3.content = "heihei";
        zSetOperations.add(testZSetKey, JSON.toJSONString(redisEntry1), redisEntry1.val);
        zSetOperations.add(testZSetKey, JSON.toJSONString(redisEntry2), redisEntry2.val);
        zSetOperations.add(testZSetKey, JSON.toJSONString(redisEntry3), redisEntry3.val);

        Set<Object> set = zSetOperations.range(testZSetKey, 0, 10);
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()) {
            String s = (String) (iterator.next());
            RedisEntry redisEntry = JSON.parseObject(s, new TypeReference<RedisEntry>() {
            });
            System.out.println(redisEntry);
            zSetOperations.remove(testZSetKey, s);
        }


    }
    @After
    public void deleteRedisKey() {
        zSetOperations.getOperations().delete(testZSetKey);
    }

    @Autowired
    private EmailSender emailSender;
    @Test
    public void testEmailSender() throws MessagingException, GeneralSecurityException {
//        emailSender.sendEmail("1361573692@qq.com", "求教", "dddd");
    }




}
