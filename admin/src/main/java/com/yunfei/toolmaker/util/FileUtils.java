package com.yunfei.toolmaker.util;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
}
