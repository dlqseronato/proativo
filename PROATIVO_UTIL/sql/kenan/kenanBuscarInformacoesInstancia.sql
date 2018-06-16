select * from (
select subscr_no, external_id instancia, active_date data_ativacao, row_number() over (partition by subscr_no order by active_date desc) rn
from arbor.external_id_equip_map
where subscr_no = ?
) where rn = 1;