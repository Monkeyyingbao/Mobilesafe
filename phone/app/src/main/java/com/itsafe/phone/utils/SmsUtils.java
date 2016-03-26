package com.itsafe.phone.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

/**
 * 短信备份还原的工具类  格式:json格式
 * Created by Hello World on 2016/3/23.
 */
public class SmsUtils {


    /**
     * @param s 源字符串
     * @return 特殊字符串
     */
    private static String convert2ts(String s) {
        String res = "";
        for (int i = 0; i < s.length(); i++) {
            res += convert2ts(s.charAt(i));
        }
        return res;
    }

    /**
     * @param s 特殊字符串
     * @return 源字符串
     */
    private static String convert2source(String s) {
        String res = "";
        for (int i = 0; i < s.length(); i++) {
            res += convert2source(s.charAt(i));
        }
        return res;
    }

    /**
     * @param c json 格式字符
     * @return 转译字符
     */
    private static char convert2ts(char c) {
        char resule = '\u0000';
        //  " ★  { 卍  } 卐 [ ◎ ] ¤ : ℗ , ✿
        switch (c) {
            case '"':
                resule = '★';
                break;
            case '{':
                resule = '卍';
                break;
            case '}':
                resule = '卐';
                break;
            case '[':
                resule = '◎';
                break;
            case ']':
                resule = '¤';
                break;
            case ':':
                resule = '℗';
                break;
            case ',':
                resule = '✿';
                break;
            default:
                resule = c;
                break;
        }

        return resule;
    }

    /**
     * 还原字符
     *
     * @param c json 转译字符
     * @return 格式字符
     */
    private static char convert2source(char c) {
        char resule = '\u0000';
        //  " ★  { 卍  } 卐 [ ◎ ] ¤ : ℗ , ✿
        switch (c) {
            case '★':
                resule = '"';
                break;
            case '卍':
                resule = '{';
                break;
            case '卐':
                resule = '}';
                break;
            case '◎':
                resule = '[';
                break;
            case '¤':
                resule = ']';
                break;
            case '℗':
                resule = ':';
                break;
            case '✿':
                resule = ',';
                break;
            default:
                resule = c;
                break;
        }

        return resule;
    }

    /**
     * 流转换成字符串
     *
     * @param is 输入流
     * @return 字符串
     */
    public static String stream2String(InputStream is) {
        StringBuilder mess = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try {
            String line = reader.readLine();
            while (line != null) {
                mess.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                is.close();
            } catch (Exception e) {

            }
        }


        return mess.toString();
    }


    /**
     * 短信的还原
     *
     * @param context
     */
    public static void smsRecovery(final Activity context, final ProgressDialog pd) {
        class Data{
            int progress;
        }
        final Data dataProgress = new Data();
        File file = null;
        //文件 sd
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //有sd卡
            file = new File(Environment.getExternalStorageDirectory(), "smses.json");
            //如果文件不存在
        } else {
            throw new RuntimeException("sd卡挂载不成功或没有sd卡");
        }

        //备份文件存在
        try {
            //1.解析读取json信息
            String smsJson = stream2String(new FileInputStream(file));
            //class
            Gson gson = new Gson();
            final SmsJsonData smsJsonData = gson.fromJson(smsJson, SmsJsonData.class);

            //循环取短信写短信
            final Uri uri = Uri.parse("content://sms");

            pd.setMax(smsJsonData.smses.size());
            pd.show();
            //子线程写短信
            Thread thread = new Thread() {
                @Override
                public void run() {
                    for (SmsJsonData.Sms sms : smsJsonData.smses) {
                        ContentValues values = new ContentValues();
                        values.put("address", sms.address);
                        values.put("body", EncodeUtils.encode(convert2source(sms.body),StrUtils.MUSIC));
                        values.put("date", Long.parseLong(sms.date));
                        values.put("type", Integer.parseInt(sms.type));
                        //取一条写一条
                        context.getContentResolver().insert(uri, values);

                        SystemClock.sleep(200);
                        //备份的进度
                        dataProgress.progress++;
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.setProgress(dataProgress.progress);
                            }
                        });
                    }//end for

                    //关闭对话框
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                        }
                    });
                }
            };
            thread.join();
            thread.start();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public class SmsJsonData {
        public List<Sms> smses;

        public class Sms {
            public String address;//	888
            public String body;//	卍卐◎¤★★✿
            public String date;//	1458726699505
            public String type;//	1
        }
    }

    /**
     * 备份还原进度的接口回调
     */
    public interface SmsBackRestoreListener {
        //显示进度
        void show();
        //进度消失
        void dismiss();
        //总进度
        void setMax(int max);
        //当前进度
        void setProgress(int currentProgress);

    }

    /**
     * 短信备份
     *
     * @param context
     */
    public static void smsBackup(final Activity context, final SmsBackRestoreListener pd) {
        class Data{
            int progress;
        }
        final Data dataProgress = new Data();
        File file = null;
        //文件 sd
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //有sd卡
            //是否有剩余空间
            //获取sd卡的剩余空间
            long freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
            if (freeSpace < 1024 * 1024 * 5) {
                //如果小于5M
                throw new RuntimeException("sd卡空间不足,请先删除数据释放空间");
            } else {
                //正常
                file = new File(Environment.getExternalStorageDirectory(), "smses.json");
            }
        } else {
            throw new RuntimeException("sd卡挂载不成功或没有sd卡");
        }

        try {
            //写入字符串
            final PrintWriter out = new PrintWriter(file);
            out.print("{\"smses\":[");
            //短信数据库
            Uri uri = Uri.parse("content://sms");
            final Cursor cursor = context.getContentResolver().query(uri, new String[]{"address", "date", "body", "type"}, null, null, null);
            //多少条数据
            pd.setMax(cursor.getCount());
            //在这里显示进度
            pd.show();

            //子线程拷贝
            new Thread() {
                @Override
                public void run() {
                    String sms = null;
                    //int number = 0;
                    //游标循环一条数据,写一条数据
                    //取一条短信{"address":"132333","date":"322143432432","body":"hello","type":"1"},
                    //  " ★  { 卍  } 卐 [ ◎ ] ¤ : ℗ , ✿
                    while (cursor.moveToNext()) {
                        //取一条短信
                        sms = "{";
                        sms += "\"address\":\"" + cursor.getString(0) + "\"";
                        sms += ",\"date\":\"" + cursor.getString(1) + "\"";
                        sms += ",\"body\":\"" + convert2ts(EncodeUtils.encode(cursor.getString(2),StrUtils.MUSIC)) + "\"";
                        sms += ",\"type\":\"" + cursor.getString(3) + "\"}";

                        //判断是否是最后一条信息
                        if (cursor.isLast()) {
                            sms += "]}";
                        } else {
                            sms += ",";
                        }

                        //写到文件中
                        out.print(sms);
                        out.flush();

                        SystemClock.sleep(200);
                        //显示更新的进度
                        dataProgress.progress++;
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.setProgress(dataProgress.progress);
                            }
                        });
                    }

                    //备份结束
                    out.close();
                    cursor.close();

                    //关闭对话框
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                        }
                    });

                }
            }.start();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
