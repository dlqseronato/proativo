select 1
from arborgvt_billing.gvt_cease_account
where run_status = 99
and error_message is null
and rotina = ?