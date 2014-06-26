package RFID.Client.Data.namespace;

public class RegPacketStruct {
        public Boolean getIsLegalPacket() {
		return isLegalPacket;
	}
	public void setIsLegalPacket(Boolean isLegalPacket) {
		this.isLegalPacket = isLegalPacket;
	}
	public int getLogMode() {
		return LogMode;
	}
	public void setLogMode(int logMode) {
		LogMode = logMode;
	}
	public int getOperValue() {
		return OperValue;
	}
	public void setOperValue(int operValue) {
		OperValue = operValue;
	}
	public byte[] getPosition() {
		return Position;
	}
	public void setPosition(byte[] position) {
		Position = position;
	}
	public byte[] getSourceIP() {
		return SourceIP;
	}
	public void setSourceIP(byte[] sourceIP) {
		SourceIP = sourceIP;
	}
	public byte[] getDestIP() {
		return DestIP;
	}
	public void setDestIP(byte[] destIP) {
		DestIP = destIP;
	}
	public byte[] getUserName() {
		return UserName;
	}
	public void setUserName(byte[] userName) {
		UserName = userName;
	}
	public byte[] getPassword() {
		return Password;
	}
	public void setPassword(byte[] password) {
		Password = password;
	}
		public Boolean isLegalPacket;
        public int LogMode; // 客户端登录类型 0:Client 有控制功能 1: Mon 只能监控 2:新的登录模式 登录端与读写器IP在服务器端绑定
        public int OperValue;   // 定义操作类型 1:登录 2:注销   返回代码 6:IP地址非法 7:密码错误 8:用户名不存在 9:确认登录 
        public byte[] Position;
        public byte[] SourceIP; 
        public byte[] DestIP;
        public byte[] UserName;
        public byte[] Password;
}
