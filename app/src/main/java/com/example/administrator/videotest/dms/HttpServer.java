package com.example.administrator.videotest.dms;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by liumin on 2016/8/7.
 */
public class HttpServer extends NanoHTTPD {

    public HttpServer(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {

        Response response = null;
        //String url=session.getUri();
        Log.e("TAG", session.getUri());
        Log.e("TAG", session.getRemoteHostName());
        try {

            File file=new File(session.getUri());
            //InputStream inputStream = getResources().openRawResource(R.raw.encrypted);
            // inputStream = new InputStreamEncrypted(inputStream);
            Log.e("video", file+"");
            InputStream inputStream=new FileInputStream(file);
            int totalLength = inputStream.available();
            Log.e("video", totalLength+"");
            String requestRange = session.getHeaders().get("range");
            if (requestRange == null) {
                //http 200
                response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "video/mp4", inputStream, totalLength);
            } else {
                //http 206

                //region get RangeStart from head
                Matcher matcher = Pattern.compile("bytes=(\\d+)-(\\d*)").matcher(requestRange);
                matcher.find();
                long start = 0;
                try { start = Long.parseLong(matcher.group(1)); } catch (Exception e) { e.printStackTrace(); }
                //endregion

                inputStream.skip(start);

                long restLength = totalLength - start;
                response = NanoHTTPD.newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, "video/mp4", inputStream, restLength);

                String contentRange = String.format("bytes %d-%d/%d", start, totalLength, totalLength);
                response.addHeader("Content-Range", contentRange);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}

