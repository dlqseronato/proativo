update service_address_assoc
set inactive_dt = ?
where subscr_no = ?
and association_status = 2;