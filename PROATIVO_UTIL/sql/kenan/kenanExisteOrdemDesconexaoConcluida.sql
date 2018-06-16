select 1
from ord_order
where order_number = ?
and account_no = ?
and order_status_id >= 80;