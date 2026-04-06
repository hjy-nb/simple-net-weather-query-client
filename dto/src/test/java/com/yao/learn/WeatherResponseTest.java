package com.yao.learn;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class WeatherResponseTest {
    @Test
    public void test() {
        WeatherData weatherData = new WeatherData(2500000.0, 60000000, "晴", 10.0, 100, "2023-05-01T12:00:00", 28.0);
        Location location = new Location("上海**", "310000", "中国", 121.5, 31.0, "Asia/Shanghai");
        WeatherResponse weatherResponse = new WeatherResponse(20000, "成功", weatherData, location);

        if(weatherResponse.isValid())
        {
            System.out.println(weatherResponse.toFormattedString());
        }
        else
        {
            System.out.println("数据记录失败");
        }
    }
}
