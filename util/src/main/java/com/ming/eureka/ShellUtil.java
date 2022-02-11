package com.ming.eureka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * shell工具
 * Created by KJ-9659 on 2017/11/8.
 */
public class ShellUtil {

    private static Logger logger = LoggerFactory.getLogger(ShellUtil.class);

    public static int callScript(String script, String args, String... workspace) {
        try {
            String cmd = "sh " + script + " " + args;
            logger.warn("输出命令 {} ", cmd);
            File dir = null;
            if (workspace[0] != null) {
                dir = new File(workspace[0]);
                logger.warn("调用目录 {} ", workspace[0]);
            }
            Process process = Runtime.getRuntime().exec(cmd, null, dir);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                logger.warn("输出日志 {} ", line);
            }
            process.waitFor();
            input.close();
            return 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 1;
        }
    }
}
