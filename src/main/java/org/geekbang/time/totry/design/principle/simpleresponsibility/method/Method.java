package org.geekbang.time.totry.design.principle.simpleresponsibility.method;


public class Method {

    private void modifyUserInfo(String userName,String address){
        userName = "Tom";
        address = "Changsha";
    }

    private void modifyUserInfo(String userName,String ... fileds){

    }

    private void modifyUserInfo(String userName,String address,boolean bool){
        if(bool){

        }else{

        }
    }

    private void modifyUserName(String userName){

    }

    private void modifyAddress(String address){

    }
}
