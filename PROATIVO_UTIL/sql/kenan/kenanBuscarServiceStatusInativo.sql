select subscr_no, subscr_no_resets, active_dt, status_type_id, status_id, status_reason_id, chg_who, chg_dt, inactive_dt
from arbor.service_status
where subscr_no = ?
and status_id = 2;