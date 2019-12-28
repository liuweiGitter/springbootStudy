package com.jshx.zq.p2p.util;

import com.jshx.zq.p2p.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liuwei
 * @date 2019-12-16 15:50
 * @desc excel读取工具类
 *
 * 支持读取xls和xlsx文件
 * 支持指定sheet下标和row下标
 * 支持数字和日期时间的格式化
 * 支持excel公式的解析
 *
 * 支持上述所有操作的默认行为
 */
@Slf4j
public class ExcelReadKitty {

    private static final String XLS = "xls";

    private static final String XLSX = "xlsx";

    private static final int DEFAULT_SHEET_INDEX = 0;

    private static final int DEFAULT_ROW_INDEX = 1;

    /**
     * 将数字单元格格式化
     * 避免科学计数法，并指定小数位数
     */
    public static final DecimalFormat DEFAULT_DIGIT_FMT = new DecimalFormat("0");
    public static final DecimalFormat FM_FLOAT1 = new DecimalFormat("0.0");
    public static final DecimalFormat FM_FLOAT2 = new DecimalFormat("0.00");
    public static final DecimalFormat FM_FLOAT3 = new DecimalFormat("0.000");
    public static final DecimalFormat FM_FLOAT4 = new DecimalFormat("0.0000");

    /**
     * 日期时间格式化
     */
    public static final SimpleDateFormat DEFAULT_DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat FM_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat FM_TIME = new SimpleDateFormat("HH:mm:ss");

    /**
     * 数字格式化
     * 单元格数字、日期、时间都对应CELL_TYPE_NUMERIC
     * 但日期时间格式需要特殊处理
     * 所有的数字和日期时间都要进行默认的或指定格式的格式化处理
     */
    private DecimalFormat decimalFormat = DEFAULT_DIGIT_FMT;
    private Map<Integer, DecimalFormat> decimalFormatMap;
    private SimpleDateFormat dateFormat = DEFAULT_DATE_FMT;
    private Map<Integer, SimpleDateFormat> dateFormatMap;

    /**
     * 解析excel公式
     * 注：并不是所有的公式都被支持
     */
    private FormulaEvaluator evaluator;
    private DecimalFormat decimalFunction = DEFAULT_DIGIT_FMT;

    /**
     * 默认读取的sheet下标和数据起始行下标
     */
    private int sheetIndex = DEFAULT_SHEET_INDEX;
    private int rowStartIndex = DEFAULT_ROW_INDEX;

    private Workbook workbook;

    //==================构造函数==================//

    public ExcelReadKitty() {
    }

    //所有数字列的格式化
    public ExcelReadKitty(DecimalFormat decimalFormat) {
        patternDecimal(decimalFormat,null);
    }

    //指定数字列的格式化
    public ExcelReadKitty(Map<Integer, DecimalFormat> decimalFormatMap) {
        patternDecimal(null,decimalFormatMap);
    }

    //所有日期列的格式化
    public ExcelReadKitty (SimpleDateFormat dateFormat) {
        patternDate(dateFormat,null);
    }

    //指定日期列的格式化
    public ExcelReadKitty (Map<Integer, SimpleDateFormat> dateFormatMap, String flag) {
        /**
         * 添加一个额外参数flag的目的是区分构造方法
         * 因为Java只区分类型而不区分类型中带有的泛型
         * 使用debug消除flag产生的代码检查影响
         */
        log.debug(flag);
        patternDate(null,dateFormatMap);
    }

    //指定读取的sheet页和起始行下标
    public ExcelReadKitty (int sheetIndex, int rowStartIndex) {
        dataIndex(sheetIndex, rowStartIndex);
    }

    //==================对象的连缀操作==================//

    //数字格式化
    public ExcelReadKitty patternDecimal(DecimalFormat decimalFormat, Map<Integer, DecimalFormat> decimalFormatMap) {
        if (decimalFormat!=null) {
            this.decimalFormat = decimalFormat;
            this.decimalFormatMap = null;
        }else if (decimalFormatMap!=null) {
            this.decimalFormatMap = decimalFormatMap;
            this.decimalFormat = null;
        }else{
            throw new RuntimeException("init ExcelReadKitty error from patternDecimal with null params!");
        }
        return this;
    }

