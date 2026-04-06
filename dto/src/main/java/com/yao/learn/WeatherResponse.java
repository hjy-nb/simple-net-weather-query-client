package com.yao.learn;


import com.alibaba.fastjson2.annotation.JSONField;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

// 天气响应类
public class WeatherResponse {
    private static final Logger LOGGER = LoggerManagement.getBusinessLogger();
    @JSONField(name = "code")
    private int code;  // 状态码
    @JSONField(name = "fxLink")
    private String message;     // 状态消息
    @JSONField(name = "now")
    private WeatherData data;// 天气数据对象
    private Location location;  // 位置信息
    @JSONField(name = "updateTime")
    private String timestamp;     // 响应时间戳
    private boolean valid= true;

    //无参构造器(部分操作通过无参+setter建立对象，并提供这种方式建立对象的数据判断重载方法）
    public WeatherResponse() {}

    public WeatherResponse(int code, String message, WeatherData data, Location location) {

        LOGGER.info("开始记录WeatherResponse：状态码={}, 状态消息={}", code, message);

        List<String> errors = checkErrors(code, message, data, location);

        if(!hasErrors(errors)) {
            this.code = code;
            this.message = message;
            this.data = data;
            this.location = location;

            LOGGER.info("数据记录成功\n");
        }
    }

    //获取错误信息
    public List<String> checkErrors(int code, String message, WeatherData data, Location location) {
        List<String> errors = new ArrayList<>();

        if(data == null ||!data.isValid(data)){
            errors.add("数据无效：WeatherData无效");
        }
        if(location == null ||!location.isValid(location)){
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

    public List<String> checkErrors(WeatherResponse response){
        return response.checkErrors(response.getCode(), response.getMessage(), response.getData(), response.getLocation());
    }

    //判断是否有错误信息
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasErrors(List<String> errors) {
        if(!errors.isEmpty()){
            valid = false;
            LOGGER.warn("数据记录失败,异常原因如下：{}\n", String.join(", ", errors));
            return true;
        }
        return false;
    }

    //验证状态消息
    public boolean isMessageValid(String message) {
        return message != null;
    }

    //验证状态码
    public boolean isCodeValid(int code) {
        return code == 200;
    }

    //验证数据是否有效
    public boolean isValid() {
        return valid;
    }

    public boolean isValid(WeatherResponse response) {
        List<String> errors = response.checkErrors(response);

        return !response.hasErrors(errors);
    }

    //获取格式化后的信息
    public String toFormattedString() {
        return String.format("\n状态码：%d \n 状态消息：%s \n 天气数据：%s \n 位置信息：%s \n 响应时间戳：%s\n",
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(WeatherData data) {
        this.data = data;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
