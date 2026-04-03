package com.yao.learn;

import org.slf4j.Logger;

public class Location {
    private static final Logger LOGGER = LoggerManagement.getBusinessLogger();
    private String cityName;               //城市名称
    private String cityId;                //城市ID
    private String country;                //国家
    private double longitude;                //经度
    private double latitude;                // 纬度
    private String timezone;               // 时区

    public Location() {}

    public Location(String cityName, String cityId, String country, double longitude, double latitude, String timezone) {
        long startTime = System.currentTimeMillis();

        try {
            LOGGER.info("开始记录数据：城市名称={}, 城市ID={}, 国家={}, 经度={}, 纬度={}, 时区={}",
                 cityName, cityId, country, longitude, latitude, timezone);
            this.cityName = cityName;
            this.cityId = cityId;
            this.country = country;
            this.longitude = longitude;
            this.latitude = latitude;
            this.timezone = timezone;

            validateCoordinates();
        }
    }

    public void validateCoordinates() throws NumberFormatException{
        if (longitude < -180 || longitude > 180) {
            NumberFormatException e = new NumberFormatException("经度范围必须在-180到180之间"+ longitude);
            LOGGER.error("经度错误范围{}",longitude,e);
            throw e;
        }

        if (latitude < -90 || latitude > 90) {
            NumberFormatException e = new NumberFormatException("纬度范围必须在-90到90之间"+ latitude);
            LOGGER.error("纬度错误范围{}",latitude,e);
            throw e;
        }
    }
}
