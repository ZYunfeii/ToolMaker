package com.yunfei.toolmaker.util;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
    public static StreamingResponseBody getStreamingOfFile(String filePath) throws IOException {
        Resource resource = new FileSystemResource(filePath);
        File file = resource.getFile();
        FileInputStream fileInputStream = new FileInputStream(file);
        StreamingResponseBody stream = out -> {
            while (true) {
                byte[] buffer = new byte[1024];
                int bytesRead = fileInputStream.read(buffer);
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
        };
        return stream;
    }

    public static String generateIdentifier(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            md.update(buffer, 0, bytesRead);
        }

        byte[] digest = md.digest();
        StringBuilder identifier = new StringBuilder();
        for (byte b : digest) {
            identifier.append(String.format("%02x", b));
        }

        fis.close();

        return identifier.toString();
    }
}
