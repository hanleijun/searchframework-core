package cn.focus.dc.focussearch.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * JsonResponse class.
 * </p>
 *
 * @author xingtaoshi@gmail.com json工具类
 * @version $Id: $Id
 */
public class JsonResponse {

    private JsonResponse() {
    }


    public static JSONObject badResult(String cause) {
        JSONObject result = new JSONObject();
        result.put("errorCode", 1);
        result.put("errorMessage", cause);
        return result;
    }

    public static JSONObject ok(Map<String, Object> params) {
        JSONObject result = new JSONObject();
        result.put("errorCode", 0);
        result.put("data", params);

        return result;
    }

    /**
     * jsonp请求失败
     *
     * @param cause    a {@link String} object.
     * @param callback a {@link String} object.
     * @return a {@link String} object.
     */
    public static String badResult(String cause, String callback) {
        JSONObject result = new JSONObject();
        result.put("errorCode", 1);
        result.put("errorMessage", cause);
        return callback + "(" + result.toJSONString() + ")";

    }

    /**
     * <p>
     * badResult.
     * </p>
     *
     * @param cause a {@link java.util.Map} object.
     * @return a {@link String} object.
     */
    public static String badResult(Map<String, String> cause) {
        JSONObject result = new JSONObject();
        result.put("errorCode", 1);
        result.put("errorMessage", cause);
        return result.toJSONString();
    }

    /**
     * 区分错误类型
     *
     * @param type  a int.
     * @param cause a {@link String} object.
     * @return a {@link String} object.
     */
    public static String badResult(int type, String cause) {
        JSONObject result = new JSONObject();
        result.put("errorCode", 1);
        result.put("errorMessage", ImmutableMap.of("type", type, "msg", cause));
        return result.toJSONString();
    }

    /**
     * <p>
     * ok.
     * </p>
     *
     * @return a {@link String} object.
     */
    public static String ok() {
        JSONObject result = new JSONObject();
        result.put("errorCode", 0);
        result.put("data", "成功");
        return result.toString();
    }

    /**
     * <p>
     * okWithEmpty.
     * </p>
     *
     * @return a {@link String} object.
     */
    public static String okWithEmpty() {
        JSONObject result = new JSONObject();
        result.put("errorCode", 0);
        result.put("data", Collections.emptyList());
        return result.toString();
    }

    /**
     * <p>
     * ok.
     * </p>
     *
     * @param key   a {@link String} object.
     * @param value a {@link Object} object.
     * @return a {@link String} object.
     */
    public static String ok(String key, Object value) {
        JSONObject result = new JSONObject();
        result.put("errorCode", 0);
        result.put("data", ImmutableMap.of(key, value));
        return result.toJSONString();
    }

    /**
     * <p>
     * ok.
     * </p>
     *
     * @param object a {@link Object} object.
     * @return a {@link String} object.
     */
    public static JSONObject ok(Object object) {
        JSONObject result = new JSONObject();
        result.put("errorCode", 0);
        result.put("data", object);
        return result;
    }

    public static String ok(Object object, SerializerFeature... features) {
        JSONObject result = new JSONObject();
        result.put("errorCode", 0);
        result.put("data", object);
        return JSONObject.toJSONString(result, features);
    }

    /**
     * 返回分页结果json字串
     *
     * @param object    数据对象
     * @param pageTotal 结果列表总共多少页
     * @param pageSize  每页多少条记录
     * @param pageNo    页号
     * @return json 字串
     */
    public static String okWithPaginate(Object object, int pageTotal, int pageSize, int pageNo) {
        JSONObject result = new JSONObject();
        result.put("pageTotal", pageTotal);
        result.put("pageSize", pageSize);
        result.put("pageNo", pageNo);
        result.put("data", object);
        result.put("errorCode", 0);
        return result.toJSONString();
    }

