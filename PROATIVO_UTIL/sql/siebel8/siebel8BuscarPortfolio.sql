select /*+ index(sip S_INV_PROF_GVT1_X) */
    (
       case
            when trim(upper(spl.name)) = upper('Planos Catarina') then 'Catarina'
            when trim(upper(spl.name)) = upper('Planos Loren') then 'Loren'
            when trim(upper(spl.name)) = upper('Planos Legado') then 'Frida'
            when trim(upper(spl.name)) = upper('Planos Touche') then 'Touche'
            when trim(upper(spl.name)) = upper('Planos Ivete') then 'Ivete'
            else 'Legado'
        end
    ) tipo_oferta
from siebel.s_inv_prof sip
  -- Oferta
    join siebel.s_asset sa 
         on sa.bill_profile_id = sip.row_id 
         and sa.root_asset_id = sa.row_id
         and sa.status_cd = 'Ativo'
         and sa.prod_id not in (
          '1-5WPB', -- Linha Telefônica
          '1-7GLW', -- Plano
          '1-7HWB', -- Banda Larga
          '1-F7ISQ', -- TV por assinatura
          '1-C1SQ', -- Serviços Adicionais
          '9-CB2MDXX', -- Linha Móvel
          '1-2ULHH' -- MeuNegocio.com
         )
    -- Descrição da Oferta
    join siebel.s_prod_int spi 
         on spi.row_id = sa.prod_id
    join siebel.s_prod_ln spl on spl.row_id = spi.pr_prod_ln_id
where sip.x_seq = ?
and spi.prod_cd = 'Promo'|| chr(231) || chr(227) ||'o';