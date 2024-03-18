package com.yunfei.toolmaker.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yunfei.toolmaker.constant.RedisConstant;
import com.yunfei.toolmaker.dto.WeatherData;
import com.yunfei.toolmaker.service.WeatherService;
import com.yunfei.toolmaker.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WeatherServiceImpl implements WeatherService {
    @Value("${weather-info.app-key}")
    private String appKey;

    @Autowired
    private ValueOperations<String, String> valueOperations;
    @Override
    public List<WeatherData> getWeatherData(String city) throws Exception {


        if (valueOperations.getOperations().hasKey(RedisConstant.WEATHER_KEY)) {
            String s = valueOperations.get(RedisConstant.WEATHER_KEY);
            List<WeatherData> data = JSON.parseObject(s, new TypeReference<List<WeatherData>>() {
            });
            if (data != null) {
                return data;
            }
        }

        List<WeatherData> weatherDataList = new ArrayList<>();
        Map<String, String> query = new HashMap<>();
        query.put("city", city);
        query.put("key", appKey);
        HttpResponse response = HttpUtils.doGet("http://apis.juhe.cn/simpleWeather", "/query", "get", new HashMap<>(), query);
        if (response.getStatusLine().getStatusCode() == 200) {
            String json = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSON.parseObject(json);
            JSONArray future = ((JSONObject) jsonObject.get("result")).getJSONArray("future");
            for (Object o : future) {
                if (o instanceof JSONObject) {
                    JSONObject obj = (JSONObject) o;
                    String date = obj.getString("date");
                    // 定义一个正则表达式，用于匹配数字
                    Pattern pattern = Pattern.compile("\\d+");

                    String temperatureRange = obj.getString("temperature");

                    // 创建 Matcher 对象，用于匹配字符串
                    Matcher matcher = pattern.matcher(temperatureRange);

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

                    int firstNumber = numbers[0];
                    int secondNumber = numbers[1];

                    WeatherData weatherData = new WeatherData();
                    weatherData.setCity(city);
                    weatherData.setDate(date);
                    weatherData.setLowTemperature(firstNumber);
                    weatherData.setHighTemperature(secondNumber);

                    weatherDataList.add(weatherData);

                }
            }

        }
        if (weatherDataList != null && !weatherDataList.isEmpty()) {
            valueOperations.set(RedisConstant.WEATHER_KEY, JSON.toJSONString(weatherDataList), 2, TimeUnit.HOURS);
        }
        return weatherDataList;
    }
}
