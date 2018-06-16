package com.proativo.util.vo;

public class ComponentMembersVo {
	private Integer memberType, memberId;
	
	public ComponentMembersVo(Integer memberType, Integer memberId){
		this.memberType = memberType;
		this.memberId = memberId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getMemberType() {
		return memberType;
	}

	public void setMemberType(Integer memberType) {
		this.memberType = memberType;
	}

}
