--
-- PostgreSQL database dump
--

-- Dumped from database version 17.2
-- Dumped by pg_dump version 17.2

-- Started on 2025-05-21 01:05:59

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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 218 (class 1259 OID 24581)
-- Name: book; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.book (
    id integer NOT NULL,
    author character varying(255) NOT NULL,
    booktitle character varying(255) NOT NULL,
    description text,
    isbn character varying(255) NOT NULL,
    publicationyear integer NOT NULL,
    storagearrivaldate timestamp(6) without time zone
);


ALTER TABLE public.book OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 24580)
-- Name: book_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.book_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.book_id_seq OWNER TO postgres;

--
-- TOC entry 4869 (class 0 OID 0)
-- Dependencies: 217
-- Name: book_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.book_id_seq OWNED BY public.book.id;


--
-- TOC entry 220 (class 1259 OID 24590)
-- Name: bookcatalogs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bookcatalogs (
    id integer NOT NULL,
    book_id integer NOT NULL,
    catalog_id integer NOT NULL
);


ALTER TABLE public.bookcatalogs OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 24589)
-- Name: bookcatalogs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bookcatalogs_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bookcatalogs_id_seq OWNER TO postgres;

--
-- TOC entry 4870 (class 0 OID 0)
-- Dependencies: 219
-- Name: bookcatalogs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bookcatalogs_id_seq OWNED BY public.bookcatalogs.id;


--
-- TOC entry 222 (class 1259 OID 24597)
-- Name: bookcopies; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bookcopies (
    copy_id integer NOT NULL,
    copy_status character varying(255),
    book_id integer,
    CONSTRAINT bookcopies_copy_status_check CHECK (((copy_status)::text = ANY ((ARRAY['AVAILABLE'::character varying, 'DAMAGED'::character varying, 'LOST'::character varying, 'RENTED'::character varying])::text[])))
);


ALTER TABLE public.bookcopies OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 24596)
-- Name: bookcopies_copy_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bookcopies_copy_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bookcopies_copy_id_seq OWNER TO postgres;

--
-- TOC entry 4871 (class 0 OID 0)
-- Dependencies: 221
-- Name: bookcopies_copy_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bookcopies_copy_id_seq OWNED BY public.bookcopies.copy_id;


--
-- TOC entry 224 (class 1259 OID 24605)
-- Name: catalogs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.catalogs (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    parent_id integer
);


ALTER TABLE public.catalogs OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 24604)
-- Name: catalogs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.catalogs_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.catalogs_id_seq OWNER TO postgres;

--
-- TOC entry 4872 (class 0 OID 0)
-- Dependencies: 223
-- Name: catalogs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.catalogs_id_seq OWNED BY public.catalogs.id;


--
-- TOC entry 226 (class 1259 OID 24612)
-- Name: rental; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rental (
    id integer NOT NULL,
    due_date timestamp(6) without time zone NOT NULL,
    return_date timestamp(6) without time zone,
    start_date timestamp(6) without time zone NOT NULL,
    rental_status character varying(255) NOT NULL,
    copy_id integer NOT NULL,
    user_id integer NOT NULL,
    CONSTRAINT rental_rental_status_check CHECK (((rental_status)::text = ANY ((ARRAY['RENTED'::character varying, 'RETURNED'::character varying, 'LATE'::character varying])::text[])))
);


ALTER TABLE public.rental OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 24611)
-- Name: rental_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.rental_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.rental_id_seq OWNER TO postgres;

--
-- TOC entry 4873 (class 0 OID 0)
-- Dependencies: 225
-- Name: rental_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.rental_id_seq OWNED BY public.rental.id;


--
-- TOC entry 228 (class 1259 OID 24620)
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles (
    id integer NOT NULL,
    rolename character varying(255) NOT NULL
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 24619)
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.roles_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.roles_id_seq OWNER TO postgres;

--
-- TOC entry 4874 (class 0 OID 0)
-- Dependencies: 227
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;


--
-- TOC entry 229 (class 1259 OID 24626)
-- Name: userroles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.userroles (
    user_id integer NOT NULL,
    role_id integer NOT NULL
);


ALTER TABLE public.userroles OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 24630)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    username character varying(255) NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 24629)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO postgres;

--
-- TOC entry 4875 (class 0 OID 0)
-- Dependencies: 230
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 4667 (class 2604 OID 24584)
-- Name: book id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.book ALTER COLUMN id SET DEFAULT nextval('public.book_id_seq'::regclass);


