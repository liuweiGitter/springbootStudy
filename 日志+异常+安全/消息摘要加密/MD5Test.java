package com.liuwei.endecode;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

/**
 * @author liuwei
 * @date 2019-09-11 22:37:23
 * @desc MD5加密测试类
 */
public class MD5Test {

	private static MessageDigest md5;

	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	算法提供者:SUN version 1.8
	算法名称:MD5
	算法原始摘要长度:16
	算法提供者信息描述:SUN (DSA key/parameter generation; DSA signing; SHA-1, MD5 digests; SecureRandom; X.509 certificates; JKS & DKS keystores; PKIX CertPathValidator; PKIX CertPathBuilder; LDAP, Collection CertStores, JavaPolicy Policy; JavaLoginConfig Configuration)
	>>>MD5加密字符串
	dbc457f14f17b1ff546f78a068178446
	>>>MD5加密用户名+密码
	748cb0b923c8c7a8e4c99febc9645e0b
	>>>MD5加密用户名+密码+固定的盐
	bab38229c8b675f3dca281a41921520d
	>>>MD5加密密码+用户名相关动态的盐
	852ef895af38f6d326b3692cd7e8a178
	 */
	public static void run() {

		// 1.算法提供者
		Provider provider = md5.getProvider();
		print("算法提供者:"+provider);
		// 2.算法名称
		String algorithm = md5.getAlgorithm();
		print("算法名称:"+algorithm);
		// 3.算法原始摘要长度
		int digestLength = md5.getDigestLength();
		print("算法原始摘要长度:"+digestLength);
		// 4.算法提供者信息描述
		String info = provider.getInfo();
		print("算法提供者信息描述:"+info);

		String origin = "ahsfuoeh";
		String userName = "liuwei";
		String password = "123456";
		String saltStatic = "salt";
		String digest;
		digest = md5Encode(origin);
		print(">>>MD5加密字符串\n" + digest);
		digest = md5Encode(userName, password);
		print(">>>MD5加密用户名+密码\n" + digest);
		digest = md5EncodeSalt(userName, password, saltStatic);
		print(">>>MD5加密用户名+密码+固定的盐\n" + digest);
		digest = md5EncodeSalt(userName, password);
		print(">>>MD5加密密码+用户名相关动态的盐\n" + digest);
	}

	/**
	 * MD5对字符串生成32位16进制摘要
	 */
	private static String md5Encode(String origin) {
		return md5EncodeSalt(origin, "", "");
	}

	/**
	 * MD5对用户名+密码明文综合生成32位16进制摘要
	 */
	private static String md5Encode(String userName, String password) {
		return md5EncodeSalt(userName, password, "");
	}

	/**
	 * MD5对用户名+密码明文+盐综合生成32位16进制摘要
	 */
	private static String md5EncodeSalt(String userName, String password, String salt) {
		
		byte[] originBytes;
		try {
			originBytes = (userName + password + salt).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		// 生成32位摘要
		// 初始摘要为16个byte值，范围自-128到+127
		byte[] digest = md5.digest(originBytes);
		//md5.reset();
		// 转为32位16进制字符
		StringBuilder builder = new StringBuilder();
		for (byte b : digest) {
			if (b < 0) {
				builder.append(Integer.toHexString(b + 256));
				continue;
			} else if (b < 16) {// 补一个字节
				builder.append("0");
			}
			builder.append(Integer.toHexString(b));
		}
		return builder.toString();
	}

	/**
	 * MD5对密码明文+用户名相关动态盐综合生成32位16进制摘要
	 */
	private static String md5EncodeSalt(String userName, String password) {
		//用户名hashCode盐、md5摘要盐等等，有多种动态盐可选，此处为hashCode盐
		String dynamicSalt = userName.hashCode()+"";
		return md5EncodeSalt("", password, dynamicSalt);
	}

	public static void print(Object obj) {
		System.out.println(obj);
	}
}
