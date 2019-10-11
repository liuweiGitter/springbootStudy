package com.liuwei.algorithm.sort.pkg4int;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-09-25 19:57:39
 * @desc 冒泡排序
 * 空间复杂度O(1)
 * 时间复杂度
 * 	1.最好情况O(n)，具体为n-1次比较，0次交换
 * 	2.最坏情况O(n²)，具体为n*(n-1)/2次比较，同等次数交换
 * 	3.平均复杂度O(n²)，具体为(n+2)*(n-1)/4次比较，一半次数交换
 * 优点：空间复杂度低，不需要额外内存，算法简单易实现
 * 缺点：时间复杂度高，尤其数组长度较大时，效率加速降低
 */
@Slf4j
public class BubbleSort{
	
	private static void sort(boolean isSortDesc) {
		int[] array = {5,3,7,6,8,4,1,0,9,2};
		int length = array.length;
		boolean hasExchange;
		
		log.info("原始队列为"+JSONObject.toJSONString(array));
		
		/**
		 * 形如：{5,3,7,6,8}
		 * 第1次从下标0比较到length-1，使得length-1为最值
		 * 第2次从下标0比较到length-2，使得length-2为相对最值
		 * ...
		 * 第length-1次从下标0比较到1，使得1为相对最值
		 * 每一次冒泡一个最值到相对最后的位置
		 */
		for (int i = length-1; i > 0; i--) {
			//记录本次遍历是否进行了交换
			hasExchange = false;
			for (int j = 0; j < i; j++) {
				/**
				 * 升序排列时，如果当前下标值比下一个下标值大，交换二者的值
				 * 降序排列时，正相反
				 */
				if (isSortDesc?array[j] < array[j+1]:array[j] > array[j+1]) {
					/**
					 * 交换两个整型变量的值
					 * 可以通过中间变量，也可以通过各种算法交换
					 * 此处通过异或算法交换
					 */
					array[j] ^= array[j+1];
					array[j+1] ^= array[j];
					array[j] ^= array[j+1];
					hasExchange = true;
				}
			}
			
			//如果本次遍历没有进行交换，说明排序已经完成，提前退出循环
			if (!hasExchange) {
				log.info("第{}次冒泡后，全部排序完成！",length-i);
				break;
			}else {
				log.info("第"+(length-i)+"次冒泡后，队列为{}",JSONObject.toJSONString(array));
			}
		}
		
		log.info("最终排序结果为"+JSONObject.toJSONString(array));
	}
	
	public static void main(String[] args) {	
		sort(true);	
	}

}
