package com.example.administrator.videotest.service;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lenovo on 2016/7/12.
 */
public class DownlaodThread extends Thread {
    /** 当前下载是否完成 */
    private boolean isCompleted = false;
    /** 当前下载文件长度 */
    private int downloadLength = 0;
    /** 文件保存路径 */
    private File file;
    /** 文件下载路径 */
    private URL downloadUrl;
    /** 当前下载线程ID */
    private int threadId;
    /** 线程下载数据长度 */
    private int blockSize;

    private int startPos;//开始位置

    private int endPos;//结束位置

    public DownlaodThread(URL downloadUrl, File file, int blocksize,
                      int threadId) {
        this.downloadUrl = downloadUrl;
        this.file = file;
        this.threadId = threadId;
        this.blockSize = blocksize;
    }

    public DownlaodThread(URL downloadUrl, File file, int startPos, int endPos,
                      int threadId) {
        this.downloadUrl = downloadUrl;
        this.file = file;
        this.threadId = threadId;
        this.startPos = startPos;
        this.endPos=endPos;
    }

    @Override
    public void run(){
        BufferedInputStream bis = null;
        RandomAccessFile raf = null;
        HttpURLConnection connection=null;
        //FileOutputStream out;
        try{
            connection=(HttpURLConnection)downloadUrl.openConnection();
            connection.setAllowUserInteraction(true);
            // int startPos = blockSize * (threadId - 1);//开始位置
            // int endPos = blockSize * threadId - 1;//结束位置
            //设置当前线程下载的起点、终点
            connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
            Log.e("size", "" + startPos + "  -- "+endPos);
            byte[] buffer = new byte[1024];
            bis = new BufferedInputStream(connection.getInputStream());
            raf = new RandomAccessFile(file, "rwd");
            raf.seek(startPos);
            int len;
            while ((len = bis.read(buffer, 0, 1024)) != -1) {
                raf.write(buffer, 0, len);
                downloadLength += len;
            }
            isCompleted = true;

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            // connection.disconnect();
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                connection.disconnect();
            }

        }

    }

    /**
     * 线程文件是否下载完毕
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * 线程下载文件长度
     */
    public int getDownloadLength() {
        return downloadLength;
    }

}