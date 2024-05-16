package com.yunfei.toolmaker.controller;

import com.alibaba.fastjson.JSON;
import com.yunfei.toolmaker.exception.ErrorCodeEnum;
import com.yunfei.toolmaker.util.CommandRunner;
import com.yunfei.toolmaker.util.FileUtils;
import com.yunfei.toolmaker.util.PageInfo;
import com.yunfei.toolmaker.util.R;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

    private static final String jekyllProjectPath = "/root/project/zyunfeii.github.io";

    private static final String postsDirName = "_posts";

    private static final String blogsPath = jekyllProjectPath + File.separator + postsDirName;

    private static final List<String> uploadJekyllProjectToGithubCommandList = new ArrayList<String>(){{
        add("git add -A");
        add("git commit -m 'docs: auto commit blog'");
        add("git push");
    }};

    @PostMapping("/login")
    public R blogLogin(@RequestBody Map<String, Object> requestBody) {
        String username = (String) requestBody.get("username");
        String password = (String) requestBody.get("password");
        try{
            //省略了其他的常规代码，比如判断字段是否为空之类的
            //此Subject就是开头提到的  代表当前用户
            Subject subject = SecurityUtils.getSubject();
            //用请求的用户名和密码创建UsernamePasswordToken(此类来自shiro包下)
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
            //调用subject.login进行验证，验证不通过则会抛出AuthenticationException异常，然后自定义返回信息
            subject.login(usernamePasswordToken);
            //未抛异常 则验证通过
            //此Session也来自shiro包  是对传统的HttpSession的封装，可以看做是一样的
//            Session session = subject.getSession();
        } catch (AuthenticationException e) {
            //这行也是自定义的代码，随你怎么写
            return R.error("认证失败！原因：" + e.getMessage());
        }
        return R.ok();
    }

    @GetMapping("/isLogin")
    public R isLogin() {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            // 如果已经登录就返回用户名称
            return R.ok(currentUser.getPrincipal().toString());
        } else {
            return R.error(ErrorCodeEnum.SHIRO_NOT_LOGIN.getCode(), ErrorCodeEnum.SHIRO_NOT_LOGIN.getMsg());
        }
    }

    @Builder
    @Data
    private static class BlogFileEntry implements Serializable {
        private String fileName;
        private String content;
        private Long fileSize;
        private String identifier;
        private String url;
    }
    @GetMapping(value = "/blog/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R getBlogList(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize) {
        List<File> fileList = processDirectory(new File(blogsPath));
        List<BlogFileEntry> blogFileEntries = new ArrayList<>();
        AtomicReference<Boolean> exceptionFlag = new AtomicReference<>(false);
        fileList.forEach((f)->{
            BlogFileEntry entry = null;
            try {
                int firstIndex = f.getName().indexOf("-");
                int secondIndex = f.getName().indexOf("-", firstIndex + 1);
                int thirdIndex = f.getName().indexOf("-", secondIndex + 1);
                int dotIndex = f.getName().indexOf(".md");
                String title = null;
                if (firstIndex != -1 && secondIndex != -1 && thirdIndex != -1 && dotIndex != -1 && thirdIndex < dotIndex) {
                    title = f.getName().substring(thirdIndex + 1, dotIndex);
                } else {
                    log.info("Invalid title");
                }
                entry = BlogFileEntry.builder().fileSize(f.length())
                                .fileName(f.getName()).content(readFileContent(f)).identifier(FileUtils.generateIdentifier(f))
                                .url("http://zyunfeii.github.io/posts/" + title)
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

        List<BlogFileEntry> blogEntriesAfterPagination = new ArrayList<>(blogFileEntries.subList(
                (page - 1) * pageSize,
                Math.min((page - 1) * pageSize + pageSize, blogFileEntries.size())
        ));

        R ok = R.ok();
        PageInfo pageInfo = new PageInfo(fileList.size(), page, pageSize, blogEntriesAfterPagination);
        ok.setData(JSON.toJSONString(pageInfo));
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
        log.info("find {} files", files.length);

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    log.info("{} is a file.", file.getName());
                    // 处理文件
                    fileList.add(file);
                } else if (file.isDirectory()) {
                    log.info("{} is not a file.", file.getName());
                    // 递归处理子目录
                    fileList.addAll(processDirectory(file));
                }
            }
        }
        log.info("find {} blogs", fileList.size());
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
