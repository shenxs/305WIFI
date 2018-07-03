package com.example.richard.a305wifi;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int MSG_RECEIVE_CODE = 1; //收到短信的验证码
    public static final String phoneNumber = "106593005";
    private MessageObserver messageObserver;
    Button dail_bt;
    Button get_pass_bt;
    String account = "";
    String sxpass = "";
    String router_address = "";
    String router_pass = "";
    EditText et_account;
    EditText et_sxpass;
    EditText et_router_address;
    EditText et_router_pass;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_RECEIVE_CODE) {
                //设置读取到的内容
                Toast.makeText(getApplicationContext(), "闪讯密码成功获取".toString(), Toast.LENGTH_SHORT).show();
                et_sxpass.setText(msg.obj.toString());
            }
        }
    };

    private void Do_Dail(View v) {
        //拨号需要做的事
        //Toast.makeText(getApplicationContext(),"拨号",Toast.LENGTH_SHORT).show();
        account = ((EditText) findViewById(R.id.editText_account)).getText().toString();
        sxpass = ((EditText) findViewById(R.id.editText_sxpass)).getText().toString();
        router_pass = ((EditText) findViewById(R.id.editText_router_pass)).getText().toString();
        router_address = ((EditText) findViewById(R.id.editText_router_address)).getText().toString();
        AsynTaskDoGet dail = new AsynTaskDoGet(account, sxpass, router_address, router_pass, getApplicationContext());

        dail.execute(new Object());

    }

    @TargetApi(23)
    private void Do_Get_Pass(View v) {

        Toast.makeText(getApplicationContext(), "获取密码", Toast.LENGTH_SHORT).show();
        SubscriptionManager subscriptionManager = SubscriptionManager.from(this);
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        String mPhoneNumber = tMgr.getLine1Number();
        Log.v("电话", mPhoneNumber);

        SmsManager sms = null;
        for (SubscriptionInfo e : subscriptionInfoList) {
            Log.v("双卡", e.toString());
            if (e.getCarrierName().toString().equals("中国电信") ||
                    e.getCarrierName().toString().equals("China Telecom")) {
                sms = SmsManager.getSmsManagerForSubscriptionId(e.getSubscriptionId());
            }
        }
        if (sms == null) {
            Toast.makeText(getApplicationContext(), "未找到电信手机卡", Toast.LENGTH_SHORT).show();

        } else {
            sms.sendTextMessage(phoneNumber, null, "mm", null, null);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        dail_bt = (Button) findViewById(R.id.dail_button);
        get_pass_bt = (Button) findViewById(R.id.get_sx_pass);
        dail_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Do_Dail(v);
            }
        });
        get_pass_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Do_Get_Pass(v);
            }
        });

        et_account = (EditText) findViewById(R.id.editText_account);
        et_sxpass = (EditText) findViewById(R.id.editText_sxpass);
        et_router_address = (EditText) findViewById(R.id.editText_router_address);
        et_router_pass = (EditText) findViewById(R.id.editText_router_pass);

        SharedPreferences settings = this.getPreferences(MODE_PRIVATE);

        account = settings.getString(getString(R.string.account), "17816879774@GDPF.XY");
        sxpass = settings.getString(getString(R.string.sxpass), "12345");
        router_address = settings.getString(getString(R.string.router_address), "192.168.1.1");
        router_pass = settings.getString(getString(R.string.router_pass), "123456");

        et_account.setText(account);
        et_sxpass.setText(sxpass);
        et_router_address.setText(router_address);
        et_router_pass.setText(router_pass);

        messageObserver = new MessageObserver(this, handler);
        getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, messageObserver);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(getString(R.string.account), et_account.getText().toString());
        editor.putString(getString(R.string.sxpass), et_sxpass.getText().toString());
        editor.putString(getString(R.string.router_address), et_router_address.getText().toString());
        editor.putString(getString(R.string.router_pass), et_router_pass.getText().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(messageObserver);
    }
}
