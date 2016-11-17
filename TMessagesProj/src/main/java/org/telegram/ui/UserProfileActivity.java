package org.telegram.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
//import org.telegram.messenger.AnimationCompat.ViewProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.socialuser.BackgroundExecuter;
import org.telegram.socialuser.CustomGridAdapter;
import org.telegram.socialuser.GridViewWithHeaderAndFooter;
import org.telegram.socialuser.OnTelegramSync;
import org.telegram.socialuser.model.CustomHttpParams;
import org.telegram.socialuser.runable.AddContactRequester;
import org.telegram.socialuser.runable.GetMutualFriendRq;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
//import org.telegram.ui.Components.FrameLayoutFixed;
import org.telegram.ui.listners.OnServerResponse;

import java.util.ArrayList;

/**
 * Created by ram on 11/6/16.
 */
public class UserProfileActivity extends BaseFragment implements PhotoViewer.PhotoViewerProvider,View.OnClickListener,OnServerResponse,OnTelegramSync {
    private final String cCode;
    private BackupImageView avatarImageView;
    private TextView statusTextView,nameTextView;
    private FrameLayout avatarContainer;
    private RelativeLayout phone;
    private RelativeLayout location;
    private TextView country_txt;
    private TextView phone_txt;
    protected TLRPC.Chat currentChat;
    protected TLRPC.User currentUser;
    private Context mcContext;
    /*private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    private int selectedgender;
    private Button save_btn;
    */private  GridView grid;
    private CustomGridAdapter adapter;
    private GetMutualFriendRq reqester;
    private View doneButton;
    private final static int done_button = 1;
    private String friendId;
    ProgressBar progressBar;
    private static TLRPC.User currUserTemp;
//    public static int ii;
    //  private static TLRPC.TelegramUsers telegramUsers = null;

    private static TLRPC.TelegramUsers user = null;
    private boolean isMessageSend ;
    private TextView emptyView;

    @Override
    public void onUserSyncSuccess(TLRPC.User user) {

    }

    @Override
    public void onUserSyncFailed() {

    }



    public class MutualFriend {
        public  String userId;
        public String friendId;

    }


    final ArrayList<TLRPC.TelegramUsers> listnew = new ArrayList<>();
    Bundle bundle;

    public UserProfileActivity(Bundle args,TLRPC.TelegramUsers telegramUser ) {
        user=telegramUser ;
        friendId  = args.getString("friendId");
        cCode = args.getString("cCode");
    }



