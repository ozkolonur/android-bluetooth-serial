package doctor.bt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ConnectThread extends Thread {

	private static final String TAG = ConnectThread.class.getName();
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BTRemoteDeviceInfo mRemoteDeviceInfo = null;
	
	public ConnectThread(BTRemoteDeviceInfo remoteDeviceInfo) {
		Log.w(TAG, "I am new connection thread! " + Thread.currentThread().getId());
		this.mRemoteDeviceInfo = remoteDeviceInfo;
	}
	
	public void run() {
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice remoteDevice = btAdapter.getRemoteDevice(mRemoteDeviceInfo.getMacAddr());
		
		mRemoteDeviceInfo.setmConnStatus(true);
		//Log.w(TAG, "conn status setted true, sleeping for 10 secs");
		
		try {
			startClient(remoteDevice);
			//Thread.sleep(10000);
		} catch (IOException e) {
			Log.e(TAG, "ioexception client error!");
			e.printStackTrace();
		} 
//		catch (InterruptedException e) {
//			Log.e(TAG, "interrupted exception client error!");
//			e.printStackTrace();
//		}
		
		mRemoteDeviceInfo.setmConnStatus(false);
		Log.w(TAG, "conn status setted false!\n");
	}
	
	public void startClient(BluetoothDevice remoteDevice) throws IOException {

		BluetoothSocket btSocket;
		btSocket = remoteDevice.createRfcommSocketToServiceRecord(MY_UUID);
		Log.w(TAG, "client socket created, try to connect device " + mRemoteDeviceInfo.getMacAddr());
		btSocket.connect();
		Log.w(TAG, "client socket connected to device " + mRemoteDeviceInfo.getMacAddr());
		
		InputStream inStream = btSocket.getInputStream();
		BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
		
		Log.w(TAG, "start reading:\n*****\n");
        while(true)
        {
        	String result = "";
        	int readChar = bReader.read();
        	if (readChar < 0) {
        		Log.e(TAG, "read error!");
        		break;
        	}

        	result += (":" + readChar);
        	if (readChar == 1)
        	{
        		readChar = bReader.read();
        		result += (":" + readChar);
        		if (readChar == 2)
        		{
                            readChar = bReader.read();
                            result += (":" + readChar);
                            if (readChar == 65533)
                            {
                                String strSys, strDia, strHeartRate;
                                
                                int aInt = bReader.read();
                                Log.w(TAG, "read: " + aInt);
                                strSys = "" + aInt;
                                
                                aInt = bReader.read();
                                Log.w(TAG, "read: " + aInt);
                                strDia = "" + aInt;
                                
                                aInt = bReader.read();
                                Log.w(TAG, "read: " + aInt);
                                strHeartRate = "" + aInt;
                                
                                if (1 == bReader.read() && 3 == bReader.read())
                                {
                                    Log.w(TAG, "\nFOUND: " + strSys + " : " + strDia + " : " + strHeartRate);
                                    try {
                        				HttpPostThread postThread = new HttpPostThread(strSys, strDia, strHeartRate);
                        				postThread.start();
                        				Thread.sleep(30000);
                        			} catch (InterruptedException e) {
                        				Log.e(TAG, "error while sleeping!");
                        				e.printStackTrace();
                        			}
                        			
                                    btSocket.close();
                                    break;
                                }
                            }
        		}
        	}
        	Log.w(TAG, result);
        }
	}
}
