-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id41::razamd
DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id41' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id40::razamd
DROP VIEW public.task_sub_activity_view;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id40' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id39::razamd
DROP VIEW public.task_activity_view;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id39' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id38::razamd
ALTER TABLE public.orders DROP COLUMN timeline;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id38' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id37::razamd
ALTER TABLE public.orders_log DROP COLUMN garment_type_id;

ALTER TABLE public.orders_log DROP COLUMN buyer_id;

ALTER TABLE public.orders_log DROP COLUMN season_id;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id37' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id36::razamd
DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id36' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id35::razamd
ALTER TABLE public.orders_log DROP CONSTRAINT fk_orders_log_rev;

DROP TABLE public.orders_log;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id35' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id34::razamd
ALTER TABLE public.o_activity_log DROP CONSTRAINT fk_o_activity_log_rev;

DROP TABLE public.o_activity_log;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id34' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id33::razamd
ALTER TABLE public.rev_info DROP CONSTRAINT rev_info_pkey;

DROP TABLE public.rev_info;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id33' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id32::razamd
DROP SEQUENCE public.hibernate_sequence CASCADE;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id32' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id31::razamd
ALTER TABLE public.order_log DROP COLUMN event;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id31' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id30::razamd
ALTER TABLE public.o_sub_activity DROP COLUMN due_date;

ALTER TABLE public.t_activity DROP COLUMN c_level;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id30' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id29::razamd
ALTER TABLE public.o_activity DROP COLUMN final_lead_time;

ALTER TABLE public.o_activity ADD time_from VARCHAR(255);

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id29' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id28::razamd
ALTER TABLE public.timeline DROP COLUMN std_lead_time;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id28' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id27::aditya
ALTER TABLE public.orders DROP COLUMN etd_date;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id27' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id26::aditya
ALTER TABLE public.timeline ADD tna_type VARCHAR(255);

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id26' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id25::razamd
ALTER TABLE public.t_activity DROP CONSTRAINT fk_t_activity_department_id;

ALTER TABLE public.t_activity DROP COLUMN department_id;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id25' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id24::razamd
ALTER TABLE public.activity RENAME COLUMN delay_reasons TO delay_reason;

ALTER TABLE public.t_activity DROP COLUMN delay_reasons;

ALTER TABLE public.o_activity ADD serial_no INTEGER;

ALTER TABLE public.o_activity ADD overridable BOOLEAN;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id24' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id23::razamd
ALTER TABLE public.users DROP COLUMN buyer_ids;

ALTER TABLE public.users DROP COLUMN team_id;

ALTER TABLE public.users DROP COLUMN department_id;

ALTER TABLE public.t_activity DROP COLUMN prev_lead_time;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id23' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id22::razamd
ALTER TABLE public.orders ADD timeline_id BIGINT;

ALTER TABLE public.o_sub_activity DROP COLUMN name;

ALTER TABLE public.o_activity DROP COLUMN overridable;

ALTER TABLE public.t_sub_activity DROP COLUMN name;

ALTER TABLE public.t_activity DROP COLUMN name;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id22' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id21::aditya
ALTER TABLE public.o_activity RENAME COLUMN name TO activity_name;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id21' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id20::aditya
ALTER TABLE public.o_activity DROP COLUMN serial_no;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id20' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id19::aditya
ALTER TABLE public.t_activity DROP COLUMN name;

ALTER TABLE public.t_activity DROP COLUMN serial_no;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id19' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id18::razamd
DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id18' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id17::razamd
ALTER TABLE public.users ADD team_id BIGINT;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id17' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id16::razamd
DROP INDEX public.uk_team_name;

CREATE UNIQUE INDEX uk_team_name ON public.team(lower((name)::text));

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id16' AND AUTHOR = 'razamd' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id15::aditya
ALTER TABLE public.order_log DROP CONSTRAINT fk_order_log_o_activity_id;

ALTER TABLE public.order_log DROP CONSTRAINT fk_order_log_order_id;

DROP TABLE public.order_log;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id15' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id14::aditya
ALTER TABLE public.o_sub_activity DROP CONSTRAINT fk_o_sub_activity_t_sub_activity_id;

ALTER TABLE public.o_sub_activity DROP CONSTRAINT fk_o_sub_activity_o_activity_id;

DROP TABLE public.o_sub_activity;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id14' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id13::aditya
ALTER TABLE public.o_activity DROP CONSTRAINT fk_o_activity_t_activity_id;

ALTER TABLE public.o_activity DROP CONSTRAINT fk_o_activity_order_id;

DROP TABLE public.o_activity;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id13' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id12::aditya
ALTER TABLE public.orders DROP CONSTRAINT fk_order_garment_type_id;

ALTER TABLE public.orders DROP CONSTRAINT fk_order_timeline_id;

ALTER TABLE public.orders DROP CONSTRAINT fk_order_season_id;

ALTER TABLE public.orders DROP CONSTRAINT fk_order_buyer_id;

DROP INDEX public.uk_order_po_ref;

DROP TABLE public.orders;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id12' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id11::aditya
ALTER TABLE public.t_sub_activity DROP CONSTRAINT fk_t_sub_activity_sub_activity_id;

ALTER TABLE public.t_sub_activity DROP CONSTRAINT fk_t_sub_activity_t_activity_id;

DROP TABLE public.t_sub_activity;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id11' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id10::aditya
ALTER TABLE public.t_activity DROP CONSTRAINT fk_t_activity_activity_id;

ALTER TABLE public.t_activity DROP CONSTRAINT fk_t_activity_timeline_id;

DROP TABLE public.t_activity;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id10' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id9::aditya
ALTER TABLE public.timeline DROP CONSTRAINT fk_timeline_buyer_id;

DROP INDEX public.uk_timeline_name;

DROP TABLE public.timeline;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id9' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id8::aditya
ALTER TABLE public.sub_activity DROP CONSTRAINT fk_sub_activity_activity_id;

DROP INDEX public.uk_sub_activity_name;

DROP TABLE public.sub_activity;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id8' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id7::aditya
ALTER TABLE public.activity DROP CONSTRAINT fk_activity_department_id;

DROP INDEX public.uk_activity_name;

DROP TABLE public.activity;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id7' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id6::aditya
ALTER TABLE public.users DROP CONSTRAINT fk_users_team_id;

DROP INDEX public.uk_users_email;

DROP INDEX public.uk_users_username;

DROP TABLE public.users;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id6' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id5::aditya
DROP INDEX public.uk_season_name;

DROP TABLE public.season;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id5' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id4::aditya
ALTER TABLE public.team DROP CONSTRAINT fk_team_department_id;

DROP INDEX public.uk_team_name;

DROP TABLE public.team;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id4' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id3::aditya
DROP INDEX public.uk_department_name;

DROP TABLE public.department;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id3' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id2::aditya
DROP INDEX public.uk_garment_type_name;

DROP TABLE public.garment_type;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id2' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';

-- Rolling Back ChangeSet: db/changelog/changelog-2.0.x.xml::v2.0.x-id1::aditya
DROP INDEX public.uk_buyer_name;

DROP TABLE public.buyer;

DELETE FROM public.databasechangelog WHERE ID = 'v2.0.x-id1' AND AUTHOR = 'aditya' AND FILENAME = 'db/changelog/changelog-2.0.x.xml';
