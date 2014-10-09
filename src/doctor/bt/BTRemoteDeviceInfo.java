package doctor.bt;

public class BTRemoteDeviceInfo {
	
	private String mName = null;
	private String mMacAddr = null;
	private Boolean mConnStatus = false;
	
	public BTRemoteDeviceInfo(String name, String macAddr) {
		this.mName = name;
		this.mMacAddr = macAddr;
	}

	public Boolean getmConnStatus() {
		return mConnStatus;
	}

	public void setmConnStatus(Boolean mConnStatus) {
		this.mConnStatus = mConnStatus;
	}
	
	public String getMacAddr() {
		return mMacAddr;
	}
	public void setMacAddr(String macAddr) {
		this.mMacAddr = macAddr;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}

}
