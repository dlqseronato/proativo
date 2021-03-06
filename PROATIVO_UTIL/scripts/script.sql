CREATE TABLE PROATIVO_OWNER.PROATIVO_CONTROLE_EXECUCAO (	
	ID_CENARIO VARCHAR2(10 BYTE) NOT NULL ENABLE, 
	PERIODICIDADE NUMBER(3,0) NOT NULL ENABLE, 
	TIPO_EXECUCAO NUMBER(3,0) NOT NULL ENABLE, 
	EXECUTAVEL NUMBER(3,0) NOT NULL ENABLE, 
	QUANTIDADE_THREADS NUMBER(3,0), 
	QUANTIDADE_ERROS NUMBER(3,0), 
	CONSTRAINT PK_PROATIVO_EXEC_PK PRIMARY KEY ("ID_CENARIO")
	CONSTRAINT FK_TIPO_EXECUCAO FOREIGN KEY ("TIPO_EXECUCAO") REFERENCES PROATIVO_OWNER.PROATIVO_TIPO_EXECUCAO ("ID") ENABLE
);

CREATE TABLE PROATIVO_OWNER.PROATIVO_LOG_EXECUCAO (	
	ID_EXECUCAO NUMBER(12,0),
	ID_CENARIO VARCHAR2(10 BYTE) NOT NULL ENABLE, 
	INICIO DATE, 
	FIM DATE, 
	CONSTRAINT "PK_ID_EXECUCAO" PRIMARY KEY ("ID_EXECUCAO")
);

CREATE TABLE PROATIVO_OWNER.PROATIVO_PARAMETROS(	
	PARAMETRO VARCHAR2(100 BYTE), 
	DESCRICAO VARCHAR2(200 BYTE), 
	VALOR VARCHAR2(100 BYTE)
);
  
CREATE TABLE PROATIVO_OWNER.PROATIVO_SEGMENTOS (
	ACCOUNT_CATEGORY NUMBER(3,0), 
	SEGMENTO VARCHAR2(30 BYTE),
	CONSTRAINT "PK_ACCOUNT_CATEGORY" PRIMARY KEY ("ACCOUNT_CATEGORY")
);
  
CREATE TABLE PROATIVO_OWNER.PROATIVO_TIPO_EXECUCAO (	
	ID NUMBER, 
	DESCRICAO VARCHAR2(255 BYTE), 
	CONSTRAINT "PK_TIPO_EXECUCAO" PRIMARY KEY ("ID")
);
  
create or replace PROCEDURE PROATIVO_OWNER.prc_proativo_log_exec (
    v_id_cenario IN varchar2,
    v_id_execucao OUT number
)
is
    v_msg_error varchar2(255);
begin
    insert into PROATIVO_OWNER.proativo_log_execucao (id_execucao, id_cenario, inicio) values (PROATIVO_OWNER.seq_id_execucao_proativo.nextval, v_id_cenario, sysdate) returning id_execucao into v_id_execucao;
exception
    when others then
        v_msg_error := 'Ocorreu um erro nao mapeado - '||SQLCODE||' -ERRO- '||SQLERRM;
        raise_application_error (-20001, v_msg_error);
end;
/

create sequence PROATIVO_OWNER.SEQ_ID_EXECUCAO_PROATIVO start with   1  increment by   1  nocache  nocycle;

insert into PROATIVO_OWNER.PROATIVO_PARAMETROS (PARAMETRO,DESCRICAO,VALOR) values ('diretorioContingencia','diretorio para o arquivos de contingencia','c:/proativo/contingencia');
insert into PROATIVO_OWNER.PROATIVO_PARAMETROS (PARAMETRO,DESCRICAO,VALOR) values ('diretorioSqlUtil','diret�rio para os arquivos sql que podem ser utilizados por todos os cenarios','c://proativo/sql');

insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('9','SME');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('10','Residencial');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('11','Retail');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('12','Corporate');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('13','Grande Empresa');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('14','Atacadista');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('15','Governo');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('16','Escrit�rio GVT');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('17','Clientes n�o GVT');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('18','Clientes n�o GVT - BRT');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('19','Clientes n�o GVT - CTBC');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('20','Clientes n�o GVT - SERCOMTEL');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('21','Autarquia Federal');
insert into PROATIVO_OWNER.PROATIVO_SEGMENTOS (ACCOUNT_CATEGORY,SEGMENTO) values ('22','Call Center');

insert into PROATIVO_OWNER.PROATIVO_TIPO_EXECUCAO (ID,DESCRICAO) values ('1','Levantamento');
insert into PROATIVO_OWNER.PROATIVO_TIPO_EXECUCAO (ID,DESCRICAO) values ('2','Tratamento');
insert into PROATIVO_OWNER.PROATIVO_TIPO_EXECUCAO (ID,DESCRICAO) values ('3','Levantamento e Tratamento');