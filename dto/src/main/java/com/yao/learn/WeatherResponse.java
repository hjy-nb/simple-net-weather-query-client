package com.yao.learn;


import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

// 天气响应类
public class WeatherResponse {
    private static final Logger LOGGER = LoggerManagement.getBusinessLogger();
    private int code;           // 状态码
    private String message;     // 状态消息
    private WeatherData data;// 天气数据对象
    private Location location;  // 位置信息
    private long timestamp;     // 响应时间戳
    private boolean valid= true;

    public WeatherResponse(int code, String message, WeatherData data, Location location) {

        LOGGER.info("开始记录WeatherResponse：状态码={}, 状态消息={}", code, message);

        List<String> errors = checkErrors(code, message, data, location);

        if(!errors.isEmpty()){
            valid = false;
            LOGGER.warn("数据记录失败,异常原因如下：{}\n", String.join(", ", errors));
        }
        else{
            this.code = code;
            this.message = message;
            this.data = data;
            this.location = location;
            this.timestamp = System.currentTimeMillis();

            LOGGER.info("数据记录成功\n");
        }
    }

    private List<String> checkErrors(int code, String message, WeatherData data, Location location) {
        List<String> errors = new ArrayList<>();

        if(!data.isValid()){
            errors.add("数据无效：WeatherData无效");
        }
        if(!location.isValid()){
            errors.add("数据无效：Location无效");
        }
        if(!isCodeValid(code)){
            errors.add(String.format("状态码: %d 错误", code));
        }
        if(!isMessageValid(message)){
            errors.add(String.format("状态消息: %s 错误", message));
        }

        return errors;
    }

    //验证状态消息
    public boolean isMessageValid(String message) {
        return message != null && message.matches("[a-zA-Z\\u4e00-\\u9fa50-9\\s.,!?]+");
    }

    //验证状态码
    public boolean isCodeValid(int code) {
        return code == 200;
    }

    //验证数据是否有效
    public boolean isValid() {
        return valid;
    }

    //获取格式化后的信息
    public String toFormattedString() {
        return String.format("状态码：%d | 状态消息：%s | 天气数据：%s \n| 位置信息：%s \n| 响应时间戳：%d",
                code, message, data.toFormattedString(), location.toFormattedString(), timestamp);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public WeatherData getData() {
        return data;
    }

    public Location getLocation() {
        return location;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
