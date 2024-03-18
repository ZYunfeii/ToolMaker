package com.yunfei.toolmaker.util;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class CodeRunner {

    public static String runJavaCode(String code) throws Exception {
        StringBuilder outputBuilder = new StringBuilder();

        // 1. 使用JavaCompiler编译代码
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileObject sourceFile = new SimpleJavaFileObject(
                java.net.URI.create("string:///UserSolution.java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return code;
            }
        };

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sourceFile);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        boolean compilationSuccess = compiler.getTask(null, null, diagnostics, null, null, compilationUnits).call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            // 只追加error级别的信息，不包括warning
            if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                outputBuilder.append(diagnostic.toString());
                outputBuilder.append("\n");
            }
        }

        if (!compilationSuccess) {
            return outputBuilder.toString();
        }

        // 2. 使用反射API运行编译后的代码，并捕获输出
        ByteArrayOutputStream execOutputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(execOutputStream));

        try {
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File("").toURI().toURL()});
            Class<?> cls = Class.forName("UserSolution", true, classLoader);
            Method method = cls.getDeclaredMethod("main", String[].class);
            String[] params = null;  // 这里你可以传入main方法的参数
            method.invoke(null, (Object) params);
            outputBuilder.append(execOutputStream.toString());
        } finally {
            System.setOut(originalOut);
        }

        return outputBuilder.toString();
    }
}
