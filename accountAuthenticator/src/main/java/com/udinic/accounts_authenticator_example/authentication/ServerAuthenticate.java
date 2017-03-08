package com.udinic.accounts_authenticator_example.authentication;

/**
 * User: udinic
 * Date: 3/27/13
 * Time: 2:35 AM
 * 授权相关操作(登录和注册)
 *
 */
public interface ServerAuthenticate {
    /**
     * 注册
     * @param name
     * @param email
     * @param pass
     * @param authType
     * @return
     * @throws Exception
     */
    public String userSignUp(final String name, final String email, final String pass, String authType) throws Exception;

    /**
     * 登录
     * @param user
     * @param pass
     * @param authType
     * @return
     * @throws Exception
     */
    public String userSignIn(final String user, final String pass, String authType) throws Exception;
}
