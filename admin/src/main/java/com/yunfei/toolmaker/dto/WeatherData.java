package com.yunfei.toolmaker.dto;

import lombok.Data;

@Data
public class WeatherData {
    private int lowTemperature;
    private int highTemperature;
    private String city;
    private String date;
}
