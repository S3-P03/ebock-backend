--
-- PostgreSQL database dump
--

\restrict HXzpaVq1q3sElLJCc27f1hkWeXeb4bLSfqTf5yUsLd8bPH8qSNmgUM1jN4XFygu

-- Dumped from database version 13.23 (Debian 13.23-1.pgdg13+1)
-- Dumped by pg_dump version 13.23 (Debian 13.23-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: ebock; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA ebock;


ALTER SCHEMA ebock OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: address; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.address (
    address_id integer NOT NULL,
    civic_number integer NOT NULL,
    appt_number integer,
    street character varying(60) NOT NULL,
    postal_code character varying(7) NOT NULL,
    country character varying(30) NOT NULL,
    province_code character varying(2) NOT NULL
);


ALTER TABLE ebock.address OWNER TO postgres;

--
-- Name: address_address_id_seq; Type: SEQUENCE; Schema: ebock; Owner: postgres
--

CREATE SEQUENCE ebock.address_address_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ebock.address_address_id_seq OWNER TO postgres;

--
-- Name: address_address_id_seq; Type: SEQUENCE OWNED BY; Schema: ebock; Owner: postgres
--

ALTER SEQUENCE ebock.address_address_id_seq OWNED BY ebock.address.address_id;


--
-- Name: category; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.category (
    category_id integer NOT NULL,
    name character varying(50) NOT NULL,
    parent_category integer
);


ALTER TABLE ebock.category OWNER TO postgres;

--
-- Name: category_category_id_seq; Type: SEQUENCE; Schema: ebock; Owner: postgres
--

CREATE SEQUENCE ebock.category_category_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ebock.category_category_id_seq OWNER TO postgres;

--
-- Name: category_category_id_seq; Type: SEQUENCE OWNED BY; Schema: ebock; Owner: postgres
--

ALTER SEQUENCE ebock.category_category_id_seq OWNED BY ebock.category.category_id;


--
-- Name: comment_; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.comment_ (
    comment_id integer NOT NULL,
    timestamp_ timestamp without time zone NOT NULL,
    content character varying(360) NOT NULL,
    updated_at timestamp without time zone,
    comment_id_1 integer NOT NULL,
    item_id integer NOT NULL,
    sender_cip character varying(8) NOT NULL
);


ALTER TABLE ebock.comment_ OWNER TO postgres;

--
-- Name: comment__comment_id_seq; Type: SEQUENCE; Schema: ebock; Owner: postgres
--

CREATE SEQUENCE ebock.comment__comment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ebock.comment__comment_id_seq OWNER TO postgres;

--
-- Name: comment__comment_id_seq; Type: SEQUENCE OWNED BY; Schema: ebock; Owner: postgres
--

ALTER SEQUENCE ebock.comment__comment_id_seq OWNED BY ebock.comment_.comment_id;


--
-- Name: delivery_option; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.delivery_option (
    delivery_optn_id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE ebock.delivery_option OWNER TO postgres;

--
-- Name: delivery_option_delivery_optn_id_seq; Type: SEQUENCE; Schema: ebock; Owner: postgres
--

CREATE SEQUENCE ebock.delivery_option_delivery_optn_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ebock.delivery_option_delivery_optn_id_seq OWNER TO postgres;

--
-- Name: delivery_option_delivery_optn_id_seq; Type: SEQUENCE OWNED BY; Schema: ebock; Owner: postgres
--

ALTER SEQUENCE ebock.delivery_option_delivery_optn_id_seq OWNED BY ebock.delivery_option.delivery_optn_id;


--
-- Name: favorite; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.favorite (
    cip character varying(8) NOT NULL,
    item_id integer NOT NULL,
    added_at timestamp without time zone NOT NULL
);


ALTER TABLE ebock.favorite OWNER TO postgres;

--
-- Name: image_; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.image_ (
    guid character varying(50) NOT NULL,
    original_filename character varying(200) NOT NULL,
    file_extension character varying(6),
    created_at timestamp without time zone
);


ALTER TABLE ebock.image_ OWNER TO postgres;

--
-- Name: item; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.item (
    item_id integer NOT NULL,
    name character varying(60) NOT NULL,
    description character varying(350) NOT NULL,
    price money NOT NULL,
    added_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone,
    sold boolean NOT NULL,
    quantity smallint NOT NULL,
    archived boolean NOT NULL,
    category_id integer NOT NULL,
    wear_id integer NOT NULL,
    seller_cip character varying(8) NOT NULL
);


ALTER TABLE ebock.item OWNER TO postgres;

--
-- Name: item_del_option; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.item_del_option (
    item_id integer NOT NULL,
    delivery_optn_id integer NOT NULL
);


ALTER TABLE ebock.item_del_option OWNER TO postgres;

--
-- Name: item_image; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.item_image (
    item_id integer NOT NULL,
    guid character varying(50) NOT NULL,
    displayorder smallint NOT NULL
);


ALTER TABLE ebock.item_image OWNER TO postgres;

--
-- Name: item_item_id_seq; Type: SEQUENCE; Schema: ebock; Owner: postgres
--

CREATE SEQUENCE ebock.item_item_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ebock.item_item_id_seq OWNER TO postgres;

--
-- Name: item_item_id_seq; Type: SEQUENCE OWNED BY; Schema: ebock; Owner: postgres
--

ALTER SEQUENCE ebock.item_item_id_seq OWNED BY ebock.item.item_id;


--
-- Name: item_paym_option; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.item_paym_option (
    item_id integer NOT NULL,
    payment_optn_id integer NOT NULL
);


ALTER TABLE ebock.item_paym_option OWNER TO postgres;

--
-- Name: log_; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.log_ (
    id integer NOT NULL,
    timestamp_ timestamp without time zone NOT NULL,
    content_1 character varying(350) NOT NULL,
    content_2 character varying(350),
    log_category_name character varying(50) NOT NULL
);


ALTER TABLE ebock.log_ OWNER TO postgres;

--
-- Name: log__id_seq; Type: SEQUENCE; Schema: ebock; Owner: postgres
--

CREATE SEQUENCE ebock.log__id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ebock.log__id_seq OWNER TO postgres;

--
-- Name: log__id_seq; Type: SEQUENCE OWNED BY; Schema: ebock; Owner: postgres
--

ALTER SEQUENCE ebock.log__id_seq OWNED BY ebock.log_.id;


--
-- Name: log_category; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.log_category (
    log_category_name character varying(50) NOT NULL
);


ALTER TABLE ebock.log_category OWNER TO postgres;

--
-- Name: order_; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.order_ (
    order_id integer NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at character varying(50),
    quantity character varying(50) NOT NULL,
    item_id integer NOT NULL,
    buyer_cip character varying(8) NOT NULL
);


ALTER TABLE ebock.order_ OWNER TO postgres;

--
-- Name: order__order_id_seq; Type: SEQUENCE; Schema: ebock; Owner: postgres
--

CREATE SEQUENCE ebock.order__order_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ebock.order__order_id_seq OWNER TO postgres;

--
-- Name: order__order_id_seq; Type: SEQUENCE OWNED BY; Schema: ebock; Owner: postgres
--

ALTER SEQUENCE ebock.order__order_id_seq OWNED BY ebock.order_.order_id;


--
-- Name: order_message; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.order_message (
    timestamp_ timestamp without time zone NOT NULL,
    content text NOT NULL,
    is_read boolean NOT NULL,
    order_id integer NOT NULL,
    sender_cip character varying(8) NOT NULL
);


ALTER TABLE ebock.order_message OWNER TO postgres;

--
-- Name: payment_option; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.payment_option (
    payment_optn_id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE ebock.payment_option OWNER TO postgres;

--
-- Name: payment_option_payment_optn_id_seq; Type: SEQUENCE; Schema: ebock; Owner: postgres
--

CREATE SEQUENCE ebock.payment_option_payment_optn_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ebock.payment_option_payment_optn_id_seq OWNER TO postgres;

--
-- Name: payment_option_payment_optn_id_seq; Type: SEQUENCE OWNED BY; Schema: ebock; Owner: postgres
--

ALTER SEQUENCE ebock.payment_option_payment_optn_id_seq OWNED BY ebock.payment_option.payment_optn_id;


--
-- Name: province; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.province (
    province_code character varying(2) NOT NULL,
    province_name character varying(30) NOT NULL
);


ALTER TABLE ebock.province OWNER TO postgres;

--
-- Name: review; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.review (
    reviewer_cip character varying(8) NOT NULL,
    reviewed_cip character varying(8) NOT NULL,
    timestamp_ timestamp without time zone NOT NULL,
    content character varying(360) NOT NULL,
    rating smallint NOT NULL,
    updated_at timestamp without time zone
);


ALTER TABLE ebock.review OWNER TO postgres;

--
-- Name: schema_migrations; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.schema_migrations (
    version text NOT NULL,
    applied_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE ebock.schema_migrations OWNER TO postgres;

--
-- Name: tag; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.tag (
    tag_id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE ebock.tag OWNER TO postgres;

--
-- Name: tag_item; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.tag_item (
    item_id integer NOT NULL,
    tag_id integer NOT NULL
);


ALTER TABLE ebock.tag_item OWNER TO postgres;

--
-- Name: tag_tag_id_seq; Type: SEQUENCE; Schema: ebock; Owner: postgres
--

CREATE SEQUENCE ebock.tag_tag_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ebock.tag_tag_id_seq OWNER TO postgres;

--
-- Name: tag_tag_id_seq; Type: SEQUENCE OWNED BY; Schema: ebock; Owner: postgres
--

ALTER SEQUENCE ebock.tag_tag_id_seq OWNED BY ebock.tag.tag_id;


--
-- Name: user_; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.user_ (
    cip character varying(8) NOT NULL,
    first_name character varying(50) NOT NULL,
    last_name character varying(50) NOT NULL,
    email character varying(90) NOT NULL,
    is_admin boolean NOT NULL,
    profile_picture_guid character varying(50),
    enabled boolean NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at character varying(50),
    address_id integer DEFAULT 1 NOT NULL
);


ALTER TABLE ebock.user_ OWNER TO postgres;

--
-- Name: wear; Type: TABLE; Schema: ebock; Owner: postgres
--

CREATE TABLE ebock.wear (
    wear_id integer NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE ebock.wear OWNER TO postgres;

--
-- Name: wear_wear_id_seq; Type: SEQUENCE; Schema: ebock; Owner: postgres
--

CREATE SEQUENCE ebock.wear_wear_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ebock.wear_wear_id_seq OWNER TO postgres;

--
-- Name: wear_wear_id_seq; Type: SEQUENCE OWNED BY; Schema: ebock; Owner: postgres
--

ALTER SEQUENCE ebock.wear_wear_id_seq OWNED BY ebock.wear.wear_id;


--
-- Name: address address_id; Type: DEFAULT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.address ALTER COLUMN address_id SET DEFAULT nextval('ebock.address_address_id_seq'::regclass);


--
-- Name: category category_id; Type: DEFAULT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.category ALTER COLUMN category_id SET DEFAULT nextval('ebock.category_category_id_seq'::regclass);


--
-- Name: comment_ comment_id; Type: DEFAULT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.comment_ ALTER COLUMN comment_id SET DEFAULT nextval('ebock.comment__comment_id_seq'::regclass);


--
-- Name: delivery_option delivery_optn_id; Type: DEFAULT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.delivery_option ALTER COLUMN delivery_optn_id SET DEFAULT nextval('ebock.delivery_option_delivery_optn_id_seq'::regclass);


--
-- Name: item item_id; Type: DEFAULT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item ALTER COLUMN item_id SET DEFAULT nextval('ebock.item_item_id_seq'::regclass);


--
-- Name: log_ id; Type: DEFAULT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.log_ ALTER COLUMN id SET DEFAULT nextval('ebock.log__id_seq'::regclass);


--
-- Name: order_ order_id; Type: DEFAULT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.order_ ALTER COLUMN order_id SET DEFAULT nextval('ebock.order__order_id_seq'::regclass);


--
-- Name: payment_option payment_optn_id; Type: DEFAULT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.payment_option ALTER COLUMN payment_optn_id SET DEFAULT nextval('ebock.payment_option_payment_optn_id_seq'::regclass);


--
-- Name: tag tag_id; Type: DEFAULT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.tag ALTER COLUMN tag_id SET DEFAULT nextval('ebock.tag_tag_id_seq'::regclass);


--
-- Name: wear wear_id; Type: DEFAULT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.wear ALTER COLUMN wear_id SET DEFAULT nextval('ebock.wear_wear_id_seq'::regclass);


--
-- Data for Name: address; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.address (address_id, civic_number, appt_number, street, postal_code, country, province_code) FROM stdin;
1	2500	\N	Bd de lUniversité	J1N 3C6	Canada	QC
\.


--
-- Data for Name: category; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.category (category_id, name, parent_category) FROM stdin;
1	Vêtements	\N
2	Électronique	\N
3	Livres	\N
4	Maisons	\N
5	Sports	\N
6	Autres	\N
7	Hauts	1
8	Bas	1
9	Chaussures	1
10	Accessoires	1
\.


--
-- Data for Name: comment_; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.comment_ (comment_id, timestamp_, content, updated_at, comment_id_1, item_id, sender_cip) FROM stdin;
\.


--
-- Data for Name: delivery_option; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.delivery_option (delivery_optn_id, name) FROM stdin;
1	Livraison
2	À récupérer
3	Transfert par courriel
\.


--
-- Data for Name: favorite; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.favorite (cip, item_id, added_at) FROM stdin;
pele3157	1	2026-06-23 21:26:00
\.


--
-- Data for Name: image_; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.image_ (guid, original_filename, file_extension, created_at) FROM stdin;
bbcdf6e1-214b-4caf-b5fa-27f5fa980e1e	S2_APP7_Classes.png	.png	2026-06-15 20:38:12.559624
f8c86541-0847-4fdd-a95a-fa3560364c4f	chalk.jpg	.jpg	2026-06-15 20:41:31.859791
\.


--
-- Data for Name: item; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.item (item_id, name, description, price, added_at, updated_at, sold, quantity, archived, category_id, wear_id, seller_cip) FROM stdin;
1	Mac Book avec Puce M5	MacBook avec une puce M5 qui run linux très bien	$2,500.12	2026-06-10 00:27:52.734861	2026-06-10 00:27:52.734861	f	1	f	2	1	herl2700
2	Auto BAJA	Belle auto baja avec une bonne transmission	$15,234.60	2026-06-10 00:27:52.734861	2026-06-10 00:27:52.734861	f	1	f	2	1	boum7113
3	Fusée L1	Une belle fusée qui peux être utilisé comme un missile	$1,000.00	2026-06-10 00:27:52.734861	2026-06-10 00:27:52.734861	f	1	f	2	1	dubw5596
5	Prise de laptop	Une prise de laptop vraiment longue	$10.00	2026-06-10 00:27:52.734861	2026-06-10 00:27:52.734861	f	1	f	2	1	pele3157
6	Lit	Pas besoin de lit si je dors pas	$649.00	2026-06-10 00:27:52.734861	2026-06-10 00:27:52.734861	t	0	t	4	1	bela3439
4	Chalk	Chalk pour l escalade	$67.67	2026-06-10 00:27:52.734861	2026-06-10 00:27:52.734861	t	1	f	5	1	larj4236
\.


--
-- Data for Name: item_del_option; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.item_del_option (item_id, delivery_optn_id) FROM stdin;
4	1
4	2
1	1
1	2
\.


--
-- Data for Name: item_image; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.item_image (item_id, guid, displayorder) FROM stdin;
4	bbcdf6e1-214b-4caf-b5fa-27f5fa980e1e	1
4	f8c86541-0847-4fdd-a95a-fa3560364c4f	2
\.


--
-- Data for Name: item_paym_option; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.item_paym_option (item_id, payment_optn_id) FROM stdin;
4	1
4	2
1	2
\.


--
-- Data for Name: log_; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.log_ (id, timestamp_, content_1, content_2, log_category_name) FROM stdin;
\.


--
-- Data for Name: log_category; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.log_category (log_category_name) FROM stdin;
User Actions
Item Management
Orders
Comments
System Events
\.


--
-- Data for Name: order_; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.order_ (order_id, created_at, updated_at, quantity, item_id, buyer_cip) FROM stdin;
1	2026-06-17 13:19:25.189001	2026-06-17 13:19:25.189001-04	1	4	larj4236
2	2026-06-17 13:19:42.383485	2026-06-17 13:19:42.383485-04	1	4	larj4236
3	2026-06-17 13:20:30.012509	2026-06-17 13:20:30.012509-04	1	4	larj4236
4	2026-06-17 13:21:42.914472	2026-06-17 13:21:42.914472-04	1	4	larj4236
5	2026-06-17 13:51:21.237211	2026-06-17 13:51:21.237211-04	1	4	larj4236
6	2026-06-17 13:55:15.742924	2026-06-17 13:55:15.742924-04	1	5	larj4236
7	2026-06-17 13:56:35.779751	2026-06-17 13:56:35.779751-04	1	5	larj4236
8	2026-06-18 10:07:26.131096	2026-06-18 10:07:26.131096-04	1	4	larj4236
9	2026-06-18 10:08:19.955974	2026-06-18 10:08:19.955974-04	1	4	pele3157
10	2026-06-18 10:10:02.246593	2026-06-18 10:10:02.246593-04	1	4	pele3157
\.


--
-- Data for Name: order_message; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.order_message (timestamp_, content, is_read, order_id, sender_cip) FROM stdin;
2026-06-17 13:59:05.849555	Salut !	f	6	larj4236
2026-06-17 14:02:17.389151	Bonjour à vous!	f	6	pele3157
2026-06-17 14:02:53.56755	J'aimerais acheter cet article, mon ordinateur est à 67% actuellement, je suis cooked	f	6	larj4236
2026-06-17 14:07:20.450053	Bin je sais pas trop j'ai encore besoin de ma charge...	f	6	pele3157
2026-06-17 14:16:20.309614	What??	f	6	larj4236
2026-06-17 14:18:23.760393	3000$ ?	f	6	larj4236
2026-06-17 14:18:49.337813	J'ai un exam tantôt	f	6	larj4236
2026-06-17 22:44:08.908649	Je commence a stresser	f	6	larj4236
2026-06-17 22:47:21.257778	Doooonc???	f	6	larj4236
2026-06-17 22:48:00.514321	goddam calm down	f	6	pele3157
2026-06-17 22:50:09.855163	Je ne souhaite pas te le vendre	f	6	pele3157
2026-06-17 22:52:18.835812	Je ne souhaite pas te le vendre	f	6	pele3157
2026-06-17 22:52:27.43083	Je ne souhaite pas te le vendre	f	6	pele3157
2026-06-17 22:53:42.469945	C'est bon j'ai compris	f	6	larj4236
2026-06-18 09:12:34.132228	Non t'as pas compris	f	6	pele3157
2026-06-18 09:54:44.600569	Bon matin !	f	6	larj4236
2026-06-18 09:55:03.857547	shut up	f	6	pele3157
2026-06-18 09:57:25.689473	fk u je t'ai prêté mon chargeur plein de fois	f	6	larj4236
2026-06-18 10:09:33.358533	Bonjour !	f	9	pele3157
2026-06-18 10:09:41.439661	Je suis une grimpeuse avide de V5	f	9	pele3157
2026-06-18 10:09:47.217989	J'aurais donc besoin de craie	f	9	pele3157
2026-06-18 10:09:55.10737	Car le problème est clairement soi la craie soit mes souliers	f	9	pele3157
2026-06-18 14:00:30.200464	toi fk u	f	6	pele3157
2026-06-19 08:07:25.934676	Allo	f	6	larj4236
2026-06-22 09:21:59.051156	womp womp	f	9	larj4236
2026-06-22 17:31:42.62867	waddup	f	6	larj4236
\.


--
-- Data for Name: payment_option; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.payment_option (payment_optn_id, name) FROM stdin;
1	Interac
2	Cash
\.


--
-- Data for Name: province; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.province (province_code, province_name) FROM stdin;
AB	Alberta
BC	Colombie-Britannique
MB	Manitoba
NB	Nouveau-Brunswick
NL	Terre-Neuve-et-Labrador
NT	Territoires du Nord-Ouest
NS	Nouvelle-Écosse
NU	Nunavut
ON	Ontario
PE	île-du-Prince-Édouard
QC	Québec
SK	Saskatchewan
YT	Yukon
\.


--
-- Data for Name: review; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.review (reviewer_cip, reviewed_cip, timestamp_, content, rating, updated_at) FROM stdin;
\.


--
-- Data for Name: schema_migrations; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.schema_migrations (version, applied_at) FROM stdin;
000_init.sql	2026-06-10 00:27:56.67199+00
001_add_address.sql	2026-06-10 00:27:57.034613+00
002_add_payment_options.sql	2026-06-10 00:27:57.457183+00
003_add_address_v2.sql	2026-06-10 00:27:57.837381+00
004_change_image_storage.sql	2026-06-10 00:27:58.206457+00
\.


--
-- Data for Name: tag; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.tag (tag_id, name) FROM stdin;
1	Électronique
2	Neuf
3	Cours
4	Usager
\.


--
-- Data for Name: tag_item; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.tag_item (item_id, tag_id) FROM stdin;
1	3
3	2
5	2
5	1
\.


--
-- Data for Name: user_; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.user_ (cip, first_name, last_name, email, is_admin, profile_picture_guid, enabled, created_at, updated_at, address_id) FROM stdin;
bela3439	Alex	Bellefroid Lefkakis	bela3439@usherbrooke.ca	f	\N	t	2026-06-10 00:27:52.713995	\N	1
boum7113	Milo	Boucher	boum7113@usherbrooke.ca	f	\N	t	2026-06-10 00:27:52.713995	\N	1
dubw5596	William	Dubuc	dubw5596@usherbrooke.ca	f	\N	t	2026-06-10 00:27:52.713995	\N	1
herl2700	Léanne	Héroux	herl2700@usherbrooke.ca	f	\N	t	2026-06-10 00:27:52.713995	\N	1
larj4236	Jean-Félix	Larouche	larj4236@usherbrooke.ca	f	\N	t	2026-06-10 00:27:52.713995	\N	1
pele3157	Éliane	Pelletier	pele3157@usherbrooke.ca	f	\N	t	2026-06-10 00:27:52.713995	\N	1
test1234	Utiilisateur	Test	test1234@usherbrooke.ca	f	\N	t	2026-06-10 00:27:52.713995	\N	1
bouc1234	Cuh	Boucher	bouc1234@usherbrooke.ca	f	\N	t	2026-06-18 12:21:05.966338	2026-06-18 12:21:05.966338-04	1
\.


--
-- Data for Name: wear; Type: TABLE DATA; Schema: ebock; Owner: postgres
--

COPY ebock.wear (wear_id, name) FROM stdin;
1	Factory New
2	Minimal Wear
3	Field-Tested
4	Well-Worn
5	Battle-Scarred
\.


--
-- Name: address_address_id_seq; Type: SEQUENCE SET; Schema: ebock; Owner: postgres
--

SELECT pg_catalog.setval('ebock.address_address_id_seq', 1, true);


--
-- Name: category_category_id_seq; Type: SEQUENCE SET; Schema: ebock; Owner: postgres
--

SELECT pg_catalog.setval('ebock.category_category_id_seq', 10, true);


--
-- Name: comment__comment_id_seq; Type: SEQUENCE SET; Schema: ebock; Owner: postgres
--

SELECT pg_catalog.setval('ebock.comment__comment_id_seq', 1, false);


--
-- Name: delivery_option_delivery_optn_id_seq; Type: SEQUENCE SET; Schema: ebock; Owner: postgres
--

SELECT pg_catalog.setval('ebock.delivery_option_delivery_optn_id_seq', 3, true);


--
-- Name: item_item_id_seq; Type: SEQUENCE SET; Schema: ebock; Owner: postgres
--

SELECT pg_catalog.setval('ebock.item_item_id_seq', 6, true);


--
-- Name: log__id_seq; Type: SEQUENCE SET; Schema: ebock; Owner: postgres
--

SELECT pg_catalog.setval('ebock.log__id_seq', 1, false);


--
-- Name: order__order_id_seq; Type: SEQUENCE SET; Schema: ebock; Owner: postgres
--

SELECT pg_catalog.setval('ebock.order__order_id_seq', 11, true);


--
-- Name: payment_option_payment_optn_id_seq; Type: SEQUENCE SET; Schema: ebock; Owner: postgres
--

SELECT pg_catalog.setval('ebock.payment_option_payment_optn_id_seq', 2, true);


--
-- Name: tag_tag_id_seq; Type: SEQUENCE SET; Schema: ebock; Owner: postgres
--

SELECT pg_catalog.setval('ebock.tag_tag_id_seq', 4, true);


--
-- Name: wear_wear_id_seq; Type: SEQUENCE SET; Schema: ebock; Owner: postgres
--

SELECT pg_catalog.setval('ebock.wear_wear_id_seq', 5, true);


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);


--
-- Name: category category_name_key; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.category
    ADD CONSTRAINT category_name_key UNIQUE (name);


--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_id);


--
-- Name: comment_ comment__pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.comment_
    ADD CONSTRAINT comment__pkey PRIMARY KEY (comment_id);


--
-- Name: delivery_option delivery_option_name_key; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.delivery_option
    ADD CONSTRAINT delivery_option_name_key UNIQUE (name);


--
-- Name: delivery_option delivery_option_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.delivery_option
    ADD CONSTRAINT delivery_option_pkey PRIMARY KEY (delivery_optn_id);


--
-- Name: favorite favorite_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.favorite
    ADD CONSTRAINT favorite_pkey PRIMARY KEY (cip, item_id);


--
-- Name: image_ image__pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.image_
    ADD CONSTRAINT image__pkey PRIMARY KEY (guid);


--
-- Name: item_del_option item_del_option_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item_del_option
    ADD CONSTRAINT item_del_option_pkey PRIMARY KEY (item_id, delivery_optn_id);


--
-- Name: item_image item_image_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item_image
    ADD CONSTRAINT item_image_pkey PRIMARY KEY (item_id, guid, displayorder);


--
-- Name: item_paym_option item_paym_option_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item_paym_option
    ADD CONSTRAINT item_paym_option_pkey PRIMARY KEY (item_id, payment_optn_id);


--
-- Name: item item_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (item_id);


--
-- Name: log_ log__pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.log_
    ADD CONSTRAINT log__pkey PRIMARY KEY (id);


--
-- Name: log_category log_category_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.log_category
    ADD CONSTRAINT log_category_pkey PRIMARY KEY (log_category_name);


--
-- Name: order_ order__pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.order_
    ADD CONSTRAINT order__pkey PRIMARY KEY (order_id);


--
-- Name: order_message order_message_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.order_message
    ADD CONSTRAINT order_message_pkey PRIMARY KEY (timestamp_);


--
-- Name: payment_option payment_option_name_key; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.payment_option
    ADD CONSTRAINT payment_option_name_key UNIQUE (name);


--
-- Name: payment_option payment_option_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.payment_option
    ADD CONSTRAINT payment_option_pkey PRIMARY KEY (payment_optn_id);


--
-- Name: province province_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.province
    ADD CONSTRAINT province_pkey PRIMARY KEY (province_code);


--
-- Name: province province_province_name_key; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.province
    ADD CONSTRAINT province_province_name_key UNIQUE (province_name);


--
-- Name: review review_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.review
    ADD CONSTRAINT review_pkey PRIMARY KEY (reviewer_cip, reviewed_cip, timestamp_);


--
-- Name: schema_migrations schema_migrations_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.schema_migrations
    ADD CONSTRAINT schema_migrations_pkey PRIMARY KEY (version);


--
-- Name: tag_item tag_item_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.tag_item
    ADD CONSTRAINT tag_item_pkey PRIMARY KEY (item_id, tag_id);


--
-- Name: tag tag_name_key; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.tag
    ADD CONSTRAINT tag_name_key UNIQUE (name);


--
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (tag_id);


--
-- Name: user_ user__email_key; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.user_
    ADD CONSTRAINT user__email_key UNIQUE (email);


--
-- Name: user_ user__pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.user_
    ADD CONSTRAINT user__pkey PRIMARY KEY (cip);


--
-- Name: wear wear_name_key; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.wear
    ADD CONSTRAINT wear_name_key UNIQUE (name);


--
-- Name: wear wear_pkey; Type: CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.wear
    ADD CONSTRAINT wear_pkey PRIMARY KEY (wear_id);


--
-- Name: address address_province_code_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.address
    ADD CONSTRAINT address_province_code_fkey FOREIGN KEY (province_code) REFERENCES ebock.province(province_code);


--
-- Name: category category_parent_category_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.category
    ADD CONSTRAINT category_parent_category_fkey FOREIGN KEY (parent_category) REFERENCES ebock.category(category_id);


--
-- Name: comment_ comment__comment_id_1_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.comment_
    ADD CONSTRAINT comment__comment_id_1_fkey FOREIGN KEY (comment_id_1) REFERENCES ebock.comment_(comment_id);


--
-- Name: comment_ comment__item_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.comment_
    ADD CONSTRAINT comment__item_id_fkey FOREIGN KEY (item_id) REFERENCES ebock.item(item_id);


--
-- Name: comment_ comment__sender_cip_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.comment_
    ADD CONSTRAINT comment__sender_cip_fkey FOREIGN KEY (sender_cip) REFERENCES ebock.user_(cip);


--
-- Name: favorite favorite_cip_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.favorite
    ADD CONSTRAINT favorite_cip_fkey FOREIGN KEY (cip) REFERENCES ebock.user_(cip);


--
-- Name: favorite favorite_item_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.favorite
    ADD CONSTRAINT favorite_item_id_fkey FOREIGN KEY (item_id) REFERENCES ebock.item(item_id);


--
-- Name: item item_category_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item
    ADD CONSTRAINT item_category_id_fkey FOREIGN KEY (category_id) REFERENCES ebock.category(category_id);


--
-- Name: item_del_option item_del_option_delivery_optn_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item_del_option
    ADD CONSTRAINT item_del_option_delivery_optn_id_fkey FOREIGN KEY (delivery_optn_id) REFERENCES ebock.delivery_option(delivery_optn_id);


--
-- Name: item_del_option item_del_option_item_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item_del_option
    ADD CONSTRAINT item_del_option_item_id_fkey FOREIGN KEY (item_id) REFERENCES ebock.item(item_id);


--
-- Name: item_image item_image_guid_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item_image
    ADD CONSTRAINT item_image_guid_fkey FOREIGN KEY (guid) REFERENCES ebock.image_(guid);


--
-- Name: item_image item_image_item_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item_image
    ADD CONSTRAINT item_image_item_id_fkey FOREIGN KEY (item_id) REFERENCES ebock.item(item_id);


--
-- Name: item_paym_option item_paym_option_item_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item_paym_option
    ADD CONSTRAINT item_paym_option_item_id_fkey FOREIGN KEY (item_id) REFERENCES ebock.item(item_id);


--
-- Name: item_paym_option item_paym_option_payment_optn_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item_paym_option
    ADD CONSTRAINT item_paym_option_payment_optn_id_fkey FOREIGN KEY (payment_optn_id) REFERENCES ebock.payment_option(payment_optn_id);


--
-- Name: item item_seller_cip_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item
    ADD CONSTRAINT item_seller_cip_fkey FOREIGN KEY (seller_cip) REFERENCES ebock.user_(cip);


--
-- Name: item item_wear_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.item
    ADD CONSTRAINT item_wear_id_fkey FOREIGN KEY (wear_id) REFERENCES ebock.wear(wear_id);


--
-- Name: log_ log__log_category_name_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.log_
    ADD CONSTRAINT log__log_category_name_fkey FOREIGN KEY (log_category_name) REFERENCES ebock.log_category(log_category_name);


--
-- Name: order_ order__buyer_cip_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.order_
    ADD CONSTRAINT order__buyer_cip_fkey FOREIGN KEY (buyer_cip) REFERENCES ebock.user_(cip);


--
-- Name: order_ order__item_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.order_
    ADD CONSTRAINT order__item_id_fkey FOREIGN KEY (item_id) REFERENCES ebock.item(item_id);


--
-- Name: order_message order_message_order_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.order_message
    ADD CONSTRAINT order_message_order_id_fkey FOREIGN KEY (order_id) REFERENCES ebock.order_(order_id);


--
-- Name: order_message order_message_sender_cip_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.order_message
    ADD CONSTRAINT order_message_sender_cip_fkey FOREIGN KEY (sender_cip) REFERENCES ebock.user_(cip);


--
-- Name: user_ profile_picture; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.user_
    ADD CONSTRAINT profile_picture FOREIGN KEY (profile_picture_guid) REFERENCES ebock.image_(guid);


--
-- Name: review review_reviewed_cip_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.review
    ADD CONSTRAINT review_reviewed_cip_fkey FOREIGN KEY (reviewed_cip) REFERENCES ebock.user_(cip);


--
-- Name: review review_reviewer_cip_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.review
    ADD CONSTRAINT review_reviewer_cip_fkey FOREIGN KEY (reviewer_cip) REFERENCES ebock.user_(cip);


--
-- Name: tag_item tag_item_item_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.tag_item
    ADD CONSTRAINT tag_item_item_id_fkey FOREIGN KEY (item_id) REFERENCES ebock.item(item_id);


--
-- Name: tag_item tag_item_tag_id_fkey; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.tag_item
    ADD CONSTRAINT tag_item_tag_id_fkey FOREIGN KEY (tag_id) REFERENCES ebock.tag(tag_id);


--
-- Name: user_ user_address; Type: FK CONSTRAINT; Schema: ebock; Owner: postgres
--

ALTER TABLE ONLY ebock.user_
    ADD CONSTRAINT user_address FOREIGN KEY (address_id) REFERENCES ebock.address(address_id);


--
-- PostgreSQL database dump complete
--

\unrestrict HXzpaVq1q3sElLJCc27f1hkWeXeb4bLSfqTf5yUsLd8bPH8qSNmgUM1jN4XFygu

