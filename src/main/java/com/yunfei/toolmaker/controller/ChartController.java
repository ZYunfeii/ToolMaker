package com.yunfei.toolmaker.controller;

import com.yunfei.toolmaker.dto.WeatherData;
import com.yunfei.toolmaker.service.WeatherService;
import com.yunfei.toolmaker.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChartController {
    @Autowired
    private WeatherService weatherService;

    @GetMapping("/chart/weather")
    public R getWeatherInfo(@RequestParam String city) throws Exception {
        List<WeatherData> weatherData = weatherService.getWeatherData(city);
        return R.ok().put("data", weatherData);
    }
}
