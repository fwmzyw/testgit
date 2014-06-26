package RFID.Client.Comfunc.namespace;

public class ThreadResult {
	public static Boolean Received; //登录时是否收到返回包
	public static byte[] logdata; //登录时返回包内容
	public static int logdatalength; //登录时返回包大小
	public static Boolean bStopRec;
}
