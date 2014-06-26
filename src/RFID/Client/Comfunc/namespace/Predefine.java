package RFID.Client.Comfunc.namespace;

public class Predefine {
	
        public static final int PortServerLog = 54250;
        public static final int PortServerData = 54251;
        public static final int PortAdminLog = 54252;
        public static final int PortAdminData = 54253;
        public static final int PortMonitorLog = 54254;
        public static final int PortMonitorData = 54255;  

          // 从自己的251端口 发到人家的250端口 免得将来服务器又发又收的堵上 ：）
        public static final int IPAddressLength = 16;
        public static final int MaxRFIDList = 4800;  // 每个包 最大能记录200条RFID记录 也够用了
        public static final int UserNameLength = 20;
        public static final int PasswordLength = 20;
        public static final int TimeStringLength = 20;
        public static final int PositionLength = 20;
        
        public static final int MaxRecLogSize = 104; //登录时接收线程能收的最大包
        public static final int WaitTimeout = 3000; //登录时接收线程等待时间

        public static final int MsgRefreshlist = 0;
        public static final String PRIVATE_FILE = "RFIDClient.pri";
}
