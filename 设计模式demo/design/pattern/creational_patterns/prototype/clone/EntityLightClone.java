package design.pattern.creational_patterns.prototype.clone;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.cj.conf.DatabaseUrlContainer;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.jdbc.ConnectionImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * @author liuwei
 * @date 2019-07-27 23:06:25
 * @desc 浅克隆的类(原型类)
 * 多例模式需要创建多个对象，创建对象除了new构造还可以克隆对象
 * 克隆对象是比较消耗资源的，但因为一些原因，导致new创建对象比克隆还消耗资源
 * 此时根据已经创建的某一个对象再克隆创建一个新对象反而更快
 */
@Slf4j
public class EntityLightClone implements Cloneable {
	
	public int basicValue = 5;
	public String stringValue = "5";
	public Helper objectValue = new Helper();
	
	public String getDesc() {
		return "浅克隆的类(原型类)";
	}
	
	public EntityLightClone() {
		imitateCreate(false);
	}
	
	private void imitateCreate(boolean start) {
		if (!start) {
			return;
		}
		//创建此对象很耗系统资源：网络通信(连接数据库等)、文件读写、大量计算、复杂依赖等
		//此处模拟数据库连接
		try {
			DatabaseUrlContainer url = null;
			String host = "127.0.0.1";
			int port = 6379;
			String user = "liuwei";
			String password = "mysql";
			HostInfo hostInfo = new HostInfo(url,host,port,user,password);
			@SuppressWarnings("resource")
			Connection sqlConnection = new ConnectionImpl(hostInfo);
			sqlConnection.prepareStatement("select count(1) from user");
			log.info(sqlConnection.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object clone() {
		try {
			//浅克隆，创建新的对象，但新对象中的引用字段指向克隆源中相应引用字段的同一内存地址
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void printMsg(String preffix) {
		if (null==preffix) {
			preffix="";
		}
		log.info(preffix+"hashcode:"+hashCode()+",basicValue:"+basicValue
				+",stringValue:"+stringValue+",objectValue:"+objectValue);
	}
}