    //日期格式化
    public ExcelReadKitty patternDate(SimpleDateFormat dateFormat, Map<Integer, SimpleDateFormat> dateFormatMap) {
        if (dateFormat!=null) {
            this.dateFormat = dateFormat;
            this.dateFormatMap = null;
        }else if (dateFormatMap!=null) {
            this.dateFormatMap = dateFormatMap;
            this.dateFormat = null;
        }else{
            throw new RuntimeException("init ExcelReadKitty error from patternDate with null params!");
        }
        return this;
    }

    //excel公式计算结果数字格式化
    public ExcelReadKitty patternFunction(DecimalFormat decimalFunction) {
        if (decimalFunction!=null) {
            this.decimalFunction = decimalFunction;
        }
        return this;
    }

    //指定读取的sheet页和起始行下标
    public ExcelReadKitty dataIndex(int sheetIndex, int rowStartIndex) {
        this.sheetIndex = sheetIndex;
        this.rowStartIndex = rowStartIndex;
        return this;
    }

    //==================绝对路径读取excel==================//

    /**
     * 读取excel
     *
     * @param absolutePath 绝对路径
     * @param keys         Map<sheet表列索引,列索引对应的key名称>，如2,city表示索引2列名称为city
     * @return
     */
    public List<Map<String, Object>> readExcel(String absolutePath, Map<Integer, String> keys) {
        String[] keyArr = new String[keys.size()];
        Integer[] indexArr = new Integer[keys.size()];
        keyIndexInit(keys, keyArr, indexArr);
        if (absolutePath.endsWith(XLS)) {
            return readExcel(absolutePath, keyArr, indexArr, XLS);
        } else if (absolutePath.endsWith(XLSX)) {
            return readExcel(absolutePath, keyArr, indexArr, XLSX);
        } else {
            throw new BaseException("excel tail error check which in (xls,xlsx)!");
        }
    }

    /**
     * 读取excel
     *
     * @param absolutePath 绝对路径
     * @param keyArr       列索引对应的key名称，顺序对应
     * @return
     */
    public List<Map<String, Object>> readExcel(String absolutePath, String[] keyArr) {
        Integer[] indexArr = getIndexArr(keyArr);
        if (absolutePath.endsWith(XLS)) {
            return readExcel(absolutePath, keyArr, indexArr, XLS);
        } else if (absolutePath.endsWith(XLSX)) {
            return readExcel(absolutePath, keyArr, indexArr, XLSX);
        } else {
            throw new BaseException("excel tail error check which in (xls,xlsx)!");
        }
    }

    //==================文件读取excel==================//

    /**
     * 读取excel
     *
     * @param file MultipartFile格式文件
     * @param keys Map<sheet表列索引,列索引对应的key名称>，如2,city表示索引2列名称为city
     * @return
     */
    public List<Map<String, Object>> readExcel(MultipartFile file, Map<Integer, String> keys) {
        String[] keyArr = new String[keys.size()];
        Integer[] indexArr = new Integer[keys.size()];
        keyIndexInit(keys, keyArr, indexArr);
        return readExcel(file, keyArr, indexArr);
    }

    /**
     * 读取excel
     *
     * @param file   MultipartFile格式文件
     * @param keyArr 列索引对应的key名称，顺序对应
     * @return
     */
    public List<Map<String, Object>> readExcel(MultipartFile file, String[] keyArr) {
        Integer[] indexArr = getIndexArr(keyArr);
        return readExcel(file, keyArr, indexArr);
    }


    //==================私有方法==================//

