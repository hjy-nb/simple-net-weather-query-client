package com.yao.learn;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;
import org.slf4j.Logger;

//天气数据
public class WeatherData {
    private static final Logger LOGGER = LoggerManagement.getBusinessLogger();
    @JSONField(name = "temp")
    private double temperature;                     // 温度(℃)
    @JSONField(name = "humidity")
    private int humidity;                         // 湿度(%)
    @JSONField(name = "text")
    private String weather;                             //天气状况
    @JSONField(name = "windSpeed")
    private double windSpeed ;                         // 风速(m/s)
    @JSONField(name = "vis")
    private int visibility;                             // 能见度(km)
    @JSONField(name = "obsTime")
    private String updateTime;                   // 更新时间
    @JSONField(name = "feelsLike")
    private double feelsLike;           // 体感温度
    private boolean isTure= true;

    //无参构造器(部分操作通过无参+setter建立对象，并提供这种方式建立对象的数据判断重载方法）
    public WeatherData(){}

    public WeatherData(double temperature, int humidity, String weather, double windSpeed, int visibility,
            String updateTime, double feelsLike) {

        LOGGER.info("开始记录WeatherData：温度={}, 湿度={}, 天气={}, 风速={}, 能见度={}, 更新时间={}, 体感温度={}",
                 temperature, humidity, weather, windSpeed, visibility, updateTime, feelsLike);

        List<String> errors = checkErrors(temperature, humidity, weather, windSpeed, visibility, updateTime, feelsLike);

        if(!hasErrors(errors)) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.weather = weather;
            this.windSpeed = windSpeed;
            this.visibility = visibility;
            this.updateTime = updateTime;
            this.feelsLike = feelsLike;

            LOGGER.info("数据记录成功\n");
        }
    }

    // 数据正确性验证
    public boolean isValid() {
        return isTure;
    }

    public boolean isValid(WeatherData weatherData) {
        List<String> errors = checkErrors(weatherData);

        return !weatherData.hasErrors(errors);
    }

    //验证数据范围
    public List<String> checkErrors(double temperature, int humidity, String weather, double windSpeed, int visibility,String updateTime, double feelsLike) {
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
            errors.add("更新时间错误为null");
        }
        if (!isFeelsLikeValid(feelsLike)) {
            errors.add(String.format("体感温度: %.1f 错误", feelsLike));
        }
        return errors;
    }

    public List<String> checkErrors(WeatherData weatherData){
        return checkErrors(weatherData.getTemperature(), weatherData.getHumidity(), weatherData.getWeather(), weatherData.getWindSpeed(), weatherData.getVisibility(), weatherData.getUpdateTime(), weatherData.getFeelsLike());
    }

    //判断是否有错误信息
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasErrors(List<String> errors) {
        if(!errors.isEmpty()){
            LOGGER.warn("数据记录失败,异常原因如下：{}\n", String.join(", ", errors));
            isTure = false;
            return true;
        }

        return false;
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
    public boolean isUpdateTimeValid(String updateTime) {
        return updateTime != null;
    }

    //验证体感温度
    public boolean isFeelsLikeValid(double feelsLike) {
        return feelsLike >= -50 && feelsLike <= 50;
    }

    //验证天气
    public boolean isWeatherValid(String weather) {
        return weather != null && weather.matches("[a-zA-Z\\u4e00-\\u9fa5]+");
    }

    //获取格式化后的信息
    public String toFormattedString() {
        return String.format("温度：%.1f℃ | 湿度：%d%% | 天气：%s | 风速：%.1fm/s | 能见度：%dkm | 更新时间：%s | 体感温度：%.1f℃",
                temperature, humidity, weather, windSpeed, visibility,updateTime, feelsLike);
    }

// ... existing code ...

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

    public String getUpdateTime() {
        return updateTime;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

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

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }
}
