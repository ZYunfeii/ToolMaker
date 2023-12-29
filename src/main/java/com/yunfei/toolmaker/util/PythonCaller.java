package com.yunfei.toolmaker.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class PythonCaller {

    public static String call(String pythonScriptPath, String[] args) {
        log.info("scripts:{}", pythonScriptPath);
        try {
            // 创建Runtime对象
            Runtime runtime = Runtime.getRuntime();

            // 定义Python脚本路径和参数
            String[] cmd = {"python", pythonScriptPath};

            String[] input = new String[cmd.length + args.length];
            System.arraycopy(cmd, 0, input, 0, cmd.length);
            System.arraycopy(args, 0, input, cmd.length, args.length);

            // 执行Python脚本
            Process process = runtime.exec(input);

            // 获取脚本输出结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 等待脚本执行完成
            int exitCode = process.waitFor();

            return output.toString();


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }
}