--
-- TOC entry 4668 (class 2604 OID 24593)
-- Name: bookcatalogs id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bookcatalogs ALTER COLUMN id SET DEFAULT nextval('public.bookcatalogs_id_seq'::regclass);


--
-- TOC entry 4669 (class 2604 OID 24600)
-- Name: bookcopies copy_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bookcopies ALTER COLUMN copy_id SET DEFAULT nextval('public.bookcopies_copy_id_seq'::regclass);


--
-- TOC entry 4670 (class 2604 OID 24608)
-- Name: catalogs id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.catalogs ALTER COLUMN id SET DEFAULT nextval('public.catalogs_id_seq'::regclass);


--
-- TOC entry 4671 (class 2604 OID 24615)
-- Name: rental id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rental ALTER COLUMN id SET DEFAULT nextval('public.rental_id_seq'::regclass);


--
-- TOC entry 4672 (class 2604 OID 24623)
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);


--
-- TOC entry 4673 (class 2604 OID 24633)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 4850 (class 0 OID 24581)
-- Dependencies: 218
-- Data for Name: book; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.book VALUES (3, 'Макс Фрай', 'Чужак', 'В первую книгу серии «Лабиринты Ехо» вошли семь повестей о приключениях сэра Макса и его друзей в городе Сердца Мира и за его пределами. Открывает сборник «Дебют в Ехо». Читатели узнают, кто же такой сэр Макс и как его угораздило попасть из нашей вселенной в магическую.', 'A3Y789432RHU3GRY3', 2005, '2025-05-15 22:38:46.748637');
INSERT INTO public.book VALUES (4, 'Автор1', 'Книга1', 'В первую книгу серии «Лабиринты Ехо» вошли семь повестей о приключениях сэра Макса и его друзей в городе Сердца Мира и за его пределами. Открывает сборник «Дебют в Ехо». Читатели узнают, кто же такой сэр Макс и как его угораздило попасть из нашей вселенной в магическую.', 'A3Y789432RHU3GRY3213', 2002, '2025-05-19 16:59:04.986541');
INSERT INTO public.book VALUES (5, 'Автор2', 'Книга2', 'В первую книгу серии «Лабиринты Ехо» вошли семь повестей о приключениях сэра Макса и его друзей в городе Сердца Мира и за его пределами. Открывает сборник «Дебют в Ехо». Читатели узнают, кто же такой сэр Макс и как его угораздило попасть из нашей вселенной в магическую.', 'A3Y789432RHU3GRY321323', 2002, '2025-05-19 16:59:34.305139');
INSERT INTO public.book VALUES (6, 'Автор3', 'Книга3', 'В первую книгу серии «Лабиринты Ехо» вошли семь повестей о приключениях сэра Макса и его друзей в городе Сердца Мира и за его пределами. Открывает сборник «Дебют в Ехо». Читатели узнают, кто же такой сэр Макс и как его угораздило попасть из нашей вселенной в магическую.', 'A3Y789432RHU3GRY321323ввв', 2002, '2025-05-19 17:00:48.975738');
INSERT INTO public.book VALUES (9, 'Автор4', 'Книга4', 'В первую книгу серии «Лабиринты Ехо» вошли семь повестей о приключениях сэра Макса и его друзей в городе Сердца Мира и за его пределами. Открывает сборник «Дебют в Ехо». Читатели узнают, кто же такой сэр Макс и как его угораздило попасть из нашей вселенной в магическую.', 'A3Y789432RHU3GRY321323345324ввв', 2002, '2025-05-19 17:12:57.130405');


