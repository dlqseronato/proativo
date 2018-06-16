select id, comando, base
from proativo_owner.proativo_scripts
where id_execucao = ?
and id_cenario = ?
order by id;