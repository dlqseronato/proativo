package com.proativo.util.vo;

import com.proativo.util.enums.TipoElemento;

public class ComponentVo {
	private Long componentInstId;
	private Integer componentInstIdServ;
	private TipoElemento tipoElemento;
	
	public Long getComponentInstId() {
		return componentInstId;
	}
	public void setComponentInstId(Long componentInstId) {
		this.componentInstId = componentInstId;
	}
	public Integer getComponentInstIdServ() {
		return componentInstIdServ;
	}
	public void setComponentInstIdServ(Integer componentInstIdServ) {
		this.componentInstIdServ = componentInstIdServ;
	}
	public TipoElemento getTipoElemento() {
		return tipoElemento;
	}
	public void setTipoElemento(TipoElemento tipoElemento) {
		this.tipoElemento = tipoElemento;
	}

}
