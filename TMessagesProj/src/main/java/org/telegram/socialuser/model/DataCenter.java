package org.telegram.socialuser.model;

import org.telegram.messenger.ContactsController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ram on 19/6/16.
 */
public class DataCenter {


    public static HashMap<String,ArrayList<TLRPC.TelegramUsers>> getBigdata(){
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = ContactsController.getInstance().usersSectionsDict;
        ArrayList<ContactsController.Contact> contactses = ContactsController.getInstance().phoneBookContacts;
        HashMap<String,ArrayList<TLRPC.TelegramUsers>> bigList =new HashMap<>();
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance().sortedUsersSectionsArray;
        final ArrayList<TLRPC.User> userArrayList = new ArrayList<>();
        ArrayList<TLRPC.TelegramUsers> telegramUserses = new ArrayList<>();
        for (String index : sortedUsersSectionsArray) {
            for (TLRPC.TL_contact con : usersSectionsDict.get(index)) {
                userArrayList.add(MessagesController.getInstance().getUser(con.user_id));
                TLRPC.TelegramUsers uu = new TLRPC.TelegramUsers();
                TLRPC.User usertemp = MessagesController.getInstance().getUser(con.user_id);
                uu.setPhoto(usertemp.photo);
                uu.setPhone(usertemp.phone);
                uu.setUsername(usertemp.username);
                uu.setname(usertemp.first_name + " " + (usertemp.last_name!=null?usertemp.last_name:""));
                uu.setId(usertemp.id + "");
                telegramUserses.add(uu);
            }
        }

        for(int i =0;i<5;i++){
            bigList.put(i+"",telegramUserses);
        }
        return bigList;
    }
}
