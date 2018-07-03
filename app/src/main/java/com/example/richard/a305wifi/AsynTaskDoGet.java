package com.example.richard.a305wifi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by richard on 17-3-15.
 */

public class AsynTaskDoGet extends AsyncTask {
    private Context context;
    String ip = "无法获取当前ip";
    private static char[] base64EncodeChars = new char[]{'A', 'B', 'C',
            'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '+', '/'};

    private static byte[] base64DecodeChars = new byte[]{-1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1,
            -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1,
            -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
            25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32,
            33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46,
            47, 48, 49, 50, 51, -1, -1, -1, -1, -1};


    private String account;
    private String sxpass;
    private String router_ip;
    private String router_pass;

    public AsynTaskDoGet(String account, String sxpass, String router_ip, String router_pass, Context context) {
        super();
        this.account = account;
        this.sxpass = sxpass;
        this.router_ip = router_ip;
        this.router_pass = router_pass;
        this.context = context;
    }


    //进行网络请求

    @Override
    protected Object doInBackground(Object[] objects) {


        String router_key = "admin:" + router_pass;
        String router = this.encode(router_key.getBytes());

        String url1 = "http://" + router_ip + "/userRpm/PPPoECfgRpm.htm?wan=0&wantype=2&acc=";


        String url2 = bin2hex(jiami(account));
        System.out.println(url2);
        String url3 = "&psw=";
        String url4 = "&confirm=";
        String url5 = "&SecType=0&sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=4&waittime2=0&Connect=%C1%AC+%BD%D3";
        String u = url1 + url2 + url3 + sxpass + url4
                + sxpass + url5;
        try {
            URL url = new URL(u);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Host", router_ip);
            connection.setRequestProperty("Connection", "close");
            connection.setRequestProperty("Authorization", "Basic "
                    + router);
            connection
                    .setRequestProperty("Accept",
                            "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection
                    .setRequestProperty(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.76 Safari/537.36");

            connection
                    .setRequestProperty(
                            "Referer",
                            url1
                                    + url2
                                    + url3
                                    + sxpass
                                    + url4
                                    + sxpass
                                    + "&SecType=0&sta_ip=0.0.0.0&sta_mask=0.0.0.0&linktype=4&waittime2=0&Disconnect=%B6%CF+%CF%DF");
            connection.setRequestProperty("Accept-Encoding",
                    "gzip,deflate,sdch");
            connection.setRequestProperty("Accept-Language",
                    "zh-CN,zh;q=0.8,en;q=0.6");
            connection.setRequestProperty("Cookie",
                    "Authorization=Basic " + router);
            connection.setConnectTimeout(1000);
            connection.connect();
            connection.getHeaderFields();

            InputStream is = connection.getInputStream();
            Log.d("ROUTER_RETURN", inputStream2String(is));


            //Thread.sleep(1500);

            String visitUrl = "http://ip.6655.com/ip.aspx?area=1";
            ip = this.HttpGet(visitUrl);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Toast.makeText(this.context, ip, Toast.LENGTH_SHORT).show();
    }

    public String inputStream2String(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    public static String bin2hex(String bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();

        for (int i = 0; i < bs.length; i++) {
            sb.append('%');
            int bit = (bs[i] & 0xF0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0xF;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }


    public static char trans(long n) {
        if (n <= 127)
            return (char) n;
        else
            return (char) (n - 256);
    }


    public static String decode(String str)
            throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        byte[] data = str.getBytes("US-ASCII");
        int len = data.length;
        int i = 0;
        int b1, b2, b3, b4;
        while (i < len) {
            /* b1 */
            do {
                b1 = base64DecodeChars[data[i++]];
            } while (i < len && b1 == -1);
            if (b1 == -1)
                break;
			/* b2 */
            do {
                b2 = base64DecodeChars[data[i++]];
            } while (i < len && b2 == -1);
            if (b2 == -1)
                break;
            sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
			/* b3 */
            do {
                b3 = data[i++];
                if (b3 == 61)
                    //	return sb.toString().getBytes("ISO-8859-1");
                    return sb.toString();
                b3 = base64DecodeChars[b3];
            } while (i < len && b3 == -1);
            if (b3 == -1)
                break;
            sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
			/* b4 */
            do {
                b4 = data[i++];
                if (b4 == 61)
                    //	return sb.toString().getBytes(	"ISO-8859-1");
                    return sb.toString();
                b4 = base64DecodeChars[b4];
            } while (i < len && b4 == -1);
            if (b4 == -1)
                break;
            sb.append((char) (((b3 & 0x03) << 6) | b4));
        }
        //	return sb.toString().getBytes("ISO-8859-1");
        return sb.toString();
    }

    public static String encode(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4)
                        | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4)
                    | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2)
                    | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

    public static byte intToByte(long n) {
        if (n <= 127)
            return (byte) n;
        else
            return (byte) (n - 256);
    }

    public String jiami(String account) {

        String radius = "singlenet01";
        long time = (new Date()).getTime();//得到系统时间，从1970.01.01.00:00:00 开始的秒数
        time /= 1000;

        long m_time1c;                        //时间初处理m_time1c为结果,经过时间计算出的第一次加密
        long m_time1convert;                //对时间操作后的结果，此为格式字串的原始数据
        char ss[] = {0, 0, 0, 0};        //源数据1,对m_time1convert进行计算得到格式符源数据
        byte[] by2 = new byte[4];    //md5加密参数的一部分,m_time1c的byte形式
        String m_formatsring = "";                //由m_timece算出的字符串,一般为可视字符
        String m_md5;                        //对初加密(m_timec字符串表示+m_username+radius)的MD5加密
        String m_md5use;                    //md5 Lower模式的前两位

        long t;
        t = time;
        t *= 0x66666667;
        t >>= 0x20; //右移32位
        t >>= 0x01; //右移1位
        m_time1c = (long) t;  //强制转换

        long m_lasttimec = m_time1c;

        t = m_time1c;
        by2[3] = intToByte(t & 0xFF);
        by2[2] = intToByte((t & 0xFF00) / 0x100);
        by2[1] = intToByte((t & 0xFF0000) / 0x10000);
        by2[0] = intToByte((t & 0xFF000000) / 0x1000000);

        //System.out.println(by2[3]+" "+by2[2]+" "+by2[1]+" "+by2[0]);

        /**
         * 倒置过程m_time1convert为结果
         */
        int t0 = 0, t1, t2, t3;
        t0 = (int) m_time1c;
        t1 = t0;
        t2 = t0;
        t3 = t0;
        t3 = t3 << 0x10;
        t1 = t1 & 0x0FF00;
        t1 = t1 | t3;
        t3 = t0;
        t3 = t3 & 0x0FF0000;
        t2 = t2 >> 0x10;
        t3 = t3 | t2;
        t1 = t1 << 0x08;
        t3 = t3 >> 0x08;
        t1 = t1 | t3;
        m_time1convert = t1;

        //System.out.println(m_time1convert);

        /**
         * 源数据1,对m_time1convert进行计算得到格式符源数据
         */

        long tc = 0;
        tc = m_time1convert;
        ss[3] = trans(tc & 0xFF);
        ss[2] = trans((tc & 0xFF00) / 0x100);
        ss[1] = trans((tc & 0xFF0000) / 0x10000);
        ss[0] = trans((tc & 0xFF000000) / 0x1000000);

        //System.out.println(ss[3]+" "+ss[2]+ " "+ss[1]+ " "+ss[0]);
        /**
         * 格式符初加密
         */
        char pp[] = {0, 0, 0, 0};
        int i = 0, j = 0, k = 0;
        for (i = 0; i < 0x20; i++) {
            j = i / 0x8;
            k = 3 - (i % 0x4);
            pp[k] *= 0x2;
            if (ss[j] % 2 == 1) {
                pp[k]++;
            }
            ss[j] /= 2;
        }

        /**
         * 格式符计算,m_formatsring为结果
         */
        char pf[] = {0, 0, 0, 0, 0, 0};
        short st1, st2;
        st1 = (short) pp[3];
        st1 /= 0x4;
        pf[0] = trans(st1);
        st1 = (short) pp[3];
        st1 = (short) (st1 & 0x3);
        st1 *= 0x10;
        pf[1] = trans(st1);
        st2 = (short) pp[2];
        st2 /= 0x10;
        st2 = (short) (st2 | st1);
        pf[1] = trans(st2);
        st1 = (short) pp[2];
        st1 = (short) (st1 & 0x0F);
        st1 *= 0x04;
        pf[2] = trans(st1);
        st2 = (short) pp[1];
        st2 /= 0x40;
        st2 = (short) (st2 | st1);
        pf[2] = trans(st2);
        st1 = (short) pp[1];
        st1 = (short) (st1 & 0x3F);
        pf[3] = trans(st1);
        st2 = (short) pp[0];
        st2 /= 0x04;
        pf[4] = trans(st2);
        st1 = (short) pp[0];
        st1 = (short) (st1 & 0x03);
        st1 *= 0x10;
        pf[5] = trans(st1);

		/*	String arr="";
			for(int x=0;x<6;x++){
				arr+=(pf[x]+" ");
			}
			System.out.println(arr);*/

        for (int n = 0; n < 6; n++) {
            pf[n] += 0x20;
            if ((pf[n]) >= 0x40) {
                pf[n]++;
            }
        }

        //System.out.println("m_f"+m_formatsring);

        for (int m = 0; m < 6; m++) {
            m_formatsring += pf[m];
        }

        //System.out.println("m_f"+m_formatsring);

        String strInput;
        String strtem;
        if (account.contains("@")) {
            strtem = account.substring(0, account.indexOf("@"));
        } else {
            strtem = account;
        }
        strInput = strtem + radius;
        byte[] temp = new byte[by2.length + strInput.getBytes().length];
        System.arraycopy(by2, 0, temp, 0, by2.length);
        System.arraycopy(strInput.getBytes(), 0, temp, by2.length, strInput.getBytes().length);
        m_md5 = MD5.getMD5(temp);

        //System.out.println("m5:"+m_md5);
        m_md5use = m_md5.substring(0, 2);
        String m_realusername = m_formatsring + m_md5use + account;
        String LR = "\r\n";
        m_realusername = LR + m_realusername;//前面两位为回车换行0D0A,接着再是后续的


        return m_realusername;
    }

    public static byte[] read(InputStream inStream) throws Exception {
        ByteArrayOutputStream outp = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outp.write(buffer, 0, len);

        }
        inStream.close();
        return outp.toByteArray();
    }

    public static String HttpGet(String urlString) throws Exception {
        URL url = new URL(urlString);
        String line = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(5000);
        urlConnection.setRequestMethod("GET");
        if (urlConnection.getResponseCode() == 200) {
            InputStream inStream = urlConnection.getInputStream();
            byte[] data = read(inStream);
            line = new String(data);
        }
        Log.d("HTTP_GET", line);
        return line;
    }

}