    private List<Map<String, Object>> readExcel(MultipartFile file, String[] keyArr, Integer[] indexArr) {
        String fileName = file.getOriginalFilename();
        InputStream is = null;
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            is = file.getInputStream();
            if (fileName.endsWith(XLS)) {
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(XLSX)) {
                workbook = new XSSFWorkbook(is);
            } else {
                ResourceClose.close(is);
                throw new BaseException("excel tail error check which in (xls,xlsx)!");
            }
            //读取sheet
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            readToList(sheet, keyArr, indexArr, list);
        } catch (IOException e) {
            LogAndThrowException.error("excel [" + fileName + "] read error !", e);
        } finally {
            ResourceClose.close(is);
        }
        return list;
    }

    private List<Map<String, Object>> readExcel(String absolutePath, String[] keyArr, Integer[] indexArr, String type) {
        List<Map<String, Object>> list = new ArrayList<>();
        try{
            if (type.equals(XLS)) {
                workbook = new HSSFWorkbook(new FileInputStream(absolutePath));
            } else {
                workbook = new XSSFWorkbook(new FileInputStream(absolutePath));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BaseException("excel [" + absolutePath + "] read error!");
        }
        //读取sheet
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        readToList(sheet, keyArr, indexArr, list);
        return list;
    }

    private void readToList(Sheet sheet, String[] keyArr, Integer[] indexArr, List<Map<String, Object>> list) {
        //从第rowStartIndex行开始遍历数据：之前为标题行或空行等无效数据行
        for (Row row : sheet) {
            if (row.getRowNum() < rowStartIndex) {
                continue;
            }
            Map<String, Object> data = new HashMap<>();
            for (int i = 0; i < keyArr.length; i++) {
                data.put(keyArr[i], getCellValue(row.getCell(indexArr[i]), i));
            }
            list.add(data);
        }
    }

    private Object getCellValue(Cell cell, int index) {
        if (cell==null) {
            return "";
        }
        int type = cell.getCellType();
        switch (type) {
            case Cell.CELL_TYPE_NUMERIC:
                //判断NUMERIC类型是数字还是日期时间
                if (isDateOrTime(cell.getCellStyle().getDataFormat())) {
                    Date value = cell.getDateCellValue();
                    return dateFormat != null ? dateFormat.format(value) : dateFormatMap.get(index).format(value);
                } else {
                    double value = cell.getNumericCellValue();
                    return decimalFormat != null ? decimalFormat.format(value) : decimalFormatMap.get(index).format(value);
                }
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_FORMULA:
                //解析excel公式
                return parseFunction(cell);
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_ERROR:
                return cell.getErrorCellValue();
            default:
                return "";
        }
    }

    private void keyIndexInit(Map<Integer, String> keys, String[] keyArr, Integer[] indexArr) {
        Set<Map.Entry<Integer, String>> set = keys.entrySet();
        Iterator<Map.Entry<Integer, String>> iterator = set.iterator();
        List<Integer> index = new ArrayList<>();
        List<String> key = new ArrayList<>();
        if (iterator.hasNext()) {
            Map.Entry<Integer, String> indexKey = iterator.next();
            index.add(indexKey.getKey());
            key.add(indexKey.getValue());
        }
        for (int i = 0; i < index.size(); i++) {
            keyArr[i] = key.get(i);
            indexArr[i] = index.get(i);
        }
    }

    private Integer[] getIndexArr(String[] keyArr) {
        Integer[] indexArr = new Integer[keyArr.length];
        for (int i = 0; i < keyArr.length; i++) {
            indexArr[i] = i;
        }
        return indexArr;
    }

    /**
     * 判断一个格式是不是日期或时间
     * @param formatIndex 格式的索引
     * @return
     *
     * yyyy-MM-dd   14
     * yyyy年m月d日    31
     * yyyy年m月  57
     * m月d日 58
     * HH:mm    20
     * h时mm分    32
     * 整数和小数都是0
     */
    private static boolean isDateOrTime(int formatIndex) {
        if (formatIndex==0) {
            return false;
        }
        switch (formatIndex) {
            case 14:
            case 31:
            case 57:
            case 58:
            case 20:
            case 32:
                return true;
            default:
                return false;
        }
    }

    /**
     * 解析excel公式
     * @param cell 单元格对象
     * @return
     */
    private Object parseFunction(Cell cell){
        if (evaluator == null) {
            evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        }
        return formatFunction(evaluator.evaluate(cell));
    }

    /**
     * 公式计算结果的取值和格式化
     * 计算的结果可能是数字、字符串或布尔值
     * 数字需要格式化，默认不保留小数
     * @param cell 单元格公式计算结果对象
     * @return
     */
    private Object formatFunction(CellValue cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringValue();
            case Cell.CELL_TYPE_NUMERIC:
                return decimalFunction.format(cell.getNumberValue());
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanValue();
            case Cell.CELL_TYPE_ERROR:
                return cell.getErrorValue();
            default:
                return "";
        }
    }

}
