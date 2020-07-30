package com.ping.job.cover.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuwei
 * @date 2020-07-29 15:41
 * @desc linux shell命令、脚本执行工具类
 */
public class ShellCommandUtil {

    //运行时环境
    private final static Runtime SYS_RUN = Runtime.getRuntime();

    /**
     * 执行命令，返回多行结果
     */
    public static List<String> cmdMultiResult(String command) throws IOException {
        return multiResult(new String[]{"sh","-c",command});
    }

    /**
     * 执行脚本，返回多行结果
     */
    public static List<String> scriptMultiResult(String command) throws IOException {
        return multiResult(new String[]{"sh", command});
    }

    private static List<String> multiResult(String[] commandArr) throws IOException {
        Process process = SYS_RUN.exec(commandArr);
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        List<String> list = new ArrayList<>();
        while ((line = input.readLine()) != null) {
            list.add(line);
        }
        input.close();
        return list;
    }

    /**
     * 执行命令，返回一行结果
     */
    public static String cmdSingleResult(String command) throws IOException {
        return singleResult(new String[]{"sh","-c",command});
    }

    /**
     * 执行脚本，返回一行结果
     */
    public static String scriptSingleResult(String command) throws IOException {
        return singleResult(new String[]{"sh",command});
    }

    private static String singleResult(String[] commandArr) throws IOException {
        Process process = SYS_RUN.exec(commandArr);
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = input.readLine();
        input.close();
        return result;
    }

    /**
     * 执行命令，不需要返回结果
     */
    public static void cmdEmptyResult(String command) throws IOException {
        SYS_RUN.exec(new String[]{"sh","-c",command});
    }

    /**
     * 执行脚本，不需要返回结果
     */
    public static void scriptEmptyResult(String command) throws IOException {
        SYS_RUN.exec(new String[]{"sh",command});
    }

}
