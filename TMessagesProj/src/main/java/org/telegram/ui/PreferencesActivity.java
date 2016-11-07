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
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
//import org.telegram.rangeseekbar.RangeSeekBar;
import org.telegram.rangeseekbar.RangeSeekBar;
import org.telegram.socialuser.BackgroundExecuter;
import org.telegram.socialuser.Util;
import org.telegram.socialuser.model.CustomHttpParams;
import org.telegram.socialuser.runable.GetPreferenceRq;
import org.telegram.socialuser.runable.PreferencesRequester;
//import org.telegram.tracker.AnalyticsTrackers;
import org.telegram.tracker.AnalyticsTrackers;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.CountryAdapter;
import org.telegram.ui.listners.OnGetpreferences;
import org.telegram.ui.listners.OnPreferencesListner;
import org.telegram.ui.listners.PreferencesListner;

import java.util.ArrayList;
import java.util.HashMap;

public class PreferencesActivity extends BaseFragment implements OnPreferencesListner,OnGetpreferences {



    @Override
    public void setPreferencesSuccess() {
        getParentActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Bundle args2 = new Bundle();
                        args2.putString("s_friend","wink");
                        presentFragment(new SocialFriendActivity(args2));

                    }
                }
        );
    }

    @Override
    public void setPreferencesFailed() {
        getParentActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getParentActivity(),"Please try again",Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }

    public static class PreferenceData{

        private String cCode;
        private String sex;
        private String minage;
        private String maxage;


        public String getcCode() {
            return cCode;
        }

        public void setcCode(String cCode) {
            this.cCode = cCode;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getMinage() {
            return minage;
        }

        public void setMinage(String minage) {
            this.minage = minage;
        }

        public String getMaxage() {
            return maxage;
        }

        public void setMaxage(String maxage) {
            this.maxage = maxage;
        }
    }
    private  TextView txt_Age;
    private TextView txtGender;
    private Context mContext;
    private String cName;
    private TextView txt_country;
    private LinearLayout location_layout;
    private LinearLayout gender_layout;
    private LinearLayout age_layout;
    private Button button;
    private int selectedgender;
    private static PreferencesListner launcherActivity;
    private TextView min1;
    private TextView max1;
    int minvalue1,maxvalue1;
    private static int  sexselected;
    private boolean isAgeGroupChange;
    private RangeSeekBar<Integer> multiSlider1;
    ProgressBar progressBar;
    //private RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(15, 60, mContext);
    private ArrayList<CountryAdapter.Country> contrys = new ArrayList<>();


    @Override
    public View createView(Context context) {

        hideTabsAnsMenu();
        mContext = context;
        //actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackButtonImage(0x00000000);//todo
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("preferences", R.string.preferences));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = View.inflate(context, R.layout.preferences_layout, null);
        txt_country =(TextView) fragmentView.findViewById(R.id.id_location_txt);
        progressBar = (ProgressBar)fragmentView.findViewById(R.id.pb_load);
        initRangeBar();
        min1 = (TextView) fragmentView.findViewById(R.id.minValue1);
        max1 = (TextView) fragmentView.findViewById(R.id.maxValue1);
        txtGender =(TextView) fragmentView.findViewById(R.id.gender_txt);
        location_layout = (LinearLayout)fragmentView.findViewById(R.id.location_layout);
        gender_layout = (LinearLayout)fragmentView.findViewById(R.id.gender_layout) ;
        button = (Button)fragmentView.findViewById(R.id.id_btn_preferences);
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        txtGender.setText(preferences.getString("sexPreferences","").equals("M")?"Male":"Female");
        txt_country.setText(preferences.getString("preferenceCountry","India"));

        cName = preferences.getString("preferenceCountry","India");
        min1.setText(preferences.getString("minage",18+""));
        max1.setText(preferences.getString("maxage",30+""));
        ArrayList<CustomHttpParams> params = new ArrayList<>();
        params.add(new CustomHttpParams("userId",ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE).getString("social_id","")));
        BackgroundExecuter.getInstance().execute(new GetPreferenceRq(params,PreferencesActivity.this));
        fragmentView.findViewById(R.id.black_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        gender_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForSelection(new String[]{"Male","Female"});
            }
        });
        location_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CountrySelectActivity fragment = new CountrySelectActivity();
                fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                    @Override
                    public void didSelectCountry(String name) {
                        cName = name;
                        ApplicationLoader.applicationContext.getSharedPreferences("preferences", Activity.MODE_PRIVATE).edit().putString("preferenceCountry",cName).commit();
                        txt_country.setText(name);
                    }
                });
                presentFragment(fragment);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                PreferenceData u = new PreferenceData();
                if(cName==null){
                    Toast.makeText(mContext,LocaleController.getString("pleaseselectcountry", R.string.pleaseselectcountry),Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
                    CountryAdapter.Country c = getcountrybyName(p.getString("preferenceCountry",cName));
                    u.setcCode(c!=null?c.shortname:"IN");
                    if(isAgeGroupChange) {
                        u.setMinage(minvalue1+"");
                        u.setMaxage(maxvalue1+"");
                    } else {
                        u.setMinage(p.getString("minage",minvalue1+""));
                        u.setMaxage(p.getString("maxage",maxvalue1+""));
                    }
                    u.setSex(p.getString("sexPreferences",getGender()));
                }
                SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
                ArrayList<CustomHttpParams> params = new ArrayList<>();
                params.add(new CustomHttpParams("userId",p.getString("social_id","")));
                BackgroundExecuter.getInstance().execute(new PreferencesRequester(u,params,PreferencesActivity.this));
                //finishFragment();

            }
        });
        fragmentView.findViewById(R.id.backview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTabsAndmenu();
                finishFragment();
            }
        });

        return fragmentView;
    }
    @Override
    public void onResume() {
        super.onResume();
        ApplicationLoader.getInstance().trackScreenView(AnalyticsTrackers.CHANGE_PHONE_HELP);
    }



    private void showDialogForSelection(final String[] items) {
        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext);
        alt_bld.setTitle(mContext.getResources().getString(R.string.gender));
        alt_bld.setSingleChoiceItems(items, sexselected, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                selectedgender = item;
                sexselected =item;
                if(fragmentView !=null){
                    txtGender.setText(items[selectedgender]);
                    SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
                    p.edit().putString("sexPreferences",getGender()).commit();
                }
                dialog.dismiss();

            }
        });

        AlertDialog alert = alt_bld.create();
        alert.show();


    }

    public CountryAdapter.Country getcountrybyName(String name){
        CountryAdapter k = new CountryAdapter(mContext);
        HashMap<String, ArrayList<CountryAdapter.Country>> countries = k.getCountries();
    //    ArrayList<String> sortedCountries = k.getSortedCountries();
        String s = name.substring(0,1).toUpperCase();
        ArrayList<CountryAdapter.Country> dd = countries.get(s);
        for(CountryAdapter.Country selectcountry:dd){
            if(selectcountry.name.equalsIgnoreCase(name)){
                return selectcountry;
            }
        }
        return null;
    }

    @Override
    public void onGetpreferencessuccess() {
        getParentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
                txtGender.setText(preferences.getString("sexPreferences","").equals("M")?"Male":"Female");
                String cName = Util.getCountryName(preferences.getString("preferencecCode","IN"));
                txt_country.setText(cName);
                preferences.edit().putString("preferenceCountry",cName);
                min1.setText(preferences.getString("minage",18+""));
                max1.setText(preferences.getString("maxage",30+""));
                //initRangeBar();
                minvalue1 = Integer.parseInt(preferences.getString("minage",18+""));//settings.getAgeMin();
                maxvalue1 = Integer.parseInt(preferences.getString("maxage",30+""));//settings.getAgeMax();
                //RangeSeekBar<Integer> seekBar = (RangeSeekBar<Integer>)fragmentView.findViewById(R.id.id_age_range);
                if(multiSlider1!=null) {
                    multiSlider1.setSelectedMinValue(minvalue1);
                    multiSlider1.setSelectedMaxValue(maxvalue1);
                }
            }
        });

    }
    private String getGender(){
        return selectedgender==0?"M":"F";
    }

    public static void setLauncherInstanse(PreferencesListner launcherInstanse){
        launcherActivity = launcherInstanse;
    }

    private int previousThumb;
    private void initRangeBar() {
        final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(15, 60, mContext);

        multiSlider1 =seekBar;
        minvalue1 = Integer.parseInt(preferences.getString("minage",18+""));//settings.getAgeMin();
        maxvalue1 = Integer.parseInt(preferences.getString("maxage",30+""));//settings.getAgeMax();
        seekBar.setSelectedMinValue(minvalue1);
        seekBar.setSelectedMaxValue(maxvalue1);

        //seekBar.set

        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                if ((maxValue - minValue) < 4) {
                    if(bar.getPressedThumb() == -1) {
                        minValue = maxValue - 4;
                        bar.setSelectedMinValue();
                        previousThumb = -1;
                    } else if(bar.getPressedThumb() == 1) {
                        maxValue = minValue + 4;
                        if(maxValue>58)
                            maxValue=58;
                        bar.setSelectedMaxValue();
                        previousThumb = 1;

                    } else {
                        if(previousThumb == -1) {
                            minValue = maxValue - 4;
                            bar.setSelectedMinValue();
                        } else {
                            maxValue = minValue + 4;
                            bar.setSelectedMaxValue();
                        }
                    }
                }
                isAgeGroupChange = true;
                minvalue1 = minValue;
                maxvalue1 =maxValue;
                min1.setText(minValue+"");
                max1.setText(maxValue+"");
            }
        });

        ((LinearLayout) fragmentView.findViewById(R.id.id_age_range)).addView(seekBar);
    }

}