    /**
     * <p>
     * jsonp.
     * </p>
     *
     * @param object   a {@link Object} object.
     * @param callback a {@link String} object.
     * @return a {@link String} object.
     */
    public static String jsonp(Object object, String callback) {
        JSONObject result = new JSONObject();
        result.put("errorCode", 0);
        result.put("data", object);
        return callback + "(" + result.toJSONString() + ")";
    }

    /**
     * 可以通过map形式传递参数，但是如果是ok，msg这两个参数的值会被覆盖掉
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link String} object.
     */
    // public static String ok(Map<String, Object> params) {
    // JSONObject result = new JSONObject();
    // result.put("errorCode", 0);
    // result.put("data", params);
    // return result.toJSONString();
    // }

    /**
     * 可以通过map形式传递参数，但是如果是ok，msg这两个参数的值会被覆盖掉
     *
     * @param params a {@link com.google.common.collect.ImmutableMap} object.
     * @return a {@link String} object.
     */
    public static String ok(ImmutableMap<String, Object> params) {
        JSONObject result = new JSONObject();
        result.put("errorCode", 0);
        result.put("data", params);
        return result.toJSONString();
    }

    /**
     * 返回分页结果json字串
     *
     * @param list
     * @param pageTotal
     * @param pageSize
     * @param pageNo
     * @param <T>
     * @return json 字串
     */
    public static <T> String okWithPaginate(List<T> list, int pageTotal, int pageSize, int pageNo) {
        JSONObject result = new JSONObject();
        result.put("pageTotal", pageTotal / pageSize + (pageTotal % pageSize == 0 ? 0 : 1));
        result.put("pageSize", pageSize);
        result.put("pageNo", pageNo);
        result.put("errorCode", 0);
        result.put("data", list);
        return result.toJSONString();
    }

    /**
     * 返回分页结果的json
     *
     * @param params
     * @param pageTotal
     * @param pageSize
     * @param pageNo
     * @return json 字串
     */
    public static String okWithPaginate(Map<String, Object> params, int pageTotal, int pageSize,
                                        int pageNo) {
        JSONObject result = new JSONObject();
        result.put("pageTotal", pageTotal);
        result.put("pageSize", pageSize);
        result.put("pageNo", pageNo);
        result.put("errorCode", 0);
        result.put("data", params);
        return result.toJSONString();
    }

    public static String okWithDateFarmat(Object object, String dateFormat) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("errorCode", 0);
        result.put("data", object);
        return JSONObject.toJSONStringWithDateFormat(result, dateFormat == null
                ? "yyyy-MM-dd HH:mm:ss"
                : dateFormat);
    }

    public static String okWithDateFarmat(Object object) {
        return okWithDateFarmat(object, "yyyy-MM-dd HH:mm:ss");
    }

    public static <T> String okWithDateFarmat(List<T> list, int total, int pageSize, int pageNo,
                                              String dateFormat) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("total", total);
        result.put("pageTotal", total / pageSize + (total % pageSize == 0 ? 0 : 1));
        result.put("pageSize", pageSize);
        result.put("pageNo", pageNo);
        result.put("errorCode", 0);
        result.put("data", list);
        return JSONObject.toJSONStringWithDateFormat(result, dateFormat == null
                ? "yyyy-MM-dd HH:mm:ss"
                : dateFormat);
    }

    public static <T> String okWithDateFarmat(List<T> list, int total, int pageSize, int pageNo) {
        return okWithDateFarmat(list, total, pageSize, pageNo, "yyyy-MM-dd HH:mm:ss");
    }

    public static String okWithDateFarmat(Map<String, Object> map, String dateFormat) {
        return JSONObject.toJSONStringWithDateFormat(map, dateFormat == null
                ? "yyyy-MM-dd HH:mm:ss"
                : dateFormat);
    }

    public static String okWithDateFarmat(Map<String, Object> map) {
        return JSONObject.toJSONStringWithDateFormat(map, "yyyy-MM-dd HH:mm:ss");
    }

}
