package com.example.administrator.videotest.util;

import android.util.Log;

import com.example.administrator.videotest.entity.Version;
import com.google.gson.Gson;

import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lenovo on 2016/7/12.
 */
public class ParseUtil {

    public static Version parserJson(String data) {
        // Log.e("version",s);
        Gson gson=new Gson();
        Version version=gson.fromJson(data,Version.class);
        //  Log.e("version",version.getVersionInfo()+"*"+version.getInfo()+"*"+version.getUrl());
        return version;
    }

    public static Device parserXml(String xmlData, ControlPoint controlPoint){
        Device device = null;
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            InputStream in = new ByteArrayInputStream(xmlData.getBytes());
            //xmlPullParser.setInput(new StringReader(xmlData));
            xmlPullParser.setInput(in,"utf-8");
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                //Log.e("Event", ""+eventType);
                switch (eventType) {

                    case XmlPullParser.START_TAG:
                       // Log.e("TAG", "pName is " + XmlPullParser.START_TAG);
                        String nodeName = xmlPullParser.getName();
                        //Log.e("Node", nodeName);
                        if ("UDN".equals(nodeName)) {
                            //Log.e("device udn",xmlPullParser.nextText());
                            device=controlPoint.getDevice(xmlPullParser.nextText());
                            break;
                        }

                    default://Log.e("Event", "default"+eventType);
                        break;
                }

                eventType = xmlPullParser.next();

            }
            // Log.e("Event", ""+eventType);
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return device;

    }
}
