update proativo_log_execucao a 
set fim = sysdate 
where id_cenario = ? 
and inicio = (
	select max(inicio)
	from proativo_log_execucao b
	where a.id_cenario = b.id_cenario
	and fim is null
)