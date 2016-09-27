package org.telegram.payment;

import android.content.Context;

import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.CustomHttpParams;

import java.util.ArrayList;




public class CheckPremiumUserRequester implements Runnable {

    private  CheckPremiumListener mListener;
    private PaymentConfirmationListener listener;
    private ArrayList<CustomHttpParams> params =new ArrayList<>();
    String userId;
    private  Context mContext ;




    public CheckPremiumUserRequester( String userId){
        this.userId=userId ;
       params.add(new CustomHttpParams("userId",userId));

    }


    public CheckPremiumUserRequester( String userId,CheckPremiumListener listener){
        mListener=listener ;
        params.add(new CustomHttpParams("userId",userId));
        this.userId=userId ;
    }
    @Override
    public void run() {

    if( userId != ""){

        CZResponse data1 = HttpUrlConnectionUtil.get(UriUtil.getPaymentUrl(),"application/json",params);
        if(data1.getResponseCode() == 200) {
            try {
             if (data1.getResponseString().contains("true")){
                 if(mListener!=null){
                 mListener.onPremiumUser();
                 }

                UserPaymentInfo.getInstatance().setPaymentStatus(UserPaymentInfo.paidUser);
             }
                else{ UserPaymentInfo.getInstatance().setPaymentStatus(UserPaymentInfo.unPaidUser);
                 if(mListener!=null){
                 mListener.onGeneralUser();
                 }
             }
            } catch (Exception e) {

            }
        }
        else {
            UserPaymentInfo.getInstatance().setPaymentStatus(UserPaymentInfo.unPaidUser);
        }


    }


}

}







