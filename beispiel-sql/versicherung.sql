 -- Table: Abteilung Kategory: none
CREATE TABLE Abteilung (
       Bezeichnung          VARCHAR2(1) NOT NULL,
       Nummer               INTEGER NOT NULL
);

ALTER TABLE Abteilung
       ADD  ( CONSTRAINT XPKAbteilung PRIMARY KEY (Nummer, Bezeichnung ) ) ;

 -- Table: Dienstwagen Kategory: none
CREATE TABLE Dienstwagen (
       Kennzeichen          VARCHAR2(10) NOT NULL,
       PersonalNr           INTEGER NULL
);

CREATE INDEX XIF100Fahrzeug ON Dienstwagen
(
       Kennzeichen               ASC 
);

CREATE INDEX XIF101Mitarbeiter ON Dienstwagen
(
       PersonalNr                ASC 
);

ALTER TABLE Dienstwagen
       ADD  ( CONSTRAINT XPKDienstwagen PRIMARY KEY (Kennzeichen ) ) ;

 -- Table: Fahrzeug Kategory: none
CREATE TABLE Fahrzeug (
       Fahrzeugtyp          VARCHAR2(30) NOT NULL,
       Farbe                VARCHAR2(10) NULL,
       Kennzeichen          VARCHAR2(10) NOT NULL
);

ALTER TABLE Fahrzeug
       ADD  ( CONSTRAINT XPKFahrzeug PRIMARY KEY (Kennzeichen ) ) ;

 -- Table: Mitarbeiter Kategory: none
CREATE TABLE Mitarbeiter (
       Abteilung            VARCHAR2(10) NOT NULL,
       AptBezeichnung       VARCHAR2(1) NOT NULL,
       AptNummer            INTEGER NOT NULL,
       EMail                VARCHAR2(30) NULL,
       Geburtsdatum         DATE NOT NULL,
       Name                 VARCHAR2(30) NOT NULL,
       PersonalNr           INTEGER NOT NULL,
       Raum                 VARCHAR2(10) NULL,
       Telefon              VARCHAR2(10) NULL,
       Vorname              VARCHAR2(30) NOT NULL
);

CREATE INDEX XIF102Abteilung ON Mitarbeiter
(
       AptNummer                 ASC ,
       AptBezeichnung            ASC 
);

ALTER TABLE Mitarbeiter
       ADD  ( CONSTRAINT XPKMitarbeiter PRIMARY KEY (PersonalNr ) ) ;

 -- Table: Schadensfall Kategory: none
CREATE TABLE Schadensfall (
       Datum                DATE NOT NULL,
       Ort                  VARCHAR2(20) NOT NULL,
       Sachbearbeiter       INTEGER NOT NULL,
       Schadenshoehe        NUMBER(18,5) NULL,
       Umstaende            VARCHAR2(40) NOT NULL,
       Verletzte            CHAR(1) NULL,
       Vorgangsnummer       INTEGER NOT NULL
);

CREATE INDEX XIF106Mitarbeiter ON Schadensfall
(
       Sachbearbeiter            ASC 
);

ALTER TABLE Schadensfall
       ADD  ( CONSTRAINT XPKSchadensfall PRIMARY KEY (Vorgangsnummer ) ) ;

 -- Table: VersNehmer Kategory: none
CREATE TABLE VersNehmer (
       FuehrerscheinSeit    DATE NULL,
       Geburtstag           DATE NULL,
       Hausnummer           VARCHAR2(30) NOT NULL,
       KundenNr             INTEGER NOT NULL,
       Name                 VARCHAR2(30) NOT NULL,
       Ort                  VARCHAR2(30) NOT NULL,
       PLZ                  VARCHAR2(30) NOT NULL,
       Strasse              VARCHAR2(30) NOT NULL,
       Vorname              VARCHAR2(30) NULL
);

ALTER TABLE VersNehmer
       ADD  ( CONSTRAINT XPKVersNehmer PRIMARY KEY (KundenNr ) ) ;

 -- Table: VersVertrag Kategory: none
CREATE TABLE VersVertrag (
       Kennzeichen          VARCHAR2(10) NOT NULL,
       KundenNr             INTEGER NOT NULL,
       PersonalNr           INTEGER NOT NULL,
       Vertragsabschluss    DATE NULL,
       Vertragsart          VARCHAR2(10) NOT NULL,
       Vertragsnr           INTEGER NOT NULL
);

CREATE INDEX XIF103VersNehmer ON VersVertrag
(
       KundenNr                  ASC 
);

CREATE INDEX XIF104Fahrzeug ON VersVertrag
(
       Kennzeichen               ASC 
);

CREATE INDEX XIF105Mitarbeiter ON VersVertrag
(
       PersonalNr                ASC 
);

ALTER TABLE VersVertrag
       ADD  ( CONSTRAINT XPKVersVertrag PRIMARY KEY (Vertragsnr ) ) ;

ALTER TABLE Dienstwagen
       ADD  ( CONSTRAINT R_100
              FOREIGN KEY (Kennzeichen)
                             REFERENCES Fahrzeug ) ;

ALTER TABLE Dienstwagen
       ADD  ( CONSTRAINT R_101
              FOREIGN KEY (PersonalNr)
                             REFERENCES Mitarbeiter ) ;

ALTER TABLE Mitarbeiter
       ADD  ( CONSTRAINT R_102
              FOREIGN KEY (AptNummer, AptBezeichnung)
                             REFERENCES Abteilung ) ;

ALTER TABLE Schadensfall
       ADD  ( CONSTRAINT R_106
              FOREIGN KEY (Sachbearbeiter)
                             REFERENCES Mitarbeiter ) ;

ALTER TABLE VersVertrag
       ADD  ( CONSTRAINT R_103
              FOREIGN KEY (KundenNr)
                             REFERENCES VersNehmer ) ;

ALTER TABLE VersVertrag
       ADD  ( CONSTRAINT R_104
              FOREIGN KEY (Kennzeichen)
                             REFERENCES Fahrzeug ) ;

ALTER TABLE VersVertrag
       ADD  ( CONSTRAINT R_105
              FOREIGN KEY (PersonalNr)
                             REFERENCES Mitarbeiter ) ;

