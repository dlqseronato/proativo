select tipo, (valor - ((valor/100) * (percentual_desconto))) valor from (
select    
    (
        case 
        when soi.action_cd = 'Desconectar' then (select billing_code from siebel.cx_comm_story fid_atual where fid_atual.row_id = soi.x_commitment_id) 
        when soi.action_cd = 'Modificar' then (select billing_code from siebel.cx_comm_story fid_antiga where fid_antiga.row_id = soiex.attrib_03) 
        end 
    ) tipo, 
    nvl((
        select ROUND(to_number(replace(sowl.pri_name,'.',',')),2) * 100
        from siebel.s_order_item soi, siebel.s_ordit_wtr_log sowl 
        where so.row_id = soi.order_id 
        and so.bill_profile_id = soi.bill_profile_id 
        and soi.row_id = sowl.order_item_id 
        and sowl.x_pricing_step_code = (
            select max(sowl2.x_pricing_step_code) 
            from siebel.s_ordit_wtr_log sowl2 
            where sowl.order_item_id = sowl2.order_item_id
        )  
        and sowl.pri_adj_type_cd = 'Sobreposição de preço' 
        and soi.prod_id in (
            '1-F96XX'--Multa Fidelidade TV
            ,'1-6L1CU'--Multa Rescisoria da Banda Larga
            ,'1-6L1DA'--Multa Rescisoria da Oferta
        )
    ),0) valor, 
    nvl((
		select adj_val_amt
		from SIEBEL.S_ORDIT_WTR_LOG pri_desc, siebel.s_order_item soii 
		where pri_desc.order_item_id = soii.row_id
		and pri_desc.x_pricing_step_code = '60'
        and soii.order_id =  so.row_id 
        and soii.bill_profile_id = so.bill_profile_id 
        and soii.prod_id in (
            '1-F96XX'--Multa Fidelidade TV
            ,'1-6L1CU'--Multa Rescisoria da Banda Larga
            ,'1-6L1DA'--Multa Rescisoria da Oferta
        )
	),0) percentual_desconto
from  siebel.s_order so, 
siebel.s_order_item soi, 
siebel.s_inv_prof sip, 
siebel.s_order_item_x soiex 
where 1=1  
and sip.x_seq = ?
and so.integration_id = ?
and so.status_cd not in ('Abandonado','Cancelado')
and trunc(so.created) >= trunc(sysdate-90)
and so.bill_profile_id = sip.row_id 
and so.row_id = soi.order_id 
and so.bill_profile_id = soi.bill_profile_id 
and soi.row_id = soiex.row_id(+) 
and soi.x_commitment_id is not null 
and soi.action_cd <> 'Adicionar' 
and soi.prod_id in ('1-5WA2','1-5WAJ','1-1DT5Y','1-1DT6H','1-1DN37','1-1DT70','1-1DT7J','1-1DT82','1-1DO87','1-1DT4W','1-1DT5F','1-1DN2O','1-1DTB8','1-1DTBR','1-1DT94','1-1DTCA','1-1DTCT','1-1DTDC','1-1DT9N','1-1DTA6','1-1DTAP','1-1DT8L','1-G0TET','1-1WXRP','1-12MJCJ','1-12MJEH','9-61SME5','9-61SMEU','1-12MJD1','1-12MP4P','9-61SMEI','9-61SMF6','9-61SMFV','9-61RDS1','1-11C4AN','1-12MJDP','1-12MP59','9-61SMFJ','1-1DTFG','1-1DTDV','1-1DTEE','1-1DTEX','1-26SWX','1-26SYJ','1-26SZ2','1-26SY0','1-26SXH','9-649XZP','1-5WBY','1-5WCF','1-5WCW','1-2UO3C','1-FTUB5','8-2ECYAT','1-16ZV9R','9-6AK45L','1-5WB0','1-5W86','1-LCN2','1-5W7N','1-LC52','1-130RH','1-130S0','1-1300T','1-1301C','1-5WBH','1-6U3J') 
and exists (
    select 1  --Existe multa rescisória aprovisionada 
    from siebel.s_order_item soii 
    where soii.order_id =  so.row_id 
    and soii.bill_profile_id = so.bill_profile_id 
    and soii.prod_id in (
            '1-F96XX'--Multa Fidelidade TV
            ,'1-6L1CU'--Multa Rescisoria da Banda Larga
            ,'1-6L1DA'--Multa Rescisoria da Oferta
    )
) 
and exists (
    select 1  --Existe uma desconexão de plano ou instância
    from siebel.s_order_item soi 
    where soi.order_id = so.row_id  
    and soi.bill_profile_id = so.bill_profile_id 
    and soi.prod_id in ('9-61SME5','9-61SMEU','9-61SMEI','9-61SMF6','9-61SMFV','9-61RDS1','9-61SMFJ','1-7HWB','1-5WPB','1-F7ISQ')  
    and soi.action_cd = 'Desconectar' 		
    and soi.status_cd in ('Concluído','Instalado')
)
);