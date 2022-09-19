package com.zcitc.httplibrary.net

import com.lzy.okgo.model.Response


interface NetInterface<T> {


        fun onMySuccess(data:T)
        fun onError(data: Response<T>)
        fun onFinish()


}
