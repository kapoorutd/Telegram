package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.payment.PaymentManager;
import org.telegram.payment.UserPaymentInfo;
import org.telegram.socialuser.BackgroundExecuter;
import org.telegram.socialuser.CustomGridAdapter;
import org.telegram.socialuser.GridViewWithHeaderAndFooter;
import org.telegram.socialuser.OnTelegramSync;
import org.telegram.socialuser.Util;
import org.telegram.socialuser.model.CustomHttpParams;
import org.telegram.socialuser.runable.AddContactRequester;
import org.telegram.socialuser.runable.GetSuggestFriendsRq;
import org.telegram.tgnet.TLRPC;

import org.telegram.tracker.AnalyticsTrackers;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.listners.OnServerResponse;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ram on 3/6/16.
 */
public class SocialFriendActivity extends BaseFragment implements OnServerResponse,OnTelegramSync {
    private static boolean isSend;
    private Context mContext;
    private ImageView img_Back;
    private CustomGridAdapter adapter;
    private static TLRPC.TelegramUsers response = null;
    private GetSuggestFriendsRq reqester;
    private GridView grid;
    private Button paybutton;
    private Bundle bundle;
    ProgressBar progressBar;
    private ArrayList<TLRPC.TelegramUsers> listnew;
    GridViewWithHeaderAndFooter gr_View;
    private ArrayList<TLRPC.TelegramUsers> telegramUsersesList = new ArrayList<>();
    private String friendId;
    private TextView emptyView;
    private static int remPosition = -1;

    public SocialFriendActivity(Bundle args){
        this.bundle = args;
    }

    @Override
    public View createView(final Context context) {
        mContext = context;
        SharedPreferences pp = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
        if(!pp.getBoolean("datasend",false)) {
            HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = ContactsController.getInstance().usersSectionsDict;
            ArrayList<ContactsController.Contact> contactses = ContactsController.getInstance().phoneBookContacts;

            ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance().sortedUsersSectionsArray;
            final ArrayList<TLRPC.User> userArrayList = new ArrayList<>();
            for (String index : sortedUsersSectionsArray) {
                for (TLRPC.TL_contact con : usersSectionsDict.get(index)) {
                    userArrayList.add(MessagesController.getInstance().getUser(con.user_id));
                    TLRPC.TelegramUsers telegramuser = new TLRPC.TelegramUsers();
                    TLRPC.User usertemp = MessagesController.getInstance().getUser(con.user_id);
                    telegramuser.setPhoto(usertemp.photo);
                    telegramuser.setPhone(usertemp.phone);
                    telegramuser.setUsername(usertemp.username);
                    telegramuser.setname((usertemp.first_name!=null?usertemp.first_name:"") + " " + (usertemp.last_name!=null?usertemp.last_name:""));
                    telegramuser.setId(usertemp.id + "");
                    telegramUsersesList.add(telegramuser);
                }
            }

            for(int i=0 ; i< contactses.size(); i++){
                TLRPC.TelegramUsers  uu = new TLRPC.TelegramUsers();
                uu.setname(contactses.get(i).first_name +" " +(contactses.get(i).last_name!=null?contactses.get(i).last_name:""));
                uu.setPhone(Util.getMobileNumber(contactses.get(i).phones.get(0)));
                uu.setId(null);
                uu.setPhoto(null);
                uu.setUsername(null);
                telegramUsersesList.add(uu);
            }
            ArrayList<CustomHttpParams> params = new ArrayList<>();
            params.add(new CustomHttpParams("userId", pp.getString("social_id", "")));
            BackgroundExecuter.getInstance().execute(new AddContactRequester(telegramUsersesList, params));
        }

        hideTabsAnsMenu();
        //ArrayList<CustomHttpParams> parem = new ArrayList<>();
        ArrayList<CustomHttpParams> params = new ArrayList<>();
        params.add(new CustomHttpParams("userId", pp.getString("social_id", "")));
        if(bundle.get("s_friend").equals("wink")){
            reqester = new GetSuggestFriendsRq(params,SocialFriendActivity.this,"search");
        } else {
            reqester = new GetSuggestFriendsRq(params,SocialFriendActivity.this,"searchdgree");
        }

        actionBar.setBackButtonImage(0x00000000);//todo
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTextLast("");
        final TLRPC.User user = UserConfig.getCurrentUser();
        if(bundle.get("s_friend").equals("wink")){
            actionBar.setTitle(LocaleController.getString("yourpreferences", R.string.yourpreferences));
        } else {
            actionBar.setTitle(LocaleController.getString("social_friend", R.string.social_friend));
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });
        listnew = new ArrayList<>();