--
-- TOC entry 4852 (class 0 OID 24590)
-- Dependencies: 220
-- Data for Name: bookcatalogs; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 4854 (class 0 OID 24597)
-- Dependencies: 222
-- Data for Name: bookcopies; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.bookcopies VALUES (31, 'AVAILABLE', 4);
INSERT INTO public.bookcopies VALUES (32, 'AVAILABLE', 4);
INSERT INTO public.bookcopies VALUES (33, 'AVAILABLE', 4);
INSERT INTO public.bookcopies VALUES (34, 'AVAILABLE', 4);
INSERT INTO public.bookcopies VALUES (35, 'AVAILABLE', 4);
INSERT INTO public.bookcopies VALUES (36, 'AVAILABLE', 5);
INSERT INTO public.bookcopies VALUES (37, 'AVAILABLE', 5);
INSERT INTO public.bookcopies VALUES (38, 'AVAILABLE', 5);
INSERT INTO public.bookcopies VALUES (39, 'AVAILABLE', 5);
INSERT INTO public.bookcopies VALUES (40, 'AVAILABLE', 5);
INSERT INTO public.bookcopies VALUES (41, 'AVAILABLE', 6);
INSERT INTO public.bookcopies VALUES (42, 'AVAILABLE', 6);
INSERT INTO public.bookcopies VALUES (43, 'AVAILABLE', 6);
INSERT INTO public.bookcopies VALUES (56, 'AVAILABLE', 9);
INSERT INTO public.bookcopies VALUES (57, 'AVAILABLE', 9);
INSERT INTO public.bookcopies VALUES (58, 'AVAILABLE', 9);
INSERT INTO public.bookcopies VALUES (59, 'AVAILABLE', 9);
INSERT INTO public.bookcopies VALUES (60, 'AVAILABLE', 9);
INSERT INTO public.bookcopies VALUES (45, 'AVAILABLE', 6);
INSERT INTO public.bookcopies VALUES (44, 'AVAILABLE', 6);


--
-- TOC entry 4856 (class 0 OID 24605)
-- Dependencies: 224
-- Data for Name: catalogs; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.catalogs VALUES (15, 'Приключения', NULL);


--
-- TOC entry 4858 (class 0 OID 24612)
-- Dependencies: 226
-- Data for Name: rental; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.rental VALUES (6, '2025-05-30 23:59:59', '2025-05-19 20:06:33.41681', '2025-05-19 19:59:09.14071', 'RETURNED', 45, 11);
INSERT INTO public.rental VALUES (5, '2025-05-30 23:59:59', '2025-05-19 20:06:45.334944', '2025-05-19 19:58:54.914734', 'RETURNED', 44, 11);


--
-- TOC entry 4860 (class 0 OID 24620)
-- Dependencies: 228
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.roles VALUES (1, 'ROLE_USER');
INSERT INTO public.roles VALUES (2, 'ROLE_ADMIN');


--
-- TOC entry 4861 (class 0 OID 24626)
-- Dependencies: 229
-- Data for Name: userroles; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.userroles VALUES (10, 1);
INSERT INTO public.userroles VALUES (10, 2);
INSERT INTO public.userroles VALUES (3, 1);
INSERT INTO public.userroles VALUES (11, 1);


--
-- TOC entry 4863 (class 0 OID 24630)
-- Dependencies: 231
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.users VALUES (3, 'email@example.com', 'pupy', 'lotty');
INSERT INTO public.users VALUES (10, 'ultrabros@gmail.com', '$2a$10$fG0PpuKyuq122i8j/FwF6e/dwWaq8T3RC3qPZfQcZsB5ousJzsSEW', 'adminGANGGG');
INSERT INTO public.users VALUES (11, 'test@exampledefaultuser.com', '$2a$10$KzZb.Q2sguRU96yejU27QOgcVMc8PmAGqBVJgiRNCZ9sw6d5mrkra', 'defaultuser');


--
-- TOC entry 4876 (class 0 OID 0)
-- Dependencies: 217
-- Name: book_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.book_id_seq', 9, true);


--
-- TOC entry 4877 (class 0 OID 0)
-- Dependencies: 219
-- Name: bookcatalogs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bookcatalogs_id_seq', 4, true);


--
-- TOC entry 4878 (class 0 OID 0)
-- Dependencies: 221
-- Name: bookcopies_copy_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.bookcopies_copy_id_seq', 70, true);


--
-- TOC entry 4879 (class 0 OID 0)
-- Dependencies: 223
-- Name: catalogs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.catalogs_id_seq', 18, true);


--
-- TOC entry 4880 (class 0 OID 0)
-- Dependencies: 225
-- Name: rental_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.rental_id_seq', 6, true);


--
-- TOC entry 4881 (class 0 OID 0)
-- Dependencies: 227
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.roles_id_seq', 4, true);


--
-- TOC entry 4882 (class 0 OID 0)
-- Dependencies: 230
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 11, true);


-- Completed on 2025-05-21 01:05:59

--
-- PostgreSQL database dump complete
--

