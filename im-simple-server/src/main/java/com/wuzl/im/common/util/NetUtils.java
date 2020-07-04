package com.wuzl.im.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;
/**
 * 
 * 类NetUtils.java的实现描述：获取ip的网络工具
 * @author ziliang.wu 2017年2月24日 下午5:06:10
 */
public class NetUtils {
	private static volatile LocalIpMsg LOCAL_IP_MSG;
	public static final String LOCALHOST = "127.0.0.1";

	public static final String ANYHOST = "0.0.0.0";
	private static final Pattern IP_PATTERN = Pattern
			.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

	/**
	 * 获取ip外网优先
	 * 
	 * @return
	 */
	public static String getLocalIpNetFirst() {
		LocalIpMsg localIpMsg = getLocalIp();
		if (localIpMsg == null) {
			return null;
		}
		if (localIpMsg.getNetIp() != null) {
			return localIpMsg.getNetIp();
		}
		return localIpMsg.getLocalIp();
	}

	/**
	 * 获取ip信息
	 * 
	 * @return
	 */
	public static LocalIpMsg getLocalIp() {
		if (LOCAL_IP_MSG != null) {
			return LOCAL_IP_MSG;
		}
		String localIp = null;// 本地IP，如果没有配置外网IP则返回它
		String netIp = null;// 外网IP
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface
					.getNetworkInterfaces();
			InetAddress ip = null;
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (isValidAddress(ip)) {
						if (!ip.isSiteLocalAddress()) {// 外网IP
							netIp = ip.getHostAddress();
							break;
						} else if (ip.isSiteLocalAddress()) {// 内网IP
							localIp = ip.getHostAddress();
						}
					}
				}
			}
		} catch (SocketException e) {
		}
		LocalIpMsg localIpMsg = new LocalIpMsg();
		localIpMsg.setNetIp(netIp);
		localIpMsg.setLocalIp(localIp);
		NetUtils.LOCAL_IP_MSG = localIpMsg;
		return localIpMsg;
	}

	private static boolean isValidAddress(InetAddress address) {
		if (address == null || address.isLoopbackAddress())
			return false;
		String name = address.getHostAddress();
		return (name != null && !ANYHOST.equals(name)
				&& !LOCALHOST.equals(name) && IP_PATTERN.matcher(name)
				.matches());
	}

	public static class LocalIpMsg {
		String localIp = null;// 本地IP
		String netIp = null;// 外网IP

		public String getLocalIp() {
			return localIp;
		}

		public void setLocalIp(String localIp) {
			this.localIp = localIp;
		}

		public String getNetIp() {
			return netIp;
		}

		public void setNetIp(String netIp) {
			this.netIp = netIp;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("内网ip:").append(localIp).append(",外网ip：").append(netIp);
			return sb.toString();
		}
	}
}
