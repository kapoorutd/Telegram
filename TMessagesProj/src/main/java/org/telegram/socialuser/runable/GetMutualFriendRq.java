package org.telegram.socialuser.runable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.Logger;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.ContactsRequest;
import org.telegram.ui.UserProfileActivity;
import org.telegram.ui.listners.OnServerResponse;

/**
 * Created by ram on 15/6/16.
 */
public class GetMutualFriendRq implements Runnable{

    public boolean isMore = false;
    public boolean isPending = false;
    public int ofset = 0;
    private OnServerResponse listner;
    private Gson gson;
    private ContactsRequest contactsRequest = null;
    private String url;
    private UserProfileActivity.MutualFriend mutualFriend;


    public GetMutualFriendRq(UserProfileActivity.MutualFriend mutualFriend, OnServerResponse listner, String url) {
        this.listner = listner;
        this.mutualFriend = mutualFriend;
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
                Gson gson = new Gson();
                String body = gson.toJson(mutualFriend);
                CZResponse data1 = HttpUrlConnectionUtil.post(UriUtil.getHttpUrl(url,ofset),body,null, "application/json",null);
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
                                //listner.setFriendList(bigData.get(ofset + ""));
                        }
                    }
                } else {
                    listner.setErrorList();
                }
            } catch (Exception e){
                listner.setErrorList();
                Logger.d("Get_Mutual_friend",e.getStackTrace().toString());
            }
        }

    }
    public GetMutualFriendRq loadMore() {
        this.ofset++;
        return this;
    }
}
