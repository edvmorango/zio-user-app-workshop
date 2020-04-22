CREATE TABLE account(
  uuid UUID PRIMARY KEY,
  document VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE account_info(
  uuid UUID PRIMARY KEY,
  account_uuid UUID NOT NULL,
  current  BOOLEAN NOT NULL,
  status VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP
);

CREATE TABLE person(
  uuid UUID PRIMARY KEY,
  account_uuid UUID NOT NULL,
  current  BOOLEAN NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  birth_date DATE  NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE event(
  uuid UUID PRIMARY KEY ,
  correlation_uuid UUID NOT NULL,
  serial INTEGER NOT NULL,
  account_uuid UUID NOT NULL,
  body JSON NOT NULL,
  created_at TIMESTAMP NOT NULL,
  UNIQUE(correlation_uuid, serial)
);

ALTER TABLE account_info
ADD CONSTRAINT account_info_account_uuid FOREIGN KEY (account_uuid) REFERENCES account(uuid);

ALTER TABLE person
ADD CONSTRAINT person_account_uuid FOREIGN KEY (account_uuid) REFERENCES account(uuid);

ALTER TABLE event
ADD CONSTRAINT event_account_uuid FOREIGN KEY (account_uuid) REFERENCES account(uuid);