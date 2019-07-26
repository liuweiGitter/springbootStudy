package com.telecom.js.noc.hxtnms.operationplan.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author liuwei
 * @date 2019-07-22 00:47
 * @desc excel导出入口
 */
@Slf4j
public class ExcelExportMan {

    /**
     *
     * @param response 请求的响应对象
     * @param list 查询的结果集
     * @param kitty ExcelWriteKitty对象
     * @param fileNameSuffix 导出表格的文件名后缀
     * @return
     */
    public static ResponseBox downloadExcel(HttpServletResponse response, List<Map<String, Object>> list, ExcelWriteKitty kitty, String fileNameSuffix){
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.TIME_TYPE_TOGETHER)) +"_"+fileNameSuffix+".xls";
        response.setContentType("application/x-msdownload");
        try {
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "iso8859-1"));
        } catch (UnsupportedEncodingException e) {
            log.info("字符集不支持！",e);
            return new ResponseBox("字符集不支持！");
        }
        try {
            kitty.writeExcel(list,response.getOutputStream());
        } catch (IOException e) {
            log.info("导出数据失败！",e);
            return new ResponseBox("导出数据失败！",false);
        }
        return new ResponseBox("导出数据成功！",true);
    }
}
