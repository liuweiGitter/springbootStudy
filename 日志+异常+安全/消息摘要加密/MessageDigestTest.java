package com.liuwei.endecode;

import java.security.Provider;
import java.security.Security;

/**
 * @author liuwei
 * @date 2019-09-11 21:05:24
 * @desc 消息摘要测试类 MD5、SHA256、SHA512加密
 */
public class MessageDigestTest {

	public static void main(String[] args) {
		// getAllProviderAndAlgorithm();
		// MD5Test.run();
		// SHA256Test.run();
		// SHA512Test.run();
	}

	// 获取Java Security支持的所有Provider及其算法
	protected static void getAllProviderAndAlgorithm() {
		for (Provider provider : Security.getProviders()) {
			System.out.println("Provider: " + provider.getName());
			for (Provider.Service service : provider.getServices()) {
				System.out.println("  Algorithm: " + service.getAlgorithm());
			}
			System.out.println("\n");
		}
	}

}
