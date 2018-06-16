update service_view 
set service_inactive_dt = ?,
chg_who = ?,
chg_dt = sysdate
where subscr_no = ?
and view_status = 2;