package com.example.lenovo.videodemo.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.example.lenovo.videodemo.global.GlobalContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lenovo on 2016/7/13.
 */
public class ImageUtil {

    public static Bitmap getImage(String time,File file){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        //File file=new File(Environment.getExternalStorageDirectory(),"Download/test.mp4");
        //Log.e("bitmap", file.getPath());
        retriever.setDataSource(file.getPath());
        String duration=retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        //Log.e("url", file.getPath()+": "+time+" "+getShowTime(Long.parseLong(duration)));
        time=MediaUtil.getShowTime(Long.parseLong(duration));
        Bitmap bitmap=retriever.getFrameAtTime(Long.parseLong(duration));
        return bitmap;

    }

    public static String saveBitmap(String name,Bitmap bm) throws IOException {
        //File f = new File(getActivity().getFilesDir(), name);
        File f = new File(GlobalContext.getContext().getExternalCacheDir(), name+".png");
        //Log.e("path",f.getPath() );
        if (f.exists()) {
            f.delete();
        }
        FileOutputStream out=null;
        try {
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();

            Log.i("Bitmap", "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            out.close();
        }
        return f.getPath();
    }
}
