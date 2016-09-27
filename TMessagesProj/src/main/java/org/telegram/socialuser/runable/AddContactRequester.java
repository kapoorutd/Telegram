package org.telegram.socialuser.runable;


import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.ContactsRequest;
import org.telegram.socialuser.model.CustomHttpParams;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

/**
 * Created by ram on 2/6/16.
 */
public class AddContactRequester implements Runnable {
    private ArrayList<TLRPC.TelegramUsers> userList;
    private ArrayList<CustomHttpParams> params;

    public AddContactRequester(ArrayList<TLRPC.TelegramUsers> uList, ArrayList<CustomHttpParams> params) {
        this.userList = uList;
        this.params = params;
    }

    @Override
    public void run() {
        ContactsRequest model = new ContactsRequest();
        model.setTelegramUsers(userList);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(model);
        CZResponse data1 = HttpUrlConnectionUtil.post(UriUtil.getHttpUrl("contact"),json,null,"application/json",params);

        if(data1.getResponseCode() == 200){
            ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE).edit().putBoolean("datasend",true).commit();
        }
    }
}
