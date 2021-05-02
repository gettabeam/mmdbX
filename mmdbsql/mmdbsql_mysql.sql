/* MySQL 4.1.3 script */

create database mm;
use mm;

create table sys_tbl
(keyname char(20) not null,
 keyvalue int not null) type=innodb;

insert into sys_tbl values ('seq_mmkey',0);
insert into sys_tbl values ('seq_catgkey',0);
insert into sys_tbl values ('mm_regkey',0);
insert into sys_tbl values ('db_ver','20050101');

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


/* grant remote login */
/* login mysql and use the 'mysql' db */
grant all privileges on mm.* to myadmin@localhost IDENTIFIED by 'myadmin';
grant all privileges on mm.* to myadmin@'%';
select user,host,password from mysql.user;
SET PASSWORD FOR 'myadmin'@'localhost' = password('myadmin');



/* my.cnf */
[client]
port		= 3306
[mysqld]
port		= 3306
max_allowed_packet = 16M
innodb_data_file_path = ericdata1:10M;ericdata2:10M:autoextend
innodb_log_file_size = 100M


/* db_version : 20050318 */
alter table catg_grp add view_lvl numeric(3) ;
alter table catg_grpitem add item_order int ;
alter table catg_grpitem add view_cnt int;
alter table mm_datastore add view_cnt int ;


/* db_bersion : 20050404 */
insert into sys_tbl values ('db_ver','20050404');
create table security_user (user_key int, username char(50), password char(200), access_lvl numeric(3));
create unique index i_security_user on security_user (username);
create table security_user_group (group_key int, groupname char(50),access_lvl numeric(3));
create unique index i_security_user_group on security_user_group (groupname);
create table security_function (function_name char(50), access_lvl numeric(3));
create unique index i_security_function on security_function (function_name);
insert into security_function values ('CATG_MAINT',100);
insert into security_function  values ('ALLOW_SETUP',100);
insert into security_function  values ('GUESS_MODE',0);
insert into security_user values (1,'guest','public',1);


alter table catg_grp drop view_lvl  ;
alter table catg_grp add access_lvl numeric(3) ;

create index i_mm_grpitem_2 on catg_grpitem(item_key,catg_key);
