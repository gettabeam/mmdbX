/* MySQL 4.1.3 script */

create database mm;
use mm;

create table sys_tbl
(keyname char(20) not null,
 keyvalue int not null) type=innodb;

insert into sys_tbl values ('seq_mmkey',0);
insert into sys_tbl values ('seq_catgkey',0);


create table mm_datastore
(
mm_key int primary key,
mm_data LONGBLOB null,
mm_name BLOB null,
mm_desc BLOB null,
mm_size numeric(12) null,
mm_sig BLOB null,
mm_seclvl numeric(3),
create_dte date
) type=innodb;


create table catg_grp
(catg_app char(10) not null,
 catg_key int not null,
 catg_code BLOB not null,
 catg_desc BLOB not null,
 catg_seclvl numeric(3) not null,
 last_upd_by varchar(20),
 last_upd_dtm date not null,
 primary key (catg_app,catg_key)
 ) type=innodb;

create unique index i_catg_grp_1 on catg_grp(catg_app,index(catg_code,300));

create table catg_grpitem
(catg_app char(10) not null,
 catg_key int not null,
 item_key int not null,
 last_upd_by varchar(20),
 last_upd_dtm date not null,
 primary key (catg_app,catg_key,item_key)
) type=innodb;

create table mm_register
(
  mm_key int primary key,
  mm_name BLOB null,
  mm_size numeric(12),
  mm_sig BLOB,
  mm_status numeric(3),
  create_dte date
) type=innodb;

create table mm_datacache
(
mm_cache int,
mm_key int ,
mm_data blob
) type=innodb;

create index i_mm_datacache_1 on mm_datacache(mm_key);
create index i_mm_grpitem_1 on catg_grpitem(catg_key,item_key);
create index i_mm_datestore_1 on mm_datastore(mm_key);
create index i_mm_datestore_2 on mm_datastore(mm_size,mm_key);


