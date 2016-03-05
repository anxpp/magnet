package com.anxpp.magnet.Utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class NetUtils {
    public static String getHtml(String path) throws Exception {
        //创建连接
        HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            //获取输入流
            InputStream is = conn.getInputStream();
            //通过StreamTool解析输入流得到byte[]数组
            byte[] data = StreamUtil.read(is);
            //返回解析后的脚本代码
            return new String(data);
        }
        return null;
    }
}

class StreamUtil {
    public static byte[] read(InputStream is) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while ((len = is.read(b)) != -1) {
            out.write(b, 0, len);
        }
        is.close();
        return out.toByteArray();
    }
}
