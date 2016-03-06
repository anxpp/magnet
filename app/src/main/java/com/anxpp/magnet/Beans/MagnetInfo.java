package com.anxpp.magnet.Beans;

/**
 * 描述一个磁力链的相关信息
 *
 * Created by anxpp on 2016/3/6.
 *
 * @author anxpp.com
 *
 */
public class MagnetInfo {
    private Item item;
    private String url;

    public Item getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
