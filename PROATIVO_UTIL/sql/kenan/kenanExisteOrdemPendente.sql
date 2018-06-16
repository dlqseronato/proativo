select 1
from ord_order oo
where oo.account_no = ?
and oo.order_number <> ?
and oo.order_status_id < 80;