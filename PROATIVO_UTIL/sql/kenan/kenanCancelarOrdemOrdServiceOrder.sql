update ord_service_order
set order_status_id = 90
where order_id in (
	select order_id
	from ord_order
	where order_number = ?
	and account_no = ?
	and order_status_id <= 80
);