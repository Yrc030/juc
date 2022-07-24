package com.yrc.juc.example1;


import com.yrc.juc.example1.util.FileReader;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * Description:
 * User: joker
 * Date: 2022-07-22-18:21
 * Time: 18:21
 */
@Slf4j(topic = "c.Sync")
public class Sync {
    public static void main(String[] args) {
        // 同步执行
        FileReader.read("src/main/java/com/yrc/juc/example1/text.txt"); // 先执行，执行完毕后在继续执行下面的代码
        log.debug("do other things..."); // 等 FileReader.read() 执行完毕后才开始执行
    }
}
