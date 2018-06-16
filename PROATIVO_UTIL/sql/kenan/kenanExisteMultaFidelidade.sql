select 1
from nrc
where billing_account_no = ?
and type_id_nrc in (12850, 12848, 12849);