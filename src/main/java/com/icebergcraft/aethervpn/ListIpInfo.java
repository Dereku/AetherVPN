package com.icebergcraft.aethervpn;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListIpInfo
{
	@SerializedName("IPList")
	@Expose
	private List<IpInfo> ipList = null;

	public List<IpInfo> getIpList() {
	return ipList;
	}

	public void setIpList(List<IpInfo> iPList) {
	this.ipList = iPList;
	}
}
