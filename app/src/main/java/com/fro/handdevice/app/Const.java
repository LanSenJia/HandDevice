package com.fro.handdevice.app;

public class Const {

	public static final String TAG="HandDevice";

	public static String CURRENT_TAG = "0000000000000";//当前选中的标签

	/**
	 * 服务端
	 */
	public static final String SERV_IP_KEY = "serv_ip";
	public static final String SERV_PORT_KEY = "serv_port";
	public static final String SERV_IP_DEFAULT = "192.168.0.141";
	public static final String SERV_PORT_DEFAULT = "4001";
	public static final String SERV_RECV_SUCCESS = "AA AA";

	/**
	 * 读卡器
	 */
//	public static final Integer DEVICE = 2;//1：条码 2：rfid 3：底部
	public static final Integer DEVICE = 0;//1：条码 2：rfid 3：底部
	public static final Integer BAUDRATE = 115200;//波特率
	public static final int EVENMODE = 1;//事件类型

}
