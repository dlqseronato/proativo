select 1
from cmf_package_component
where 1=1
and (
 (inactive_dt is null or inactive_dt > ?) and (inactive_dt <> inactive_dt)
)
and parent_account_no = ?
union 
select 1 
from service 
where parent_account_no = ? 
and (
(service_inactive_dt is null or service_inactive_dt > ?) and (service_active_dt <> service_inactive_dt)
);