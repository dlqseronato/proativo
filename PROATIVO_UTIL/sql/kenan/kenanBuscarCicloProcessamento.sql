select 
	 upper(proc.processamento) processamento, 
  case when bc.cutoff_date > sysdate-5
    then add_months(bc.cutoff_date,-1)
    else  bc.cutoff_date  
  end data_corte,
	--bc.cutoff_date data_corte, 
	case when bc.cutoff_date > sysdate-5
    then add_months(bc.cutoff_date,-2)
    else  add_months(bc.cutoff_date,-1)  
  end data_corte_anterior
from bill_cycle bc, 
gvt_processamento_ciclo proc
where bc.bill_period like 'M%'
and proc.processamento = ?
and bc.cutoff_date = (
  select min(cutoff_date) from bill_cycle bc2 where bc2.cutoff_date >= trunc(sysdate) and bc2.bill_period = proc.bill_period 
)
and bc.bill_period = proc.bill_period
and rownum < 2
order by bc.cutoff_date
;