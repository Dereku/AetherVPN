package com.icebergcraft.aethervpn.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CacheModel
{
	@SerializedName("Cache")
	@Expose
	private List<IpInfo> ipList = new ArrayList<>();

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
