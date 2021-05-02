
/* not sure which version is the latest */


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
STORAGE (
   INITIAL 10K
   NEXT 20K
   PCTINCREASE 1
)
LOB (mm_data) store as seg_mm_data(
STORAGE (
  INITIAL 10M
  NEXT 10M
));


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
)
STORAGE( initial 10K next 10K pctincrease 2);


create table catg_grpitem
(catg_app char(10) not null,
 catg_key int not null,
 item_key int not null,
 last_upd_by varchar2(20),
 last_upd_dtm date not null,
 primary key (catg_app,catg_key,item_key)
)
STORAGE( initial 20K next 20K pctincrease 1);


create table mm_register
(
  mm_key int primary key,
  mm_name raw(300) null,
  mm_size numeric(12),
  mm_sig raw(300),
  mm_status numeric(3),
  create_dte date default (sysdate)
)
STORAGE (initial 1K next 1K pctincrease 1);


create index i_mm_register on mm_register(mm_size,mm_sig);
create index i_mm_datastore on mm_datastore(mm_size,mm_sig);
create index i_catg_grpitem_01 on catg_grpitem(item_key,catg_key,catg_app);

create table mm_datacache
(
mm_cache int,
mm_key int ,
mm_data blob
)
STORAGE (
   INITIAL 10K
   NEXT 20K
   PCTINCREASE 1
)
LOB (mm_data) store as seg_mm_datacache(
STORAGE (
  INITIAL 10M
  NEXT 10M
));
create index i_mm_datacache_01 on mm_datacache(mm_key);

create sequence seq_mmkey increment by 1 start with 170000 nomaxvalue nocycle order;
create sequence seq_catgkey increment by 1 start with 400 nomaxvalue nocycle order;

create view v_catg_grp
as
select
  a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl,nvl(b.total,0) total
from
  catg_grp a,
  (select catg_key,count(*) total from catg_grpitem group by catg_key) b
where
  a.catg_app = 'MM' and
  b.catg_key (+)= a.catg_key ;


create or replace function f_getcatgkey
(app char,code varchar2) return numeric is
  key numeric;
begin
  select catg_key into key from catg_grp
  where catg_app=app and catg_code = rawtohex(code);
  return key;
end f_getcatgkey;

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

create or replace function f_raw2char (rawdata raw) return varchar2
is
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

create or replace procedure p_i_mm_register
(   rawname varchar2,
    rawsize numeric,
    rawsig raw,
    rawstatus numeric,
    rawkey out numeric
)
as
begin
insert into mm_register (mm_key,mm_name,mm_size,mm_sig,mm_status,create_dte)
values
(seq_mmkey.nextval,rawtohex(rawname),rawsize,rawsig,rawstatus,sysdate);
select seq_mmkey.currval into rawkey from dual;
end ;

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

create table catg_relation
( catg_app char(10) not null,
  catg_key int not null,
  child_key int not nul
)

CREATE GLOBAL TEMPORARY TABLE tmp_catg_tree(
   catg_key number,
   child_key number
)
ON COMMIT PRESERVE ROWS;



CREATE OR REPLACE PACKAGE catg_tree AS
  TYPE catg_relation_cv_type IS REF CURSOR RETURN catg_relation%ROWTYPE;
  PROCEDURE p_clear_tmp_tbl ;
  PROCEDURE p_get_catg_tree (vcatg_key number);
  PROCEDURE p_get_catg_relation_cv (vcatg_cv IN catg_relation_cv_type);
END catg_tree;



CREATE OR REPLACE PACKAGE BODY catg_tree AS
  procedure p_clear_tmp_tbl is
  begin
    delete from tmp_catg_tree;
  end p_clear_tmp_tbl;



  procedure p_get_catg_tree( vcatg_key in number) is
    catg_cv catg_relation_cv_type;
    vcnt int ;
  begin
    vcnt:=0;
    select count(*) into vcnt from tmp_catg_tree where catg_key = vcatg_key;
    if vcnt =0 then
       insert into tmp_catg_tree values (vcatg_key,vcatg_key);
    end if;
    open catg_cv for select * from catg_relation where catg_app = 'MM' and catg_key =  vcatg_key;
    p_get_catg_relation_cv(catg_cv);
  end p_get_catg_tree;



  procedure p_get_catg_relation_cv(vcatg_cv in catg_relation_cv_type) is
    rec catg_relation%ROWTYPE;
    vcatg_cv2 catg_relation_cv_type;
  begin
    loop
    fetch vcatg_cv into rec;
    exit when vcatg_cv%NOTFOUND;
      insert into tmp_catg_tree values (rec.catg_key,rec.child_key);
      open vcatg_cv2 for select a.* from catg_relation a where a.catg_app = 'MM' and a.catg_key =rec.child_key and not exists (select 1 from tmp_catg_tree where child_key = a.child_key);
      p_get_catg_relation_cv(vcatg_cv2);
    end loop;
  end p_get_catg_relation_cv;

END catg_tree;





