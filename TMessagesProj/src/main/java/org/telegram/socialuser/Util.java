package org.telegram.socialuser;

import android.app.Activity;
import android.content.SharedPreferences;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Adapters.CountryAdapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ram on 13/6/16.
 */
public class Util {
    public static String getDate(String val){

        if(val!=null &&!val.equalsIgnoreCase("")) {
            Long value = Long.parseLong(val);
            Date date=new Date(value);
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MMM-dd");
            String dateText = df2.format(date);
            return dateText;
        }
        return "2001-jun-20";
    }
    public static String getNumber(String code) {
        SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
        String pcode =p.getString("pCode","911");
        String cCode =p.getString("cCode","AU");
        //int index = code.indexOf(pcode);
        code = code.substring(pcode.length(),code.length());
        return cCode+"_"+code;
    }

    public static String getDateforserver(int yr,int mt,int day){
       Calendar cc =  Calendar.getInstance();
        cc.set(yr,mt,day);
        Long value =cc.getTimeInMillis();
        Date date=new Date(value);
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MMM-dd");
        String dateText = df2.format(date);
        return dateText;
    }

    public static String getMonth(int no){
        switch (no){

            case 0: return "Jan";
            case 1:return "Feb";
            case 2 : return "Mar";
            case 3 : return  "Apr";
            case 4: return "May";
            case 5: return "Jun";
            case 6 : return "Jul";
            case 7: return "Aug";
            case 8:return "Sep";
            case 9 : return "Oct";
            case 10 : return  "Nov";
            case 11: return "Dec";
            default:return "Mon";
        }
    }

    public static String getCountryName(String shortname) {
        ArrayList<CountryAdapter.Country> arr = new ArrayList<>();
        try {

            InputStream stream = ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] args = line.split(";");
                CountryAdapter.Country c = new CountryAdapter.Country();
                c.name = args[2];
                c.code = args[0];
                c.shortname = args[1];
                arr.add(c);
            }
            reader.close();
            stream.close();
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }

        for(CountryAdapter.Country cc:arr){
            if(shortname.equalsIgnoreCase(cc.shortname)){
                return cc.name;
            }
        }
   return "India";
    }

    public static CountryAdapter.Country getcountry(String name){
        ArrayList<CountryAdapter.Country> arr = new ArrayList<>();
        try {

            InputStream stream = ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] args = line.split(";");
                CountryAdapter.Country c = new CountryAdapter.Country();
                c.name = args[2];
                c.code = args[0];
                c.shortname = args[1];
                arr.add(c);
            }
            reader.close();
            stream.close();
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }

        for(CountryAdapter.Country cc:arr){
            if(name.equalsIgnoreCase(cc.name)){
                return cc;
            }
        }
        return null;
    }

    public static String getMobileNumber(String number) {
        if (number==null){
            return number;
        }
        if(number.startsWith("0")){
            number = number.substring(1);
            SharedPreferences p = ApplicationLoader.applicationContext.getSharedPreferences("socialuser", Activity.MODE_PRIVATE);
            String pcode =p.getString("pCode","911");
            number = pcode+number;
            return number;
        } else{
            return number;
        }
    }

    public static String convertDate(String input) {
       try{
           String date[] = input.split("-");
           return date[0]+"-"+getMonthvalue(date[1])+"-"+date[2];
       } catch (Exception e){
           return "2001-jun-20";
       }
    }
        private static String getMonthvalue(String month) {
            switch(month) {
                case "Jan": return "01";
                case "Feb":return "02";
                case "Mar" : return "03";
                case  "Apr" : return  "04";
                case "May": return "05";
                case "Jun": return "06";
                case "Jul" : return "07";
                case "Aug": return "08";
                case "Sep":return "09";
                case "Oct" : return "10";
                case "Nov" : return  "11";
                case "Dec": return "12";
                default:return "00";
            }
        }

    private String getDatetoserver(int year, int month, int day) {
        return (new StringBuilder().append(year+"").append("-")
                .append(month+"".length()==1?"0"+month:month+"").append("-").append(day+"".length()==1?"0"+day:day+"")).toString();
    }

}
