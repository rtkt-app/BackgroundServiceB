package com.myvnc.androidapplication.backgroundservice;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class SendFileTask extends AsyncTask<String, Integer, String> {
    Application application;
    String response;
    private NotificationManager manager;
    private Notification notification;
    NotificationChannel channel;
    final int id = 1010;
    int requestCode = 999;
    Context context;
    String channelId = "default2";
    String title="";

    public SendFileTask(Application application){
        this.application = application;
        context= this.application.getApplicationContext();
        title = context.getString(R.string.app_name);
        manager = (NotificationManager)application.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableVibration(true);
        channel.canShowBadge();
        channel.enableLights(true);
        channel.setLightColor(Color.BLUE);
        // the channel appears on the lockscreen
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        channel.setShowBadge(true);
    }

    @Override
    protected void onPreExecute(){
        if(manager.getNotificationChannel(channelId) == null){
            manager.createNotificationChannel(channel);
        }
        notification = new Notification.Builder(context, channelId).setContentTitle(title)
                // android標準アイコンから
                .setSmallIcon(android.R.drawable.ic_menu_upload).setContentText("Secure Camera is running …")
                .setAutoCancel(true).setWhen(System.currentTimeMillis()).setProgress(100, 0 , false)
                .build();
        // 通知
        manager.notify(R.string.app_name, notification);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // Update progress
        super.onProgressUpdate(values[0]);
        notification = new Notification.Builder(context, channelId).setContentTitle(title)
                // android標準アイコンから
                .setSmallIcon(android.R.drawable.ic_menu_upload).setContentText("Secure Camera is running …")
                .setAutoCancel(true).setWhen(System.currentTimeMillis()).setProgress(100, values[0] , false)
                .build();
        manager.notify(R.string.app_name, notification);
    }

    @Override
    protected String doInBackground(String... strings) {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        response = "";
        String returnString = "error";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 50 * 1024;
            File file = new File(strings[0]);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            //URL url = new URL("http://192.168.1.6/fileUpload/upload.php");
            URL url = new URL("http://172.17.52.220/fileUpload/upload.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setChunkedStreamingMode(1024);
            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            String token = "anyvalye";
            outputStream.writeBytes("Content-Disposition: form-data; name=\"Token\"" + lineEnd);
            outputStream.writeBytes("Content-Type: text/plain;charset=UTF-8" + lineEnd);
            outputStream.writeBytes("Content-Length: " + token.length() + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(token + lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            String taskId = "anyvalue";
            outputStream.writeBytes("Content-Disposition: form-data; name=\"TaskID\"" + lineEnd);
            outputStream.writeBytes("Content-Type: text/plain;charset=UTF-8" + lineEnd);
            outputStream.writeBytes("Content-Length: " + taskId.length() + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(taskId + lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);

            String connstr = null;
            connstr = "Content-Disposition: form-data; name=\"up_file\";filename=\""+ file.getName() + "\"" + lineEnd;

            outputStream.writeBytes(connstr);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            System.out.println("Image length " + bytesAvailable + "");

            while (bytesRead > 0) {
                try {
                    outputStream.write(buffer, 0, bufferSize);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    response = "outofmemoryerror";
                    return response;
                }
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            System.out.println("Server Response Code " + " " + serverResponseCode);
            System.out.println("Server Response Message " + serverResponseMessage);

            if (serverResponseCode == 200) {
                response = "true";
            } else {
                response = "false";
            }
            fileInputStream.close();
            outputStream.flush();

            connection.getInputStream();
            //for android InputStream is = connection.getInputStream();
            java.io.InputStream is = connection.getInputStream();

            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            String responseString = b.toString();
            System.out.println("response string is" + responseString); //Here is the actual output
            outputStream.close();
            outputStream = null;

            if(responseString.equals("uploaded")){
                returnString = "uploaded";
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = "error";
        } finally {
            publishProgress(100);
            return returnString + "@" + file.getName();
        }
    }

    @Override
    protected void onPostExecute(String response){
        String result = response.split("@")[0];
        String fileName = response.split("@")[1];
        if(result.equals("uploaded")){
            File file = new File(application.getFilesDir()+"/"+fileName);
            file.delete();
        }
        Toast.makeText(application.getApplicationContext(), response, Toast.LENGTH_SHORT).show();
        notification = new Notification.Builder(context, channelId).setContentTitle(title)
                // android標準アイコンから
                .setSmallIcon(android.R.drawable.ic_menu_upload).setContentText("File Upload successfully")
                .setAutoCancel(true).setWhen(System.currentTimeMillis()).setProgress(0,  0, false)
                .build();
        manager.notify(R.string.app_name, notification);
    }
}
