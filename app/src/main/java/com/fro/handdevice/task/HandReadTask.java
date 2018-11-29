package com.fro.handdevice.task;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fro.handdevice.app.App;
import com.fro.handdevice.app.Const;
import com.fro.util.HexStrConvertUtil;
import com.fro.util.StreamUtil;

/**
 * Created by Jorble on 2016/3/7.
 */
public class HandReadTask extends AsyncTask<Void, Void, Void> {

    // 循环状态
    public Boolean CIRCLE = false;
    // 任务开关控制
    public Boolean STATU = false;

    private ConnectTask connectTask;

    // 临时
    private byte[] readbuf = null;


    public HandReadTask(ConnectTask connectTask) {
        this.connectTask = connectTask;
    }

    /**
     * 准备
     */
    @Override
    protected void onPreExecute() {
    }

    /**
     * 读取子任务
     *
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            // 等待3秒，连接成功再读取
            Thread.sleep(3000);

            // 循环
            while (CIRCLE) {

                // 如果Task被取消了，马上退出循环
                if (isCancelled())
                    return null;

                STATU = connectTask.getStatu() ? true : false;

                if (STATU) {

                    // 读取
                    readbuf = StreamUtil.readData(connectTask.getInputStream());
                    if (readbuf != null) {
                        //转换为字符串（前后无空格、大写）
                        String isoString = HexStrConvertUtil.bytesToHexString(readbuf).toUpperCase().trim();
                        //判断是否返回异常
                        Log.i("返回数据=", isoString);
                        if (Const.SERV_RECV_SUCCESS.equals(isoString)) {
                            // 更新界面
                            publishProgress();
                        }

                        Thread.sleep(300);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 更新界面
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(Void... values) {
        // 如果Task被取消了，不再继续执行后面的代码
        if (isCancelled())
            return;

        // 更新界面数据
        Toast.makeText(App.getContext(), "发送成功!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
