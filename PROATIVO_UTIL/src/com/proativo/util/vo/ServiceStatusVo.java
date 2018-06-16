package com.proativo.util.vo;

import java.sql.Timestamp;

public class ServiceStatusVo {
	private Integer subscrNo;
	private Integer subscrNoResets;
	private Timestamp activeDt;
	private Integer statusTypeId;
	private Integer statusId;
	private Integer statusReasonId;
	private String chgWho;
	private Timestamp chgDt;
	private Timestamp inactiveDt;
	
	public ServiceStatusVo(Integer subscrNo){
		this.subscrNo = subscrNo;
	}
	
	public ServiceStatusVo(Integer subscrNo, String chgWho){
		this.subscrNo = subscrNo;
		this.chgWho = chgWho;
	}
	
	public Integer getSubscrNo() {
		return subscrNo;
	}
	public void setSubscrNo(Integer subscrNo) {
		this.subscrNo = subscrNo;
	}
	public Integer getSubscrNoResets() {
		return subscrNoResets;
	}
	public void setSubscrNoResets(Integer subscrNoResets) {
		this.subscrNoResets = subscrNoResets;
	}
	public Timestamp getActiveDt() {
		return activeDt;
	}
	public void setActiveDt(Timestamp activeDt) {
		this.activeDt = activeDt;
	}
	public Integer getStatusTypeId() {
		return statusTypeId;
	}
	public void setStatusTypeId(Integer statusTypeId) {
		this.statusTypeId = statusTypeId;
	}
	public Integer getStatusId() {
		return statusId;
	}
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}
	public Integer getStatusReasonId() {
		return statusReasonId;
	}
	public void setStatusReasonId(Integer statusReasonId) {
		this.statusReasonId = statusReasonId;
	}
	public String getChgWho() {
		return chgWho;
	}
	public void setChgWho(String chgWho) {
		this.chgWho = chgWho;
	}
	public Timestamp getChgDt() {
		return chgDt;
	}
	public void setChgDt(Timestamp chgDt) {
		this.chgDt = chgDt;
	}
	public Timestamp getInactiveDt() {
		return inactiveDt;
	}
	public void setInactiveDt(Timestamp inactiveDt) {
		this.inactiveDt = inactiveDt;
	}
}
