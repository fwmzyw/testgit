package RFID.Client.Data.namespace;

public class DetailInfoRFIDStruct {
	  
	  public byte[] SourceIP;
	  public byte[] DestIP;
	  public byte[] CaptrueTime;
	  public byte[] RFIDNumber;
	  public byte[] PersonName;
	  public byte[] Organization;
	  public byte[] ItemName;
	  public byte[] isItemAllowed;
	  public byte[] Position;
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
	public byte[] getCaptrueTime() {
		return CaptrueTime;
	}
	public void setCaptrueTime(byte[] captrueTime) {
		CaptrueTime = captrueTime;
	}
	public byte[] getRFIDNumber() {
		return RFIDNumber;
	}
	public void setRFIDNumber(byte[] rFIDNumber) {
		RFIDNumber = rFIDNumber;
	}
	public byte[] getPersonName() {
		return PersonName;
	}
	public void setPersonName(byte[] personName) {
		PersonName = personName;
	}
	public byte[] getOrganization() {
		return Organization;
	}
	public void setOrganization(byte[] organization) {
		Organization = organization;
	}
	public byte[] getItemName() {
		return ItemName;
	}
	public void setItemName(byte[] itemName) {
		ItemName = itemName;
	}
	public byte[] getIsItemAllowed() {
		return isItemAllowed;
	}
	public void setIsItemAllowed(byte[] isItemAllowed) {
		this.isItemAllowed = isItemAllowed;
	}
	public byte[] getPosition() {
		return Position;
	}
	public void setPosition(byte[] position) {
		Position = position;
	}	

}
