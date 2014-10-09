package doctor.bt;

import android.util.Log;

public class ThreadManager extends Thread {
	
	private BTRemoteDeviceInfo mRemoteDeviceInfo = null;
	private static final String TAG = ThreadManager.class.getName();
	private BTDoctor mBtDoctor = null;
	
	public ThreadManager (BTRemoteDeviceInfo remoteDeviceInfo) {
		this.mRemoteDeviceInfo = remoteDeviceInfo;
	}
	
	public BTDoctor getBtDoctor() {
		return mBtDoctor;
	}

	public void setBtDoctor(BTDoctor btDoctor) {
		this.mBtDoctor = btDoctor;
	}

	public void run() {
		
		int threadCounter = 0;
		
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error while sleeping!");
				e.printStackTrace();
			}
			
			if (mRemoteDeviceInfo.getmConnStatus() == false) {
				threadCounter++;
				Log.w(TAG, "connection is false, start new client[" + threadCounter + "]:");
				ConnectThread connectThread = new ConnectThread(mRemoteDeviceInfo);
				connectThread.start();						
			}
		}
	}
}
