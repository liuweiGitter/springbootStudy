package com.liuwei.endecode;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

/**
 * @author liuwei
 * @date 2019-09-11 22:37:23
 * @desc SHA512加密测试类
 */
public class SHA512Test {

	private static MessageDigest sha512;

	static {
		try {
			sha512 = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	算法提供者:SUN version 1.8
	算法名称:SHA-512
	算法原始摘要长度:64
	算法提供者信息描述:SUN (DSA key/parameter generation; DSA signing; SHA-1, MD5 digests; SecureRandom; X.509 certificates; JKS & DKS keystores; PKIX CertPathValidator; PKIX CertPathBuilder; LDAP, Collection CertStores, JavaPolicy Policy; JavaLoginConfig Configuration)
	>>>SHA512加密字符串
	71a25a389dad8deb74cb0d8042bc8f2d
	>>>SHA512加密用户名+密码
	c194245bfe2dcb7573b88b017a4e6f56
	>>>SHA512加密用户名+密码+固定的盐
	2d5e4294ba04440fbd1a912d381dcfa6
	>>>SHA512加密密码+用户名相关动态的盐
	c4e31baf78b7cf56dbfb97c0b31ecb10
	 */
	public static void run() {

		// 1.算法提供者
		Provider provider = sha512.getProvider();
		print("算法提供者:"+provider);
		// 2.算法名称
		String algorithm = sha512.getAlgorithm();
		print("算法名称:"+algorithm);
		// 3.算法原始摘要长度
		int digestLength = sha512.getDigestLength();
		print("算法原始摘要长度:"+digestLength);
		// 4.算法提供者信息描述
		String info = provider.getInfo();
		print("算法提供者信息描述:"+info);

		String origin = "ahsfuoeh";
		String userName = "liuwei";
		String password = "123456";
		String saltStatic = "salt";
		String digest;
		digest = sha512Encode(origin);
		print(">>>SHA512加密字符串\n" + digest);
		digest = sha512Encode(userName, password);
		print(">>>SHA512加密用户名+密码\n" + digest);
		digest = sha512EncodeSalt(userName, password, saltStatic);
		print(">>>SHA512加密用户名+密码+固定的盐\n" + digest);
		digest = sha512EncodeSalt(userName, password);
		print(">>>SHA512加密密码+用户名相关动态的盐\n" + digest);
	}

	/**
	 * SHA512对字符串生成32位16进制摘要
	 */
	private static String sha512Encode(String origin) {
		return sha512EncodeSalt(origin, "", "");
	}

	/**
	 * SHA512对用户名+密码明文综合生成32位16进制摘要
	 */
	private static String sha512Encode(String userName, String password) {
		return sha512EncodeSalt(userName, password, "");
	}

	/**
	 * SHA512对用户名+密码明文+盐综合生成32位16进制摘要
	 */
	private static String sha512EncodeSalt(String userName, String password, String salt) {
		
		byte[] originBytes;
		try {
			originBytes = (userName + password + salt).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		// 生成32位摘要
		// 初始摘要为64个byte值，范围自-128到+127
		byte[] digest = sha512.digest(originBytes);
		//sha512.reset();
		// 转为32位16进制字符：取中间的32位
		StringBuilder builder = new StringBuilder();
		int iValue;
		for (int i = 16; i < 48; i++) {
			iValue = digest[i];
			if (iValue < 0) {
				iValue*=-1;
			}
			builder.append(Integer.toHexString(iValue%16));
		}
		return builder.toString();
	}

	/**
	 * SHA512对密码明文+用户名相关动态盐综合生成32位16进制摘要
	 */
	private static String sha512EncodeSalt(String userName, String password) {
		//用户名hashCode盐、sha512摘要盐等等，有多种动态盐可选，此处为hashCode盐
		String dynamicSalt = userName.hashCode()+"";
		return sha512EncodeSalt("", password, dynamicSalt);
	}

	public static void print(Object obj) {
		System.out.println(obj);
	}
}
