update product_charge_map
set billed_thru_dt = ?
where tracking_id = ?
and tracking_id_serv = ?;