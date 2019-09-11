package com.liuwei.endecode;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

/**
 * @author liuwei
 * @date 2019-09-11 22:37:23
 * @desc SHA256加密测试类
 */
public class SHA256Test {

	private static MessageDigest sha256;

	static {
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	算法提供者:SUN version 1.8
	算法名称:SHA-256
	算法原始摘要长度:32
	算法提供者信息描述:SUN (DSA key/parameter generation; DSA signing; SHA-1, MD5 digests; SecureRandom; X.509 certificates; JKS & DKS keystores; PKIX CertPathValidator; PKIX CertPathBuilder; LDAP, Collection CertStores, JavaPolicy Policy; JavaLoginConfig Configuration)
	>>>SHA256加密字符串
	056cfa341011d19aaf75118ea368c9d7
	>>>SHA256加密用户名+密码
	05b227fad00f1b6da4234d1cd3928aa7
	>>>SHA256加密用户名+密码+固定的盐
	890f9db3346b1e7ba034c9f4f07d6dac
	>>>SHA256加密密码+用户名相关动态的盐
	1fed332752f25b1d5ab33bf92d0def73
	 */
	public static void run() {

		// 1.算法提供者
		Provider provider = sha256.getProvider();
		print("算法提供者:"+provider);
		// 2.算法名称
		String algorithm = sha256.getAlgorithm();
		print("算法名称:"+algorithm);
		// 3.算法原始摘要长度
		int digestLength = sha256.getDigestLength();
		print("算法原始摘要长度:"+digestLength);
		// 4.算法提供者信息描述
		String info = provider.getInfo();
		print("算法提供者信息描述:"+info);

		String origin = "ahsfuoeh";
		String userName = "liuwei";
		String password = "123456";
		String saltStatic = "salt";
		String digest;
		digest = sha256Encode(origin);
		print(">>>SHA256加密字符串\n" + digest);
		digest = sha256Encode(userName, password);
		print(">>>SHA256加密用户名+密码\n" + digest);
		digest = sha256EncodeSalt(userName, password, saltStatic);
		print(">>>SHA256加密用户名+密码+固定的盐\n" + digest);
		digest = sha256EncodeSalt(userName, password);
		print(">>>SHA256加密密码+用户名相关动态的盐\n" + digest);
	}

	/**
	 * SHA256对字符串生成32位16进制摘要
	 */
	private static String sha256Encode(String origin) {
		return sha256EncodeSalt(origin, "", "");
	}

	/**
	 * SHA256对用户名+密码明文综合生成32位16进制摘要
	 */
	private static String sha256Encode(String userName, String password) {
		return sha256EncodeSalt(userName, password, "");
	}

	/**
	 * SHA256对用户名+密码明文+盐综合生成32位16进制摘要
	 */
	private static String sha256EncodeSalt(String userName, String password, String salt) {
		
		byte[] originBytes;
		try {
			originBytes = (userName + password + salt).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		// 生成32位摘要
		// 初始摘要为32个byte值，范围自-128到+127
		byte[] digest = sha256.digest(originBytes);
		//sha256.reset();
		// 转为32位16进制字符
		StringBuilder builder = new StringBuilder();
		for (byte b : digest) {
			if (b < 0) {
				builder.append(Integer.toHexString(-b%16));
			} else {
				builder.append(Integer.toHexString(b%16));
			}
		}
		return builder.toString();
	}

	/**
	 * SHA256对密码明文+用户名相关动态盐综合生成32位16进制摘要
	 */
	private static String sha256EncodeSalt(String userName, String password) {
		//用户名hashCode盐、sha256摘要盐等等，有多种动态盐可选，此处为hashCode盐
		String dynamicSalt = userName.hashCode()+"";
		return sha256EncodeSalt("", password, dynamicSalt);
	}

	public static void print(Object obj) {
		System.out.println(obj);
	}
}
