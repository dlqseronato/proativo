create sequence proativo_owner.seq_grupos_email start with 1 increment by 1 nocache nocycle;

create table proativo_owner.proativo_grupos_email(
  id number default proativo_owner.seq_grupos_email.nextval primary key, 
  nome_grupo varchar2(100),
  email_log varchar2(4000),
  email_para varchar2(4000),
  email_cc varchar2(4000)
);

alter table proativo_owner.proativo_cenarios
add (
	id_grupo_email number(10) references proativo_owner.proativo_grupos_email(id) 
);

create or replace public synonym proativo_grupos_email for proativo_owner.proativo_grupos_email;

grant select, insert, update, delete on proativo_owner.proativo_grupos_email to r_proativo_prod;
grant select on proativo_owner.proativo_grupos_email to r_itbilling_ro;
grant select on proativo_owner.seq_grupos_email to r_proativo_prod;

create or replace procedure proativo_owner.prc_proativo_cenario (v_cenario in varchar2, v_segmento in varchar2,  v_descricao in varchar2) is
v_msg_error varchar2(255);
v_id number (10);
v_id_grupo number(10) := 1;

begin
	select proativo_owner.seq_cenarios.nextval
	into v_id
	from dual;
  
	insert into proativo_owner.proativo_cenarios (id_cenario, cenario, segmento, descricao) values (v_id, v_cenario, v_segmento, v_descricao);
	insert into proativo_controle_execucao (id_cenario) values (v_id);
  
  case 
  when v_segmento = 'R' then 
      select id
      into v_id_grupo
      from proativo_owner.proativo_grupos_email
      where lower(email_para) like '%retail%'
      and rownum < 2;
  else
      select id
      into v_id_grupo
      from proativo_owner.proativo_grupos_email
      where lower(email_para) like '%ofc%'
      and rownum < 2;
  end case;
  
  update proativo_owner.proativo_cenarios set id_grupo_email = v_id_grupo where id_cenario = id_cenario;
  
	commit;
  
exception
	when others then
		v_msg_error := 'ocorreu um erro nao mapeado - '||sqlcode||' -erro- '||sqlerrm;
		raise_application_error (-20001, v_msg_error);
end;
/
