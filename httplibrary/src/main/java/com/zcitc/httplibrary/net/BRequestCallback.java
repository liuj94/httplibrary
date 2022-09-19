package com.zcitc.httplibrary.net;

import android.app.ProgressDialog;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.BuildConfig;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.request.base.Request;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.UnknownHostException;

import okhttp3.Response;

/**
 * @author liuj
 */
public abstract class BRequestCallback<T> extends AbsCallback<T> {

    public Type mType;

    private ProgressDialog progressDialog;

    public BRequestCallback(ProgressDialog progressDialog) {
        this();
        this.progressDialog = progressDialog;
    }

    public BRequestCallback() {
        Type type = getClass().getGenericSuperclass();
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        mType = new ParameterizedTypeImpl(BaseJson.class, new Type[]{types[0]});
    }


    @Override
    public T convertResponse(Response response) throws Throwable {

        String bodyStr = null;
        if (response.body() != null) {
            bodyStr = response.body().string();
        }

        if (BuildConfig.DEBUG) {
            Log.d("convertResponse", "========================");
            Log.d("convertResponse", "========================");
            Log.d("convertResponse", "===========║  url : " + response.request().url() + "  =============");
            Log.d("convertResponse", "bodyStr==" + bodyStr);
            Log.d("convertResponse", "========================");
        }


        if (response.code() == 200) {
            if (bodyStr == null) {
                throw new RequestException(response.code(), "请求数据为空");
            }

            BaseJson<T> data = null;
            try {
                data = JSONObject.parseObject(bodyStr, mType);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.d("convertResponse", "Exception=" + e.toString());
                }
            }
            if (data == null) {
                throw new RequestException(response.code(), "解析错误:数据解析出错");
            }

            if (data.isSuccess()) {
//                LogPrintUtils.INSTANCE.printLog(response, bodyStr);
                return data.getResult();
            } else if (data.getStatus() == 304) {

                throw new RequestException(304, "不刷新数据");
            } else {


                throw new RequestException(response.code(), data.getMessage());
            }
        } else {



            if (bodyStr != null) {

                JSONObject errorJson = null;
                try {
                    errorJson = JSONObject.parseObject(bodyStr);
                } catch (Exception e) {
                }
                if (errorJson != null) {
                    throw new RequestException(response.code(), errorJson.toJSONString());
                } else {
                    throw new RequestException(response.code(), bodyStr);
                }
            }
            throw new RequestException(response.code(), "服务器错误");
        }
    }


    @Override
    public void onSuccess(com.lzy.okgo.model.Response<T> response) {
        if (response.body() != null) {
            onMySuccess(response.body());
        } else {
            onSuccessNullData();
        }
    }

    public void onMySuccess(T data) {

    }

    public void onSuccessNullData() {

    }

    public void onSuccessNullData(String code) {

    }

    public void onErrorBusiness(String msg) {
        //ToastUtils.show(msg);
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        super.onError(response);


}

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }


    }

    @Override
    public void onFinish() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }
}
