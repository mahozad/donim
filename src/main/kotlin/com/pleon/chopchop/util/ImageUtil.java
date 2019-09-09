package com.pleon.chopchop.util;

import com.pleon.chopchop.Main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {

    public static byte[] getImage(String path) {
        InputStream inputStream = Main.class.getResourceAsStream(path);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int readBytesCount;
        byte[] data = new byte[16384];
        try {
            while ((readBytesCount = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, readBytesCount);
            }
            buffer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }

}
