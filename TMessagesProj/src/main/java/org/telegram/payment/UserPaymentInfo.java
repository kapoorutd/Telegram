package org.telegram.payment;

/**
 * Created by craterzone3 on 14/6/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.paypal.android.sdk.payments.PayPalConfiguration;

import org.telegram.messenger.ApplicationLoader;

/**
 *     This class will store all the
 *     information about  user
 *     paid , unpaid or his paid
 *     verification is in pending
 *     from server.
 *
 */


    public class UserPaymentInfo {



    public static final int paidUser        = 1 ;
    public static final int unPaidUser      = 2 ;
    public static final int pendingUser     = 3 ;

    public static String videoAds="";
    public static String videoCall="";
    public static String voiceCall="";
    public static String liveStreaming="";
    public static String socialFriend="";
     public static final int REQUEST_CODE_PAYMENT = 1;
     static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
     static final int REQUEST_CODE_PROFILE_SHARING = 3;


    private static UserPaymentInfo _paymentInfo ;

    public static UserPaymentInfo getInstatance() {
        if (_paymentInfo == null) {
            _paymentInfo = new UserPaymentInfo();
            return _paymentInfo;
        }
    return _paymentInfo;

    }


    SharedPreferences
 preferences = ApplicationLoader.applicationContext.getSharedPreferences("userpaymentinfo", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();

    public void setPaymentId(String paymentId){
      editor.putString("payment_id",paymentId).commit();
    }

    public void setPaymentStatus(int paymentStatus){
        editor.putInt("payment_status",paymentStatus).commit();
    }

    //  pushString = preferences.getString("pushString", "");

    public String getPaymentId(){
       return preferences.getString("payment_id","");
    }

    public int getPaymentStatus(){
        return preferences.getInt("payment_status",0);

    }

     public String getUserId(){
         return  ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE)
              .getString("social_id","");

}


    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
    private static final String CONFIG_CLIENT_ID = "ARafipSb7SknBnBKsQY1Na9CvKZPypeHORyOQ-f-1sEAfwt0NEymuOniy4__9oIJ1JYEleESrqSkESGB";

    public static PayPalConfiguration getConfiguration(){

        return  new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(CONFIG_CLIENT_ID)
                // The following are only used in PayPalFuturePaymentActivity.
                .merchantName("CRATERZONE PVT LTD").acceptCreditCards(true);
    }




}
