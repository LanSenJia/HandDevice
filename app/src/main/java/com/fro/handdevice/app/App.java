package com.fro.handdevice.app;

import android.app.Application;

import com.fro.handdevice.cache.ACache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jorble on 2016/5/9.
 */
public class App extends Application{
    private static App context;
    private static ACache mAcache;
    private static ExecutorService executorService;

    public static ACache getAcache() {
        return mAcache;
    }
    
    public static App getContext() {
        return context;
    }
    
    public static ExecutorService getExecutor() {
        return executorService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //缓存
        mAcache=ACache.get(this);
      //定长线程池
      executorService = Executors.newFixedThreadPool(5);
        
    }
    
    /**
     * 获取key-value
     * @param key
     * @param index
     * @return
     */
    public static String getCacheString(String key,String index){
    	String value=App.getAcache().getAsString(key);
		if(value!=null && value!=""){
			return value;
		}else{
			return index;
		}
    }
    
}
