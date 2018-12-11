package com.fro.handdevice.activity;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.comm.callbackimp.CallBack;
import com.comm.callbackimp.MessageRevicer;
import com.device.serialport.SerialPort;
import com.fro.handdevice.R;
import com.fro.handdevice.app.App;
import com.fro.handdevice.app.Const;
import com.fro.handdevice.task.ConnectTask;
import com.fro.handdevice.task.HandReadTask;
import com.fro.handdevice.task.ScanTask;
import com.fro.handdevice.utils.HelpUtil;
import com.fro.handdevice.utils.RegExpValidatorUtils;
import com.fro.util.StreamUtil;
import com.ntzzDecode.MacCmd;
import com.ntzzDecode.ModelType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CallBack {

    //打开串口的参数
    int baudrate = 115200;
    int sets = 1;
    int device = 0;

    //手持机
    private ToggleButton scanTb;
    private ToggleButton hdConnectBtn;
    private ListView hdRecvLv;
    private ConnectTask hdConnectTask;
    private HandReadTask hdReadTask;

    HashMap<String, String> map;
    SimpleAdapter adapter;
    static ArrayList<HashMap<String, String>> tagList;

    //读卡器
    protected SerialPort mSerialPort;
    private MessageRevicer remote ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //绑定控件
        hdRecvLv= (ListView) findViewById(R.id.hdRecvLv);
        scanTb = (ToggleButton) findViewById(R.id.scanTb);
        hdConnectBtn = (ToggleButton) findViewById(R.id.hdConnectBtn);
        //初始化数据
        initData();
        //打开串口
        openSerialPort();
        initEven();
    }

    /**
     * 按钮点击事件
     * @param v
     */
    public void onClick(View v) {

        /***
         * 点击事件
         */
        switch (v.getId()){
            //发送
            case R.id.tagSend:
                tagSend();
                break;
            //设置
            case R.id.hdSettingBtn:
                hdSetting();
                break;
            //清空
            case R.id.hdClearBtn:
                clear();
                break;
            default:
                break;
        }
    }

    /**
     * 打开串口
     */
    private void openSerialPort(){
        //给模块上电
        mSerialPort.SetPowerState(1);
        if (SerialPort.openSerial(Const.DEVICE, Const.BAUDRATE,  Const.EVENMODE + 1) == 0) {
            Toast.makeText(this, "串口打卡开失败", Toast.LENGTH_SHORT).show();
            mSerialPort.SetPowerState(0);
        }

        // 初始化串口信息
        HelpUtil.sp = mSerialPort;
        HelpUtil.mc.initReturnBySerialPort(HelpUtil.sp, 0);

    }

    /**
     * 初始化数据
     */
    private void initData(){
        mSerialPort = new SerialPort();
        remote = new MessageRevicer();

        tagList = new ArrayList<HashMap<String, String>>();
        adapter = new SimpleAdapter(MainActivity.this, tagList, R.layout.item_list_tag,
                new String[] { "tagUii", "tagCount" },
                new int[] { R.id.tagUii, R.id.tagCount });
        hdRecvLv.setAdapter(adapter);
    }

    /**
     * 初始化监听事件
     */
    private void initEven(){

        //扫描
        scanTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //开始盘存
                    sendMessage(HelpUtil.sp, ModelType.STARTINV, 0, null);
                    ScanTask scanTask = new ScanTask();
                } else {
                    //停止盘存
                    sendMessage(HelpUtil.sp, ModelType.STOPINV, 0, null);
                }
            }
        });


        //连接服务端
        hdConnectBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 连接
                    String IP = App.getCacheString(Const.SERV_IP_KEY, Const.SERV_IP_DEFAULT);
                    String port = App.getCacheString(Const.SERV_PORT_KEY, Const.SERV_PORT_DEFAULT);

                    hdConnectTask = new ConnectTask(IP, Integer.parseInt(port));
                    hdConnectTask.execute();
                    // 启动读取线程
                    hdReadTask = new HandReadTask(hdConnectTask);
                    hdReadTask.execute();
                    hdReadTask.CIRCLE = true;
                } else {
                    // 断开
                    if (hdConnectTask != null && hdConnectTask.getmSocket() != null) {
                        try {
                            hdReadTask.cancel(true);
                            hdConnectTask.getmSocket().close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //列表项点击事件
        hdRecvLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView=(TextView)view.findViewById(R.id.tagUii);
                Const.CURRENT_TAG=textView.getText().toString();
                Log.i(Const.TAG,Const.CURRENT_TAG);
            }
        });
    }

    /**
     * 发送标签号到服务端
     */
    private void tagSend(){
        if (hdConnectTask != null && hdConnectTask.getStatu()) {
            StreamUtil.writeCommand(hdConnectTask.getOutputStream(), Const.CURRENT_TAG);
        } else {
            Toast.makeText(App.getContext(), "请先连接成功后再操作！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 手持机设置
     */
    private void hdSetting() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("服务端设置");
        dialog.setContentView(R.layout.dialog_setting);
        dialog.show();

        Button confirmBtn = (Button) dialog.findViewById(R.id.setting_dlg_confirm_btn);
        Button cancelBtn = (Button) dialog.findViewById(R.id.setting_dlg_cancel_btn);
        final EditText ipEt = (EditText) dialog.findViewById(R.id.ip_et);
        final EditText portEt = (EditText) dialog.findViewById(R.id.port_et);

        // 检查缓存有没有数据
        String IP = App.getCacheString(Const.SERV_IP_KEY, Const.SERV_IP_DEFAULT);
        String port = App.getCacheString(Const.SERV_PORT_KEY, Const.SERV_PORT_DEFAULT);
        ipEt.setText(IP);
        portEt.setText(port);

        // 确定
        confirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 获取输入框内容
                String IP = ipEt.getText().toString().trim();
                String port = portEt.getText().toString().trim();

                // 检查IP格式,如果正确则保存到数据库
                if (!TextUtils.isEmpty(IP) && RegExpValidatorUtils.isIP(IP) && !TextUtils.isEmpty(port)
                        && Integer.parseInt(port) > 0 && Integer.parseInt(port) < 65535) {
                    // 保存数据
                    App.getAcache().put(Const.SERV_IP_KEY, IP);
                    App.getAcache().put(Const.SERV_PORT_KEY, port);
                    dialog.dismiss();
                } else {
                    ipEt.setText("");
                    portEt.setText("");
                    Toast.makeText(App.getContext(), "输入信息不正确，请重新输入！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 取消
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 发送消息
     */
    public void sendMessage(SerialPort mSerialPort, int ModelType, int flag,
                            Map map) {
        try {
            remote.executeMessage(mSerialPort, flag, ModelType, map, MainActivity.this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 在UI线程中加入到列表
     */
    private void addToListView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = -1;
                HashMap<String, String> tmpmap = map;

                String uii = tmpmap.get("tagUii");
                // System.out.println(uii);
                index = checkIsExist(uii, tagList);

                if (index == -1) {
                    tagList.add(tmpmap);
                    Log.i(Const.TAG,"tagListSize="+tagList.size());
                } else {
                    int tagcount = Integer.parseInt(
                            tagList.get(index).get("tagCount"), 10) + 1;
                    tmpmap.put("tagCount", String.valueOf(tagcount));
                    tagList.set(index, tmpmap);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 清空
     */
    private void clear(){
        tagList.clear();
        adapter.notifyDataSetChanged();
    }

    /**
     * 判断标签是否已存在，如果不存在，返回-1，如果存在，返回下标
     * @param uiiStr
     * @param tagList
     * @return
     */
    public int checkIsExist(String uiiStr,
                            ArrayList<HashMap<String, String>> tagList) {
        int existFlag = -1;
        try {
            String tempStr = "";
            for (int i = 0; i < tagList.size(); i++) {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp = tagList.get(i);
                tempStr = temp.get("tagUii");
                if (uiiStr != "" && uiiStr.equals(tempStr)) {
                    existFlag = i;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return existFlag;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            sendMessage(HelpUtil.sp, ModelType.STOPINV, 0, null);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 断开socket
        if (hdConnectTask != null && hdConnectTask.getmSocket() != null) {
            try {
                hdReadTask.cancel(true);
                hdConnectTask.getmSocket().close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //停止盘存
        sendMessage(HelpUtil.sp, ModelType.STOPINV, 0, null);

        //关闭串口
        SerialPort.closeSerial();
    }


    /**
     * 发送消息回调函数
     * @param strings
     */
    @Override
    public void execute(String[] strings) {
        // System.out.println("打印返回的数据:----" + objects[0]);
        Log.d(Const.TAG,"objects[0]="+strings[0]);
        int cmd = Integer.parseInt((String) strings[1], 10);
        switch (cmd) {
            case MacCmd.RCP_CMD_READ_C_UII:
                //获得标签号
                String tag = HelpUtil.getTagByCons((String) strings[0]);
                map = new HashMap<String, String>();
                map.put("tagUii", tag);
                map.put("tagCount", String.valueOf(1));
//                Log.i(Const.TAG,"tag="+tag);
                //加入到列表
                addToListView();

                break;
        }

    }

    @Override
    public void execute2(String[] strings) {

    }

    @Override
    public void execute3(String[] strings) {

    }
}
