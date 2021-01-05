package com.icebergcraft.aethervpn;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListIpInfo
{
	@SerializedName("IPList")
	@Expose
	private List<IpInfo> ipList = null;

	public List<IpInfo> getIpList()
	{
		return ipList;
	}

	public void setIpList(List<IpInfo> ipList)
	{
		this.ipList = ipList;
	}

	public void addIpInfo(IpInfo ipInfo)
	{
		ipList.add(ipInfo);
	}

	public void removeIpInfo(IpInfo ipinfo)
	{
		ipList.removeIf(i -> i.ipAddress.equals(ipinfo.ipAddress));
	}
}
