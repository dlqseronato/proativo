update service_status 
set chg_who = ?, 
chg_dt = sysdate, 
active_dt = ?, 
inactive_dt = ? 
where subscr_no = ? 
and status_id = ?;