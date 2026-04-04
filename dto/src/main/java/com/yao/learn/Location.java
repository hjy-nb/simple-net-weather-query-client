package com.yao.learn;

import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.List;


//位置信息
public class Location {
    private static final Logger LOGGER = LoggerManagement.getBusinessLogger();
    private String cityName;               //城市名称
    private String cityId;                //城市ID
    private String country;                //国家
    private double longitude;                //经度
    private double latitude;                // 纬度
    private String timezone;               // 时区
    private boolean isValid = true;

    public Location(String cityName, String cityId, String country, double longitude, double latitude, String timezone) {

        LOGGER.info("开始记录Location：城市={}, 经度={}, 纬度={}, 时区={}", cityName, longitude, latitude, timezone);
        List<String> errors = checkErrors(cityName, cityId, country, longitude, latitude, timezone);

        if (!errors.isEmpty()) {
            LOGGER.warn("数据记录失败,异常原因如下：{}\n", String.join(", ", errors));
            isValid = false;
        }
        else{
            this.cityName = cityName;
            this.cityId = cityId;
            this.country = country;
            this.longitude = longitude;
            this.latitude = latitude;
            this.timezone = timezone;

            LOGGER.info("数据记录成功\n");
        }
    }

    private List<String> checkErrors(String cityName, String cityId, String country, double longitude, double latitude, String timezone) {
        List<String> errors = new ArrayList<>();
        if (!isCityNameValid(cityName)) {
            errors.add(String.format("城市: %s 错误", cityName));
        }
        if (!isCityIdValid(cityId)) {
            errors.add(String.format("城市ID: %s 错误", cityId));
        }
        if (!isCountryValid(country)) {
            errors.add(String.format("国家: %s 错误", country));
        }
        if (!isLongitudeValid(longitude)) {
            errors.add(String.format("经度: %.1f 错误", longitude));
        }
        if (!isLatitudeValid(latitude)) {
            errors.add(String.format("纬度: %.1f 错误", latitude));
        }
        if (!isTimezoneValid(timezone)) {
            errors.add(String.format("时区: %s 错误", timezone));
        }
        return errors;
    }

    //验证城市名称
    public boolean isCityNameValid(String cityName) {
        return cityName != null && !cityName.isEmpty() && 
               cityName.matches("[a-zA-Z0-9\\u4e00-\\u9fa5\\s.-]+");
    }

    //验证城市 ID
    public boolean isCityIdValid(String cityId) {
        return cityId != null && !cityId.isEmpty() && cityId.matches("\\d+");
    }

    //验证国家名称
    public boolean isCountryValid(String country) {
        return country != null && country.matches("[a-zA-Z\\u4e00-\\u9fa5]+");
    }

    //验证经度
    public boolean isLongitudeValid(double longitude) {
        return longitude >= -180 && longitude <= 180;
    }

    //验证纬度
    public boolean isLatitudeValid(double latitude) {
        return latitude >= -90 && latitude <= 90;
    }

    //验证时区
    public boolean isTimezoneValid(String timezone) {
        return timezone != null && !timezone.isEmpty() && 
               timezone.matches("[a-zA-Z0-9/_+-]+");
    }

    //检验数据是否合理
    public boolean isValid() {
        return isValid;
    }

    //获取格式化后的信息
    public String toFormattedString() {
        return String.format("城市名称：%s | 城市ID：%s | 国家：%s | 经度：%.1f | 纬度：%.1f | 时区：%s",
            cityName, cityId, country, longitude, latitude, timezone);
    }

    public String getCityName() {
        return cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public String getCountry() {
        return country;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getTimezone() {
        return timezone;
    }
}