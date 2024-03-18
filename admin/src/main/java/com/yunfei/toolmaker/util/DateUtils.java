package com.yunfei.toolmaker.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String transformSecondTimestampToYYYYMMDDHHMMSS(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);

        // 使用指定时区（这里使用默认时区）将Instant对象格式化为字符串
        String formattedDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(instant);
        return formattedDateTime;

    }
}
