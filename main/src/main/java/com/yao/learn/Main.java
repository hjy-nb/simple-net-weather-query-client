package com.yao.learn;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;


//天气数据在https://mf4ky4q37p.re.qweatherapi.com/v7/weather/now 基础url下  -》获取实时天气数据
//城市信息在https://mf4ky4q37p.re.qweatherapi.com/v7/city/lookup 获取
//获取json响应体中字段与自定义字段名不一致，用jsonfield注解统一，反序列化时只能用注解指定的名称。
public class Main {
    private static final Logger BUSINESS_LOGGER = LoggerManagement.getBusinessLogger();
    private static final Logger ERROR_LOGGER = LoggerManagement.getErrorLogger();

    private  WeatherApiClient client;
    private WeatherApiClient getIdClient;

    private WeatherResponseParser parser;

    private Scanner scanner;
    private AtomicInteger successCount;            // 成功计数器
    private AtomicInteger failCount;               // 失败计数器
    private Map<String, Long> cityQueryTime;   // 城市查询总时间统计
    private Map<String, Long> cityQueryCount;   // 城市查询总次数统计

    //初始化
    public void init(){
        BUSINESS_LOGGER.info("开始初始化");

        //创建WeatherApiClient实例
        String apiKey = LoadConfig.getProperty("api.key");
        String baseUrl = LoadConfig.getProperty("api.base.url");
        WeatherHttpClientBuilder builder=new WeatherHttpClientBuilder();
        this.client = new WeatherApiClient(apiKey, baseUrl, builder);

        String idUrl = LoadConfig.getProperty("api.id.url");
        this.getIdClient = new WeatherApiClient(apiKey, idUrl, builder);

        this.parser = new WeatherResponseParser(new JsonParserHelper());

        this.scanner = new Scanner(System.in);
        this.successCount = new AtomicInteger(0);
        this.failCount = new AtomicInteger(0);
        this.cityQueryTime = new HashMap<>();
        this.cityQueryCount = new HashMap<>();

        //添加默认请求头
        this.client.addDefaultHeaders("Accept", "application/json");
        this.client.addDefaultHeaders("Content-Type", "application/json");
    }
    public static void main(String[] args) {
        Main mainObject = new Main();
        try {
            mainObject.init();
            BUSINESS_LOGGER.info("初始化完成");

            mainObject.runMain();
        } catch (Exception e) {
            ERROR_LOGGER.error("程序出错退出", e);
            System.exit(1);         //return：只退出main方法，JVM可能还在运行（如果有非守护线程）System.exit(1)：强制终止整个JVM进程，确保程序完全退出
        }

    }

    //运行主程序
    public void runMain() throws Exception {
        BUSINESS_LOGGER.info("开始执行");

        while (true) {
            System.out.println("选择状态：1->运行 | 0->退出");
            int status = scanner.nextInt();
            if(status==0){
                BUSINESS_LOGGER.info("选择退出程序");
                showStatistics();

                close();

                break;
            }
            else if(status==1){
                BUSINESS_LOGGER.info("选择运行程序");

                try{
                    run();
                } catch (IllegalArgumentException e) {
                    ERROR_LOGGER.error("参数名称不存在或国家查询暂不支持", e);
                    System.out.println("参数名称不存在或国家查询暂不支持");
                } catch (IOException e) {
                    ERROR_LOGGER.error("查询请求不存在，请重试", e);
                    System.out.println("查询请求不存在，请重试");
                } catch (Exception e) {
                    ERROR_LOGGER.error("程序出错退出", e);
                    throw e;
                }
            }
            else{
                BUSINESS_LOGGER.info("输入错误");
                System.out.println("输入错误,请重试");
            }
        }
    }

