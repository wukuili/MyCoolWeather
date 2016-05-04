package com.liyj.coolweather.util;

/**
 * Created by Administrator on 2016/5/4.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
