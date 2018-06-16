select 1
from arborgvt_billing.gvt_cease_account
where run_status = 99
and error_message is null
and SERV_INST = ?
and EXTERNAL_ACCT_ID = ?
and DISCONECT_DATE = ?
and DISCONECT_ACCT = ?
and EXT_ID_TYPE_INST = ?
and rotina = ?