        fragmentView = View.inflate(getParentActivity(), R.layout.gridview, null);


        gr_View = (GridViewWithHeaderAndFooter)fragmentView.findViewById(R.id.grid);
        paybutton =(Button)fragmentView.findViewById(R.id.paypal_btn_id) ;
        progressBar = (ProgressBar)fragmentView.findViewById(R.id.pb_load);
        progressBar.setVisibility(View.VISIBLE);

        if(UserPaymentInfo.getInstatance().getPaymentStatus()!=UserPaymentInfo.paidUser){
            gr_View.setPadding(0,0,0,0);
            paybutton.setVisibility(View.VISIBLE);
            paybutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PaymentManager.createIntent(getParentActivity());
                }
            });
        } else {
            paybutton.setVisibility(View.GONE);
            gr_View.setPadding(0,0,0,0);
        }
        img_Back = (ImageView)fragmentView.findViewById(R.id.backview) ;
        adapter = new CustomGridAdapter(mContext,listnew);
        emptyView = (TextView)fragmentView.findViewById(R.id.empty_view);
        grid=(GridView)fragmentView.findViewById(R.id.grid);
        grid.setAdapter(adapter);
        fragmentView.findViewById(R.id.black_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        BackgroundExecuter.getInstance().execute(reqester);

        grid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (UserPaymentInfo.getInstatance().getPaymentStatus() != UserPaymentInfo.paidUser) {
                    paybutton.setVisibility(View.VISIBLE);

                    paybutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PaymentManager.createIntent(getParentActivity());
                        }
                    });
                } else {
                    paybutton.setVisibility(View.GONE);

                    if (reqester.isMore)
                        BackgroundExecuter.getInstance().execute(reqester.loadMore());
                }

            }
        });


        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                remPosition = position;

                Bundle args = new Bundle();
                try {
                    final int pos=position;
                    TLRPC.TelegramUsers u = listnew.get(pos);
                    if (u !=null) {
                        args.putInt("user_id", Integer.parseInt(listnew.get(pos).id));
                        args.putString("friendId", listnew.get(pos).getUserId());
                        presentFragment(new UserProfileActivity(args,listnew.get(pos)));
                        ArrayList<CustomHttpParams> params = new ArrayList<>();
                        params.add(new CustomHttpParams("userId", ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE).getString("social_id", "")));
                        ArrayList<TLRPC.TelegramUsers> telegramList = new ArrayList<>();
                        telegramList.add(listnew.get(pos));
                        //  BackgroundExecuter.getInstance().execute(new AddContactRequester(telegramList, params));
                        //  listnew.remove(listnew.get(pos));

                    } else {

                        Toast.makeText(mContext,"Invalid user",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d("User_Key", e.getStackTrace().toString());
                }

            }
        });
        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFragment();
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationLoader.getInstance().trackScreenView(AnalyticsTrackers.CHANGE_PHONE_HELP);

        if(isSend && remPosition!= -1)
        {
            listnew.remove(listnew.get(remPosition));
            isSend=false;
            remPosition= -1;
        }
        if(listnew.size()<21 && UserPaymentInfo.getInstatance().getPaymentStatus()!=1){
            actionBar.setTextLast(listnew.size()+"/20");
        }else{ actionBar.setTextLast("");
        }
    }


    public static void getResponse(TLRPC.TelegramUsers respon){
        response = respon;
    }

    @Override
    public void setFriendList(final ArrayList<TLRPC.TelegramUsers> users) {
        getParentActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {

                        progressBar.setVisibility(View.GONE);

                        listnew.addAll(users);
                        if(listnew.size()<21 && UserPaymentInfo.getInstatance().getPaymentStatus()!=1){
                            actionBar.setTextLast(listnew.size()+"/20");
                        }
                        else{ actionBar.setTextLast("");
                        }

                        adapter.notifyDataSetChanged();

                    }
                }
        );
    }

    @Override
    public void setErrorList(){
        getParentActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if(listnew.size()== 0){
                            emptyView.setVisibility(View.VISIBLE);
                        }else{emptyView.setVisibility(View.GONE);
                        }
                    }
                }
        );
    }







    @Override
    public void onUserSyncSuccess(final TLRPC.User user) {

    }

    @Override
    public void onUserSyncFailed() {

    }

    public static  void setMessageSend(boolean isMessageSend){
        isSend = isMessageSend;
    }



}
