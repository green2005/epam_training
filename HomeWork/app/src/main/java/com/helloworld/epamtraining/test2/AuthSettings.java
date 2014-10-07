package com.helloworld.epamtraining.test2;

public class AuthSettings {
    private static String userName;
    private static String pwd;
    AuthSettings(){

    }

    public static String getUserName(){
        return userName;
    }

    public static  void setUserName(String sUserName){
        userName=sUserName;
    }

    public  static  String getPwd(){
        return pwd;
    }

    public static void setPwd(String sPwd){
        pwd=sPwd;
    }
 }
