package com.haoyunhu.tools.rest.constant;

/**
 * Created by weijun.hu on 2015/7/13.
 */
public class RestBaseConstant {
    public final static String SECRET_LEVEL_2 = "L2";
    public final static String SECRET_LEVEL_3 = "L3";
    public final static String REQUEST_GET = "GET";
    public final static String REQUEST_POST = "POST";
    public final static String L3_KEY_D = "D";
    public final static String VALIDATE_LEVEL = "validate_level";
    public final static String PARAMS_METHOD = "method";
    public final static String PARAMS_SIGN = "sign";
    public static final String PARAMS_APPID = "appId";
    public static final String PARAMS_TOKEN = "token";
    public static final String REQUEST_IP = "requestIp";
    public static final String USER_AGENT = "userAgent";
    public static final String PARAMS_EVENT_TIME = "eventTime";
    public final static String PARAMS_IDEMPOTENT = "idempotent";
    public final static String FROM_SOURCE = "from_source";
    public final static String FROMSOURCE = "fromSource";
    public final static String FROM_SOURCE_ID = "from_source_id";
    public final static String FROMSOURCEID = "fromSourceId";

    public final static String LEVEL_ONE = "1";
    public final static String LEVEL_TWO = "2";
    public final static String LEVEL_THREE = "3";

    public final static String STRING_ONE = "1";

    public final static String OPERATOR_SUCCESS = "操作成功";
    public final static String OPERATOR_FAILURE = "操作失败";
    public final static String OPERATOR_PARAMS_FAILURE = "Json参数格式不正确";
    public final static String OPERATOR_METHOD_FAILURE = "method值不正确";
    public final static String OPERATOR_HTTP_VALID_FAILURE = "验证协议不正确";
    public static final String OPERATOR_PARAMS_VALUE_EMPTY = "参数{0}不能为空";
    public static final String OPERATOR_PARAMS_VALUE_COVERT = "参数{0}转换类型失败";
    public static final String EMPTY_APPID = "appId不能为空";
    public static final String EMPTY_METHOD = "method不能为空";
    public static final String EMPTY_SIGN = "sign不能为空";
    public static final String EMPTY_EVENT_TIME = "eventTime不能为空";
    public static final String ERROR_SECRET = "非法调用,身份ID不正确";
    public static final String ERROR_SIGN = "非法调用,签名错误";
    public static final String OPERATOR_PARAMS_L3_ERROR = "L3参数不能为空并且长度大于64位";
    public static final String OPERATOR_ERROR_TOKEN = "token错误或失效";
    public static final String OPERATOR_L3_AES_ERROR = "AES解密错误";
    public static final String REQUEST_METHOD_ERROR = "请使用POST请求";
    public static final String REQUEST_PARAM_EMPTY = "Json参数不能为空";
    public final static String GET_RESPONSE_TIME_FAILURE = "获取返回时间失败";
    public final static String REQUEST_LIMIT_IDEMPOTENT_REPEAT = "请不要重复请求，请稍后再试";
    public final static String REQUEST_LIMIT_EXT_METHOD_REPEAT = "该请求已经被限制流量，请稍后再试";
    public final static String REQUEST_LIMIT_METHOD_REPEAT = "被限制流量，请稍后再试";

    public final static String APPLICATION_INFO = "application_info";
}
