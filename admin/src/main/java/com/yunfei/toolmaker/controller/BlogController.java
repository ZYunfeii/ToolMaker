package com.yunfei.toolmaker.controller;

import com.alibaba.fastjson.JSON;
import com.yunfei.toolmaker.util.CommandRunner;
import com.yunfei.toolmaker.util.FileUtils;
import com.yunfei.toolmaker.util.R;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @projectName: ToolMaker
 * @package: com.yunfei.toolmaker.controller
 * @className: BlogController
 * @author: Yunfei
 * @description: TODO
 * @date: 2024/4/12 15:48
 * @version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class BlogController {

    private static final String jekyllProjectPath = "F:\\OneDrive\\日常coding\\yunfei.github.io";

    private static final String postsDirName = "_posts";

    private static final String blogsPath = jekyllProjectPath + File.separator + postsDirName;

    private static List<String> uploadJekyllProjectToGithubCommandList = new ArrayList<String>(){{
        add("git add .");
        add("git commit -m 'docs: auto commit blog'");
        add("git push");
    }};


    @Builder
    @Data
    private static class BlogFileEntry implements Serializable {
        private String fileName;
        private String content;
        private Long fileSize;
        private String identifier;
    }
    @GetMapping("/blog/list")
    public R getBlogList() {
        List<File> fileList = processDirectory(new File(blogsPath));
        List<BlogFileEntry> blogFileEntries = new ArrayList<>();
        AtomicReference<Boolean> exceptionFlag = new AtomicReference<>(false);
        fileList.forEach((f)->{
            BlogFileEntry entry = null;
            try {
                entry = BlogFileEntry.builder().fileSize(f.length()).
                        fileName(f.getName()).content(readFileContent(f)).identifier(FileUtils.generateIdentifier(f))
                .build();
            } catch (Exception e) {
                exceptionFlag.set(true);
                log.error(e.getMessage());
            }
            blogFileEntries.add(entry);
        });
        if (exceptionFlag.get()) {
            return R.error("生成blog信息时异常!");
        }

        R ok = R.ok();
        ok.setData(JSON.toJSONString(blogFileEntries));
        return ok;
    }

    @PostMapping("/blog/submit")
    public R submitBlog(@RequestBody Map<String, Object> requestBody) {
        String content = (String) requestBody.get("markdown");
        String blogName = (String) requestBody.get("blogName");
        String filePath = blogsPath + File.separator + blogName + ".md";
        try {
            File file = new File(filePath);
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
            log.info("File written successfully.");
        } catch (IOException e) {
            log.error(e.getMessage());
            return R.error(e.getMessage());
        }

        return uoloadJekyllProjectToGithub();
    }

    private static R uoloadJekyllProjectToGithub() {
        AtomicInteger exitcode = new AtomicInteger();
        uploadJekyllProjectToGithubCommandList.forEach((command)->{
            exitcode.updateAndGet(v -> v | CommandRunner.execution(jekyllProjectPath, command));
        });
        if (exitcode.get() != 0) {
            log.error("upload the jekyll project to github error.");
            return R.error("upload the jekyll project to github error.");
        }
        return R.ok();
    }

    @DeleteMapping("/blog/delete/{identifier}")
    public R deleteBlog(@PathVariable("identifier") String identifier) {
        List<File> fileList = processDirectory(new File(blogsPath));
        AtomicReference<Boolean> exceptionFlag = new AtomicReference<>(false);
        fileList.stream().filter(f->{
            boolean flag = false;
            try {
                flag = FileUtils.generateIdentifier(f).equals(identifier);
            } catch (Exception e) {
                exceptionFlag.set(true);
                log.error(e.getMessage());
            }
            return flag;
        }).findFirst().ifPresent(file -> {
            try {
                Files.delete(file.toPath()); // 删除文件
                log.info("File deleted: " + file.getAbsolutePath());
            } catch (IOException e) {
                log.error("Failed to delete file: " + file.getAbsolutePath());
                exceptionFlag.set(true);
            }
        });
        if (exceptionFlag.get()) {
            return R.error("删除blog时异常!");
        }
        return uoloadJekyllProjectToGithub();
    }

    private static List<File> processDirectory(File directory) {
        List<File> fileList = new ArrayList<>();
        // 获取目录下的所有文件和子目录
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // 处理文件
                    fileList.add(file);
                } else if (file.isDirectory()) {
                    // 递归处理子目录
                    processDirectory(file);
                }
            }
        }
        return fileList;
    }

    private static String readFileContent(File file) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }
}
