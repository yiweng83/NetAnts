package com.MY.netTools;

import com.MY.pingtest.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 setContentView(R.layout.welcome);
		 new Handler().postDelayed(r, 3000);// 3秒后关闭，并跳转到主页面
	}
	
	Runnable r = new Runnable() {
		@Override
		public void run() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(WelcomeActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
		}
		};
}
