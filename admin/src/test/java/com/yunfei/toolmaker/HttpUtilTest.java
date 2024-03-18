package com.yunfei.toolmaker;

import com.yunfei.toolmaker.util.HttpUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.toolmaker
 * @className: HttpUtilTest
 * @author: Yunfei
 * @description: TODO
 * @date: 2024/3/6 11:04
 * @version: 1.0
 */
public class HttpUtilTest {
    @Test
    public void testHttpUtil() throws Exception {
        HttpResponse response = HttpUtils.doGet("https://github.com", "", "get", new HashMap<>(), new HashMap<>());
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String responseBody = stringBuilder.toString();
        System.out.println(responseBody);
    }
}