    @Override
    public View createView(final Context context) {

        hideTabsAnsMenu();
        mcContext = context;
        actionBar.setBackButtonImage(0);//todo

        actionBar.setAllowOverlayTitle(true);
        ActionBarMenu menu = actionBar.createMenu();
        doneButton = menu.addItemWithWidth(done_button, R.drawable.ic_add_user, AndroidUtilities.dp(56));

        if( user != null) {
            actionBar.setTitle(user.getname());
        } else {
            actionBar.setTitle("Unknown");
        }

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });


        avatarContainer = new FrameLayout(context);
        avatarContainer.setBackgroundResource(R.drawable.bar_selector);
        avatarContainer.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);

        fragmentView = View.inflate(context, R.layout.usernw_profile_layout, null);

        View viewf =View.inflate(getParentActivity(), R.layout.footer_of_gridview, null);
        GridViewWithHeaderAndFooter gr_View = (GridViewWithHeaderAndFooter)fragmentView.findViewById(R.id.grid);
        gr_View.addFooterView(viewf);
        location = (RelativeLayout)fragmentView.findViewById(R.id.location);

        progressBar = (ProgressBar)fragmentView.findViewById(R.id.pb_load);
        progressBar.setVisibility(View.VISIBLE);

        country_txt= (TextView)fragmentView.findViewById(R.id.id_country) ;

        avatarImageView = (BackupImageView)fragmentView.findViewById(R.id.user_avatar_view);
        nameTextView=(TextView)fragmentView.findViewById(R.id.user_name_label);
        statusTextView =(TextView)fragmentView.findViewById(R.id.user_staus_label);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(30));
        //   emptyView=(TextView)fragmentView.findViewById(R.id.empty_view);
        checkAndUpdateAvatar();
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
        country_txt.setText(cCode);
        adapter = new CustomGridAdapter(mcContext,listnew);
        grid=(GridView)fragmentView.findViewById(R.id.grid);
        emptyView=(TextView)fragmentView.findViewById(R.id.empty_view);
        grid.setAdapter(adapter);
        MutualFriend m = new MutualFriend();
        m.friendId  = friendId;
        m.userId = preferences.getString("social_id","");
        reqester = new GetMutualFriendRq(m,UserProfileActivity.this,"getmutual");
        BackgroundExecuter.getInstance().execute(reqester);
        fragmentView.findViewById(R.id.black_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContactsController.getInstance().telegramResponse(user.name,user.phone,UserProfileActivity.this);

                final ProgressDialog dialog = ProgressDialog.show(v.getContext(), "", "Loading...",
                        true);

                dialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        dialog.dismiss();

                        TLRPC.User u = MessagesController.getInstance().getUser(Integer.parseInt(user.getId()));
                        if (u != null) {
                            isMessageSend=true;
                            Bundle args = new Bundle();
                            args.putBoolean("social_invite",true);
                            args.putInt("user_id", u != null ? u.id : 179338637);
                            presentFragment(new ChatActivity(args));
                            ArrayList<CustomHttpParams> params = new ArrayList<>();
                            params.add(new CustomHttpParams("userId", ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE).getString("social_id", "")));
                            ArrayList<TLRPC.TelegramUsers> telegramList = new ArrayList<>();
                            telegramList.add(user);
                            SocialFriendActivity.setMessageSend(true);
                            BackgroundExecuter.getInstance().execute(new AddContactRequester(telegramList, params));
                        } else {
                            SocialFriendActivity.setMessageSend(false);
                        }
                    }
                },5000);

            }
        });




        grid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(reqester.isMore)
                    BackgroundExecuter.getInstance().execute(reqester.loadMore());
            }
        });


        if(user!=null) {
            nameTextView.setText(user.getname());
        } else {
            nameTextView.setText(LocaleController.getString("Username", R.string.Username));
        }

        avatarImageView.setOnClickListener(this);
        fragmentView.findViewById(R.id.backview).setOnClickListener(this);
        return fragmentView;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backview:

                if(!isMessageSend){
                    SocialFriendActivity.setMessageSend(false);
                }
                finishFragment();
                break;
            case R.id.user_avatar_view:
                if (currentUser!=null&&currentUser.id != 0) {
                    if (currentUser!=null&&currentUser.photo != null && currentUser.photo.photo_big != null) {
                        PhotoViewer.getInstance().setParentActivity(getParentActivity());
                        PhotoViewer.getInstance().openPhoto(currentUser.photo.photo_big,UserProfileActivity.this);
                    }
                }
                break;
            case R.id.save:
                Bundle args = new Bundle();
                args.putBoolean("social_invite",true);
                args.putInt("user_id",currentUser!=null?currentUser.id:179338637);
                presentFragment(new ChatActivity(args));
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        ContactsController.getInstance().checkContacts();
        statusTextView.setText(LocaleController.formatUserStatus(currentUser));
        hideTabsAnsMenu();

    }







    private void checkAndUpdateAvatar() {


        if(user.photo !=null && user.photo.photo_id != 0){
            avatarImageView.setImage(user.photo.photo_small, "50_50",new AvatarDrawable(user,false),true);

        }
        else if(user.photo.photo_id == 0){
            avatarImageView.setImage(null, "50_50",new AvatarDrawable(user,false),true);
        }
    }



    @Override
    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index){
        if (fileLocation == null) {
            return null;
        }

        TLRPC.FileLocation photoBig = null;
        if (currentUser.id != 0) {
            TLRPC.User user = MessagesController.getInstance().getUser(currentUser.id);
            if (user != null && user.photo != null && user.photo.photo_big != null) {
                photoBig = user.photo.photo_big;
            }
        }
        if (photoBig != null && photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
            int coords[] = new int[2];
            avatarImageView.getLocationInWindow(coords);
            PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
            object.viewX = coords[0];
            object.viewY = coords[1] - AndroidUtilities.statusBarHeight;
            object.parentView = avatarImageView;
            object.imageReceiver = avatarImageView.getImageReceiver();
            object.user_id = currentUser.id;
            object.thumb = object.imageReceiver.getBitmap();
            object.size = -1;
            object.radius = avatarImageView.getImageReceiver().getRoundRadius();
           // object.scale = ViewProxy.getScaleX(avatarImageView);
            return object;
        }
        return null;
    }


    @Override
    public Bitmap getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
        return null;
    }

    @Override
    public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {

    }

    @Override
    public void willHidePhotoViewer() {

    }

    @Override
    public boolean isPhotoChecked(int index) {
        return false;
    }

    @Override
    public void setPhotoChecked(int index) {

    }

    @Override
    public boolean cancelButtonPressed() {
        return false;
    }

    @Override
    public void sendButtonPressed(int index) {

    }

    @Override
    public int getSelectedCount() {
        return 0;
    }

    @Override
    public void updatePhotoAtIndex(int index) {

    }

    @Override
    public void setFriendList(final ArrayList<TLRPC.TelegramUsers> usersList) {
        getParentActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        listnew.addAll(usersList);
                        progressBar.setVisibility(View.GONE);
                        adapter.clear();
                        adapter.addAll(usersList);
                        if(usersList.size()==0){
                            emptyView.setVisibility(View.VISIBLE);
                        } else{
                            emptyView.setVisibility(View.GONE);
                        }

                        //notify();

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
                        if(listnew.size()==0){
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
    }





    /*public static void setCurrentUser(TLRPC.User user, TLRPC.TelegramUsers telegramUsers1) {

        currUserTemp = user;
        telegramUsers = telegramUsers1;
    }*/

}
