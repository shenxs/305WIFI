package com.example.richard.a305wifi;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by richard on 17-3-21.
 */

public class MessageObserver extends ContentObserver {

    private Context mContext;
    private Handler mHandler;
    private String code;

    public MessageObserver(Context context, Handler handler){
        super(handler);
        mContext=context;
        mHandler=handler;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }
        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor c = mContext.getContentResolver().query(inboxUri,  null, null, null, "date desc");  // 按时间顺序排序短信数据库
        if (c != null) {
            if (c.moveToFirst()) {
                String address = c.getString(c.getColumnIndex("address"));//发送方号码
                String body = c.getString(c.getColumnIndex("body")); // 短信内容
                if (!address.equals(MainActivity.phoneNumber)) {
                    return;
                }
                Pattern pattern = Pattern.compile("(\\d{6})");//正则表达式匹配验证码
                Matcher matcher = pattern.matcher(body);
                if (matcher.find()) {
                    code = matcher.group(0);
                    Message msg = Message.obtain();
                    msg.what = MainActivity.MSG_RECEIVE_CODE;
                    msg.obj = code;
                    mHandler.sendMessage(msg);
                }
            }
            c.close();
        }
    }

}
