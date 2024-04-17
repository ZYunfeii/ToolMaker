package com.yunfei.toolmaker;

import com.yunfei.toolmaker.util.PythonCaller;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Scanner;


@Slf4j
public class TestChat8Api {
    @Test
    public void testChat8Api() {
        String call = PythonCaller.call("chat8-crawler/req.py", new String[]{"你好讨厌!"});
        System.out.println(call);
    }

    @Test
    public void testOthers() {

    }
}
