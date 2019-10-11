package com.liuwei.algorithm.sort.pkg4int;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-09-25 22:49:23
 * @desc 选择排序
 * 空间复杂度O(1)
 * 时间复杂度
 * 	1.最好情况O(n²)，具体为n*(n-1)/2次比较，0次交换
 * 	2.最坏情况O(n²)，具体为n*(n-1)/2次比较，同等次数交换
 * 	3.平均复杂度O(n²)，具体为n*(n-1)/2次比较，一半次数交换
 * 优点：空间复杂度低，不需要额外内存，算法简单易实现
 * 缺点：时间复杂度始终很高，在任何情况下时间效率都比冒泡排序低
 */
@Slf4j
public class SelectionSort {
	
	private static void sort(boolean isSortDesc) {
		int[] array = {5,3,7,6,8,4,1,0,9,2};
		int length = array.length;
		int selIndex;
		
		log.info("原始队列为"+JSONObject.toJSONString(array));
		
		/**
		 * 形如：{5,3,7,6,8}
		 * 第1次从下标0到length-1，找到最值后和排到下标0处，必要时进行交换
		 * 第2次从下标1比较到length-1，找到相对最值后排到下标1处，必要时进行交换
		 * ...
		 * 第length-1次从下标length-2比较到length-1，使得length-2为相对最值
		 * 每一次选择一个最值排到已排序的队列尾部，使已排序队列长度加1，未排序队列长度减1
		 */
		for (int i = 0; i < length-1; i++) {
			//比较初始时，最值下标为i，即未排序队列的第一个下标
			selIndex = i;
			for (int j = i; j < length; j++) {
				/**
				 * 升序排列时，选择一个最小值
				 * 降序排列时，选择一个最大值
				 */
				if (isSortDesc?array[j] > array[selIndex]:array[j] < array[selIndex]) {
					//记录新的最值下标
					selIndex = j;
				}
			}
			//此时selIndex为本轮选择的最值，如果不等于初始下标i，需要和i交换
			if (selIndex != i) {
				array[i] ^= array[selIndex];
				array[selIndex] ^= array[i];
				array[i] ^= array[selIndex];
			}
		}
		
		log.info("排序结果为"+JSONObject.toJSONString(array));
	}
	
	public static void main(String[] args) {	
		sort(false);	
	}
}
