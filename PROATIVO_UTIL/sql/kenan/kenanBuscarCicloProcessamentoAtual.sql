select 
processamento,
from_dt data_corte,
add_months(from_dt,-1) data_corte_anterior
	from (
		select 
			processamento, 
			vv.from_dt,
			 lead(from_dt) over(partition by rn order by from_dt)-1 to_dt
			from (
				select 
					v.*,
					row_number()over(partition by processamento, from_dt order by from_dt)rn
				from (      
					select 
						pro.processamento,
						trunc(bc.cutoff_date) from_dt
					from bill_cycle bc
					join gvt_processamento_ciclo pro on pro.bill_period = bc.bill_period
					where 1=1
					and bc.bill_period like 'M%'
					and bc.cutoff_date between trunc(sysdate-90) and trunc(sysdate+90)
				)v
			)vv
		where vv.rn = 1
) where trunc(sysdate) between from_dt and to_dt;