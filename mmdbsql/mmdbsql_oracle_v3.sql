/* mmdbx database script   */
/* last update 2002-03-21 */
/* last update 2002-03-25 */
/* last update 2002-04-10 */

create tablespace mmdbx_data
datafile 'd:\public\data\mmdbx_data_01.dbf' size 10M reuse
autoextend on next 10M maxsize 640M
,
         'd:\public\data\mmdbx_data_02.dbf' size 10M reuse
autoextend on next 10M maxsize 640M	 
	 ,
	 'd:\public\data\mmdbx_data_03.dbf' size 10M reuse autoextend on next 10M maxsize 640M,
	 'd:\public\data\mmdbx_data_04.dbf' size 10M reuse
autoextend on next 10M maxsize 640M
default storage( initial 128K next 256k);

alter tablespace mmdbx_data
add datafile 'd:\public\data\mmdbx_data_05.dbf' size 10M 
autoextend on next 10M maxsize 640M

alter tablespace mmdbx_data
add datafile 'd:\public\data\mmdbx_data_06.dbf' size 10M 
autoextend on next 10M maxsize 640M

alter tablespace mmdbx_data
add datafile 'd:\public\data\mmdbx_data_07.dbf' size 200M 
autoextend on next 20M maxsize 640M

alter table mm_datastore modify lob (mm_data) (storage (pctincrease 1));

alter table mm_register storage (pctincrease 1);

alter table mm_datastore modify lob (mm_data) (storage ( next  20M))


create user mmdb identified by mmdbx
default tablespace mmdbx_data
quota unlimited on mmdbx_data


grant developer to mmdb

D:\PUBLIC\DUMP\ORA>exp userid=system/ora815aa395 file=sun99data.exp full=Y incty
pe=complete owner=sun99 log=sun99data.exp.log consistent=Y


create table mm_datastore
(
mm_key int primary key,
mm_data blob,
mm_name raw(300) null,
mm_desc raw(2000) null,
mm_size numeric(12),
mm_sig raw(300),
mm_seclvl numeric(3),
create_dte date default (sysdate)
)
tablespace mmdbx_data
STORAGE ( INITIAL 10M
          NEXT 10M
          PCTINCREASE 10 );

create table catg_grp
(catg_app char(10) not null,
 catg_key int not null,
 catg_code raw(100) not null,
 catg_desc raw(1000) not null,
 catg_seclvl numeric(3) not null,
 last_upd_by varchar2(20),
 last_upd_dtm date not null,
 primary key (catg_app,catg_key),
 unique(catg_app,catg_code)
) tablespace mmdbx_data
STORAGE( initial 1M next 100K pctincrease 5);



create table catg_grpitem
(catg_app char(10) not null,
 catg_key int not null,
 item_key int not null,
 last_upd_by varchar2(20),
 last_upd_dtm date not null,
 primary key (catg_app,catg_key,item_key)
) tablespace mmdbx_data
STORAGE( initial 1M next 100K pctincrease 5);



create view v_catg_grp
as
select a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl,nvl(b.total,0) total
from catg_grp a, (select catg_key,count(*) total from catg_grpitem group by catg_key) b
where a.catg_app = 'MM' and b.catg_key (+)= a.catg_key ;



create sequence seq_mmkey increment by 1 start with 170000 nomaxvalue nocycle order;
create sequence seq_catgkey increment by 1 start with 400 nomaxvalue nocycle order;


create or replace function f_getcatgkey
(app char,code varchar2) return numeric is
  key numeric;
begin
  select catg_key into key from catg_grp where catg_app=app and catg_code = rawtohex(code);
  return key;
end f_getcatgkey;
/

create or replace procedure mminfo
as
  bb mm_datastore%ROWTYPE;
  isize int;
  cursor c1 is select * from mm_datastore;
