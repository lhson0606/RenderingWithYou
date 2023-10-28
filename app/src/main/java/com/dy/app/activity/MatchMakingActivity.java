package com.dy.app.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dy.app.R;
import com.dy.app.gameplay.Player;
import com.dy.app.manager.ConnectionManager;
import com.dy.app.manager.SoundManager;
import com.dy.app.network.WiFiDirectBroadcastReceiver;
import com.dy.app.utils.ImageLoader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.List;

public class MatchMakingActivity extends AppCompatActivity
        implements View.OnClickListener,
        WifiP2pManager.PeerListListener,
        WifiP2pManager.ConnectionInfoListener,
        WifiP2pManager.ActionListener,
        AdapterView.OnItemClickListener {
    public static final String TAG = "MatchMakingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.match_making_activity);
        init();
        exqListener();
        clearLog();
    }

    private void exqListener() {
        btnExit.setOnClickListener(this);
        btnReload.setOnClickListener(this);
        btnJoin.setOnClickListener(this);
        btnWifi.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        lvPeers.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
        disconnectAllWifiDirect();
        startDiscovery();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void init() {
        //request for permission
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.NEARBY_WIFI_DEVICES,
        }, 0);

        lvPeers = findViewById(R.id.lvPeers);
        btnExit = findViewById(R.id.btnExit);
        btnReload = findViewById(R.id.btnReload);
        btnJoin = findViewById(R.id.btnJoin);
        btnWifi = findViewById(R.id.btnWifi);
        btnClear = findViewById(R.id.btnClear);
        tvLog = findViewById(R.id.tvLog);
        screenView = findViewById(R.id.screenView);
        soundManager = SoundManager.getInstance().initInContext(this);

        peers = new java.util.ArrayList<>();

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);
        broadcastReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        screenView.setBackground(ImageLoader.loadImage(getResources().openRawResource(R.raw.chess_wallpaper)));
    }

    @Override
    public void onClick(View v) {
        soundManager.playSound(getBaseContext(), SoundManager.SoundType.BTN_BLOP);
        if (v == btnExit) {
            disconnectAllWifiDirect();
            finish();
        } else if (v == btnReload) {
            startDiscovery();
        } else if (v == btnJoin) {
            startConnection();
        } else if (v == btnWifi) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                askForConnection();
            } else if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
                logUser("Wifi disabled");
            } else {
                wifiManager.setWifiEnabled(true);
                logUser("Wifi enabled");
            }

        } else if (v == btnClear) {
            clearLog();
        }
    }

    private void clearLog(){
        tvLog.setText("Log cleared ...\n");
    }

    private void startDiscovery() {
        disconnectAllWifiDirect();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
            logSystem("Permission(s) is not granted");
        }
        wifiP2pManager.discoverPeers(channel, this);
    }

    private void disconnectAllWifiDirect() {
        if(wifiP2pManager == null){
            return;
        }

        try {
            wifiP2pManager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    logSystem("Disconnected from all Wifi Direct");
                }

                @Override
                public void onFailure(int reason) {
                    logSystem("Failed to disconnect from all Wifi Direct");
                }
            });
        } catch (Exception e) {
            logSystem("Failed to disconnect from all Wifi Direct");
        }

    }

    private void askForConnection() {
        Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
        startActivityForResult(panelIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (this.wifiManager != null) {
                if (this.wifiManager.isWifiEnabled()) {
                    logUser("Wifi is connected");
                } else {
                    logUser("Wifi is not connected");
                }
            } else {
                logUser("Wi-Fi is not available or there is an issue with the system");
            }
        }
    }

    public void logUser(String msg) {
        tvLog.append("[User]: " + msg + "\n");
    }

    public void logSystem(String msg) {
        tvLog.append("[System]:" + msg + "\n");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Permission " + permissions[i] + " granted");
                    logSystem("Permission " + permissions[i] + " granted");
                } else {
                    Log.d("MainActivity", "Permission " + permissions[i] + " denied");
                    logSystem("Permission " + permissions[i] + " denied");
                }

                if (grantResults[i] == PackageManager.PERMISSION_DENIED && permissions[i].equals(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new android.app.AlertDialog.Builder(this)
                            .setTitle("Permission denied")
                            .setMessage("Without this permission the app is unable to discover peers")
                            .setPositiveButton("OK", null)
                            .show();
                    //finish();
                }
            }
        }

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
            //logSystem("I am the group owner");
            Player.getInstance().setHost(true);
            waitForClientConnection();
        } else if (wifiP2pInfo.groupFormed) {
            //logSystem("I am not the group owner");
            Player.getInstance().setHost(false);
            connectToGroupOwner(wifiP2pInfo.groupOwnerAddress.getHostAddress());
        }
    }

    private void connectToGroupOwner(String hostAddress) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket();

                    socket.connect(new InetSocketAddress(hostAddress, 8888), 1000);

                    ConnectionManager.startNewInstance(socket.getInputStream(), socket.getOutputStream(), new ConnectionManager.ConnectionManagerCallback() {
                        @Override
                        public void onConnectionManagerReady() {
                            //start game lobby activity after connection manager is ready
                            Intent intent = new Intent(MatchMakingActivity.this, GameLobbyActivity.class);
                            startActivity(intent);
                        }
                    });


                } catch (IllegalBlockingModeException | IllegalArgumentException | IOException e) {
                    //throw new RuntimeException("Connection timeout");
                    //Log.e("MainActivity", e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(MatchMakingActivity.this);
                    builder.setTitle("Cannot connect to host, click OK to try again.");
                    builder.setCancelable(false);  // Prevent the dialog from being canceled by clicking outside
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        // Handle the OK button click
                        dialog.dismiss();
                        connectToGroupOwner(hostAddress);
                        // You can finish the activity or take other actions here
                        //finish();
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        // Handle the OK button click
                        dialog.dismiss();
                        // You can finish the activity or take other actions here
                        //finish();
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });
                }
            }
        });

        t.start();
    }

    private void waitForClientConnection() {

        Thread t = new Thread(new Runnable() {
            ServerSocket ss = null;
            @Override
            public void run() {
                try {
                    ss = new ServerSocket(8888);

                    ss.setSoTimeout(10000);
                    Socket socket = ss.accept();
                    socket.setSoTimeout(1000);
                    ss.close();
                    ConnectionManager.startNewInstance(socket.getInputStream(), socket.getOutputStream(), new ConnectionManager.ConnectionManagerCallback() {
                        @Override
                        public void onConnectionManagerReady() {
                            //start game lobby activity after connection manager is ready
                            Intent intent = new Intent(MatchMakingActivity.this, GameLobbyActivity.class);
                            startActivity(intent);
                        }
                    });

                } catch (IOException e) {
                    //throw new RuntimeException("Connection timeout");
                    //Log.d("MainActivity", e.getMessage());
                    //finish();
                    if(ss!=null) {
                        try {
                            ss.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MatchMakingActivity.this);
                    builder.setTitle("Cannot connect to host, click OK to try again.");
                    builder.setCancelable(false);  // Prevent the dialog from being canceled by clicking outside
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        // Handle the OK button click
                        dialog.dismiss();
                        waitForClientConnection();
                        // You can finish the activity or take other actions here
                        //finish();
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        // Handle the OK button click
                        dialog.dismiss();
                        // You can finish the activity or take other actions here
                        //finish();
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });
                }
            }
        });

        t.start();

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (!peerList.getDeviceList().equals(peers)) {
            peers.clear();
            peers.addAll(peerList.getDeviceList());

            deviceNames = new String[peerList.getDeviceList().size()];
            devices = new WifiP2pDevice[peerList.getDeviceList().size()];

            int index = 0;

            for (WifiP2pDevice device : peerList.getDeviceList()) {
                deviceNames[index] = device.deviceName;
                devices[index] = device;
                index++;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNames);
            lvPeers.setAdapter(adapter);
        }

        if (peers.size() == 0) {
            logSystem("No device found");
        }
    }

    @Override
    public void onSuccess() {
        logSystem("Discovery started");
    }

    @Override
    public void onFailure(int reason) {
        if (reason == WifiP2pManager.P2P_UNSUPPORTED) {
            logSystem("P2P is not supported on this device");
        } else if (reason == WifiP2pManager.ERROR) {
            logSystem("Discovery failed due to an internal error");
        } else if (reason == WifiP2pManager.BUSY) {
            logSystem("Discovery failed because the framework is busy and unable to service the request");
        } else if (reason == WifiP2pManager.NO_SERVICE_REQUESTS) {
            logSystem("Discovery failed because no service requests are added");
        } else {
            logSystem("Discovery failed");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentDevice = devices[position];
        //change color of selected item
        changeColorOfSelectedItem(view);
    }

    private void changeColorOfSelectedItem(View view) {
        if (currentSelectedItem == view || view == null) {
            return;
        }
        //restore color
        if(currentSelectedItem != null){
            currentSelectedItem.setBackgroundColor(Color.parseColor("#00000000"));
        }
        //set new color
        currentSelectedItem = view;
        currentSelectedItem.setBackgroundColor(Color.parseColor("#FF4081"));
    }

    private void startConnection() {
        //check
        if (currentDevice == null || wifiP2pManager == null) {
            return;
        }
        //connect
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = currentDevice.deviceAddress;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
            logSystem("connection started");
        }

        try{
            wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    logSystem("Connected to " + currentDevice.deviceName);
                }

                @Override
                public void onFailure(int reason) {
                    logSystem("Not connected to " + currentDevice.deviceName);
                }
            });
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    private TextView tvLog;
    private Button btnExit, btnReload, btnJoin, btnWifi, btnClear;
    private SoundManager soundManager;
    private View screenView;
    private IntentFilter intentFilter;
    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WiFiDirectBroadcastReceiver broadcastReceiver;
    private List<WifiP2pDevice> peers;
    private String[] deviceNames;
    private WifiP2pDevice[] devices;
    private ListView lvPeers;
    private WifiP2pDevice currentDevice;
    private View currentSelectedItem;
}
