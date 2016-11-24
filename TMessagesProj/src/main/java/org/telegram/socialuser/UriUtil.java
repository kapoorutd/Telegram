package org.telegram.socialuser;

import org.telegram.messenger.ApplicationLoader;

/**
 * Created by ram on 20/2/16.
 */
public class UriUtil {
    //http://107.170.73.149:8080/socialshareupapi/user
    private static String getHTTPRootPath() {
        return new StringBuilder()
                .append("http://")
                .append(ApplicationLoader.getInstance().getHttpServer())
                .append("user").toString();
    }

    public static String getHttpUrl(String uid) {
        return new StringBuilder()
                .append(getHTTPRootPath())
                .append("/").append(uid).toString();
    }
    public static String getHttpUrl(String uid,int count) {
        return new StringBuilder()
                .append(getHTTPRootPath())
                .append("/").append(uid).append("?count=").append(count).toString();
    }

//http://{{host}}/socialshareupapi/user/premiumuser

    public static String getPaymentUrl() {
        return new StringBuilder().append(getHTTPRootPath()).append("/").append("premiumuser").toString();

    }

    public static String getKarmaBalanceUrl(String mobileNo){
        // return  /socialshareupapi/user/   getCredit?mobile=9990243020";

        return new StringBuilder().append("http://192.168.2.250:8080/socialshareupapi/user"/*getHTTPRootPath()*/).append("/").append("getCredit").toString();

    }

//    http://192.168.1.34:8080/socialshareupapi/user/
    // Credit url

     public static String getCreditUrl(){
         http://{{host}}/socialshareupapi/user/     credit?source=VIDEO_ADS&mobile=9990243020

         return  new StringBuilder().append("http://192.168.2.250:8080/socialshareupapi/user"/*getHTTPRootPath()*/).append("/").append("credit").toString();

     }

// Debit url

    public static String getDebitUrl(){
        http://{{host}}/socialshareupapi/user/debit?source=VIDEO_WATCH&mobile=9990243020

        return  new StringBuilder().append("http://192.168.2.250:8080/socialshareupapi/user"/*getHTTPRootPath()*/).append("/").append("debit").toString();

    }



}
