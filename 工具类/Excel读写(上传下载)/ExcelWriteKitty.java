package com.telecom.js.noc.hxtnms.operationplan.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: liuwei
 * Date: 2019-05-24 10:43
 * Desc: 简单excel(单sheet单行header)写入/导出工具类
 */
@Slf4j
public class ExcelWriteKitty {

    //工作簿
    private HSSFWorkbook workbook = new HSSFWorkbook();
    //工作表
    private HSSFSheet sheet;

    //工作表名称
    private String sheetName;
    //表头
    private String[] headerNames;
    //表头对应的数据库cloumn值(map keyset)，内部使用变量
    private String[] columnNames;
    //表头样式
    private HSSFCellStyle headerStyle;
    //表体样式
    private HSSFCellStyle bodyStyle;
    //结果集从第几列开始读写，默认从第1列开始，允许自定义开始列，列数基于0
    private int mapResultStartIndex = 0;

    public ExcelWriteKitty(String sheetName, String[] headerNames, HSSFCellStyle headerStyle, HSSFCellStyle bodyStyle){
        init(sheetName,headerNames,headerStyle,bodyStyle);
        createSheet();
    }

    public ExcelWriteKitty(String sheetName, String[] headerNames){
        init(sheetName,headerNames,workbook.createCellStyle(),workbook.createCellStyle());
        defaultStyle();
        createSheet();
    }

    //设置从第mapResultStartIndex列开始读写，列数基于0
    public ExcelWriteKitty setStartColumnIndex(int mapResultStartIndex){
        this.mapResultStartIndex = mapResultStartIndex;
        return this;
    }

    public HSSFWorkbook getWorkbook(){
        return this.workbook;
    }

    private void init(String sheetName, String[] headerNames, HSSFCellStyle headerStyle, HSSFCellStyle bodyStyle){
        this.sheetName = sheetName;
        this.headerNames = headerNames;
        this.headerStyle = headerStyle;
        this.bodyStyle = bodyStyle;
        this.columnNames = new String[headerNames.length];
    }

    //初始化map数据列名
    private void initColumnName(Set<String> keySet){
        int keySize = keySet.size();
        //如果查询结果集中列数减去开始导出的列的下标后小于表头列的列数，抛出异常
        if (headerNames.length > keySize-mapResultStartIndex){
            throw new IllegalStateException("查询结果集共"+keySet.size()+
                    "列，指定导出"+
                    (keySize-mapResultStartIndex)+"列，表头共"+headerNames.length+"列，表头列数过多！");
        }
        String[] temArray = new String[keySize];
        temArray = keySet.toArray(temArray);
        for(int i = 0;i<headerNames.length;i++){
            columnNames[i] = temArray[i+mapResultStartIndex];
        }
    }

    //默认样式
    private void defaultStyle(){
        /**
         * 1.表头设置
         */
        // 生成一种字体
        HSSFFont headerFont = workbook.createFont();
        // 设置字体
        headerFont.setFontName("微软雅黑");
        // 设置字体大小
        headerFont.setFontHeightInPoints((short) 12);
        // 字体加粗
        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 在样式中引用这种字体
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(HSSFColor.WHITE.index);

        /**
         * 2.表体设置
         */
        // 生成一种字体
        HSSFFont bodyFont = workbook.createFont();
        // 设置字体
        bodyFont.setFontName("微软雅黑");
        // 设置字体大小
        bodyFont.setFontHeightInPoints((short) 12);
        // 在样式中引用这种字体
        bodyStyle.setFont(bodyFont);

        //3.公共设置
        for (HSSFCellStyle style:new HSSFCellStyle[]{headerStyle,bodyStyle}) {
            //设置样式
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        }
    }

    //创建工作表
    private void createSheet() {
        // 生成一个表格
        sheet = workbook.createSheet(sheetName);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(15);
        // 产生表格标题行
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headerNames.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(new HSSFRichTextString(headerNames[i]));
        }
    }

    /**
     * 写入excel，不保存到文件(只驻留JVM)
     */
    public boolean writeExcel(List<Map<String,Object>> dataListMap){
        if (null==dataListMap || dataListMap.size()==0){
            log.info("无数据写入！");
            return false;
        }
        //初始化列名
        initColumnName(dataListMap.get(0).keySet());
        int dataCount = dataListMap.size();
        int columnCount = headerNames.length;

        Map<String,Object> map = null;
        HSSFRow currentRow = null;
        HSSFCell cell = null;
        //遍历每一行数据
        for (int i=0;i<dataCount;i++){
            map = dataListMap.get(i);
            currentRow = sheet.createRow(i+1);
            //遍历每一个单元格
            for (int j = 0; j < columnCount; j++) {
                cell = currentRow.createCell(j);
                cell.setCellStyle(bodyStyle);
                //赋值
                setCellValue(cell,map.get(columnNames[j]));
            }
        }
        return true;
    }

    /**
     * 单元格赋值相应数据类型
     * @param cell
     * @param value
     */
    private void setCellValue(HSSFCell cell,Object value){
        if (value instanceof String){//字符串
            cell.setCellValue(value.toString());
        }else if (value instanceof Integer){//整数
            cell.setCellValue((Integer) value);
        }else if (value instanceof Double){//浮点数
            cell.setCellValue((Double) value);
        }else if (value instanceof Date){//日期
            cell.setCellValue((Date) value);
        }else {//其它全部设置为字符串，用户获取excel后，根据需要自定义数据类型
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 写入excel，保存到本地文件或异地输出流(到浏览器或网络管道)
     */
    public boolean writeExcel(List<Map<String,Object>> dataListMap, OutputStream out){
        if (!writeExcel(dataListMap)){
            return false;
        }
        try {
            //workbook.write(new FileOutputStream("E:\\123.xlsx"));postman传参，本地写入文件测试
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            log.info("工作簿保存到输出流失败！",e);
            return false;
        }
        return true;
    }

}
