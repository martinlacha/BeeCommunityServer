CREATE TABLE ADDRESS (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    country TEXT NOT NULL,
    state TEXT,
    town TEXT NOT NULL,
    street TEXT,
    house_number TEXT NOT NULL
);

CREATE TABLE USER_INFO (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name TEXT NOT NULL,
    surname TEXT NOT NULL,
    date_of_birth DATE NOT NULL,
    created_date DATE DEFAULT NOW(),
    experience TEXT,
    address_id INT NOT NULL REFERENCES ADDRESS(id)
);

CREATE TABLE AUTH_ROLE (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    role_name TEXT UNIQUE NOT NULL
);

CREATE TABLE AUTH_USER (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    login_attempts INT DEFAULT 0,
    suspended BOOLEAN DEFAULT FALSE,
    new_account BOOLEAN DEFAULT TRUE,
    user_info_id INT REFERENCES USER_INFO(id)
);

CREATE TABLE AUTH_USER_ROLE (
    user_id INT NOT NULL REFERENCES AUTH_USER(id),
    role_id INT NOT NULL REFERENCES AUTH_ROLE(id),
    UNIQUE (user_id, role_id)
);

CREATE TABLE FRIENDSHIP (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    status TEXT NOT NULL,
    sender_id INT NOT NULL REFERENCES AUTH_USER(id),
    receiver_id INT NOT NULL REFERENCES AUTH_USER(id),
    UNIQUE (sender_id, receiver_id)
);

CREATE TABLE COMMUNITY_POST(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT NOT NULL REFERENCES AUTH_USER(id),
    title TEXT NOT NULL,
    post TEXT NOT NULL,
    image bytea,
    access TEXT NOT NULL,
    type TEXT,
    created DATE DEFAULT now()
);

CREATE TABLE POST_COMMENT(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    post_id INT NOT NULL REFERENCES COMMUNITY_POST(id),
    user_id INT NOT NULL REFERENCES AUTH_USER(id),
    comment TEXT NOT NULL,
    date DATE NOT NULL DEFAULT now()
);

CREATE TABLE APIARY (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT NOT NULL REFERENCES AUTH_USER(id),
    name TEXT NOT NULL,
    environment TEXT NOT NULL,
    terrain TEXT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    notes TEXT,
    image bytea
);

CREATE TABLE QUEEN (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT NOT NULL REFERENCES AUTH_USER(id),
    name TEXT,
    breed TEXT,
    color TEXT,
    queen_hatch DATE DEFAULT now(),
    notes TEXT,
    image bytea
);

CREATE TABLE HIVE (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT NOT NULL REFERENCES AUTH_USER(id),
    apiary_id INT NOT NULL REFERENCES APIARY(id),
    queen_id INT NOT NULL REFERENCES QUEEN(id),
    name TEXT NOT NULL,
    color TEXT NOT NULL,
    bee_source TEXT NOT NULL,
    date_establishment DATE NOT NULL,
    structure TEXT,
    notes TEXT,
    image bytea
);

CREATE TABLE NEWS (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title TEXT NOT NULL,
    article TEXT NOT NULL,
    date DATE NOT NULL,
    title_image bytea,
    first_image bytea,
    second_image bytea,
    author_id INT NOT NULL REFERENCES AUTH_USER(id)
);

CREATE TABLE EVENT(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT NOT NULL REFERENCES AUTH_USER(id),
    title TEXT NOT NULL,
    activity TEXT,
    type TEXT NOT NULL,
    notes TEXT,
    date DATE NOT NULL,
    finished BOOLEAN DEFAULT FALSE
);

CREATE TABLE STRESSORS (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    varroa_mites BOOLEAN DEFAULT FALSE,
    chalkbrood BOOLEAN DEFAULT FALSE,
    sacbrood BOOLEAN DEFAULT FALSE,
    foulbrood BOOLEAN DEFAULT FALSE,
    nosema BOOLEAN DEFAULT FALSE,
    beetles BOOLEAN DEFAULT FALSE,
    mice BOOLEAN DEFAULT FALSE,
    ants BOOLEAN DEFAULT FALSE,
    moths BOOLEAN DEFAULT FALSE,
    wasps BOOLEAN DEFAULT FALSE,
    hornet BOOLEAN DEFAULT FALSE
);

