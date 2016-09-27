package org.telegram.ui.listners;

import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

/**
 * Created by ram on 15/6/16.
 */
public interface OnServerResponse {

    void setFriendList(ArrayList<TLRPC.TelegramUsers> users);
    void setErrorList();

}
