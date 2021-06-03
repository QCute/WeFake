package com.github.qcute.wefake;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    View view;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        // setup status card
        setupStatusCard();
        // setup info card
        setupInfoCard();
        // wifi state change listener
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        view.getContext().registerReceiver(new WifiStateChangeReceiver(this), intentFilter);
        return view;
    }

    static class WifiStateChangeReceiver extends BroadcastReceiver {

        final private HomeFragment fragment;

        public WifiStateChangeReceiver(HomeFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION) && !action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) return;
            fragment.setupInfoCard();
        }
    }

    private void setupStatusCard() {
        if (Settings.isEnabled()) {
            int color = view.getResources().getColor(R.color.active, view.getContext().getTheme());
            ((CardView) view.findViewById(R.id.status)).setCardBackgroundColor(color);
            ((ImageView) view.findViewById(R.id.status_icon)).setImageResource(R.drawable.ic_check_circle_32);
            ((TextView) view.findViewById(R.id.status_text)).setText(R.string.active);
        } else {
            int color = view.getResources().getColor(R.color.inactive, view.getContext().getTheme());
            ((CardView) view.findViewById(R.id.status)).setCardBackgroundColor(color);
            ((ImageView) view.findViewById(R.id.status_icon)).setImageResource(R.drawable.ic_cancel_32);
            ((TextView) view.findViewById(R.id.status_text)).setText(R.string.inactive);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setupInfoCard() {
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
}