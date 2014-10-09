package doctor.bt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import android.util.Log;


public class HttpPostThread extends Thread {
	
	private static final String TAG = HttpPostThread.class.getName();
	private String mSys;
	private String mDia;
	private String mHearthBeat;
	
	public HttpPostThread (String sys, String dia, String heartBeat) {
		this.mSys = sys;
		this.mDia = dia;
		this.mHearthBeat = heartBeat;
	}
	
	public void run() {
		doHttpGet(mSys, mDia, mHearthBeat);
		//doHttpPost(mSys, mDia, mHearthBeat);
	}
	
	public void doHttpGet(String sys, String dia, String heartBeat) {
		
		String fullUri = "http://www.doktorfil.com/service.php?email=ozkolonur@gmail.com&password=123456&systolic=" + 
							sys + "&diastolic=" + dia + "&heartbeat=" + heartBeat + "&bpm_mac=00:1C:97:33:0B:49";
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet getReq = new HttpGet();
		
		try {
			getReq.setURI(new URI(fullUri));
			HttpResponse response = httpClient.execute(getReq);
			Log.w(TAG, response.toString());
			InputStream inStream = response.getEntity().getContent();
			BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
			Log.w(TAG, "response: " + bReader.readLine());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void doHttpPost(int sys, int dia, int heartBeat) {
	    String uriStr = "http://www.doktorfil.com/service.php";
		String macStr = "00:1C:97:33:00:8C";
		String sysStr = Integer.toString(sys);
		String diaStr = Integer.toString(dia);
		String heartStr = Integer.toString(heartBeat);
		
		Log.w(TAG, "Start to posting");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(uriStr);
		HttpResponse httpResponse;
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("email", "ozkolonur@gmail.com"));
		pairs.add(new BasicNameValuePair("password", "123456"));
		pairs.add(new BasicNameValuePair("systolic", sysStr));
		pairs.add(new BasicNameValuePair("diastolic", diaStr));
		pairs.add(new BasicNameValuePair("heartbeat", heartStr));
		pairs.add(new BasicNameValuePair("bpm_mac", macStr));
		
		Log.w(TAG, pairs.toString());
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
			httpResponse = httpClient.execute(httpPost);
			Log.w(TAG, "Http Response: " + httpResponse.toString());
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "unsupported encoding exception!");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e(TAG, "client protocol exception!");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "io exception!");
			e.printStackTrace();
		}
	}

}
