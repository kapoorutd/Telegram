package org.telegram.socialuser.model;

import com.google.gson.annotations.Expose;

import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

/**
 * Created by ram on 10/6/16.
 */
public class ContactsRequest {

    @Expose
    private ArrayList<TLRPC.TelegramUsers> telegramUsers;

    public ArrayList<TLRPC.TelegramUsers> getTelegramUsers ()
    {
        return telegramUsers;
    }

    public void setTelegramUsers (ArrayList<TLRPC.TelegramUsers> telegramUsers)
    {
        this.telegramUsers = telegramUsers;
    }

}
