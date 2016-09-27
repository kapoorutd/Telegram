package org.telegram.ui.listners;

import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;

/**
 * Created by ram on 18/6/16.
 */
public interface OnMutualFriendListner {
    void onMutualfriendList(ArrayList<TLRPC.TelegramUsers> list);
}
