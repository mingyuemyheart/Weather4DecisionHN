package com.pmsc.weather4decision.phone.hainan.act;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.pmsc.weather4decision.phone.hainan.R;


/**
 * Depiction: 登录界面
 * <p>
 * Modify:
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2015年11月13日 下午4:33:38
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class LoginActivity extends AbsLoginActivity {
	private EditText accountView;
	private EditText passwdView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		accountView = (EditText) findViewById(R.id.account_view);
		passwdView = (EditText) findViewById(R.id.passwd_view);
	}
	
	public void onLoginAction(View v) {
		String account = accountView.getText().toString();
		String passwd = passwdView.getText().toString();
		if (TextUtils.isEmpty(account)) {
			showToast(R.string.input_account_tip);
			return;
		}
		
		if (TextUtils.isEmpty(passwd)) {
			showToast(R.string.input_passwd_tip);
			return;
		}
		
		login(account, passwd);
	}
	
}
