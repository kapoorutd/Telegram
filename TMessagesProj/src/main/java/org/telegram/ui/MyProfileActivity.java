/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2015.
 */

package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.messenger.AndroidUtilities;
//import org.telegram.messenger.AnimationCompat.ViewProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.socialuser.BackgroundExecuter;
import org.telegram.socialuser.Util;
import org.telegram.socialuser.model.CustomHttpParams;
import org.telegram.socialuser.runable.AddContactRequester;
import org.telegram.socialuser.runable.AddUserRequester;
import org.telegram.tgnet.TLRPC;
//import org.telegram.tracker.AnalyticsTrackers;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.CountryAdapter;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
//import org.telegram.ui.Components.FrameLayoutFixed;
import org.telegram.ui.listners.OnAddUserListner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
public class MyProfileActivity extends BaseFragment implements OnAddUserListner,PhotoViewer.PhotoViewerProvider,View.OnClickListener,NotificationCenter.NotificationCenterDelegate{
    private BackupImageView avatarImageView;
    private TextView statusTextView,nameTextView;
    private FrameLayout avatarContainer;
    private RelativeLayout phone;
    private RelativeLayout location;
    private RelativeLayout gender;
    private RelativeLayout dob;
    private TextView country_txt;
    private TextView gender_txt;
    private TextView dob_txt;
    private View doneButton;
    private final static int done_button = 1;
    private TextView phone_txt;
    protected TLRPC.Chat currentChat;
    protected TLRPC.User currentUser;
    private Context mcContext;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    private int selectedgender;
    private Button save_btn;
    private static int selectSex;



    @Override
    public View createView(final Context context) {

//        hideTabsAnsMenu();
        mcContext = context;
        currentUser = UserConfig.getCurrentUser();
        //actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackButtonImage(0x00000000);//todo
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("myprofile", R.string.myprofile));

