update customer_id_equip_map_view
set inactive_date = ?
where subscr_no = ?
and is_current = 1
and view_status = 2;
