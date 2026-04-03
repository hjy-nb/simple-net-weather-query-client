package com.yao.learn;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

public class WeatherDataTest {
    @Test
    public void test() {
        LocalDateTime updateTime = LocalDateTime.now();
        WeatherData weatherData = new WeatherData(25.0, 60, "", 10.0, 100, updateTime, 28.0);
    }
}
