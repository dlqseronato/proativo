package com.proativo.util.vo;

import java.sql.Timestamp;
import java.util.List;

public class ComponentApiVo {
	private Integer 
					packageInstId, 
					packageInstIdServ, 
					packageId, 
					accountNo, 
					subscrNo, 
					subscrNoResets, 
					componentId,
					level, 
					componentInstId,
					componentInstIdServ; 
	
	private Timestamp dataAtivacao;
	
	private List<ComponentMembersVo> members;
	
	public ComponentApiVo(Integer packageInstId, Integer packageInstIdServ, Integer packageId, Integer accountNo, Integer subscrNo, Integer subscrNoResets, Integer componentId, Timestamp dataAtivacao){
		this.packageInstId=packageInstId;
		this.packageInstIdServ=packageInstIdServ;
		this.packageId=packageId;
		this.accountNo=accountNo;
		this.subscrNo=subscrNo;
		this.subscrNoResets=subscrNoResets;
		this.componentId=componentId;
		this.dataAtivacao=dataAtivacao;
	}

	public Integer getPackageInstId() {
		return packageInstId;
	}

	public void setPackageInstId(Integer packageInstId) {
		this.packageInstId = packageInstId;
	}

	public Integer getPackageInstIdServ() {
		return packageInstIdServ;
	}

	public void setPackageInstIdServ(Integer packageInstIdServ) {
		this.packageInstIdServ = packageInstIdServ;
	}

	public Integer getPackageId() {
		return packageId;
	}

	public void setPackageId(Integer packageId) {
		this.packageId = packageId;
	}

	public Integer getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(Integer accountNo) {
		this.accountNo = accountNo;
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

	public Integer getComponentId() {
		return componentId;
	}

	public void setComponentId(Integer componentId) {
		this.componentId = componentId;
	}

	public Timestamp getDataAtivacao() {
		return dataAtivacao;
	}

	public void setDataAtivacao(Timestamp dataAtivacao) {
		this.dataAtivacao = dataAtivacao;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public List<ComponentMembersVo> getMembers() {
		return members;
	}

	public void setMembers(List<ComponentMembersVo> members) {
		this.members = members;
	}

	public Integer getComponentInstId() {
		return componentInstId;
	}

	public void setComponentInstId(Integer componentInstId) {
		this.componentInstId = componentInstId;
	}

	public Integer getComponentInstIdServ() {
		return componentInstIdServ;
	}

	public void setComponentInstIdServ(Integer componentInstIdServ) {
		this.componentInstIdServ = componentInstIdServ;
	}

}
