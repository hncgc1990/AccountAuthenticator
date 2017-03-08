package com.udinic.accounts_authenticator_example.authentication;

/**
 * Handles the comminication with Parse.com
 *
 * User: udinic
 * Date: 3/27/13
 * Time: 3:30 AM
 * 登录和注册的具体实现(根据自己需求进行实现)
 */
public class ParseComServerAuthenticate implements ServerAuthenticate{
    @Override
    public String userSignUp(String name, String email, String pass, String authType) throws Exception {


        //根据用户名、邮箱和密码进行注册，并返回一个授权的token

        String authtoken="";

        return authtoken;
    }

    @Override
    public String userSignIn(String user, String pass, String authType) throws Exception {


        //根据用户名和密码进行登录,并返回一个登录授权的token

        String authtoken="";

        return authtoken;
    }


}
