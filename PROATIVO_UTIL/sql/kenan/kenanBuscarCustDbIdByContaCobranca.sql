select (server_id - 2) base
from external_id_acct_map
where external_id = ?
and external_id_type = 1
and inactive_date is null;