package cz.zcu.kiv.server.beecommunity.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Slf4j
public class ImageUtil {
    private ImageUtil(){}

    /**
     * Compress raw image data from client
     * @param data image from client to compress
     * @return compressed image file as byte array
     */
    public static byte[] compressImage(byte[] data) {
        if (data == null) {
            return new byte[0];
        }

        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception e) {
            log.error("Exception while compress image: {}", e.getMessage());
        }
        return outputStream.toByteArray();
    }

    /**
     * Decompress byte array image from database
     * @param data byte array to decompress
     * @return decompressed byte array
     */
    public static byte[] decompressImage(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception e) {
            log.error("Exception while decompress image: {}", e.getMessage());
        }
        return outputStream.toByteArray();
    }
}