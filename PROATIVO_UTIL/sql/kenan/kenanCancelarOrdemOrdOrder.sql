update ord_order
set order_status_id = 90
where order_number = ?
and account_no = ?
and order_status_id <= 80;