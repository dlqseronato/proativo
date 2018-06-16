select 1
from siebel.s_inv_prof sip
	join siebel.s_org_ext soe
		on soe.row_id = sip.accnt_id
		and soe.x_migration_status = 'Valid'
where sip.x_seq = ?;