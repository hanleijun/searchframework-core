package cn.focus.dc.focussearch.util;

/**
 * Created by IntelliJ IDEA.
 * User: zhaozhaozhang
 * Date: 14-8-25
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public enum WriteReadEnum {
    wirte_shared("WIRTE", "WIRTE"),//redis write分片
    read_shared("READ", "READ"),; //redis read 分片
    private String key;
    private String vlaue;

    WriteReadEnum(String key, String vlaue) {
        this.key = key;
        this.vlaue = vlaue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVlaue() {
        return vlaue;
    }

    public void setVlaue(String vlaue) {
        this.vlaue = vlaue;
    }
}
