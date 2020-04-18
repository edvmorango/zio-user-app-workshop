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
  updated_at TIMESTAMP NULL
);

CREATE TABLE person(
  uuid UUID PRIMARY KEY,
  account_uuid UUID NOT NULL,
  current  BOOLEAN NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  birth_date VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE event(
  uuid UUID,
  serial INTEGER NOT NULL,
  correlation_uuid UUID NOT NULL,
  account_uuid UUID NOT NULL,
  body JSON NOT NULL,
  created_at TIMESTAMP NOT NULL,
  PRIMARY KEY (uuid, serial)
);

ALTER TABLE account_info
ADD CONSTRAINT account_info_account_uuid FOREIGN KEY (account_uuid) REFERENCES account(uuid);

ALTER TABLE person
ADD CONSTRAINT person_account_uuid FOREIGN KEY (account_uuid) REFERENCES account(uuid);

ALTER TABLE event
ADD CONSTRAINT event_account_uuid FOREIGN KEY (account_uuid) REFERENCES account(uuid);