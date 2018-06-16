update external_id_equip_map_view
set inactive_date = ?
where subscr_no = ?
and external_id = ?
and active_date = ?
and view_status = 2;
