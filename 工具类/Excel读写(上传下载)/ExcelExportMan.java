package com.jshx.zq.p2p.util;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liuwei
 * @date 2019-07-22 00:47
 * @desc excel导出入口
 */
@Slf4j
public class ExcelExportMan {

    private ExcelExportMan(){
        throw new IllegalStateException("Utility class");
    }

    /**
     *
     * @param response 请求的响应对象
     * @param list 查询的结果集：Map<String, Object>格式
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
        boolean result;
        try {
            result = kitty.writeExcel(list,response.getOutputStream());
        } catch (IOException e) {
            log.info("导出数据失败！",e);
            return new ResponseBox("导出数据失败，服务器内部错误！");
        }
        return new ResponseBox(result?"导出数据成功！":"导出数据失败，数据不存在！",result);
    }

    /**
     *
     * @param response
     * @param list 查询的结果集：Bean格式
     * @param kitty
     * @param fileNameSuffix
     * @param <T>
     * @return
     */
    public static <T> ResponseBox downloadExcelByBean(HttpServletResponse response, List<T> list, ExcelWriteKitty kitty, String fileNameSuffix){
        if (kitty.isColumnNamesNull()) {
            return new ResponseBox("使用本方法导出数据时必须指定数据列名！");
        }
        //转为map格式
        List<Map> mapList = JSONArray.parseArray(JSONArray.toJSONString(list),Map.class);
        List<Map<String, Object>> excelMapList = new ArrayList<>(mapList.size());
        for (Map map : mapList) {
            excelMapList.add(map);
        }
        return downloadExcel(response,excelMapList,kitty,fileNameSuffix);
    }


}
