update nrc_view
set no_bill = 1,
annotation2 = ?,
chg_who = ?,
chg_dt = sysdate
where view_id = ?
and view_status = 2
;