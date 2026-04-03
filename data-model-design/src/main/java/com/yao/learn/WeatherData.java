package com.yao.learn;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import com.yao.learn.LoggerManagement;

public class WeatherData {
    private static final Logger LOGGER = LoggerManagement.getBusinessLogger();
    private double temperature;                     // 温度(℃)
    private int humidity;                         // 湿度(%)
    private String weather;                             //天气状况
    private double windSpeed ;                         // 风速(m/s)
    private int visibility;                             // 能见度(km)
    private LocalDateTime updateTime;                   // 更新时间
    private double feelsLike;                     // 体感温度

    public WeatherData() {}

    public WeatherData(double temperature, int humidity, String weather, double windSpeed, int visibility,
            LocalDateTime updateTime, double feelsLike) {
        LOGGER.info("开始记录数据：天气={}, 更新时间={}",weather,updateTime.toString());

        List<String> errors = isValid(temperature, humidity, weather, windSpeed, visibility, updateTime, feelsLike);

        if (!errors.isEmpty()) {
            LOGGER.error("数据记录失败：{}", String.join(", ", errors));
            throw new IllegalArgumentException("数据记录失败：" + String.join(", ", errors));
        }

        this.temperature = temperature;
        this.humidity = humidity;
        this.weather = weather;
        this.windSpeed = windSpeed;
        this.visibility = visibility;
        this.updateTime = updateTime;
        this.feelsLike = feelsLike;

        LOGGER.info("数据记录成功");
    }

    //验证数据范围
    public List<String> isValid(double temperature, int humidity, String weather, double windSpeed, int visibility,LocalDateTime updateTime, double feelsLike) {
        List<String> errors = new ArrayList<>();
        if (!isTemperatureValid(temperature)) {
            errors.add(String.format("温度: %.1f 异常", temperature));
        }
        if (!isHumidityValid(humidity)) {
            errors.add(String.format("湿度: %d 异常", humidity));
        }
        if (!isWeatherValid(weather)) {
            errors.add(String.format("天气: %s 错误", weather));
        }
        if (!isWindSpeedValid(windSpeed)) {
            errors.add(String.format("风速: %.1f 异常", windSpeed));
        }
        if (!isVisibilityValid(visibility)) {
            errors.add(String.format("能见度: %d 异常", visibility));
        }
        if (!isUpdateTimeValid(updateTime)) {
            errors.add(String.format("更新时间: %s 错误", updateTime.toString()));
        }
        if (!isFeelsLikeValid(feelsLike)) {
            errors.add(String.format("体感温度: %.1f 错误", feelsLike));
        }
        return errors;
    }

    //验证温度范围
    public boolean isTemperatureValid(double temperature) {
        return temperature >= -50 && temperature <= 50;
    }
    //验证湿度范围
    public boolean isHumidityValid(int humidity) {
        return humidity >= 0 && humidity <= 100;
    }

    //验证风速范围
    public boolean isWindSpeedValid(double windSpeed) {
        return windSpeed >= 0 && windSpeed <= 50;
    }

    //验证能见度范围
    public boolean isVisibilityValid(int visibility) {
        return visibility >= 0 && visibility <= 100;
    }

    //验证更新时间格式
    public boolean isUpdateTimeValid(LocalDateTime updateTime) {
        return updateTime != null;
    }

    //验证体感温度
    public boolean isFeelsLikeValid(double feelsLike) {
        return feelsLike >= -50 && feelsLike <= 50;
    }

    //验证天气
    public boolean isWeatherValid(String weather) {
        return weather != null && weather.matches("[a-zA-Z]+");
    }

    //获取格式化后的信息
    public String toFormattedString() {
        return String.format("温度：%.1f℃ | 湿度：%d%% | 天气：%s | 风速：%.1fm/s | 能见度：%dkm | 更新时间：%s | 体感温度：%.1f℃",
                temperature, humidity, weather, windSpeed, visibility,updateTime.toString(), feelsLike);
    }

// ... existing code ...
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getWeather() {
        return weather;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getVisibility() {
        return visibility;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public double getFeelsLike() {
        return feelsLike;
    }
}
