package com.kierasis.clheartapp;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

public class my_singleton {

    private static my_singleton mInstance;
    private RequestQueue mRequestQueu;
    private Context mCtx;

    public my_singleton(Context mCtx) {
        this.mCtx = mCtx;
        mRequestQueu = getmRequestQueu();
    }

    public RequestQueue getmRequestQueu(){
       if (mRequestQueu == null){
           Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 1024*1024);
           Network network = new BasicNetwork(new HurlStack());
           mRequestQueu = new RequestQueue(cache,network);
           mRequestQueu = Volley.newRequestQueue(mCtx.getApplicationContext());

       }

       return mRequestQueu;
    }

    public static synchronized my_singleton getInstance(Context context){
        if(mInstance == null){
            mInstance = new my_singleton(context);
        }
        return mInstance;
    }

    public <I> void addToRequestQueue(Request<I> request){
        mRequestQueu.add(request);
    }
}
