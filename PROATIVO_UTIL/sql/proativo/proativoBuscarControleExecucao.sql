select 	
	pce.id_cenario id_cenario, 
	cenario, 
	periodicidade, 
	tipo_execucao, 
	quantidade_threads, 
	quantidade_erros,
	relatorio,
	pc.descricao,
	gerar_script,
	pce.executavel,
	pge.email_log,
	pge.email_para,
	pge.email_cc,
	pc.data_inativacao
from proativo_controle_execucao pce, proativo_cenarios pc, proativo_grupos_email pge
where pce.id_cenario = pc.id_cenario
and pge.id = pc.id_grupo_email
and cenario = ?
and segmento  = ?
