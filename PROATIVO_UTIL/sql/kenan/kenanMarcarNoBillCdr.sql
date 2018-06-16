update cdr_data 
set no_bill = 1,
second_dt = sysdate,
ANNOTATION = TRIM(ANNOTATION) || ?
where msg_id = ?
and msg_id2 = ?
and msg_id_serv = ?;