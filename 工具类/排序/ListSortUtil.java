package com.jshx.zq.p2p.util;
import java.util.*;

/**
 * @author liuwei
 * @date 2020-01-15 09:03
 * @desc list列表排序
 */
public class ListSortUtil {

    public static final int NUM = 0;
    public static final int STRING = 1;

    /**
     * List<Map<String,Object>>排序：支持多列同一顺序排序(即多列升序或降序)
     * @param mapList 源数据
     * @param asc 是否升序
     * @param type 数据类型：数字和字符串
     * @param keys 按顺序比较的key值数组，支持数字和字符串列的排序
     */
    public static void mapSort(List<Map<String,Object>> mapList,boolean asc,int type,String... keys) {
        boolean[] ascArr = new boolean[keys.length];
        int[] types = new int[keys.length];
        for (int i = 0; i < ascArr.length; i++) {
            ascArr[i] = asc;
            types[i] = type;
        }
        mapSort(mapList,keys,ascArr,types);
    }

    /**
     * List<Map<String,Object>>排序：支持多列不同顺序排序
     * @param mapList 源数据
     * @param keys 按顺序比较的key值数组，支持数字和字符串列的排序
     * @param ascArr 按顺序排序的数组
     * @param types 按顺序排序的数据类型
     */
    public static void mapSort(List<Map<String,Object>> mapList,String[] keys,boolean[] ascArr,int[] types) {
        //只对size>=2的列表排序
        if (mapList.size()<2) {
            return;
        }
        //首轮整体排序
        sort(mapList,keys[0],ascArr[0],types[0]);
        if (keys.length<2) {
            return;
        }
        /**
         * 次轮分批次局部排序
         */
        for (int i = 1; i < keys.length; i++) {
            String key = keys[i];
            String keyBefore = keys[i-1];
            int startIndex = -1;
            int endIndex = -1;
            for (int j = 1; j < mapList.size(); j++) {
                //上一轮排序值相等：记录起始索引
                if (mapList.get(j).get(keyBefore).toString().equals(mapList.get(j-1).get(keyBefore).toString())) {
                    if (startIndex==-1) {
                        startIndex = j-1;
                    }
                    endIndex = j;
                }else {
                    //已经找到局部排序的起始索引：可以局部排序
                    if (startIndex!=-1){
                        sort(mapList.subList(startIndex,endIndex+1),key,ascArr[i],types[i]);
                        startIndex = endIndex = -1;
                    }
                }
            }
            //循环内如果有残留数据，此处进行补充排序
            if (startIndex!=-1){
                sort(mapList.subList(startIndex,endIndex+1),key,ascArr[i],types[i]);
            }
        }
    }

    private static void sort(List<Map<String,Object>> mapList,String key,boolean asc,int type){
        Collections.sort(mapList, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                String v1 = o1.get(key).toString();
                String v2 = o2.get(key).toString();
                switch (type){
                    case NUM:
                        Double v1d = Double.valueOf(v1);
                        Double v2d = Double.valueOf(v2);
                        return asc?v1d.compareTo(v2d):v2d.compareTo(v1d);
                    default:
                        return asc?v1.compareTo(v2):v2.compareTo(v1);
                }
            }
        });
    }

}
