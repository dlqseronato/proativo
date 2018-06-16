select 1
from arborgvt_billing.gvt_disc_component
where run_status = 99
and error_message is null
and rotina = ?