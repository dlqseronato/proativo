update  customer_contract_view
set end_dt = ?
where view_status = 2 
and (tracking_id, tracking_id_serv) in (
	select association_id, association_id_serv 
	from cmf_component_element cce 
	where cce.component_inst_id = ? 
	and cce.component_inst_id_serv = ?
	and cce.association_type = 2
);