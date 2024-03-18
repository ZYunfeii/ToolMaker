package com.yunfei.toolmaker.service;

import com.yunfei.toolmaker.dto.WeatherData;

import java.util.List;

public interface WeatherService {
    List<WeatherData> getWeatherData(String city) throws Exception;

}