CREATE TABLE SYMPTOMS (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    -- Body and behaviour
    bees_cant_fly BOOLEAN DEFAULT FALSE,
    deformed_wings BOOLEAN DEFAULT FALSE,
    hyperactivity BOOLEAN DEFAULT FALSE,
    poor_motor_coordination BOOLEAN DEFAULT FALSE,
    deformed_abdomens BOOLEAN DEFAULT FALSE,
    bees_fighting BOOLEAN DEFAULT FALSE,
    trembling BOOLEAN DEFAULT FALSE,
    shiny_black_bees BOOLEAN DEFAULT FALSE,

    -- Brood
    dead_larvae BOOLEAN DEFAULT FALSE,
    chalky_larvae BOOLEAN DEFAULT FALSE,
    discolored_larvae BOOLEAN DEFAULT FALSE,
    mites_on_larvae BOOLEAN DEFAULT FALSE,
    patchy_brood BOOLEAN DEFAULT FALSE,
    punctured_capped_brood BOOLEAN DEFAULT FALSE,
    ropey_larvae BOOLEAN DEFAULT FALSE,
    saclike_larvae BOOLEAN DEFAULT FALSE,
    sunken_cappings BOOLEAN DEFAULT FALSE,

    -- Death
    chalky_corpses BOOLEAN DEFAULT FALSE,
    dead_bees BOOLEAN DEFAULT FALSE,
    translucent_pale_corpses BOOLEAN DEFAULT FALSE,

    -- Environment
    bad_smell BOOLEAN DEFAULT FALSE,
    fecal_markings BOOLEAN DEFAULT FALSE
);

CREATE TABLE HIVE_TREATMENT(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    disease TEXT,
    treatment TEXT,
    quantity DOUBLE PRECISION,
    dose TEXT,
    start_date DATE,
    end_date DATE
);

CREATE TABLE HIVE_FEEDING(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    food TEXT,
    ratio TEXT,
    quantity DOUBLE PRECISION,
    unit TEXT
);

CREATE TABLE HIVE_HARVEST(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    product TEXT NOT NULL,
    quantity DOUBLE PRECISION NOT NULL DEFAULT 0,
    unit TEXT NOT NULL,
    super_count int DEFAULT 0,
    frame_count INT DEFAULT 0
);

CREATE TABLE HIVE_INSPECTION (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    -- Basic information
    hive_id INT NOT NULL REFERENCES HIVE(id),
    user_id INT NOT NULL REFERENCES AUTH_USER(id),

    type TEXT NOT NULL,
    inspection_date DATE NOT NULL,
    weather TEXT NOT NULL,

    -- Population
    population TEXT NOT NULL,

    -- Food and supply
    food_storage TEXT,
    sources_nearby TEXT,
    brood_pattern TEXT,

    -- Queens and broods
    queen BOOLEAN DEFAULT FALSE,
    eggs BOOLEAN DEFAULT FALSE,
    uncapped_brood BOOLEAN DEFAULT FALSE,
    capped_brood BOOLEAN DEFAULT FALSE,

    -- Stressors reference
    stressors_id INT NOT NULL REFERENCES STRESSORS(id),

    -- Symptoms reference
    -- symptoms_id INT NOT NULL REFERENCES SYMPTOMS(id),

    treatment_id INT NOT NULL REFERENCES HIVE_TREATMENT(id),

    feeding_id INT NOT NULL REFERENCES HIVE_FEEDING(id),

    harvest_id INT NOT NULL REFERENCES HIVE_HARVEST(id),

    -- Bees mood
    colony_temperament TEXT,

    -- Other
    notes TEXT,

    inspection_image bytea,
    food_image bytea,
    population_image bytea,
    queen_image bytea,
    brood_image bytea,
    stressors_image bytea,
    disease_image bytea
);

CREATE TABLE SENSORS_DATA (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    hive_id INT NOT NULL REFERENCES HIVE(id),
    time TIMESTAMP NOT NULL,
    -- Measured data
    weight DOUBLE PRECISION DEFAULT 0,
    hive_temperature DOUBLE PRECISION DEFAULT 0,
    hive_humidity DOUBLE PRECISION DEFAULT 0,
    outside_temperature DOUBLE PRECISION DEFAULT 0,
    outside_humidity DOUBLE PRECISION DEFAULT 0
);