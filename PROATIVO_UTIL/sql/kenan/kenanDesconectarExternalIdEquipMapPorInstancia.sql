update external_id_equip_map_view
set inactive_date = ?
where subscr_no = ?
and view_status = 2
and external_id = ?;
