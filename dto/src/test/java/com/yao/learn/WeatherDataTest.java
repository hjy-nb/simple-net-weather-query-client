package com.yao.learn;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

public class WeatherDataTest {
    @Test
    public void test() {
        LocalDateTime updateTime = LocalDateTime.now();
        WeatherData weatherData = new WeatherData(25.0, 60, "晴", 10.0, 100, updateTime, 28.0);
        if(weatherData.isValid())
        {
            System.out.println(weatherData.toFormattedString());
        }
        else
        {
            System.out.println("数据记录失败");
        }
    }
}
