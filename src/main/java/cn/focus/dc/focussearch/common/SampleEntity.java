package cn.focus.dc.focussearch.common;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by haotianliang213806 on 2017/9/19.
 */
public class SampleEntity {
    private String id;
    private Object message;

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getId() {
        return this.id;
    }

    public Object getMessage() {
        return this.message;
    }
}
