package com.example.lenovo.videodemo.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.lenovo.videodemo.entity.Find;
import com.example.lenovo.videodemo.entity.Version;
import com.example.lenovo.videodemo.entity.Video;
import com.example.lenovo.videodemo.global.GlobalValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/7/12.
 */
public class FileUtil {

    //遍历整个手机目录
    public static void getFile(File file, String time, String imageUrl, List<Video> list){
        File[] subFiles=file.listFiles();
        //Log.e("file", file.getAbsolutePath());
        if(subFiles!=null){
            for(File f:subFiles){
                boolean flag=true;
                if(f.isFile()){
                   // Log.e("file child", file.getAbsolutePath());
                    String name=f.getName();
                    if(name.trim().toLowerCase().endsWith(".mp4")||name.trim().toLowerCase().endsWith(".rmvb")||name.trim().toLowerCase().endsWith(".avi")
                            ||name.trim().toLowerCase().endsWith(".mkv")){
                        /*for(Video v:list){
                            if(v.getName().equals(name)||(v.getSelected()!=null&&v.getSelected().equals("true"))){
                                flag=false;
                                break;
                            }
                        }*/
                        DbUtil dbUtil=new DbUtil();
                        if(dbUtil.isExist(GlobalValue.TABLE, name)||dbUtil.queryByName(GlobalValue.TABLE, name).getSelected()=="true"){
                           /* Video video=dbUtil.queryByName(GlobalValue.TABLE, name);
                            list.add(video);
                            Log.e("vedio db", file.getPath()+": "+name+" "+formetFileSize(getFileSizes(f)));*/
                            flag=false;
                        }
                        if(flag){
                            Video video=new Video();
                            Bitmap bitmap= ImageUtil.getImage(f, video.getPosition());
                            time=MediaUtil.getMediaTime(f);
                            try {
                                imageUrl=ImageUtil.saveBitmap(name, bitmap);
                                Log.e("vedio local", file.getPath()+": "+name+" "+formetFileSize(getFileSizes(f)));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                               // Log.e("vedio", file.getPath()+": "+name+" "+formetFileSize(getFileSizes(f)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            video.setName(name);
                            video.setSize(FileUtil.formetFileSize(FileUtil.getFileSizes(f)));
                            video.setUrl(f.getAbsolutePath());
                            video.setTime(time);
                            video.setImageUrl(imageUrl);
                            video.setSelected("false");
                           // Log.e("video detail", name+" * "+FileUtil.formetFileSize(FileUtil.getFileSizes(f))+" * "+f.getAbsolutePath()+" * "+time+" * "+imageUrl);
                            list.add(video);
                        }

                    }
                }
                else if(f.isDirectory()&&f.getPath().indexOf("/.")==-1){
                    getFile(f, time, imageUrl, list);
                }

            }
			/*if(list.size()>0){
				int positon=file.getPath().lastIndexOf("/");
				Log.e("file",file.getPath().substring(positon+1,file.getPath().length()));

			}*/

        }

    }

    //遍历某个目录
    public static void  getFile(File file,  List<Find> list){
        list.clear();
        File[] subFiles=file.listFiles();
        Log.e("file parent", file.getAbsolutePath());
        if(subFiles!=null) {
            for (File f : subFiles) {
                boolean flag = true;
                if (f.isFile()) {
                    Log.e("file movie", f.getAbsolutePath());
                    String name = f.getName();
                    if (name.trim().toLowerCase().endsWith(".mp4") || name.trim().toLowerCase().endsWith(".rmvb") || name.trim().toLowerCase().endsWith(".avi")
                            || name.trim().toLowerCase().endsWith(".mkv")) {
                        Find find=new Find();
                        find.setUrl(f.getPath());
                        find.setParentUrl(file.getPath());
                        find.setName(name);
                        find.setType(GlobalValue.FILE_TYPE_MOVE);
                        list.add(find);
                    }
                }
                else if(f.isDirectory()){
                    Log.e("file", f.getAbsolutePath());
                    Find find=new Find();
                    find.setUrl(f.getPath());
                    find.setParentUrl(file.getPath());
                    find.setName(f.getName());
                    find.setType(GlobalValue.FILE_TYPE_DIRECTORY);
                    list.add(find);
                }
                /*else {
                    Log.e("file other", f.getAbsolutePath());
                    Find find=new Find();
                    find.setUrl(f.getPath());
                    find.setParentUrl(file.getPath());
                    find.setName(f.getName());
                    find.setType(GlobalValue.FILE_TYPE_OTHER);
                    list.add(find);
                }*/

            }
        }

       // return list;
    }

    //获取文件大小
    public static long getFileSizes(File file){
        long size = 0;
        //File file=new File(path);
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    //文件大小格式化
    public static  String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSize = "";
        if (fileS < 1024) {
            fileSize = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSize = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSize = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSize = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSize;
    }
}
