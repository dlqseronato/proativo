select 1 from SINCRONISMO 
where RESPONSAVEL = ? 
and trunc(DATA_TRATAMENTO) = trunc(sysdate)
and CONTA_COBRANCA = ?
and MENSAGEM_ERRO is null
;
