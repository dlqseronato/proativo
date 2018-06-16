select *
from customer_contract_view
where parent_account_no = ?
and view_status = 1;
