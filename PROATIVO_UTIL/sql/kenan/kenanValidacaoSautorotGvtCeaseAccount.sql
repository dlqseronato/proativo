select count(*) retorno
from ARBORGVT_BILLING.gvt_cease_account_audit
where 	old_run_status = 99
	and new_run_status = 99
  	and new_serv_inst = ?
  	and new_external_acct_id = ?
  	and rotina like ? 
  