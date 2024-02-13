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
    user_info_id INT REFERENCES USER_INFO(id),
    role_id INT REFERENCES AUTH_ROLE(id)
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

CREATE TABLE GROUP_USERS (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    admin_id INT NOT NULL REFERENCES AUTH_USER(id),
    created DATE NOT NULL
);

CREATE TABLE QUEEN (
   id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
   user_id INT NOT NULL REFERENCES AUTH_USER(id),
   breed TEXT NOT NULL,
   color TEXT NOT NULL,
   acceptance_date DATE,
   notes TEXT,
   image bytea
);

CREATE TABLE APIARY (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT NOT NULL REFERENCES AUTH_USER(id),
    environment TEXT NOT NULL,
    terrain TEXT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    notes TEXT
);

CREATE TABLE HIVE (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    apiary_id INT NOT NULL REFERENCES APIARY(id),
    queen_id INT REFERENCES QUEEN(id),
    frame_count INT NOT NULL DEFAULT 0,
    color TEXT NOT NULL,
    bee_source TEXT NOT NULL,
    date_establishment DATE NOT NULL,
    notes TEXT
);

CREATE TABLE STORAGE (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    item TEXT NOT NULL,
    count INT NOT NULL DEFAULT 0
);

CREATE TABLE NEWS (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title TEXT NOT NULL,
    title_image bytea NOT NULL,
    first_image bytea NOT NULL,
    second_image bytea NOT NULL,
    author_id INT NOT NULL REFERENCES AUTH_USER(id)
);

CREATE TABLE STRESSORS (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    varroa_mites BIT,
    chalkbrood BIT,
    sacbrood BIT,
    american_foulbrood BIT,
    european_foulbrood BIT,
    nosema BIT,
    beetles BIT,
    mice BIT,
    ants BIT,
    moths BIT,
    wasps BIT,
    yellow_jackets BIT,
    other_stressors BIT,
    none_of_stressors BIT
);

CREATE TABLE SYMPTOMS (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    -- Body and behaviour
    bees_cant_fly BIT,
    deformed_wings BIT,
    hyperactivity BIT,
    poor_motor_coordination BIT,
    deformed_abdomens BIT,
    bees_fighting BIT,
    trembling BIT,
    shiny_black_bees BIT,

    -- Brood
    dead_larvae BIT,
    chalky_larvae BIT,
    discolored_larvae BIT,
    mites_on_larvae BIT,
    patchy_brood BIT,
    punctured_capped_brood BIT,
    ropey_larvae BIT,
    saclike_larvae BIT,
    sunken_cappings BIT,

    -- Death
    chalky_corpses BIT,
    dead_bees BIT,
    translucent_pale_corpses BIT,

    -- Environment
    bad_smell BIT,
    fecal_markings BIT,

    -- Other
    other BIT,
    none_of_symptoms BIT
);

CREATE TABLE HIVE_INSPECTION (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    -- Basic information
    hive_id INT NOT NULL REFERENCES HIVE(id),
    inspection_date DATE NOT NULL,
    weather TEXT NOT NULL,
    temperature INT,

    -- Population
    population TEXT NOT NULL,
    covered_frames INT,

    -- Food and supply
    food_storage TEXT,
    sources_nearby TEXT,
    harvest_time BIT,

    -- Queens and broods
    queen BIT,
    eggs BIT,
    uncapped_brood BIT,
    capped_brood BIT,
    none_brood BIT,

    -- Stressors reference
    stressors_id INT NOT NULL REFERENCES STRESSORS(id),

    -- Symptoms reference
    symptoms_id INT NOT NULL REFERENCES SYMPTOMS(id),

    -- Bees mood
    colony_temperament TEXT,

    -- Other
    notes TEXT,
    photo bytea
);

CREATE TABLE HONEY_HARVEST(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT NOT NULL REFERENCES AUTH_USER(id),
    harvest_date DATE NOT NULL,
    total_honey_weight REAL NOT NULL DEFAULT 0
);

CREATE TABLE HIVE_HARVEST_INFO(
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    honey_harvest_id INT NOT NULL REFERENCES HONEY_HARVEST(id),
    hive_id INT NOT NULL REFERENCES HIVE(id),
    honey_weight REAL NOT NULL DEFAULT 0
);