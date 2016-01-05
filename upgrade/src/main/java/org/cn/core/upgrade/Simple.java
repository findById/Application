package org.cn.core.upgrade;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Simple {

    private Context mContext;

    public Simple(Context mContext) {
        this.mContext = mContext;
    }

    public void checkUpdate(final String channel) {

        try {
            new AsyncTask<String, Long, String>() {
                @Override
                protected String doInBackground(String... params) {
                    try {
                        String url = "http://192.168.99.57:8080/admin/upgrade";

                        JSONObject param = new JSONObject();
                        param.put("appId", "");
                        param.put("version", "" + BuildConfig.VERSION_NAME);
                        param.put("appChannel", "" + channel);
                        param.put("osType", "android");

                        Log.d("Simple", "URL:" + url + " \nParam:" + param.toString());

                        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setConnectTimeout(1000 * 5);
                        conn.setReadTimeout(1000 * 5);
                        conn.addRequestProperty("Content-Type", "application/json; charset=UTF-8");

                        conn.connect();

                        OutputStream os = conn.getOutputStream();
                        os.write(param.toString().getBytes("UTF-8"));
                        os.flush();

                        int code = conn.getResponseCode();
                        if (code != 200) {
                            throw new IOException("" + code);
                        }
                        String result = IOUtil.asString(conn.getInputStream());
                        Log.d("Simple", result);
                        os.close();
                        conn.disconnect();
                        return result;
                    } catch (Throwable e) {
                        Log.d("Simple", "" + e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    try {
                        JSONObject obj = new JSONObject(result);
                        int statusCode = obj.optInt("statusCode");
                        String message = obj.optString("message");
                        final String data = obj.optString("result");

                        switch (statusCode) {
                            case 200: { // nope
                                Toast.makeText(mContext, "" + message, Toast.LENGTH_LONG).show();
                                break;
                            }
                            case 201: { // patch
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setMessage("发现新版本:" + statusCode);
                                builder.setTitle("提醒");
                                builder.setNegativeButton("稍候更新", null);
                                builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        download(data, "smart");
                                    }
                                });
                                builder.show();
                                break;
                            }
                            case 202: { // apk
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setMessage("发现新版本:" + statusCode);
                                builder.setTitle("提醒");
                                builder.setNegativeButton("稍候更新", null);
                                builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        download(data, "apk");
                                    }
                                });
                                builder.show();
                                break;
                            }
                            default:
                                Toast.makeText(mContext, message + "(" + statusCode + ")", Toast.LENGTH_LONG).show();
                                break;
                        }
                    } catch (Throwable e) {
                        Log.d("Simple", "" + e.getMessage());
                    }
                }
            }.execute("");
            return;
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private void download(String url, String type) {
        try {
            new AsyncTask<String, Long, String[]>() {
                @Override
                protected String[] doInBackground(String... params) {
                    try {
                        HttpURLConnection conn = (HttpURLConnection) new URL(params[0]).openConnection();
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        conn.setDoOutput(false);
                        conn.setConnectTimeout(1000 * 5);
                        conn.setReadTimeout(1000 * 60);
                        conn.addRequestProperty("Content-Type", "application/octet-stream; charset=UTF-8");

                        conn.connect();

                        int code = conn.getResponseCode();
                        if (code != 200) {
                            throw new IOException("" + code);
                        }

                        String type = params[1];

                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                        File file;
                        if ("smart".equals(type)) {
                            file = new File(path, "" + System.currentTimeMillis() + ".p");
                        } else {
                            String filename = "";
                            String dis = conn.getHeaderField("Content-Disponsition");
                            if (dis != null && dis.contains("filename")) {
                                String[] tmp;
                                if (dis.contains(";")) {
                                    tmp = dis.split(";");
                                } else {
                                    tmp = new String[]{dis};
                                }

                                for (String key : tmp) {
                                    key = key.trim();
                                    if (key.contains("filename")) {
                                        filename = key.replaceAll("filename=", "");
                                    }
                                }
                            }

                            if (TextUtils.isEmpty(filename)) {
                                file = new File(path, "" + System.currentTimeMillis() + ".apk");
                            } else {
                                file = new File(path, "" + filename);
                            }
                        }

                        FileOutputStream fos = new FileOutputStream(file);
                        IOUtil.copyStream(conn.getInputStream(), fos);

                        conn.disconnect();
                        return new String[]{file.getPath(), type};
                    } catch (Throwable e) {
                        Log.d("Simple", "" + e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String[] result) {
                    super.onPostExecute(result);
                    if (result == null || result.length < 2) {
                        return;
                    }

                    if ("smart".equals(result[1])) {
                        SmartUpgrade smart = new SmartUpgrade();
                        smart.smart(mContext, result[0], new SmartUpgrade.OnSmartListener() {
                            @Override
                            public void onSmart(int code, final String path) {
                                if (code != 200) {
                                    return;
                                }
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        install(new File(path));
                                    }
                                });
                            }
                        });
                    } else {
                        install(new File(result[0]));
                    }
                }
            }.execute(url, type);
            return;
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private void install(final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("是否安装");
        builder.setTitle("提醒");
        builder.setNegativeButton("稍候", null);
        builder.setPositiveButton("好", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(android.content.Intent.ACTION_VIEW);
                String type = "application/vnd.android.package-archive";
                intent.setDataAndType(Uri.fromFile(file), type);
                mContext.startActivity(intent);
            }
        });
        builder.show();
    }

}
