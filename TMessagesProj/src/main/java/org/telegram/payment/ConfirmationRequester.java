package org.telegram.payment;

import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.CustomHttpParams;

import java.util.ArrayList;


public class ConfirmationRequester implements Runnable {

    private PaymentConfirmationListener listener;
    private ArrayList<CustomHttpParams> params =new ArrayList<>();


    public ConfirmationRequester(String paymentId, String userId ,String amount,String mobNo, String cCode)
    {


        params.add(new CustomHttpParams("paymentId",paymentId));
        params.add(new CustomHttpParams("userId",userId));
        params.add(new CustomHttpParams("transAmount",amount));
        params.add(new CustomHttpParams("mobileNo",mobNo));
        params.add(new CustomHttpParams("cc",cCode));



    }
    @Override
    public void run() {

        //post(String urlString, String body, String contentType, String acceptType, ArrayList<CustomHttpParams> httpParams
        CZResponse data1 = HttpUrlConnectionUtil.post(UriUtil.getPaymentUrl(),null,"application/json","application/json",params);
        if(data1.getResponseCode() == 200) {
            try {
                UserPaymentInfo.getInstatance().setPaymentStatus(UserPaymentInfo.paidUser);
            } catch (Exception e) {

            }
        }
  else {
                UserPaymentInfo.getInstatance().setPaymentStatus(UserPaymentInfo.unPaidUser);
                }
            }



    }






