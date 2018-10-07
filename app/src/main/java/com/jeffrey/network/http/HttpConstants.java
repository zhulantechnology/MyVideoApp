package com.jeffrey.network.http;

/**
 * @author: qndroid
 * @function: 所有请求相关地址
 * @date: 16/8/12
 */
public class HttpConstants {

    private static final String ROOT_URL = "http://10.0.2.2:8080";
    public static String HOME_RECOMMAND = ROOT_URL + "/MyVideoAppServer/Data/home_data.json";
    public static String PRODUCT_LIST = ROOT_URL + "/MyVideoAppServer/Data/search.php";
    public static String PRODUCT_LATESAT_UPDATE = ROOT_URL + "/MyVideoAppServer/Data/upsearch.php";
    public static String LOGIN = ROOT_URL + "/MyVideoAppServer/Data/user_info.json";
    public static String CHECK_UPDATE = ROOT_URL + "/MyVideoAppServer/Data/update.json";
    public static String COURSE_DETAIL = ROOT_URL + "/MyVideoAppServer/Data/course_detail.php";

//    private static final String ROOT_URL = "http://imooc.com/api";
//    /**
//     * 请求本地产品列表
//     */
//   public static String PRODUCT_LIST = ROOT_URL + "/fund/search.php";
//
//    /**
//     * 本地产品列表更新时间措请求
//     */
//    public static String PRODUCT_LATESAT_UPDATE = ROOT_URL + "/fund/upsearch.php";
//
//    /**
//     * 登陆接口
//     */
//    public static String LOGIN = ROOT_URL + "/user/login_phone.php";
//
//    /**
//     * 检查更新接口
//     */
//    public static String CHECK_UPDATE = ROOT_URL + "/config/check_update.php";
//
//    /**
//     * 首页产品请求接口
//     */
//    public static String HOME_RECOMMAND = ROOT_URL + "/product/home_recommand.php";
//    /**
//     * 课程详情接口
//     */
//    public static String COURSE_DETAIL = ROOT_URL + "/product/course_detail.php";

}