    //运行程序
    public void run() throws Exception{
        System.out.println("选择查询模式：0->只查询城市 | 1->指定国家查询城市");
        int mode = scanner.nextInt();

        WeatherResponse response= null;
        switch(mode){
            case 0->{
                long startTime = System.currentTimeMillis();
                BUSINESS_LOGGER.info("只查询城市");

                System.out.println("请输入城市名称：");
                String city = scanner.next();
                cityQueryCount.put(city, cityQueryCount.getOrDefault(city, 0L)+1L);

                Location cityData = getCityId(city);
                BUSINESS_LOGGER.info("查询城市ID成功：{}", cityData.getCityId());

                response = client.queryWeather(cityData.getCityId());
                response.setLocation(cityData);

                long time = System.currentTimeMillis()-startTime;
                cityQueryTime.put(city, cityQueryTime.getOrDefault(city, 0L)+time);
            }
            case 1->{
                long startTime = System.currentTimeMillis();
                BUSINESS_LOGGER.info("指定国家查询城市");

                System.out.println("请输入城市名称：");
                String city = scanner.next();
                cityQueryCount.put(city, cityQueryCount.getOrDefault(city, 0L)+1L);

                System.out.println("请输入国家名称：");
                String country = scanner.next();

                BUSINESS_LOGGER.info("查询城市：{}，国家：{}", city, country);

                Location cityData = getCityId(city, country);

                response = client.queryWeather(cityData.getCityId(), country);
                response.setLocation(cityData);

                long time = System.currentTimeMillis()-startTime;
                cityQueryTime.put(city, cityQueryTime.getOrDefault(city, 0L)+time);
            }
            default -> {
                BUSINESS_LOGGER.info("选择查询模式输入错误");
                System.out.println("选择查询模式输入错误");
            }
        }
        if(response == null || !client.getParser().validateWeatherResponse(response)){
            BUSINESS_LOGGER.info("查询结果错误");
            System.out.println("查询结果错误,请重试");

            failCount.incrementAndGet();
        }
        else{
            System.out.println(response.toFormattedString());

            successCount.incrementAndGet();
        }
    }

    //关闭资源
    public void close() throws Exception{
        BUSINESS_LOGGER.info("开始关闭资源");

        client.close();
        scanner.close();
        BUSINESS_LOGGER.info("关闭资源完成");
    }

    //显示统计信息
    public void showStatistics(){
        BUSINESS_LOGGER.info("开始显示统计信息");
        System.out.println("查询次数："+client.getRequestCounter());
        System.out.println("成功次数："+successCount.get());
        System.out.println("失败次数："+failCount.get());
        cityQueryTime.forEach((city, time)->{
            double averageTime = (double)time/cityQueryCount.get(city);
            System.out.println("城市："+city+"，总查询时间："+time+"，总查询次数："+cityQueryCount.get(city)+"，平均查询时间："+String .format("%.2f",averageTime));
        });
    }

    //获取城市信息
    public Location getCityId(String cityName) throws Exception, IOException ,IllegalArgumentException{
        BUSINESS_LOGGER.info("获取城市ID");
        String url = getIdClient. buildRequestUrl(cityName);

        String json = getIdClient.executeGetRequest(url);

        if(json == null){
            ERROR_LOGGER.error("获取城市ID请求失败");
            throw new Exception("获取城市ID请求失败");
        }

        JSONObject jsonObj = parser.getParserHelper().deserialize(json, JSONObject.class);

        List<Object> locations = parser.getParserHelper().getArray(jsonObj, "location");
        if(locations == null || locations.isEmpty()){
            ERROR_LOGGER.error("获取城市ID失败");
            throw new Exception("获取城市ID失败");
        }

        JSONObject location = (JSONObject)locations.getFirst();

        return parser.parseLocation(location);
    }

    public Location getCityId(String cityName, String countryName) throws Exception, IOException,IllegalArgumentException{
        BUSINESS_LOGGER.info("获取指定国家城市ID");
        String url = getIdClient. buildRequestUrl(cityName, countryName);

        String json = getIdClient.executeGetRequest(url);

        if(json == null){
            ERROR_LOGGER.error("获取指定国家城市ID请求失败");
            throw new Exception("获取获取指定国家城市ID请求失败");
        }

        JSONObject jsonObj = parser.getParserHelper().deserialize(json, JSONObject.class);

        List<Object> locations = parser.getParserHelper().getArray(jsonObj, "location");
        if(locations == null || locations.isEmpty()){
            ERROR_LOGGER.error("获取指定国家城市ID失败");
            throw new Exception("获取指定国家城市ID失败");
        }

        JSONObject location = (JSONObject)locations.getFirst();

        return parser.parseLocation(location);
    }
}
