package com.kierasis.clheartapp;

public class EndPoints {

    //private static final String ROOT_URL = "https://www.kierasis.me/store/app/";
    private static final String ROOT_URL = "http://192.168.254.2/tanauan/app/";

    public static final String CHECK_APP_VERSION = ROOT_URL + "app_version.php";
    public static final String GET_PHONE_INFO = ROOT_URL + "get_phone_info.php";
    public static final String GEN_DEVICE_ID = ROOT_URL + "gen_device_id.php";
    public static final String UPDATE_DEVICE_VERSION = ROOT_URL + "update_device_version.php";

    public static final String LOGIN = ROOT_URL + "login.php";
    public static final String REGISTER = ROOT_URL + "register.php";

    public static final String GET_JSON_ANNOUNCEMENT_URL = ROOT_URL + "announcement_json.php";
    //public static final String GET_PRODUCTS_LIST = ROOT_URL + "get_product_list.php";
    //public static final String GET_PRODUCT_INFO = ROOT_URL + "get_product_info.php";
    //public static final String GET_JSON_USER_INSTANT_DIGITAL = ROOT_URL + "user_instant_digital.php";


    public static final String GET_USER_DETAILS = ROOT_URL + "api.php?action=user_details";

    public static final String GET_PRODUCTS_LIST = ROOT_URL + "api.php?action=get_product_list";
    public static final String GET_PRODUCT_INFO = ROOT_URL + "api.php?action=get_product_info";
    public static final String PURCHASE_CHECKOUT = ROOT_URL + "api.php?action=purchase_checkout";
    public static final String PURCHASE_PROCESS = ROOT_URL + "api.php?action=purchase_process";
    public static final String GET_ORDER_LIST = ROOT_URL + "api.php?action=get_order_list";
    public static final String GET_PURCHASED_EVOUCHER = ROOT_URL + "api.php?action=get_purchased_evoucher";
    public static final String SET_CLAIM_EVOUCHER = ROOT_URL + "api.php?action=set_claim_evoucher";
    public static final String GET_WALLET_TRANSACTION = ROOT_URL + "api.php?action=get_wallet_transaction";
    public static final String GET_NOTIFICATION = ROOT_URL + "api.php?action=get_notification";

    public static final String LOGOUT = ROOT_URL + "logout.php";
}
