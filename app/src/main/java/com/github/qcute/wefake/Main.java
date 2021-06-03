package com.github.qcute.wefake;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    public void initZygote(StartupParam startupParam) {
        XposedBridge.log(this.getClass().getCanonicalName() + ": Xposed loaded(" + XposedBridge.getXposedVersion() + ")!");
    }

    @SuppressLint("Deprecated")
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        // package filter recommend use LSPosed (https://github.com/LSPosed/LSPosed)
        XposedBridge.log("Hook package: " + loadPackageParam.packageName);

        // hook settings enabled
        if (loadPackageParam.packageName.equals(BuildConfig.APPLICATION_ID)) try {
            XposedHelpers.findAndHookMethod(BuildConfig.APPLICATION_ID + ".Settings", loadPackageParam.classLoader, "isEnabled", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    param.setResult(true);
                }
            });
        } catch (Throwable ignored) {
        }

        /*
         * hook network interface api group
         */
        // InterfaceAddresses getInterfaceAddresses()
        hookMethod("java.net.NetworkInterface", loadPackageParam.classLoader, "getInetAddresses", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(loadPackageParam.packageName + ": getInetAddresses()");
                InetAddress v4Address = createV4InetAddress();
                InetAddress v6Address = createV6InetAddress();
                // as Enumeration
                param.setResult(new Vector<>(Arrays.asList(v6Address, v4Address)).elements());
            }
        });
        // InterfaceAddresses getInterfaceAddresses()
        hookMethod("java.net.NetworkInterface", loadPackageParam.classLoader, "getInterfaceAddresses", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(loadPackageParam.packageName + ": getInterfaceAddresses()");
                InterfaceAddress v4Address = createV4InterfaceAddress();
                InterfaceAddress v6Address = createV6InterfaceAddress();
                // as List
                param.setResult(Arrays.asList(v6Address, v4Address));
            }
        });

        /*
         * hook wifi manager api group
         */
        // boolean isWifiEnabled()
        hookMethod("android.net.wifi.WifiManager", loadPackageParam.classLoader, "isWifiEnabled", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(loadPackageParam.packageName + ": isWifiEnabled()");
                param.setResult(true);
            }
        });
        // int getWifiState()
        hookMethod("android.net.wifi.WifiManager", loadPackageParam.classLoader, "getWifiState", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(loadPackageParam.packageName + ": getWifiState()");
                param.setResult(WifiManager.WIFI_STATE_ENABLED);
            }
        });
        // ConnectionInfo(WifiInfo) getConnectionInfo()
        hookMethod("android.net.wifi.WifiManager", loadPackageParam.classLoader, "getConnectionInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(loadPackageParam.packageName + ": getConnectionInfo()");
                param.setResult(createWifiInfo(loadPackageParam));
            }
        });
        // DhcpInfo getDhcpInfo()
        hookMethod("android.net.wifi.WifiManager", loadPackageParam.classLoader, "getDhcpInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(loadPackageParam.packageName + ": getDhcpInfo()");
                param.setResult(createDhcpInfo());
            }
        });

        /*
         * hook network info api group
         */
        // boolean isAvailable()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "isAvailable", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(true);
            }
        });
        // int getType()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "getType", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(ConnectivityManager.TYPE_WIFI);
            }
        });
        // String getTypeName()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "getTypeName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult("WIFI");
            }
        });
        // int getSubtype()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "getSubtype", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(0);
            }
        });
        // String getSubtypeName()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "getSubtypeName", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(null);
            }
        });
        // boolean isConnectedOrConnecting()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "isConnectedOrConnecting", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(true);
            }
        });
        // boolean isConnected()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "isConnected", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(true);
            }
        });
        // boolean isFailover()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "isFailover", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(false);
            }
        });
        // boolean isRoaming()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "isRoaming", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(false);
            }
        });
        // NetworkInfo.State getState()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "getState", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(NetworkInfo.State.CONNECTED);
            }
        });
        // NetworkInfo.DetailedState getDetailedState()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "getDetailedState", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(NetworkInfo.DetailedState.CONNECTED);
            }
        });
        // String getExtraInfo()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "getExtraInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(null);
            }
        });
        // int describeContents()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "describeContents", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(0);
            }
        });
        // String getReason()
        hookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "getReason", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult(null);
            }
        });

        /*
         * hook connectivity manager api group
         */
        // NetworkInfo getActiveNetworkInfo()
        hookMethod("android.net.ConnectivityManager", loadPackageParam.classLoader, "getActiveNetworkInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(loadPackageParam.packageName + ": getActiveNetworkInfo()");
                setNetworkInfo(param);
            }
        });
        // NetworkInfo getActiveNetworkInfoForUid(int)		UNDOCUMENTED
        hookMethod("android.net.ConnectivityManager", loadPackageParam.classLoader, "getActiveNetworkInfoForUid", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                int uid = (Integer) param.args[0];
                XposedBridge.log(loadPackageParam.packageName + ": getActiveNetworkInfoForUid(" + uid + ")");
                setNetworkInfo(param);
            }
        });
        // NetworkInfo getProvisioningOrActiveNetworkInfo()		UNDOCUMENTED
        hookMethod("android.net.ConnectivityManager", loadPackageParam.classLoader, "getProvisioningOrActiveNetworkInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(loadPackageParam.packageName + ": getProvisioningOrActiveNetworkInfo()");
                setNetworkInfo(param);
            }
        });
        // NetworkInfo[] getAllNetworkInfo()
        hookMethod("android.net.ConnectivityManager", loadPackageParam.classLoader, "getAllNetworkInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(loadPackageParam.packageName + ": getAllNetworkInfo()");
                setAllNetworkInfo(param);
            }
        });
        // NetworkInfo getNetworkInfo(int)
        hookMethod("android.net.ConnectivityManager", loadPackageParam.classLoader, "getNetworkInfo", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                int networkType = (Integer) param.args[0];
                XposedBridge.log(loadPackageParam.packageName + ": getNetworkInfo(" + networkType + ")");
                if (networkType == ConnectivityManager.TYPE_WIFI) {
                    setNetworkInfo(param);
                }
            }
        });
        // boolean isActiveNetworkMetered()
        hookMethod("android.net.ConnectivityManager", loadPackageParam.classLoader, "isActiveNetworkMetered", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                XposedBridge.log(loadPackageParam.packageName + ": isActiveNetworkMetered()");
                param.setResult(false);
            }
        });

        /*
         * hook network capabilities api group
         */
        // boolean hasTransport()
        hookMethod("android.net.NetworkCapabilities", loadPackageParam.classLoader, "hasTransport", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int transport = (Integer) param.args[0];
                XposedBridge.log(loadPackageParam.packageName + ": hasTransport(" + transport + ")");
                if (transport == NetworkCapabilities.TRANSPORT_WIFI || transport == NetworkCapabilities.TRANSPORT_WIFI_AWARE) {
                    param.setResult(true);
                } else {
                    super.afterHookedMethod(param);
                }
            }
        });
    }

    private void hookMethod(String className, ClassLoader classLoader, String methodName, Object... parameterTypesAndCallback) {
        try {
            XposedHelpers.findAndHookMethod(className, classLoader, methodName, parameterTypesAndCallback);
        } catch (NoSuchMethodError e) {
            XposedBridge.log("Couldn't found method: " + methodName + " in " + className);
        } catch (XposedHelpers.ClassNotFoundError e) {
            XposedBridge.log("Couldn't found class: " + className);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    private InetAddress createV4InetAddress() {
        try {
            return InetAddress.getByName(Settings.getIP());
        } catch (Exception ignored) {
            return null;
        }
    }

    private InetAddress createV6InetAddress() {
        try {
            return InetAddress.getByName("fe80::0000:0000:0000:0000");
        } catch (Exception ignored) {
            return null;
        }
    }

    private InterfaceAddress createV4InterfaceAddress() {
        InterfaceAddress interfaceAddress = (InterfaceAddress) XposedHelpers.newInstance(InterfaceAddress.class);
        InetAddress address = createV4InetAddress();
        XposedHelpers.setObjectField(interfaceAddress, "address", address);
        InetAddress broadcast = null;
        try {
            broadcast = InetAddress.getByName(Settings.getGateway());
        } catch (Exception ignored) {
        }
        XposedHelpers.setObjectField(interfaceAddress, "broadcast", broadcast);
        short maskLength = (short) (Arrays.stream(Settings.getNetmask().split("\\.")).filter(v -> Integer.parseInt(v) != 0).count() * 8);
        XposedHelpers.setShortField(interfaceAddress, "maskLength", maskLength);
        return interfaceAddress;
    }

    private InterfaceAddress createV6InterfaceAddress() {
        InterfaceAddress interfaceAddress = (InterfaceAddress) XposedHelpers.newInstance(InterfaceAddress.class);
        InetAddress address = createV6InetAddress();
        XposedHelpers.setObjectField(interfaceAddress, "address", address);
        InetAddress broadcast = null;
        try {
            broadcast = InetAddress.getByName(Settings.getGateway());
        } catch (Exception ignored) {
        }
        XposedHelpers.setObjectField(interfaceAddress, "broadcast", broadcast);
        XposedHelpers.setShortField(interfaceAddress, "maskLength", (short) 64);
        return interfaceAddress;
    }

    private WifiInfo createWifiInfo(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        // wifi info
        WifiInfo info = (WifiInfo) XposedHelpers.newInstance(WifiInfo.class);
        InetAddress address = null;
        try {
            address = InetAddress.getByName(Settings.getIP());
        } catch (Exception ignored) {
        }
        // fill field
        XposedHelpers.setIntField(info, "mNetworkId", 1);
        XposedHelpers.setObjectField(info, "mSupplicantState", SupplicantState.COMPLETED);
        XposedHelpers.setObjectField(info, "mBSSID", Settings.getBSSID().toUpperCase());
        XposedHelpers.setObjectField(info, "mMacAddress", Settings.getBSSID().toUpperCase());
        XposedHelpers.setObjectField(info, "mIpAddress", address);
        XposedHelpers.setIntField(info, "mRssi", 200); // MAX_RSSI
        XposedHelpers.setIntField(info, "mFrequency", 5000); // MHz
        XposedHelpers.setIntField(info, "mLinkSpeed", 300);  // Mbps
        // ssid
        Class<?> clazz = XposedHelpers.findClass("android.net.wifi.WifiSsid", loadPackageParam.classLoader);
        Object ssid = XposedHelpers.callStaticMethod(clazz, "createFromAsciiEncoded", Settings.getSSID());
        XposedHelpers.setObjectField(info, "mWifiSsid", ssid);
        return info;
    }

    @SuppressLint("Deprecated")
    private void setNetworkInfo(XC_MethodHook.MethodHookParam param) {
        // if we're already on wifi don't interfere.
        Object result = param.getResult();
        if (result == null) return;
        NetworkInfo info = (NetworkInfo) result;
        if (info.getType() != ConnectivityManager.TYPE_WIFI) return;
        if (info.isConnected()) return;
        param.setResult(createNetworkInfo());
    }

    @SuppressLint("Deprecated")
    private void setAllNetworkInfo(XC_MethodHook.MethodHookParam param) {
        // if we're already on wifi don't interfere.
        Object result = param.getResult();
        if (result == null) return;
        NetworkInfo[] infoList = (NetworkInfo[]) result;
        ArrayList<NetworkInfo> list = new ArrayList<>();
        for (NetworkInfo info : infoList) {
            if (info.getType() != ConnectivityManager.TYPE_WIFI) continue;
            if (info.isConnected()) continue;
            list.add(createNetworkInfo());
        }
        param.setResult(list.toArray(new NetworkInfo[0]));
    }

    @SuppressLint("Deprecated")
    private NetworkInfo createNetworkInfo() {
        NetworkInfo info = (NetworkInfo) XposedHelpers.newInstance(NetworkInfo.class, 0, 0, null, null);
        XposedHelpers.setIntField(info, "mNetworkType", ConnectivityManager.TYPE_WIFI);
        XposedHelpers.setIntField(info, "mNetworkType", 0);
        XposedHelpers.setObjectField(info, "mTypeName", "WIFI");
        XposedHelpers.setObjectField(info, "getSubtypeName", null);
        XposedHelpers.setObjectField(info, "mState", NetworkInfo.State.CONNECTED);
        XposedHelpers.setObjectField(info, "mDetailedState", NetworkInfo.DetailedState.CONNECTED);
        XposedHelpers.setBooleanField(info, "mIsAvailable", true);
        XposedHelpers.setBooleanField(info, "mIsFailover", false);
        XposedHelpers.setBooleanField(info, "mIsRoaming", false);
        XposedHelpers.setObjectField(info, "mExtraInfo", null);
        XposedHelpers.setObjectField(info, "mReason", null);
        return info;
    }

    private DhcpInfo createDhcpInfo() {
        DhcpInfo info = (DhcpInfo) XposedHelpers.newInstance(DhcpInfo.class);
        int address = 0;
        try {
            address = ByteBuffer.wrap(InetAddress.getByName(Settings.getIP()).getAddress()).order(ByteOrder.LITTLE_ENDIAN).getInt();
        } catch (Exception ignored) {
        }
        XposedHelpers.setIntField(info, "ipAddress", address);
        int gateway = 0;
        try {
            gateway = ByteBuffer.wrap(InetAddress.getByName(Settings.getGateway()).getAddress()).order(ByteOrder.LITTLE_ENDIAN).getInt();
        } catch (Exception ignored) {
        }
        XposedHelpers.setIntField(info, "gateway", gateway);
        int netmask = 0;
        try {
            netmask = ByteBuffer.wrap(InetAddress.getByName(Settings.getNetmask()).getAddress()).order(ByteOrder.LITTLE_ENDIAN).getInt();
        } catch (Exception ignored) {
        }
        XposedHelpers.setIntField(info, "netmask", netmask);
        XposedHelpers.setIntField(info, "dns1", gateway);
        XposedHelpers.setIntField(info, "dns2", gateway);
        XposedHelpers.setIntField(info, "serverAddress", gateway);
        XposedHelpers.setIntField(info, "leaseDuration", 86400);
        return info;
    }
}
