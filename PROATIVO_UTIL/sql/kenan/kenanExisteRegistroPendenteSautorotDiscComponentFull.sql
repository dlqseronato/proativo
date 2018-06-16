select 1
from arborgvt_billing.gvt_disc_component
where run_status = 99
and error_message is null
and SERV_INST = ?
and PACKAGE_ID = ?
and COMPONENT_INSTANCE_ID = ?
and END_DATE = ?
and EXT_ID_TYPE_INST = ?
and rotina = ?