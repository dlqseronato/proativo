select 1
from arborgvt_billing.gvt_prov_charges
where run_status = 99
and error_message is null
and EXTID_ACCTNO = ?
and EXT_ID_TYPE = ?
and PACKAGE_ID = ?
and COMPONENT_id = ?
and START_DATE = ? 
and END_DATE = ?
and rotina = ?