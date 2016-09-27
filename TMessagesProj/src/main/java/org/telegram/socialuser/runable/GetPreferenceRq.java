package org.telegram.socialuser.runable;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.Logger;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.CustomHttpParams;
import org.telegram.ui.PreferencesActivity;

import java.util.ArrayList;

/**
 * Created by ram on 2/6/16.
 */
public class GetPreferenceRq implements Runnable {
    private ArrayList<CustomHttpParams> params;
    private PreferencesActivity listner;
    public GetPreferenceRq(ArrayList<CustomHttpParams> params,PreferencesActivity listner)
    {
        this.params = params;
        this.listner =listner;
    }
    @Override
    public void run() {
        try {
            CZResponse data1 = HttpUrlConnectionUtil.get(UriUtil.getHttpUrl("preference"), "application/json", params);
            if (data1.getResponseCode() == 200) {
                Gson gson = new Gson();
                PreferencesActivity.PreferenceData JsonData = gson.fromJson(data1.getResponseString(), PreferencesActivity.PreferenceData.class);
                SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
                p.edit().putString("minage", JsonData.getMinage()).commit();
                p.edit().putString("maxage", JsonData.getMaxage()).commit();
                p.edit().putString("sexPreferences", JsonData.getSex()).commit();
                p.edit().putString("preferencecCode", JsonData.getcCode()).commit();
                listner.onGetpreferencessuccess();
            }
        } catch (Exception e) {
            Logger.d("Get_preferences",e.getStackTrace().toString());
        }
    }
}
