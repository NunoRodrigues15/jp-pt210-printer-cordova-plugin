package com.odkas.jpprinterplugin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.Pos;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;
import java.util.Iterator;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JpPrinterPlugin extends CordovaPlugin implements Runnable {
    private static final String TAG = "JpPrinterPlugin";
    private BroadcastReceiver broadcastReceiver = null;
    public static final BTPrinting bt = new BTPrinting();
    private static final ExecutorService es = Executors.newScheduledThreadPool(30);
    private Pos pos = new Pos();
    private ProgressDialog dialog;
    private IntentFilter intentFilter = null;
    private Context context;
    private Activity activity;
    private ArrayList<String> stringDevices;
    private ArrayList<BluetoothDevice> myDevices;
	private JSONArray devices;

	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		Log.d(TAG, "Initializing JpPrinterPlugin");
	}

	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		try{
			if(action.equals("echo")) {
				String phrase = args.getString(0);
				// Echo back the first argument
				Log.d(TAG, phrase);
			} else if(action.equals("initBroadcast")) {
				final PluginResult result = new PluginResult(PluginResult.Status.OK, this.initBroadcast());
				callbackContext.sendPluginResult(result);
			} else if(action.equals("connect")){
				final PluginResult result = new PluginResult(PluginResult.Status.OK, this.connect());
				callbackContext.sendPluginResult(result);
			} else if(action.equals("connectTo")){
				final PluginResult result = new PluginResult(PluginResult.Status.OK, this.connectTo(args.getString(0));
				callbackContext.sendPluginResult(result);
			}  else if(action.equals("printText")){
				final PluginResult result = new PluginResult(PluginResult.Status.OK, this.printText(args.getString(0));
				callbackContext.sendPluginResult(result);
			}  else if(action.equals("disconnect")){
				final PluginResult result = new PluginResult(PluginResult.Status.OK, this.disconnect();
				callbackContext.sendPluginResult(result);
			}
			return true;
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}

    public JpPrinterPlugin(Activity activity, Context context) {
        this.pos.Set(bt);
        this.stringDevices = new ArrayList<>();
        this.myDevices = new ArrayList<>();
		    this.devices = new JSONArray();
        this.activity = activity;
        this.context = context;
        this.dialog = new ProgressDialog(this.context);
        initBroadcast();
    }



    public ArrayList<String> getStringDevices() {
        return stringDevices;
    }

    public void setStringDevices(ArrayList<String> stringDevices) {
        this.stringDevices = stringDevices;
    }

    public ArrayList<BluetoothDevice> getMyDevices() {
        return myDevices;
    }

    public void setMyDevices(ArrayList<BluetoothDevice> myDevices) {
        this.myDevices = myDevices;
    }

    public void addDevice(BluetoothDevice device, String string) {
        this.myDevices.add(device);
        this.stringDevices.add(string);
		this.devices.put(string);
    }

    public void removeDevices() {
        this.myDevices.clear();
        this.stringDevices.clear();
		this.devices = new JSONArray(new ArrayList<String>());
    }

    public boolean printText( String text, Integer nWidthTimes, Integer nHeigthTimes) {
        if (this.pos.IO.IsOpened()) {
            boolean result = false;
            byte[] status = new byte[1];
            if (this.pos.POS_QueryStatus(status, 3000)) {
                this.pos.POS_S_TextOut(text,"UTF-8", 0, nWidthTimes, nHeigthTimes, 0, 0);
                result = this.pos.POS_QueryStatus(status, 3000);
            }
            final boolean bPrintResult = result;
            //sucess or error message
            String text2 = bPrintResult ? "Print Succeed ": "Print Failed";
			boolean answer = bPrintResult ? true : false;
			return answer;
            //Toast.makeText(this.context, text2 , Toast.LENGTH_SHORT).show();
        } else {
            // not connected
            //Toast.makeText(this.context, "Print Not Connected", Toast.LENGTH_SHORT).show();
			return false;
        }
    }

    public boolean disconnect() {
        try {
            bt.Close();
			return true;
        } catch (Exception ex) {
            Log.i(TAG, ex.toString());
			return false;
        }
    }

    private com.odkas.jpprinterplugin.JpPrinterPlugin getContext() {
        return this;
    }

    private JSONArray initBroadcast() {
        Context pluginCon = this.context;
        this.broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if ("android.bluetooth.device.action.FOUND".equals(action)) {
                    if (device != null) {
                        final String address = device.getAddress();
                        String name = device.getName();
                        if (name == null) {
                            name = "BT";
                        } else if (name.equals(address)) {
                            name = "BT";
                        }
                        String text = String.valueOf(name) + ": " + address;
                        getContext().addDevice(device, text);
                    }
                }
            };
        };
        this.intentFilter = new IntentFilter();
        this.intentFilter.addAction("android.bluetooth.device.action.FOUND");
        this.intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
        this.intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        context.registerReceiver(this.broadcastReceiver, this.intentFilter);
        return getContext().sendDevices();
    }

    public JSONArray sendDevices() {
        return this.devices;
    }

	public Integer findDevices(String name) {
        Integer length = this.devices.length();
        for(int i=0; i< length; i++){
            JSONObject objects = null;
            try {
                objects = this.devices.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Iterator key = objects.keys();
            while(key.hasNext()) {
                String next = key.next().toString();
                if(next.equals(name))
                    return i;
            }
        }
        return -1;
    }

    public boolean connectTo(String name) {
        int i= findDevices(name);
        if(i >= 0){
            String text = this.getStringDevices().get(i);
            if(text.equals(name)){
                BluetoothDevice device = this.getMyDevices().get(i);
                final String address = device.getAddress();

                this.dialog.setMessage("Connecting " + address);
                this.dialog.setIndeterminate(true);
                this.dialog.setCancelable(false);
                this.dialog.show();
                this.pos.Set(bt);
                ExecutorService access$4 = es;
                final String str = address;
                access$4.submit(new Runnable() {
                    public void run() {
                        final boolean result = bt.Open(str);
                        getContext().dialog.cancel();
                        if (result) {
                            Toast.makeText(getContext().context, "Connect Printer Succeed", Toast.LENGTH_SHORT).show();
                            //remove all devices
							getContext().removeDevices();
                            //return true;
                        }
                        Toast.makeText(getContext().context, "Connect Printer Failed", Toast.LENGTH_SHORT).show();
						//return false;
                    };
                });
			return true;
            }
        }
		return false;
    }

    private void uninitBroadcast() {
        if (this.broadcastReceiver != null) {
            context.unregisterReceiver(this.broadcastReceiver);
        }
    }

    public boolean connect() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            this.activity.finish();
            return false;
        }
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this.activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        if (adapter.isEnabled()) {
            adapter.cancelDiscovery();
            //remove all Devices
			this.removeDevices();
            adapter.startDiscovery();
        } else if (adapter.enable()) {
            do {
            } while (!adapter.isEnabled());
        } else {
            this.activity.finish();
            return false;
        }
        adapter.cancelDiscovery();
        //remove all Devices
		this.removeDevices();
        adapter.startDiscovery();
		return true;
    }


    public void run() {

    }
}
