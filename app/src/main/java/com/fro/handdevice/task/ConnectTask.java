package com.fro.handdevice.task;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fro.handdevice.app.App;
import com.fro.handdevice.app.Const;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


/**
 * Created by Jorble on 2016/3/4.
 */
public class ConnectTask extends AsyncTask<Void,Void,Void> {

    private String IP;
    private int port;

    private Socket mSocket;
    private SocketAddress mSocketAddress;
    private InputStream inputStream;
    private OutputStream outputStream;

    private Boolean STATU=false;

    public ConnectTask(String IP,int port){
        this.IP=IP;
        this.port=port;
    }

    @Override
    protected void onPreExecute() {
        mSocket = new Socket();
    }

    /**
     * 子线程任务
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {

//        LogUtil.i(IP+"--"+port);
        mSocketAddress = new InetSocketAddress(IP, port);
        try {
            Log.i(Const.TAG,"开始连接");
            Log.i(Const.TAG,IP);Log.i(Const.TAG,""+port);

            mSocket.connect(mSocketAddress, 3000);// 设置连接超时时间为3秒
            if(mSocket.isConnected()) {
                Log.i(Const.TAG,"连接成功");
                STATU=true;
                inputStream = mSocket.getInputStream();//得到输入流
                outputStream = mSocket.getOutputStream();//得到输出流
            }else {
                STATU=false;
                Log.i(Const.TAG,"连接失败");
            }
            publishProgress();
        } catch (IOException e) {
            Log.i(Const.TAG,"连接异常");
            STATU=false;
            publishProgress();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新UI
     * @param values
     */
    @Override
    protected void onProgressUpdate(Void... values) {
    	//显示连接状态
    	if(STATU){
    		Toast.makeText(App.getContext(), "连接成功！", Toast.LENGTH_SHORT).show();
    	}else{
    		Toast.makeText(App.getContext(), "连接失败！", Toast.LENGTH_SHORT).show();
    	}
    }

    /**
     * 判断socket连接成功
     * @return
     */
    public Boolean getStatu(){
        return STATU;
    }

    /**
     * 获取socket
     * @return
     */
    public Socket getmSocket(){return mSocket;}

    /**
     * 获取输入流
     * @return
     */
    public InputStream getInputStream(){
        return inputStream;
    }

    /**
     * 获取输出流
     * @return
     */
    public OutputStream getOutputStream(){
        return outputStream;
    }

}
