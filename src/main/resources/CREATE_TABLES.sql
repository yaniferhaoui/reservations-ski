CREATE TABLE IF NOT EXISTS EQUIPMENT(equipment_name TEXT PRIMARY KEY NOT NULL);

CREATE TABLE IF NOT EXISTS PERSON
  (personID			      BIGINT	      PRIMARY KEY			                                      NOT NULL,
  first_name          TEXT                                                                NOT NULL,
  last_name           TEXT                                                                NOT NULL,
  gender              BOOLEAN                                                             NOT NULL, -- 0 for male and 1 for female
  weight              SMALLINT                                                            NOT NULL,
  height              SMALLINT                                                            NOT NULL,
  level               TEXT                                                                NOT NULL,
  birth_date          BIGINT                                                              NOT NULL);

CREATE TABLE IF NOT EXISTS BOOKING
	(bookingID					BIGINT	      PRIMARY KEY			                                      NOT NULL);

CREATE TABLE IF NOT EXISTS PERSON_BOOKING
  (equipment_name					TEXT	                                                          NOT NULL,
  personID						  BIGINT	                                                          NOT NULL,
  bookingID 						BIGINT	                                                          NOT NULL,
  start_date						BIGINT									                                          NOT NULL,
  end_date						  BIGINT									                                          NOT NULL,
  CONSTRAINT fk_equipment_name 	FOREIGN KEY (equipment_name) 		REFERENCES EQUIPMENT(name) 				ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_personID     		FOREIGN KEY (personID) 		    REFERENCES PERSON(personID)      					ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_bookingID      	FOREIGN KEY (bookingID) 	    REFERENCES BOOKING(bookingID) 				    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT pk_person_booking 	PRIMARY KEY (equipment_name, personID, bookingID));
