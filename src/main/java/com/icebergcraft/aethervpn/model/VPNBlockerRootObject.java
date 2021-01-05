package com.icebergcraft.aethervpn.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VPNBlockerRootObject
{
	@SerializedName("status")
	@Expose
	private String status;
	@SerializedName("package")
	@Expose
	private String _package;
	@SerializedName("remaining_requests")
	@Expose
	private Integer remainingRequests;
	@SerializedName("ipaddress")
	@Expose
	private String ipaddress;
	@SerializedName("host-ip")
	@Expose
	private Boolean hostIp;
	@SerializedName("org")
	@Expose
	private String org;
	@SerializedName("msg")
	@Expose
	private String msg;

	public String getStatus() {
	return status;
	}

	public void setStatus(String status) {
	this.status = status;
	}

	public String getPackage() {
	return _package;
	}

	public void setPackage(String _package) {
	this._package = _package;
	}

	public Integer getRemainingRequests() {
	return remainingRequests;
	}

	public void setRemainingRequests(Integer remainingRequests) {
	this.remainingRequests = remainingRequests;
	}

	public String getIpaddress() {
	return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
	this.ipaddress = ipaddress;
	}

	public Boolean getHostIp() {
	return hostIp;
	}

	public void setHostIp(Boolean hostIp) {
	this.hostIp = hostIp;
	}

	public String getOrg() {
	return org;
	}

	public void setOrg(String org) {
	this.org = org;
	}
	
	public String getMsg() {
	return msg;
	}
	
	public void setMsg(String msg) {
	this.msg = msg;
	}
}
