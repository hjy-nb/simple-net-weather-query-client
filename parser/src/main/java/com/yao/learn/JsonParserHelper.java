package com.yao.learn;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JsonParserHelper {
    private static final Logger BUSINESS_LOGGER=LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER=LoggerManagement.getErrorLogger();

    private final Map<String, Object> defaultValueMap;                 // 默认值映射，存储字段的默认值
    private final Set<String> requiredFieldSet;                      // 必需字段集合，定义JSON中必须存在的字段
    private final DateTimeFormatter dateTimeFormatter;                   // 日期时间格式化器，用于解析和格式化日期时间
    private final NumberFormat numberFormat;                           // 数字格式化器，用于解析和格式化数字

    public JsonParserHelper()
    {
        BUSINESS_LOGGER.info("开始初始化JsonParserHelper");

        String dateTimeFormat =  LoadConfig.getProperty("date.time.format");
        this.dateTimeFormatter=DateTimeFormatter.ofPattern(dateTimeFormat==null?"yyyy-MM-dd HH:mm:ss":dateTimeFormat);
        this.numberFormat=NumberFormat.getInstance();
        this.requiredFieldSet = Set.of("cityName", "temperature", "updateTime");
        this.defaultValueMap = Map.of(
                "humidity", 0,
                "windSpeed", 0.0,
                "visibility", 0,
                "feelsLike", 0.0,
                "longitude", 0.0,
                "latitude", 0.0
        );

        BUSINESS_LOGGER.info("初始化JsonParserHelper完成");
    }

    //序列化，对象转为JSON字符串
    public String serialize(Object obj) throws Exception{
        return JSON.toJSONString(obj);
    }

    //反序列化，JSON字符串转为对象
    public <T> T deserialize(String json, Class<T> clazz) throws Exception{
        return JSON.parseObject(json, clazz);     //自动映射到构造函数所需字段，有嵌套类也一样  |有无参构造器+setter
    }

    //解析JSON数组
    public <T> List<T> parseArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    //判断JSON对象是否合法,及字段是否存在
    public boolean isValidJsonObject(JSONObject json,String fieldName) {
        return  json==null || !json.containsKey(fieldName);
    }

    //安全获取字符串
    public String getString(JSONObject json, String fieldName, String defaultValue) {
        if(isValidJsonObject(json,fieldName)) return defaultValue;

        String value = json.getString(fieldName);

        return value==null?defaultValue:value;
    }

    //安全获取数组
    public List<Object> getArray(JSONObject json, String fieldName) throws Exception{
        if(isValidJsonObject(json,fieldName)) return null;

        return json.getJSONArray(fieldName).toList(Object.class);
    }

    //安全获取int
    public int getInt(JSONObject json, String fieldName, int defaultValue) {
        if(isValidJsonObject(json,fieldName)) return defaultValue;

        return json.getIntValue(fieldName);
    }

    //安全获取double
    public double getDouble(JSONObject json, String fieldName, double defaultValue) {
        if(isValidJsonObject(json,fieldName)) return defaultValue;

        Double value = json.getDouble(fieldName);
        return value==null?defaultValue:value;
    }

    //安全获取boolean
    public boolean getBoolean(JSONObject json, String fieldName, boolean defaultValue) {
        if(isValidJsonObject(json,fieldName)) return defaultValue;

        Boolean value = json.getBoolean(fieldName);
        return value==null?defaultValue:value;
    }

    //获取日期时间
    public LocalDateTime getDateTime(JSONObject json, String fieldName) {
        if(isValidJsonObject(json,fieldName)) return null;

        String value = json.getString(fieldName);
        if(value==null) return null;
        else{
            try{
                return LocalDateTime.parse(value,dateTimeFormatter);
            }
            catch (Exception e){
                BUSINESS_LOGGER.warn("日期时间解析错误：{}",e.getMessage());
                return null;
            }
        }
    }

    //清洗数据
    public String sanitizeJson(String json) {
        if (json == null) return null;

        // 去除BOM和空白
        json = json.trim();

        // 去除尾随逗号
        json = json.replaceAll(",\\s*([}\\]])", "$1");

        // 替换单引号
        json = json.replace('\'', '"');

        return json;
    }

    //判断必要字段是否缺失
    public boolean isRequiredFieldsMissing(JSONObject json, Set<String> requiredFields) {
        if(json==null||requiredFields==null||requiredFields.isEmpty()){
            return true;
        }

        boolean flag= false;
        for(String field:requiredFields){
            if(!json.containsKey(field)||json.get(field)==null){
                BUSINESS_LOGGER.warn("缺少必要字段：{}",field);
                flag= true;
            }
        }

        return flag;
    }

    //判断并设置默认字段值
    public void setDefaultValues(JSONObject json, Map<String, Object> defaultValueMap) {
        if(json==null||defaultValueMap==null||defaultValueMap.isEmpty()){
            return;
        }

        for(Map.Entry<String,Object> entry:defaultValueMap.entrySet()){

            if(!json.containsKey(entry.getKey())||json.get(entry.getKey())==null){
                json.put(entry.getKey(),entry.getValue());

                BUSINESS_LOGGER.info("设置默认字段值：{}",entry.getKey());
            }
        }
    }

    public Map<String, Object> getDefaultValueMap() {
        return defaultValueMap;
    }

    public Set<String> getRequiredFieldSet() {
        return requiredFieldSet;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }
}