begin
open c1;
loop
fetch c1 into bb;
exit when c1%NOTFOUND;
isize:=dbms_lob.getlength(bb.mm_data);
dbms_output.put_line('Filename:'||bb.mm_name||  '   Size is '||isize);
end loop;
end mminfo;
/


create or replace function f_raw2char (rawdata raw) return varchar2 is
l pls_integer;
a0 pls_integer;
a1 pls_integer;
a2 pls_integer;
c char(2);
d char(1);
s varchar2(2000);
begin
  l:=length(rawdata)/2;
  s:='';
  for j in 0..l-1 loop
    c:=substr(rawdata,1+j*2,2);
    a0:=instr('0123456789ABCDEF',substr(c,1,1),1,1);
    a1:=instr('0123456789ABCDEF',substr(c,2,1),1,1);
    a2:=(a0-1)*16+(a1-1);
    d:=chr(a2);
    s:=s||d;
  end loop;
  return s;
end f_raw2char;
/


create or replace procedure p_addcatgkey
(
  app char,
  code varchar2,
  description varchar2,
  rawcode raw,
  rawdesc raw,
  seclvl numeric,
  catgkey out numeric
)
as
  count int :=0;
begin
  if seclvl = 0 then
    insert into catg_grp (catg_app,catg_key,catg_code,catg_desc,catg_seclvl,last_upd_by,last_upd_dtm)
      values (app,seq_catgkey.nextval,rawtohex(code),rawtohex(description),seclvl,user,sysdate);
  else
    insert into catg_grp (catg_app,catg_key,catg_code,catg_desc,catg_seclvl,last_upd_by,last_upd_dtm)
      values (app,seq_catgkey.nextval,rawcode,rawdesc,seclvl,user,sysdate);
  end if;
  select seq_catgkey.currval into catgkey from dual;
end p_addcatgkey;
/

create or replace procedure p_u_catgkey
(
  app char,
  code varchar2,
  description varchar2,
  rawcode raw,
  rawdesc raw,
  seclvl numeric,
  catgkey numeric
)
as
  count int :=0;
begin
  if seclvl = 0 then
    update catg_grp
      set catg_code = rawtohex(code),
          catg_desc = rawtohex(description),
          last_upd_by = user,
          last_upd_dtm = sysdate
    where
      catg_app = app and
      catg_key = catgkey and
      seclvl = seclvl;
  else
    update catg_grp
      set catg_code = rawcode,
          catg_desc = rawdesc,
          last_upd_by = user,
          last_upd_dtm = sysdate
    where
      catg_app = app and
      catg_key = catgkey and
      seclvl = seclvl;
  end if;
end p_u_catgkey;
/


/* mm register table */
create table mm_register (
mm_key int primary key,
mm_name raw(300) null,
mm_size numeric(12),
mm_sig raw(300),
mm_status numeric(3),
create_dte date default (sysdate))
tablespace mmdbx_data
;
/

create index i_mm_register on mm_register(mm_size,mm_sig);
/

create index i_mm_datastore on mm_datastore(mm_size,mm_sig);
/

/* register a mm file */
create or replace procedure p_i_mm_register
(rawname varchar2,
rawsize numeric,
rawsig raw,
rawstatus numeric,
rawkey out numeric)
as
begin
insert into mm_register (mm_key,mm_name,mm_size,mm_sig,mm_status,create_dte)
values 
(seq_mmkey.nextval,rawtohex(rawname),rawsize,rawsig,rawstatus,sysdate);
select seq_mmkey.currval into rawkey from dual;
end ;
/

/* check if a given key exists in register */
/* last update 2002-03-25 */
create or replace function f_chk_mm_register
( rawsize numeric,
rawsig raw ) 
return numeric is
existcount int ;
begin
existcount:=0;
select count(*) into existcount from mm_register where mm_size = rawsize and mm_sig = rawsig;
if existcount = 0 then
  select count(*) into existcount from mm_datastore where mm_size= rawsize and mm_sig = rawsig;
end if;
return existcount;
end f_chk_mm_register;
/


