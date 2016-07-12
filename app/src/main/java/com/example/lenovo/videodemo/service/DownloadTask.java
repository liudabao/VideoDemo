package com.example.lenovo.videodemo.service;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lenovo.videodemo.global.GlobalContext;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lenovo on 2016/7/12.
 */
public class DownloadTask  extends AsyncTask<Void, Integer, Boolean> {

    private String downloadUrl;// 下载链接地址
    private int threadNum;// 开启的线程数
    private File filePath;// 保存文件路径地址
    private int blockSize;// 每一个线程的下载量
    private ProgressDialog dialog;
    private int fileSize;


    public DownloadTask(String downloadUrl, int threadNum, File fileptah, ProgressDialog dialog) {
        this.downloadUrl = downloadUrl;
        this.threadNum = threadNum;
        this.filePath = fileptah;
        this.dialog=dialog;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        DownlaodThread[] threads = new DownlaodThread[threadNum];
        try{
            URL url = new URL(downloadUrl);
            Log.e("URL", "download file http path:" + downloadUrl);
            HttpURLConnection conn =(HttpURLConnection) url.openConnection();
            // 读取下载文件总大小
            fileSize = conn.getContentLength();
            if (fileSize <= 0) {
                return false;
            }
            blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum
                    : fileSize / threadNum + 1;

            Log.e("URL", "fileSize:" + fileSize + "  blockSize:"+blockSize);

            //File file = new File(filePath);
            for (int i = 0; i < threads.length; i++) {
                // 启动线程，分别下载每个线程需要下载的部分
                //threads[i] = new DownThread(url, filePath, blockSize,
                //       (i + 1));
                threads[i]=new DownlaodThread(url, filePath, i*blockSize,(i+1)*blockSize-1, i+1);
                threads[i].setName("Thread:" + i);
                threads[i].start();
            }

            boolean isfinished = false;
            int downloadedAllSize = 0;
            while (!isfinished) {
                isfinished = true;
                // 当前所有线程下载总量
                downloadedAllSize = 0;
                for (int i = 0; i < threads.length; i++) {
                    downloadedAllSize += threads[i].getDownloadLength();

                    if (!threads[i].isCompleted()) {
                        isfinished = false;
                    }
                    // downloadHelper.query(i+1);
                }
                publishProgress(downloadedAllSize);

            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        // Log.e("percent", values[0]+"");
        int percent=values[0]*100/fileSize;
        dialog.setProgress(percent);
    }

    @Override
    protected void onPostExecute(Boolean result){
        dialog.dismiss();
        Intent intent=new Intent("android.video.update");
        GlobalContext.getContext().sendBroadcast(intent);
        Log.e("percent", "finish");
    }

}
