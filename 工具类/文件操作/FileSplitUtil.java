package com.jshx.zq.p2p.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuwei
 * @date 2020-07-17 10:24
 * @desc 文件分割工具类
 */
public class FileSplitUtil {

    private final static Runtime SYS_RUN = Runtime.getRuntime();
    private final static int DFT_ROW_NUM = 1000;

    /**
     * 大文件分割为小文件
     * @param originFilePath 大文件路径，绝对或相对路径
     * @param targetDirPath 生成的小文件的目录路径，相对当前路径，建议深度一层
     * @param targetFilePrefix 生成的小文件的名称前缀
     * @param splitRowNum 文件分割时，每个文件的行数，平均不允许低于1000行每文件
     * @return 小文件的路径列表，切割出现异常时返回new ArrayList()
     *
     * Usage Demo:
     *  bigFileSplit("/usr/local/data/123.txt", "test", "liuwei", 1000)
     *
     *  running result
     *  /usr/local/data/test/liuweiaa
     *  /usr/local/data/test/liuweiab
     *  ...
     */
    public static List<String> bigFileSplit(String originFilePath, String targetDirPath,
                                            String targetFilePrefix, int splitRowNum) {

        //校验
        validatorEmpty(originFilePath,targetDirPath,targetFilePrefix);
        if(splitRowNum < DFT_ROW_NUM){
            splitRowNum = DFT_ROW_NUM;
        }

        String absOriginPath = validatorFile(originFilePath);
        validatorStarts("/",targetDirPath,targetFilePrefix);

        //分割

        String command = "workpath=`pwd` && rm -rf "+targetDirPath+" && mkdir -p "+targetDirPath+" && " +
                "cd "+targetDirPath+" && split -l "+splitRowNum+" "+absOriginPath+" "+targetFilePrefix+
                " && cd $workpath && ls "+targetDirPath;

        System.out.println(command);

        Process process;
        try {
            process = SYS_RUN.exec(new String[]{"sh", "-c", command});
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String contentLine;
            List<String> responseArr = new ArrayList<>();
            while ((contentLine = input.readLine()) != null) {
                responseArr.add(targetDirPath+"/"+contentLine);
            }
            return responseArr;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    private static void validatorEmpty(String... args) {
        for (String arg : args) {
            if (stringIsEmpty(arg)) {
                throw new RuntimeException("validatorEmpty throw Exception");
            }
        }
    }

    private static void validatorStarts(String start, String... args) {
        for (String arg : args) {
            if (stringIsEmpty(arg) || arg.startsWith(start)) {
                throw new RuntimeException("validatorStarts throw Exception");
            }
        }
    }

    private static String validatorFile(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            throw new RuntimeException("validatorFile throw Exception");
        }
        return file.getAbsolutePath();
    }

    private static boolean stringIsEmpty(String str) {
        return null == str || str.isEmpty();
    }

}
