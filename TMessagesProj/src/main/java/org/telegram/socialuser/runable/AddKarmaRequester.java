package org.telegram.socialuser.runable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.telegram.socialuser.HttpUrlConnectionUtil;
import org.telegram.socialuser.UriUtil;
import org.telegram.socialuser.model.CZResponse;
import org.telegram.socialuser.model.CreditModel;
import org.telegram.socialuser.model.CustomHttpParams;

import java.util.ArrayList;

/**
 * Created by craterzone3 on 23/11/16.
 */

public class AddKarmaRequester implements Runnable {
    private CreditModel model;
    private ArrayList<CustomHttpParams> params =new ArrayList<>();

    public AddKarmaRequester(CreditModel model){
        this.model=model;
    }


    @Override
    public void run() {
        if (model != null) {
            try {
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(model);
                CZResponse data1 = HttpUrlConnectionUtil.post(UriUtil.getCreditUrl(), json, "application/json", "application/json", params);

                if(data1.getResponseCode()==200){

                    String k="gdtdugdhh;";
                }


            }



            catch (Exception e) {

            }


        }


    }



}
