package com.github.qcute.wefake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    View view;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        // setup info card
        setupInfoCardPreferences();
        // setup reset button action
        view.findViewById(R.id.obtain).setOnClickListener(v -> setupInfoCardCurrent());
        // setup setup button action
        view.findViewById(R.id.save).setOnClickListener(v -> save());
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setupInfoCardPreferences() {
        Context context = view.getContext();
        if (context == null) return;
        SharedPreferences preferences = context.getSharedPreferences(Settings.name, Context.MODE_PRIVATE);
        // ssid
        String ssid = preferences.getString("SSID", "");
        // bssid
        String bssid = preferences.getString("BSSID", "");
        // ip
        String address = preferences.getString("IP", "");
        // gateway
        String gateway = preferences.getString("gateway", "");
        // netmask
        String netmask = preferences.getString("netmask", "");
        setupInfoCard(ssid, bssid, address, gateway, netmask);
    }

    @SuppressLint("SetTextI18n")
    private void setupInfoCardCurrent() {
        WifiManager wifi = (WifiManager) view.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            setupInfoCard("", "", "", "", "");
            return;
        }
        if (!wifi.isWifiEnabled()) {
            setupInfoCard("", "", "", "", "");
            return;
        }
        WifiInfo info = wifi.getConnectionInfo();
        if (info == null) {
            setupInfoCard("", "", "", "", "");
            return;
        }
        // ssid
        String ssid = info.getSSID();
        if (ssid == null) ssid = "";
        ssid = ssid.substring(1, ssid.length() - 1);
        // bssid
        String bssid = info.getBSSID();
        if (bssid == null) bssid = "";
        bssid = bssid.toUpperCase();
        // ip
        int ip = info.getIpAddress();
        String address = (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
        // dhcp
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if (dhcp == null) {
            setupInfoCard(ssid, bssid, address, "", "");
            return;
        }
        // gateway
        String gateway = (dhcp.gateway & 0xFF) + "." + ((dhcp.gateway >> 8) & 0xFF) + "." + ((dhcp.gateway >> 16) & 0xFF) + "." + ((dhcp.gateway >> 24) & 0xFF);
        // netmask
        String netmask = (dhcp.netmask & 0xFF) + "." + ((dhcp.netmask >> 8) & 0xFF) + "." + ((dhcp.netmask >> 16) & 0xFF) + "." + ((dhcp.netmask >> 24) & 0xFF);
        setupInfoCard(ssid, bssid, address, gateway, netmask);
    }

    @SuppressLint("SetTextI18n")
    private void setupInfoCard(String ssid, String bssid, String ip, String gateway, String netmask) {
        // ssid
        ((TextView) view.findViewById(R.id.ssid)).setText(ssid);
        // bssid
        ((TextView) view.findViewById(R.id.bssid)).setText(bssid.toUpperCase());
        // ip
        ((TextView) view.findViewById(R.id.ip)).setText(ip);
        // gateway
        ((TextView) view.findViewById(R.id.gateway)).setText(gateway);
        // netmask
        ((TextView) view.findViewById(R.id.netmask)).setText(netmask);
    }

    private void save() {
        // save
        Context context = view.getContext();
        if (context == null) return;
        SharedPreferences preferences = context.getSharedPreferences(Settings.name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // ssid
        String ssid = ((TextView) view.findViewById(R.id.ssid)).getText().toString();
        editor.putString("SSID", ssid);
        // bssid
        String bssid = ((TextView) view.findViewById(R.id.bssid)).getText().toString();
        editor.putString("BSSID", bssid);
        // ip
        String ip = ((TextView) view.findViewById(R.id.ip)).getText().toString();
        editor.putString("IP", ip);
        // gateway
        String gateway = ((TextView) view.findViewById(R.id.gateway)).getText().toString();
        editor.putString("gateway", gateway);
        // netmask
        String netmask = ((TextView) view.findViewById(R.id.netmask)).getText().toString();
        editor.putString("netmask", netmask);
        // apply
        editor.apply();
        // toast
        Toast.makeText(view.getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
    }
}