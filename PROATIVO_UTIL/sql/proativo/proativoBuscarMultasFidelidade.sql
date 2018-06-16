select  trunc((? * indice_calculo) * 100) valor, tipo_nrc, decode(tipo_nrc, '12850', 90, 1) open_item_id
from proativo_quebra_multa_contrato
where id_combo = ?;