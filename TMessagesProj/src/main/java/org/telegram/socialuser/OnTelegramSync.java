package org.telegram.socialuser;

import org.telegram.tgnet.TLRPC;

/**
 * Created by home on 6/26/16.
 */
public interface OnTelegramSync {


    void onUserSyncSuccess(TLRPC.User user);
    void onUserSyncFailed();



}
