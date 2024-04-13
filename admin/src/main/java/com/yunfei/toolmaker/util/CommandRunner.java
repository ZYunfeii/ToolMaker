package com.yunfei.toolmaker.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.toolmaker.util
 * @className: CommandRunner
 * @author: Yunfei
 * @description: 命令行执行工具
 * @date: 2024/4/13 13:28
 * @version: 1.0
 */
@Slf4j
public class CommandRunner {
    /**
     * @param workingDirectory: 命令行执行的工作路径
     * @param command: 命令行内容
     * @return Integer 0为执行成功 其余为失败
     * @author Yunfei
     * @description TODO
     * @date 2024/4/13 13:30
     */
    public static Integer execution(String workingDirectory, String command) {
        int exitCode = 1;
        try {
            // 创建ProcessBuilder对象，并设置要执行的命令和工作目录
            ProcessBuilder processBuilder = new ProcessBuilder(splitCommand(command));
            processBuilder.directory(new File(workingDirectory));

            // 启动进程并执行命令
            Process process = processBuilder.start();

            // 等待命令执行完成
            exitCode = process.waitFor();
        } catch (Exception e) {
            log.error(e.getMessage());
            return 1;
        }
        return exitCode;
    }
    private static List<String> splitCommand(String command) {
        List<String> parts = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        boolean inQuotes = false;

        for (char c : command.toCharArray()) {
            if (Character.isWhitespace(c) && !inQuotes) {
                if (builder.length() > 0) {
                    parts.add(builder.toString());
                    builder.setLength(0);
                }
            } else if (c == '\'') {
                inQuotes = !inQuotes;
            } else {
                builder.append(c);
            }
        }

        if (builder.length() > 0) {
            parts.add(builder.toString());
        }

        return parts;
    }

    public static void main(String[] args) {
        String command = "git commit -m 'docs: auto commit blog'";

        List<String> commandParts = splitCommand(command);
        System.out.println(commandParts);
    }
}
