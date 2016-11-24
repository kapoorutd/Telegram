package org.telegram.socialuser.model;

/**
 * Created by craterzone3 on 23/11/16.
 */

public class CreditModel  {

    private  String source;
    private  String phone;
    private  String cc;

    public CreditModel(String source,String phone,String cc){

        this.cc=cc;
        this.phone=phone;
        this.source=source;
    }


}
