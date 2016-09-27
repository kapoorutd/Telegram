package org.telegram.socialuser.runable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.Logger;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.ContactsRequest;
import org.telegram.socialuser.model.CustomHttpParams;
import org.telegram.ui.listners.OnServerResponse;

import java.util.ArrayList;

/**
 * Created by ram on 15/6/16.
 */
public class GetSuggestFriendsRq implements Runnable{



    public boolean isMore = false;
    public boolean isPending = false;
    public int ofset = 0;
    private ArrayList<CustomHttpParams> param;
    private OnServerResponse listner;
    private Gson gson;
    private ContactsRequest contactsRequest = null;
    private String url;

    public GetSuggestFriendsRq(ArrayList<CustomHttpParams> param, OnServerResponse listner,String url) {
        this.listner = listner;
        this.param = param;
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        this.url = url;
    }
    @Override
    public void run() {
        if(isPending){
            return;
        }
        isPending = true;
        if(isPending) {
            try {

               CZResponse data1 = HttpUrlConnectionUtil.get(UriUtil.getHttpUrl(url,ofset), "application/json",param);
                if (data1.getResponseCode() == 200) {
                    if (data1.getResponseString() != null) {
                        contactsRequest = gson.fromJson(data1.getResponseString(), ContactsRequest.class);
                        if (contactsRequest.getTelegramUsers().isEmpty()) {
                            isMore = false;
                            isPending= false;
                            listner.setErrorList();

                        } else {

                                isMore = true;
                                 listner.setFriendList(contactsRequest.getTelegramUsers());
                                isPending = false;

                        }
                    }
                } else {
                    listner.setErrorList();
                }
            } catch (Exception e){
                Logger.d("Get_Suggested_friend",e.getStackTrace().toString());
                listner.setErrorList();
            }
        }

    }
    public GetSuggestFriendsRq loadMore() {
        this.ofset++;
        return this;
    }
}
