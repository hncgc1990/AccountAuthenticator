package com.udinic.accounts_example;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.udinic.accounts_authenticator_example.authentication.AccountGeneral;

import static com.udinic.accounts_authenticator_example.authentication.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

/**
 * Created with IntelliJ IDEA.
 * User: Udini
 * Date: 21/03/13
 * Time: 13:50
 */
public class Main1 extends Activity {
	
	private static final String STATE_DIALOG = "state_dialog";
	private static final String STATE_INVALIDATE = "state_invalidate";

    private String TAG = this.getClass().getSimpleName();
    private AccountManager mAccountManager;
    private AlertDialog mAlertDialog;
    private boolean mInvalidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        mAccountManager = AccountManager.get(this);

        findViewById(R.id.btnAddAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //当前没有用户,需要新增用户才能进行登录

                addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);





            }
        });

        findViewById(R.id.btnGetAuthToken).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取当前用户类型的所有已授权用户
                showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, false);
            }
        });

        findViewById(R.id.btnGetAuthTokenConvenient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            }
        });
        findViewById(R.id.btnInvalidateAuthToken).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, true);
            }
        });

        findViewById(R.id.clearPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Account[] accounts = mAccountManager.getAccounts();
                for (int i=0;i<accounts.length;i++){
                    Log.d("chen",accounts[i].toString());
                    mAccountManager.clearPassword(accounts[i]);
                }

            }
        });


        
        if (savedInstanceState != null) {
        	boolean showDialog = savedInstanceState.getBoolean(STATE_DIALOG);
        	boolean invalidate = savedInstanceState.getBoolean(STATE_INVALIDATE);
        	if (showDialog) {
        		showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, invalidate);
        	}
        }


//        mAccountManager.addOnAccountsUpdatedListener(new OnAccountsUpdateListener() {
//            @Override
//            public void onAccountsUpdated(Account[] accounts) {
//
//                    for (int i=0;i<accounts.length;i++){
//                        Log.d("chen",accounts[i].toString());
//                    }
//                Log.d("chen","OnAccountsUpdateListener的线程"+Thread.currentThread().getName());
//
//            }
//        },new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
////                super.handleMessage(msg);
//
//                Log.d("chen","当前的handler的线程"+Thread.currentThread().getName());
//
//            }
//        },true);

    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	if (mAlertDialog != null && mAlertDialog.isShowing()) {
    		outState.putBoolean(STATE_DIALOG, true);
    		outState.putBoolean(STATE_INVALIDATE, mInvalidate);
    	}
    }

    /**
     * Add new account to the account manager
     * @param accountType
     * @param authTokenType
     */
    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                //新增用户之后的后续操作
                try {
                    Bundle bnd = future.getResult();
                    showMessage("Account was created");
                    Log.d("udinic", "AddNewAccount Bundle is " + bnd);
                    Log.d("udinic", "当前线程是 " + Thread.currentThread().getName());//此处是主线程
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }, null);
    }

    /**
     * Show all the accounts registered on the account manager. Request an auth token upon user select.
     * @param authTokenType
     */
    private void showAccountPicker(final String authTokenType, final boolean invalidate) {
    	mInvalidate = invalidate;
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);//获取指定类型的所有用户

        if (availableAccounts.length == 0) {
            Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
        } else {
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;
            }

            // Account picker
            mAlertDialog = new AlertDialog.Builder(this).setTitle("Pick Account").setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(invalidate)
                        invalidateAuthToken(availableAccounts[which], authTokenType);
                    else
                        getExistingAccountAuthToken(availableAccounts[which], authTokenType);
                }
            }).create();
            mAlertDialog.show();
        }
    }

    /**
     * Get the auth token for an existing account on the AccountManager
     * @param account
     * @param authTokenType
     */
    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();//会阻塞线程

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    showMessage((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL");
                    Log.d("udinic", "GetToken Bundle is " + bnd);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Invalidates the auth token for the account
     * @param account
     * @param authTokenType
     */
    private void invalidateAuthToken(final Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null,null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    mAccountManager.invalidateAuthToken(account.type, authtoken);//清空AccountManager中指定的authtoken缓存
                    showMessage(account.name + " invalidated");
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Get an auth token for the account.
     * If not exist - add it and then return its auth token.
     * If one exist - return its auth token.
     * If more than one exists - show a picker and return the select account's auth token.
     * @param accountType
     * @param authTokenType
     */
    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();
                            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                            showMessage(((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL"));
                            Log.d("udinic", "GetTokenForAccount Bundle is " + bnd);

                        } catch (Exception e) {
                            e.printStackTrace();
                            showMessage(e.getMessage());
                        }
                    }
                }
        , null);
    }

    private void showMessage(final String msg) {
    	if (TextUtils.isEmpty(msg))
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
