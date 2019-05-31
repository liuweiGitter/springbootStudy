package com.xxl.job.executor.task.hxtnms.operationplan.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.xxl.job.executor.task.common.DoubleLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

@Slf4j
/**
 * Author: liuwei
 * Date: 2019-05-15 09:29
 * Desc: ftp下载文件工具类
 */
public class FtpDownload {
    private String username;
    private String password;
    private String ftpHostName;
    private int port;
    private FTPClient ftpClient = new FTPClient();
    private int retrySize = 3;

    public FtpDownload(String username, String password, String ftpHostName, int port) {
        super();
        this.username = username;
        this.password = password;
        this.ftpHostName = ftpHostName;
        this.port = port;
    }

    /**
     * 建立连接
     */
    private boolean connect() {
        if (ftpClient.isConnected()){
            return true;
        }
        try {
            DoubleLog.info("开始连接ftp服务器 "+ftpHostName+":"+port);
            // 连接
            ftpClient.connect(ftpHostName, port);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
            }
            // 登录
            ftpClient.login(username, password);
            ftpClient.setBufferSize(1024*1024);
            ftpClient.setReceiveBufferSize(1024*1024);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setRemoteVerificationEnabled(false);
            ftpClient.setControlKeepAliveTimeout(60);
            //ftpClient.setControlEncoding("utf8");
            DoubleLog.info("服务器登录成功！");
        } catch (IOException e) {
            DoubleLog.infoArg("服务器连接或登录失败！", e);
            return false;
        }
        return true;
    }

    //文件下载：下载最新的文件
    public boolean downLastFile(String ftpFileDir, String localDir) {
        if (!connect()){
            return false;
        }
        File temp = new File(localDir);
        if (!temp.exists()) {
            temp.mkdirs();
        }
        FTPFile[] listFiles = null;
        int tryCount = 0;
        //由于网络原因，可能一次读取会失败，重试3次
        do{
            try {
                //切到目标目录
                ftpClient.changeWorkingDirectory(ftpFileDir);
                listFiles = ftpClient.listFiles();
                break;
            } catch (IOException e) {
                tryCount++;
                if(tryCount==retrySize){
                    DoubleLog.infoArg("服务器列举目标文件失败！",e);
                    return false;
                }
            }
        }while (tryCount<retrySize);
        //不论来源是xls还是xlsx，一律写入xlsx文件
        String suffix = ".xlsx";
        Long timeFlag = 0L;
        Long addTime = 0L;
        int flag = 0;
        if (listFiles.length > 0) {
            //文件名是不明确的，获取时间最新的那个
            for (int i = 0; i < listFiles.length; i++) {
                addTime = listFiles[i].getTimestamp().getTime().getTime();
                if(addTime>timeFlag){
                    flag = i;
                    timeFlag = addTime;
                }
            }
            //ftp最新文件写入本地并重命名为user_msg_download，如果有也会覆盖
            File localFile = new File(localDir + File.separator
                    + "user_msg_download"+suffix);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(localFile);
            } catch (FileNotFoundException e) {
                DoubleLog.infoArg("本地文件创建失败！",e);
                return false;
            }
            DoubleLog.info("服务器目标文件 "+listFiles[flag].getName());
            //由于网络原因，可能一次读取会失败，重试3次
            tryCount = 0;
            do{
                try {
                    ftpClient.retrieveFile(listFiles[flag].getName(), fos);
                    break;
                } catch (IOException e) {
                    tryCount++;
                    if(tryCount==retrySize){
                        DoubleLog.infoArg("服务器目标文件写入本地失败！",e);
                        return false;
                    }
                }
            }while (tryCount<retrySize);
            DoubleLog.info("服务器目标文件下载成功！");
            return true;
        } else {
            DoubleLog.info("服务器目标文件不存在！");
        }
        return false;
    }

}