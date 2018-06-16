select 1
from proativo_owner.proativo_blacklist
where conta = ?
and nvl(id_cenario, ?) = ?