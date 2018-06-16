select distinct bill_period
from gvt_processamento_ciclo
where (processamento = ? or ? is null)
order by 1
