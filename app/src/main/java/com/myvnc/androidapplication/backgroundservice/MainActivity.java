package com.myvnc.androidapplication.backgroundservice;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String storagePath, log;
    List<String> listDirectory;
    TextView textView, textView1;
    byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tvStatus);
        textView1 = findViewById(R.id.tvFileLsit);
        Button buttonStart = findViewById(R.id.btnStart);
        ;
        buttonStart.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), TestService.class);
            intent.putExtra("REQUEST_CODE", 1);

            // Serviceの開始
            //startService(intent);
            startForegroundService(intent);

        });

        Button buttonCheck = findViewById(R.id.btnCheck);
        buttonCheck.setOnClickListener(v -> {

            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> listServiceInfo = manager.getRunningServices(Integer.MAX_VALUE);
            boolean found = false;
            for (ActivityManager.RunningServiceInfo curr : listServiceInfo) {
                // クラス名を比較
                if (curr.service.getClassName().equals(TestService.class.getName())) {
                    // 実行中のサービスと一致
                    Toast.makeText(getApplicationContext(), "サービス実行中", Toast.LENGTH_LONG).show();
                    textView.setText("running");
                    found = true;
                    break;
                }
            }
            if (found == false) {
                Toast.makeText(getApplicationContext(), "サービス停止中", Toast.LENGTH_LONG).show();
                textView.setText("stopped");
            }
        });

        Button buttonStop = findViewById(R.id.btnStop);
        buttonStop.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), TestService.class);
            // Serviceの停止
            stopService(intent);
        });

        Button getFileList = findViewById(R.id.fileList);
        getFileList.setOnClickListener(v -> {
            File file = getApplicationContext().getFilesDir();
            listDirectory = new ArrayList<>();
            storagePath = file.getPath();
            searchImageFiles(storagePath, "mp4");
        });

        final Button sendFile = findViewById(R.id.sendFile);
        sendFile.setOnClickListener(v -> {
            String filePath = textView1.getText().toString();
            File file = new File(filePath);
            Toast.makeText(getApplicationContext(), file.getName().toString(), Toast.LENGTH_SHORT).show();
            Log.d("tag", "");
            SendMovieTask sendMovieTask = new SendMovieTask();
            sendMovieTask.execute(filePath);

        });
        Button playFile = findViewById(R.id.playfile);
        playFile.setOnClickListener(v -> {
            String filePath = textView1.getText().toString();
            VideoView videoView = findViewById(R.id.videoView);
            videoView.setVideoPath(filePath);
            videoView.start();
        });

    }

    private void searchImageFiles(String path, String fileType) {
        listDirectory.add(path);
        log = "";
        int m = 0;
        int n = 0;
        String[] fileName;
        String imgPath = "";

        // dirList.size() は動的変化あり注意
        while (listDirectory.size() > m) {

            // get(m) リスト内の指定された位置 m にある要素を返す
            File directory = new File(listDirectory.get(m));
            // java.io.File クラスのメソッド list()
            // 指定したディレクトリに含まれるファイル、ディレクトリの一覧を String 型の配列で返す。
            fileName = directory.list();

            n = 0;
            while (fileName.length > n) {

                File subFile;
                subFile = new File(directory.getPath() + "/" + fileName[n]);

                if (subFile.isDirectory()) {
                    Log.d("debug", "isDirectory");
                    listDirectory.add(directory.getPath() + "/" + fileName[n]);
                    imgPath = directory.getPath() + "/" + fileName[n];
                } else if (subFile.getName().endsWith(fileType)) {
                    Log.d("debug", "getName");
                    imgPath = directory.getPath() + "/" + fileName[n];
                    // Log としてパスを出力
                    putLog(imgPath);
                } else {
                    putLog("nothing to do");
                }
                n++;
            }
            m++;
        }
    }

    public void putLog(String mess) {
        log += mess;
        textView1.setText(log);
    }

    private class SendMovieTask extends AsyncTask<String, String, String> {
        String response;

        @Override
        protected String doInBackground(String... strings) {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            response = "";
            try {
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 50 * 1024;
                FileInputStream fileInputStream = new FileInputStream(new File(getApplication().getFilesDir()+"/waterfall-free-video1.mp4"));

                URL url = new URL("http://192.168.1.6/fileUpload/upload.php");

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
                connstr = "Content-Disposition: form-data; name=\"up_file\";filename=\""+ strings[0] + "\"" + lineEnd;

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

            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
            } finally {
                return response;
            }
        }

        @Override
        protected void onPostExecute(String response){
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
        }
    }
}