/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package org.telegram.messenger;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.multidex.MultiDex;
import android.util.Base64;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.tracker.AnalyticsTrackers;
import org.telegram.ui.Components.ForegroundDetector;

import java.io.File;
import java.io.RandomAccessFile;


import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;


public class ApplicationLoader extends Application {

    private static Drawable cachedWallpaper;
    private static int selectedColor;
    private static boolean isCustomTheme;
    private static final Object sync = new Object();
    private static ApplicationLoader _instance;
    private static int serviceMessageColor;
    private static int serviceSelectedMessageColor;

    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    private static volatile boolean applicationInited = false;

    public static volatile boolean isScreenOn = false;
    public static volatile boolean mainInterfacePaused = true;


    public static boolean isCustomTheme() {
        return isCustomTheme;
    }

    public static int getSelectedColor() {
        return selectedColor;
    }

    public static void reloadWallpaper() {
        cachedWallpaper = null;
        serviceMessageColor = 0;
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).edit().remove("serviceMessageColor").commit();
        loadWallpaper();
    }

    private static void calcBackgroundColor() {
        int result[] = AndroidUtilities.calcDrawableColor(cachedWallpaper);
        serviceMessageColor = result[0];
        serviceSelectedMessageColor = result[1];
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        preferences.edit().putInt("serviceMessageColor", serviceMessageColor).putInt("serviceSelectedMessageColor", serviceSelectedMessageColor).commit();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static int getServiceMessageColor() {
        return serviceMessageColor;
    }

    public static int getServiceSelectedMessageColor() {
        return serviceSelectedMessageColor;
    }

    public static void loadWallpaper() {
        if (cachedWallpaper != null) {
            return;
        }
        Utilities.searchQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                synchronized (sync) {
                    int selectedColor = 0;
                    try {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                        int selectedBackground = preferences.getInt("selectedBackground", 1000001);
                        selectedColor = preferences.getInt("selectedColor", 0);
                        serviceMessageColor = preferences.getInt("serviceMessageColor", 0);
                        serviceSelectedMessageColor = preferences.getInt("serviceSelectedMessageColor", 0);
                        if (selectedColor == 0) {
                            if (selectedBackground == 1000001) {
                                cachedWallpaper = applicationContext.getResources().getDrawable(R.drawable.background_hd);
                                isCustomTheme = false;
                            } else {
                                File toFile = new File(getFilesDirFixed(), "wallpaper.jpg");
                                if (toFile.exists()) {
                                    cachedWallpaper = Drawable.createFromPath(toFile.getAbsolutePath());
                                    isCustomTheme = true;
                                } else {
                                    cachedWallpaper = applicationContext.getResources().getDrawable(R.drawable.background_hd);
                                    isCustomTheme = false;
                                }
                            }
                        }
                    } catch (Throwable throwable) {
                        //ignore
                    }
                    if (cachedWallpaper == null) {
                        if (selectedColor == 0) {
                            selectedColor = -2693905;
                        }
                        cachedWallpaper = new ColorDrawable(selectedColor);
                    }
                    if (serviceMessageColor == 0) {
                        calcBackgroundColor();
                    }
                }
            }
        });
    }

    public static Drawable getCachedWallpaper() {
        synchronized (sync) {
            return cachedWallpaper;
        }
    }


    public static ApplicationLoader getInstance() {
        if(_instance == null) {
            _instance = new ApplicationLoader();
        }
        return _instance;
    }

    public String getHttpServer(){
      //  return "107.170.73.149:8080/socialshareupapi/";
       // return "192.168.2.250:8080/socialshareupapi/";
       // return "192.168.1.34:8080/socialshareupapi/";

        return "telegram.craterzone.com:8080/socialshareupapi/";

    }


    private static void convertConfig() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("dataconfig", Context.MODE_PRIVATE);
        if (preferences.contains("currentDatacenterId")) {
            SerializedData buffer = new SerializedData(32 * 1024);
            buffer.writeInt32(2);
            buffer.writeBool(preferences.getInt("datacenterSetId", 0) != 0);
            buffer.writeBool(true);
            buffer.writeInt32(preferences.getInt("currentDatacenterId", 0));
            buffer.writeInt32(preferences.getInt("timeDifference", 0));
            buffer.writeInt32(preferences.getInt("lastDcUpdateTime", 0));
            buffer.writeInt64(preferences.getLong("pushSessionId", 0));
            buffer.writeBool(false);
            buffer.writeInt32(0);
            try {
                String datacentersString = preferences.getString("datacenters", null);
                if (datacentersString != null) {
                    byte[] datacentersBytes = Base64.decode(datacentersString, Base64.DEFAULT);
                    if (datacentersBytes != null) {
                        SerializedData data = new SerializedData(datacentersBytes);
                        buffer.writeInt32(data.readInt32(false));
                        buffer.writeBytes(datacentersBytes, 4, datacentersBytes.length - 4);
                        data.cleanup();
                    }
                }
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }

            try {
                File file = new File(getFilesDirFixed(), "tgnet.dat");
                RandomAccessFile fileOutputStream = new RandomAccessFile(file, "rws");
                byte[] bytes = buffer.toByteArray();
                fileOutputStream.writeInt(Integer.reverseBytes(bytes.length));
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
            buffer.cleanup();
            preferences.edit().clear().commit();
        }
    }

    public static File getFilesDirFixed() {
        for (int a = 0; a < 10; a++) {
            File path = ApplicationLoader.applicationContext.getFilesDir();
            if (path != null) {
                return path;
            }
        }
        try {
            ApplicationInfo info = applicationContext.getApplicationInfo();
            File path = new File(info.dataDir, "files");
            path.mkdirs();
            return path;
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
        return new File("/data/data/org.telegram.messenger/files");
    }

    public static void postInitApplication() {
        if (applicationInited) {
            return;
        }

        applicationInited = true;
        convertConfig();

        try {
            LocaleController.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            final BroadcastReceiver mReceiver = new ScreenReceiver();
            applicationContext.registerReceiver(mReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PowerManager pm = (PowerManager)ApplicationLoader.applicationContext.getSystemService(Context.POWER_SERVICE);
            isScreenOn = pm.isScreenOn();
            FileLog.e("tmessages", "screen state = " + isScreenOn);
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }

        UserConfig.loadConfig();
        String deviceModel;
        String langCode;
        String appVersion;
        String systemVersion;
        String configPath = getFilesDirFixed().toString();

        try {
            langCode = LocaleController.getLocaleStringIso639();
            deviceModel = Build.MANUFACTURER + Build.MODEL;
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            appVersion = pInfo.versionName + " (" + pInfo.versionCode + ")";
            systemVersion = "SDK " + Build.VERSION.SDK_INT;
        } catch (Exception e) {
            langCode = "en";
            deviceModel = "Android unknown";
            appVersion = "App version unknown";
            systemVersion = "SDK " + Build.VERSION.SDK_INT;
        }
        if (langCode.trim().length() == 0) {
            langCode = "en";
        }
        if (deviceModel.trim().length() == 0) {
            deviceModel = "Android unknown";
        }
        if (appVersion.trim().length() == 0) {
            appVersion = "App version unknown";
        }
        if (systemVersion.trim().length() == 0) {
            systemVersion = "SDK Unknown";
        }

        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
        boolean enablePushConnection = preferences.getBoolean("pushConnection", true);

        MessagesController.getInstance();
        ConnectionsManager.getInstance().init(BuildVars.BUILD_VERSION, TLRPC.LAYER, BuildVars.APP_ID, deviceModel, systemVersion, appVersion, langCode, configPath, FileLog.getNetworkLogPath(), UserConfig.getClientUserId(), enablePushConnection);
        if (UserConfig.getCurrentUser() != null) {
            MessagesController.getInstance().putUser(UserConfig.getCurrentUser(), true);
            ConnectionsManager.getInstance().applyCountryPortNumber(UserConfig.getCurrentUser().phone);
            MessagesController.getInstance().getBlockedUsers(true);
            SendMessagesHelper.getInstance().checkUnsentMessages();
        }

        ApplicationLoader app = (ApplicationLoader)ApplicationLoader.applicationContext;
        app.initPlayServices();
        FileLog.e("tmessages", "app initied");

        ContactsController.getInstance().checkAppAccount();
        MediaController.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        NativeLoader.initNativeLibs(ApplicationLoader.applicationContext);


        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);


        ConnectionsManager.native_setJava(Build.VERSION.SDK_INT == 14 || Build.VERSION.SDK_INT == 15);
        new ForegroundDetector(this);

        applicationHandler = new Handler(applicationContext.getMainLooper());

        startPushService();
        startSinchService();
    }


    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }



    public void trackEvent(String category, String action, String label) {
    }


    public static void startPushService() {
        SharedPreferences preferences = applicationContext.getSharedPreferences("Notifications", MODE_PRIVATE);

        if (preferences.getBoolean("pushService", true)) {
            applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
        } else {
            stopPushService();
        }
    }

    public static void stopPushService() {
        applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));

        PendingIntent pintent = PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0);
        AlarmManager alarm = (AlarmManager)applicationContext.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            LocaleController.getInstance().onDeviceConfigurationChange(newConfig);
            AndroidUtilities.checkDisplaySize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPlayServices() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (checkPlayServices()) {
                    if (UserConfig.pushString != null && UserConfig.pushString.length() != 0) {
                        FileLog.d("tmessages", "GCM regId = " + UserConfig.pushString);
                    } else {
                        FileLog.d("tmessages", "GCM Registration not found.");
                    }

                } else {
                    FileLog.d("tmessages", "No valid Google Play Services APK found.");
                }
            }
        }, 1000);
    }


    private boolean checkPlayServices() {
        try {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            return resultCode == ConnectionResult.SUCCESS;
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
        return true;

    }

    //**************************sinch Service********************************//


    public void startSinchService(){
  //    applicationContext.startService(new Intent(applicationContext, SinchService.class));
    }


}
