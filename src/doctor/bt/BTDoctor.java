package doctor.bt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

public class BTDoctor extends Activity {
	
	private static final String TAG = BTDoctor.class.getName();
	private static final int REQUEST_ENABLE_BT = 0;
	private static final int BT_DISCOVERABLE_DURATION = 300;
	private static final int REQUEST_DISCOVERABLE_BT = BT_DISCOVERABLE_DURATION;
	private boolean isDeviceEnabled = false;
	private Set<BluetoothDevice> pairedDeviceSet;
	private Vector<BTRemoteDeviceInfo> btRemDevVector = new Vector<BTRemoteDeviceInfo>();
	
 
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.w(TAG, "********* Device Found *******");
				btRemDevVector.add(new BTRemoteDeviceInfo(device.getName(), device.getAddress()));
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Log.w(TAG, "********* Device discovery started *******");
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Log.w(TAG, "********* Device discovery finished *******");
				displayDiscoveredRemDev(btRemDevVector);
				//startClient("00:1C:97:33:00:8C", 1);
			
			}
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("\n\nTansiyon aletini kolunuza takıp ölçüm yapabilirsiniz.\n\n" +
        			"Ölçüme başlamak için tansiyon aletinde bulunan Start tuşuna basınız.");
        setContentView(tv);
        
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null)
        	Log.w(TAG, "btAdapter is null!");
        else
        	Log.w(TAG, "btAdapter is not null!");
        
    	if (btAdapter.isEnabled() == false) {
        	Log.w(TAG, "btAdapter is disabled! Try to enabling..");
        	enableBTAdapter(btAdapter);
        }
        else {
        	Log.w(TAG, "btAdapter is already enabled!");
        }
		
    	//startDeviceDiscover(btAdapter);
    	
//		if (btAdapter.isDiscovering() == true)
//			btAdapter.cancelDiscovery();
//		
		BTRemoteDeviceInfo remoteDeviceInfo = new BTRemoteDeviceInfo("alet", "00:1C:97:33:00:8C");		
		ThreadManager threadManager = new ThreadManager(remoteDeviceInfo);
		threadManager.start();
    	
    }
    
    public void startDeviceDiscover(BluetoothAdapter btAdapter) {
    	
    	IntentFilter filterActionRemoteDeviceFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterActionDiscoveryStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filterActionDiscoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filterActionRemoteDeviceFound);
        registerReceiver(mReceiver, filterActionDiscoveryStarted);
        registerReceiver(mReceiver, filterActionDiscoveryFinished);
        
        btAdapter.startDiscovery();
	}
    
    public void displayResultAlert() {

    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Measurement");
		//alertBuilder.setMessage("\nSys: " + strSys + "Dia: " + strDia + "\nHeartRate: " + strHeartRate);
		alertDialog.setMessage("hello Mahmut");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.w(TAG, "button clicked!");
			}
		});
		alertDialog.show();
    }

	private void displayBoundeds(BluetoothAdapter btAdapter) {
    	pairedDeviceSet = btAdapter.getBondedDevices();
    	if (pairedDeviceSet.isEmpty() == false) {
    		for (BluetoothDevice device : pairedDeviceSet) {
    			Log.w(TAG, device.getName() + " | " + device.getAddress());
    		}
    	}
    	else {
    		Log.w(TAG, "No bounded device yet!");
    	}
	}
	
	private void displayDiscoveredRemDev(Vector<BTRemoteDeviceInfo> btRemVector) {
		if (btRemVector.isEmpty()) {
			Log.w(TAG, "No device discovered!");
		}
		else {
			Enumeration<BTRemoteDeviceInfo> e = btRemVector.elements();
			
			while(e.hasMoreElements()) {
				BTRemoteDeviceInfo remDev = e.nextElement(); 
				Log.w(TAG, "Device[i]: " + remDev.getName() + " | " + remDev.getMacAddr());
			}
		}
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.w(TAG, "onActivityResult: requestCode: " + requestCode + ", resultCode: " + resultCode + ".");
    	if (requestCode == REQUEST_ENABLE_BT) {
    		if (resultCode == RESULT_OK) {
    			Log.w(TAG, "Bluetooth enabled successfully!");
    	        setDeviceEnabled(true);
    	        Log.w(TAG, "BT device enabled!");
    		}
    		else if (resultCode == RESULT_CANCELED) {
    			Log.w(TAG, "Enabling Bluetooth failed!");
    		}
    	}
    	
    	else if (requestCode == REQUEST_DISCOVERABLE_BT) {
    		if (resultCode == BT_DISCOVERABLE_DURATION) {
    			Log.w(TAG, "BTAdapter is made discoverable for " + BT_DISCOVERABLE_DURATION + " seconds.");
    		}
    		else if (resultCode == RESULT_CANCELED) {
    			Log.w(TAG, "Making bluetooth discoverable failed!");
    		}
    			
    	}
    }
    
    public void enableBTAdapter(BluetoothAdapter btAdapter) {
    	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

	public void setDeviceEnabled(boolean isDeviceEnabled) {
		this.isDeviceEnabled = isDeviceEnabled;
	}

	public boolean isDeviceEnabled() {
		return isDeviceEnabled;
	}
	
	public void makeBTDiscoverable() {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, REQUEST_DISCOVERABLE_BT);
		startActivityForResult(discoverableIntent, BT_DISCOVERABLE_DURATION);		
	}
	
}
