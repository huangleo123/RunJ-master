package com.rentalphang.runj.utils;

/**
 * 基本配置
 * Created by 洋 on 2016/4/22.
 */
public class ConfigUtil {

    /**
     * Application ID ，初始化用到密钥
     * 033db1894e6262436e0a8a1b2005305d
     */
    public static final String BMOB_APP_ID = "30efacb14b9ef229423dffa570ee521d";

    /**
     * REST API Key , REST API请求中HTTP头部信息必须附带密钥之一
     * 6baccc89d2206e60d584eacdbea672ee
     */
    public static final String BMOB_API_KEY = "5fef7babb85f34ddb90fd5f047607fa9";

    /**
     * Secret key ，是SDK安全密钥，不可泄漏，在云端逻辑测试云端代码时需要用到
     * d72cb97ebafadbf6
     */
    public static final String BMOB_SECRET_KEY = "61b3242e865dbb5f";

    /**
     * Master Key , 超级权限Key。应用开发或调试的时候可以使用该密钥进行各种权限的操作，此密钥不可泄漏
     * 59a4cabcc76da57739a552d429120a12
     */
    public static final String BMOB_MASTER_KEY = "f8d30d9c30d117c6c6f7e70ddf9abc79";

    /**
     * apikey ，APIStore通用key
     * 618aa2b9fcfa0575e8acefb8c843f76a
     */
    public  static final String APISTORE_API_KEY = "VowY13klTvjDTZ4wkoFjwRCOOdFG1R2U";


    /**
     * 鹰眼服务id
     * 115788
     */
    public static final String YINGYAN_SERVICE_ID = "200436";

    /**
     * 天气接口地址
     */
    public static final String WEATHER_API = "http://apis.baidu.com/heweather/weather/free";

    /**
     * 城市列表接口
     */
    public static final String CITY_LIST_API = " https://api.heweather.com/x3/citylist?search=allchina" +
            "&key=a7ec86e719d9458da2e1f67ebc73d2e4";

}
