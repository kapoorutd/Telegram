package org.telegram.socialuser.runable;

import com.google.gson.Gson;

import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.CustomHttpParams;
import org.telegram.ui.PreferencesActivity;

import java.util.ArrayList;

/**
 * Created by ram on 2/6/16.
 */
public class PreferencesRequester implements Runnable {
    private PreferencesActivity.PreferenceData data;
    private ArrayList<CustomHttpParams> params;
    private PreferencesActivity listner;


    public PreferencesRequester(PreferencesActivity.PreferenceData data,ArrayList<CustomHttpParams> params,PreferencesActivity listner) {
        this.data = data;
        this.params =params;
        this.listner = listner;

    }

    @Override
    public void run() {
        Gson gson = new Gson();
        String body = gson.toJson(data);
        CZResponse data1 = HttpUrlConnectionUtil.post(UriUtil.getHttpUrl("preference"),body,null,"application/json",params);
        if(data1.getResponseCode() == 200){
            listner.setPreferencesSuccess();
        }else {
            listner.setPreferencesFailed();
        }
   if(data1==null){
       listner.setPreferencesFailed();
   }

    }
}
