select count(*) retorno
from ARBORGVT_BILLING.gvt_disc_component_audit
where 	old_run_status = 99
	and new_run_status = 99
  	and new_serv_inst = ?
  	and new_component_instance_id = ?
  	and rotina like ?