select *
from nrc_view
where billing_account_no = ?
and view_status = 1;