        /* ActionBarMenu menu = actionBar.createMenu();
        doneButton = menu.addItemWithWidth(done_button, R.drawable.ic_done, AndroidUtilities.dp(56));
*/
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        avatarContainer = new FrameLayout(context);
        /* avatarContainer.setBackgroundResource(R.drawable.bar_selector);*/
        avatarContainer.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);

        fragmentView = View.inflate(context, R.layout.myprofile_layout, null);
        location = (RelativeLayout)fragmentView.findViewById(R.id.location);
        country_txt= (TextView)fragmentView.findViewById(R.id.id_country) ;
        gender = (RelativeLayout)fragmentView.findViewById(R.id.gender);
        gender_txt = (TextView)fragmentView.findViewById(R.id.id_gender) ;
        dob = (RelativeLayout)fragmentView.findViewById(R.id.age);
        dob_txt = (TextView)fragmentView.findViewById(R.id.id_age) ;
        phone = (RelativeLayout)fragmentView.findViewById(R.id.number);
        save_btn  = (Button)fragmentView.findViewById(R.id.save);
        phone_txt = (TextView)fragmentView.findViewById(R.id.id_number) ;
        avatarImageView = (BackupImageView)fragmentView.findViewById(R.id.user_avatar_view);
        nameTextView=(TextView)fragmentView.findViewById(R.id.user_name_label);
        statusTextView =(TextView)fragmentView.findViewById(R.id.user_staus_label);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(30));
        fragmentView.findViewById(R.id.black_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CountrySelectActivity fragment = new CountrySelectActivity();
                fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                    @Override
                    public void didSelectCountry(String name) {
                        //   cName = name;
                        ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE).edit().putString("country",name).commit();;
                        country_txt.setText(name);
                    }
                });
                presentFragment(fragment);
            }
        });

        //  phone_txt.setText("+"+currentUser.phone);
        gender.setOnClickListener(this);
        dob.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        checkAndUpdateAvatar();
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);

        country_txt.setText(preferences.getString("country","Indonesia"));
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dob_txt.setText(preferences.getString("dob",""));
        gender_txt.setText(getGender(preferences.getString("sex","")));
        if(currentUser!=null&&currentUser.username!= null) {
            nameTextView.setText(currentUser.username);
        } else {
            nameTextView.setText(currentUser.first_name);
        }
        nameTextView.setOnClickListener(this);
        avatarImageView.setOnClickListener(this);
        phone.setOnClickListener(this);
        statusTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("PhoneNumberAlert", R.string.PhoneNumberAlert));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presentFragment(new ChangePhoneActivity(), true);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            }
        });

        fragmentView.findViewById(R.id.backview).setOnClickListener(this);

        ApplicationLoader.getInstance().addUIListener(OnAddUserListner.class,MyProfileActivity.this);



        return fragmentView;




    }





    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_name_label:
                presentFragment(new ChangeUsernameActivity());
                break;
            case R.id.backview:
         //       showTabsAndmenu();
                removeUIListeners();
                break;
            case R.id.number:
                presentFragment(new ChangePhoneHelpActivity());
                break;
            case R.id.user_avatar_view:
                if (currentUser.id != 0) {
                    // TLRPC.User user = MessagesController.getInstance().getUser(user_id);
                    if (currentUser.photo != null && currentUser.photo.photo_big != null) {
                        PhotoViewer.getInstance().setParentActivity(getParentActivity());
                        PhotoViewer.getInstance().openPhoto(currentUser.photo.photo_big,MyProfileActivity.this);
                    }
                }
                break;
            case R.id.age:
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(mcContext,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year1,
                                                  int monthOfYear, int dayOfMonth) {
                                year =year1;
                                month = monthOfYear;
                                day= dayOfMonth;
                                if(dateValidater(year1,monthOfYear,dayOfMonth)){
                                    ApplicationLoader.applicationContext.
                                            getSharedPreferences("socialuser", Activity.MODE_PRIVATE).edit()
                                            .putString("dob",Util.getDateforserver(year1,monthOfYear,dayOfMonth)).commit();
                                    dob_txt.setText(Util.getDateforserver(year1,monthOfYear,dayOfMonth));
                                } else{
                                    Toast.makeText(mcContext,LocaleController.getString("pleaseselectvaliddate", R.string.pleaseselectvaliddate),Toast.LENGTH_SHORT).show();
                                    return;
                                }


                            }
                        }, year,month, day);
                dpd.show();
                break;
            case R.id.gender:
                showDialogForSelection(new String[]{"Male","Female"});
                break;
            case R.id.save:
                currentUser = UserConfig.getCurrentUser();
                SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
                setContact();
                currentUser =UserConfig.getCurrentUser();

                TLRPC.TelegramUsers newuser = new TLRPC.TelegramUsers();
                //String dob =Util.convertDate(p.getString("dob",showDate(year,month,day)));
                newuser.setDob(Util.convertDate(p.getString("dob","2000-02-02")));
                newuser.setSex(p.getString("sex",getGender()));
                newuser.setId(currentUser.id+"");
                // p.edit().putString("country",name).commit();
                CountryAdapter.Country cu =getcountry((ApplicationLoader.
                        applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE).getString("country","india")));
                newuser.setcCode(cu.shortname);
                newuser.setname((currentUser.first_name!=null?currentUser.first_name:"")+" "+(currentUser.last_name!=null?currentUser.last_name:""));
                newuser.setPhoto(currentUser.photo);
                newuser.setPhone(currentUser.phone);
                newuser.setUsername(currentUser.username);
                BackgroundExecuter.getInstance().execute(new AddUserRequester(newuser,null));
                //   finishFragment();
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
       // hideTabsAnsMenu();
        currentUser = UserConfig.getCurrentUser();
   //    ApplicationLoader.getInstance().trackScreenView(AnalyticsTrackers.CHANGE_PHONE_HELP);
        statusTextView.setText(LocaleController.formatUserStatus(currentUser));
        phone_txt.setText("+"+currentUser.phone);
        phone_txt.setText("+"+currentUser.phone);
    }

    private void checkAndUpdateAvatar() {
        TLRPC.FileLocation newPhoto = null;
        AvatarDrawable avatarDrawable = null;
        if (currentUser != null) {
            TLRPC.User user = MessagesController.getInstance().getUser(currentUser.id);
            if (user == null) {
                return;
            }
            currentUser = user;
            if (currentUser.photo != null) {
                newPhoto = currentUser.photo.photo_small;
            }
            avatarDrawable = new AvatarDrawable(currentUser);
        }
        if (avatarImageView != null) {
            avatarImageView.setImage(newPhoto, "50_50", avatarDrawable);
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
          //  object.scale = ViewProxy.getScaleX(avatarImageView);
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

    /*private String showDate(int year, int month, int day) {
        return (new StringBuilder().append(year+"").append("-")
                .append(Util.getMonth(month)).append("-").append(day+"".length()==1?"0"+day:day+"")).toString();
    }*/

    private String getDatetoserver(int year, int month, int day) {
        return (new StringBuilder().append(year+"").append("-")
                .append(month+"".length()==1?"0"+month:month+"").append("-").append(day+"".length()==1?"0"+day:day+"")).toString();
    }
    private void showDialogForSelection(final String[] items) {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mcContext);
        alt_bld.setTitle(mcContext.getResources().getString(R.string.gender));
        alt_bld.setSingleChoiceItems(items, selectSex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                selectedgender = item;
                selectSex =item;
                if(fragmentView !=null){
                    ApplicationLoader.applicationContext.
                            getSharedPreferences("socialuser", Activity.MODE_PRIVATE).edit().putString("sex",getGender()).commit();
                    gender_txt.setText(items[selectedgender]);
                    dialog.dismiss();
                }

            }
        });
   /*     alt_bld.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(fragmentView !=null){
                    ApplicationLoader.applicationContext.
                            getSharedPreferences("socialuser", Activity.MODE_PRIVATE).edit().putString("sex",getGender()).commit();
                    gender_txt.setText(items[selectedgender]);
                }

            }
        });*/
        AlertDialog alert = alt_bld.create();
        alert.show();
        alert.setCancelable(false);
    }

    private String getGender(){
        return selectedgender==0?"M":"F";
    }
    public CountryAdapter.Country getcountry(String name){
        CountryAdapter k = new CountryAdapter(mcContext);
        HashMap<String, ArrayList<CountryAdapter.Country>> countries = k.getCountries();
//        ArrayList<String> sortedCountries = k.getSortedCountries();
        String s = name.substring(0,1).toUpperCase();
        ArrayList<CountryAdapter.Country> dd = countries.get(s);
        for(CountryAdapter.Country selectcountry:dd){
            if(selectcountry.name.equalsIgnoreCase(name)){
                return selectcountry;
            }
        }
        return null;
    }

    private String getGender(String g){
        if(!g.equalsIgnoreCase("")) {
            return g.equalsIgnoreCase("m")?"Male":"Female";
        }
        return "Gender";
    }

    private boolean dateValidater(int yr,int mn,int dt){
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(yr, mn, dt);
            long startDate = calendar.getTimeInMillis();
            if(startDate > Calendar.getInstance().getTimeInMillis()){
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    void setContact(){
        ArrayList<TLRPC.TelegramUsers> telegramUserses = new ArrayList<>();
        SharedPreferences pp = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
        if(!pp.getBoolean("datasend",false)) {
            HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = ContactsController.getInstance().usersSectionsDict;
            ArrayList<ContactsController.Contact> contactses = ContactsController.getInstance().phoneBookContacts;

            ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance().sortedUsersSectionsArray;
            final ArrayList<TLRPC.User> userArrayList = new ArrayList<>();
            for (String index : sortedUsersSectionsArray) {
                for (TLRPC.TL_contact con : usersSectionsDict.get(index)) {
                    userArrayList.add(MessagesController.getInstance().getUser(con.user_id));
                    TLRPC.TelegramUsers tUser = new TLRPC.TelegramUsers();
                    TLRPC.User usertemp = MessagesController.getInstance().getUser(con.user_id);
                    tUser.setPhoto(usertemp.photo);
                    tUser.setPhone(usertemp.phone);
                    tUser.setUsername(usertemp.username);
                    tUser.setname((usertemp.first_name!=null?usertemp.first_name:"") + " " + (usertemp.last_name!=null?usertemp.last_name:""));
                    tUser.setId(usertemp.id + "");
                    telegramUserses.add(tUser);
                }
            }

            for(int i=0 ; i< contactses.size(); i++){
                TLRPC.TelegramUsers  tUser = new TLRPC.TelegramUsers();
                tUser.setname(contactses.get(i).first_name +" " +(contactses.get(i).last_name!=null?contactses.get(i).last_name:""));
                tUser.setPhone(Util.getMobileNumber(contactses.get(i).phones.get(0)));
                tUser.setId(null);
                tUser.setPhoto(null);
                tUser.setUsername(null);
                telegramUserses.add(tUser);
            }
            ArrayList<CustomHttpParams> params = new ArrayList<>();
            params.add(new CustomHttpParams("userId", pp.getString("social_id", "")));
            BackgroundExecuter.getInstance().execute(new AddContactRequester(telegramUserses, params));
        }

    }






    @Override
    public void setUserAddSuccess() {
        getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getParentActivity(),"Profile Saved !!",Toast.LENGTH_LONG).show();
                removeUIListeners();

            }
        });
    }

    @Override
    public void setUserAddFailed() {

        getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getParentActivity(),"Error in save profile !",Toast.LENGTH_LONG).show();

            }
        });

        //   removeUIListeners();
    }




    public void removeUIListeners(){
        ApplicationLoader.getInstance().removeUIListener(OnAddUserListner.class,MyProfileActivity.this);
        finishFragment();

    }

}
