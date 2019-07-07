CREATE TABLE "isissettings"."ApplicationSetting"("key" VARCHAR(128) NOT NULL,"description" VARCHAR(254),"type" VARCHAR(20) NOT NULL,"valueRaw" VARCHAR(255) NOT NULL,CONSTRAINT "ApplicationSetting_PK" PRIMARY KEY("key"));
CREATE TABLE "isissettings"."UserSetting"("key" VARCHAR(128) NOT NULL,"user" VARCHAR(50) NOT NULL,"description" VARCHAR(254),"type" VARCHAR(20) NOT NULL,"valueRaw" VARCHAR(255) NOT NULL,CONSTRAINT "UserSetting_PK" PRIMARY KEY("key","user"));

CREATE TABLE "isisaudit"."AuditEntry"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"memberIdentifier" VARCHAR(255),"postValue" VARCHAR(255),"preValue" VARCHAR(255),"propertyId" VARCHAR(50),"sequence" INTEGER NOT NULL,"targetClass" VARCHAR(50),"target" VARCHAR(2000),"timestamp" TIMESTAMP NOT NULL,"transactionId" VARCHAR(36) NOT NULL,"user" VARCHAR(50) NOT NULL,CONSTRAINT "AuditEntry_PK" PRIMARY KEY("id"));
ALTER TABLE "isisaudit"."AuditEntry" ALTER COLUMN "id" RESTART WITH 0;
CREATE INDEX "AuditEntry_target_ts_IDX" ON "isisaudit"."AuditEntry"("target","timestamp");
CREATE UNIQUE INDEX "AuditEntry_ak" ON "isisaudit"."AuditEntry"("transactionId","sequence","target","propertyId");

CREATE TABLE "isiscommand"."Command"("transactionId" VARCHAR(36) NOT NULL,"arguments" VARCHAR(16777216),"completedAt" TIMESTAMP,"exception" CLOB(1G),"executeIn" VARCHAR(10) NOT NULL,"memberIdentifier" VARCHAR(255) NOT NULL,"memento" CLOB(1G),"parentTransactionId" VARCHAR(36),"replayState" VARCHAR(10),"replayStateFailureReason" VARCHAR(255),"result" VARCHAR(2000),"startedAt" TIMESTAMP,"targetAction" VARCHAR(50) NOT NULL,"targetClass" VARCHAR(50) NOT NULL,"target" VARCHAR(2000),"timestamp" TIMESTAMP NOT NULL,"user" VARCHAR(50) NOT NULL,CONSTRAINT "Command_PK" PRIMARY KEY("transactionId"),CONSTRAINT "Command_FK1" FOREIGN KEY("parentTransactionId") REFERENCES "isiscommand"."Command"("transactionId"));
CREATE INDEX "CommandJdo_timestamp_e_s_IDX" ON "isiscommand"."Command"("timestamp","executeIn","startedAt");
CREATE INDEX "Command_N49" ON "isiscommand"."Command"("parentTransactionId");
CREATE INDEX "CommandJdo_startedAt_e_c_IDX" ON "isiscommand"."Command"("startedAt","executeIn","completedAt");

CREATE TABLE "isissecurity"."ApplicationUser"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"accountType" VARCHAR(255) NOT NULL,"atPath" VARCHAR(255),"emailAddress" VARCHAR(50),"encryptedPassword" VARCHAR(255),"familyName" VARCHAR(50),"faxNumber" VARCHAR(25),"givenName" VARCHAR(50),"knownAs" VARCHAR(20),"phoneNumber" VARCHAR(25),"status" VARCHAR(255) NOT NULL,"username" VARCHAR(30) NOT NULL,"version" BIGINT NOT NULL,CONSTRAINT "ApplicationUser_PK" PRIMARY KEY("id"),CONSTRAINT "ApplicationUser_username_UNQ" UNIQUE("username"));
ALTER TABLE "isissecurity"."ApplicationUser" ALTER COLUMN "id" RESTART WITH 2;
CREATE TABLE "isissecurity"."ApplicationTenancy"("path" VARCHAR(255) NOT NULL,"name" VARCHAR(40) NOT NULL,"parentPath" VARCHAR(255),"version" BIGINT NOT NULL,CONSTRAINT "ApplicationTenancy_PK" PRIMARY KEY("path"),CONSTRAINT "ApplicationTenancy_name_UNQ" UNIQUE("name"),CONSTRAINT "ApplicationTenancy_FK1" FOREIGN KEY("parentPath") REFERENCES "isissecurity"."ApplicationTenancy"("path"));
CREATE INDEX "ApplicationTenancy_N49" ON "isissecurity"."ApplicationTenancy"("parentPath");
CREATE TABLE "isissecurity"."ApplicationRole"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"description" VARCHAR(254),"name" VARCHAR(50) NOT NULL,CONSTRAINT "ApplicationRole_PK" PRIMARY KEY("id"),CONSTRAINT "ApplicationRole_name_UNQ" UNIQUE("name"));
ALTER TABLE "isissecurity"."ApplicationRole" ALTER COLUMN "id" RESTART WITH 6;
CREATE TABLE "isissecurity"."ApplicationPermission"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"featureFqn" VARCHAR(255) NOT NULL,"featureType" VARCHAR(255) NOT NULL,"mode" VARCHAR(255) NOT NULL,"roleId" BIGINT NOT NULL,"rule" VARCHAR(255) NOT NULL,"version" BIGINT NOT NULL,CONSTRAINT "ApplicationPermission_PK" PRIMARY KEY("id"),CONSTRAINT "ApplicationPermission_role_feature_rule_UNQ" UNIQUE("roleId","featureType","featureFqn","rule"),CONSTRAINT "ApplicationPermission_FK1" FOREIGN KEY("roleId") REFERENCES "isissecurity"."ApplicationRole"("id"));
ALTER TABLE "isissecurity"."ApplicationPermission" ALTER COLUMN "id" RESTART WITH 23;
CREATE INDEX "ApplicationPermission_N49" ON "isissecurity"."ApplicationPermission"("roleId");
CREATE TABLE "isissecurity"."ApplicationUserRoles"("roleId" BIGINT NOT NULL,"userId" BIGINT NOT NULL,CONSTRAINT "ApplicationUserRoles_PK" PRIMARY KEY("roleId","userId"),CONSTRAINT "ApplicationUserRoles_FK1" FOREIGN KEY("roleId") REFERENCES "isissecurity"."ApplicationRole"("id"),CONSTRAINT "ApplicationUserRoles_FK2" FOREIGN KEY("userId") REFERENCES "isissecurity"."ApplicationUser"("id"));
CREATE INDEX "ApplicationUserRoles_N50" ON "isissecurity"."ApplicationUserRoles"("userId");
CREATE INDEX "ApplicationUserRoles_N49" ON "isissecurity"."ApplicationUserRoles"("roleId");

CREATE TABLE "isissessionlogger"."SessionLogEntry"("sessionId" VARCHAR(15) NOT NULL,"causedBy" VARCHAR(18),"loginTimestamp" TIMESTAMP NOT NULL,"logoutTimestamp" TIMESTAMP,"user" VARCHAR(50) NOT NULL,CONSTRAINT "SessionLogEntry_PK" PRIMARY KEY("sessionId"));

CREATE TABLE "statements"."Addon"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"active" BOOLEAN DEFAULT TRUE,"addonType_id_OID" BIGINT NOT NULL,"className" VARCHAR(256),"description" VARCHAR(4000),"embedded" BOOLEAN NOT NULL,"library" VARCHAR(255),"name" VARCHAR(40) NOT NULL,"version" TIMESTAMP NOT NULL,CONSTRAINT "Addon_PK" PRIMARY KEY("id"),CONSTRAINT "Addon_name_UNQ" UNIQUE("name"));
ALTER TABLE "statements"."Addon" ALTER COLUMN "id" RESTART WITH 0;
CREATE INDEX "Addon_N49" ON "statements"."Addon"("addonType_id_OID");
CREATE TABLE "statements"."Transaction"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"amount" DECIMAL(19) NOT NULL,"categoryId" BIGINT,"narration" VARCHAR(4000) NOT NULL,"rawdata" VARCHAR(4000),"reference" VARCHAR(255),"sourceId" BIGINT NOT NULL,"subCategoryId" BIGINT,"transactionDate" TIMESTAMP NOT NULL,"type" VARCHAR(255) NOT NULL,"version" TIMESTAMP NOT NULL,CONSTRAINT "Transaction_PK" PRIMARY KEY("id"),CONSTRAINT "Transaction_hash_UNQ" UNIQUE("sourceId","rawdata"));
ALTER TABLE "statements"."Transaction" ALTER COLUMN "id" RESTART WITH 0;
CREATE INDEX "Transaction_N51" ON "statements"."Transaction"("sourceId");
CREATE INDEX "Transaction_N50" ON "statements"."Transaction"("categoryId");
CREATE INDEX "Transaction_N49" ON "statements"."Transaction"("subCategoryId");
CREATE TABLE "statements"."AddonType"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"description" VARCHAR(4000),"name" VARCHAR(40) NOT NULL,"version" TIMESTAMP NOT NULL,CONSTRAINT "AddonType_PK" PRIMARY KEY("id"),CONSTRAINT "AddonType_name_UNQ" UNIQUE("name"));
ALTER TABLE "statements"."AddonType" ALTER COLUMN "id" RESTART WITH 0;
CREATE TABLE "statements"."Category"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"active" BOOLEAN DEFAULT TRUE,"description" VARCHAR(4000),"name" VARCHAR(40) NOT NULL,"version" TIMESTAMP NOT NULL,CONSTRAINT "Category_PK" PRIMARY KEY("id"),CONSTRAINT "Category_name_UNQ" UNIQUE("name"));
ALTER TABLE "statements"."Category" ALTER COLUMN "id" RESTART WITH 0;
CREATE TABLE "statements"."StatementReaderType"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"addon_id_OID" BIGINT NOT NULL,"description" VARCHAR(4000),"name" VARCHAR(40) NOT NULL,"version" TIMESTAMP NOT NULL,CONSTRAINT "StatementReaderType_PK" PRIMARY KEY("id"),CONSTRAINT "StatementReaderType_name_UNQ" UNIQUE("name"),CONSTRAINT "StatementReaderType_FK1" FOREIGN KEY("addon_id_OID") REFERENCES "statements"."Addon"("id"));
ALTER TABLE "statements"."StatementReaderType" ALTER COLUMN "id" RESTART WITH 0;
CREATE INDEX "StatementReaderType_N49" ON "statements"."StatementReaderType"("addon_id_OID");
CREATE TABLE "statements"."StatementSource"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"description" VARCHAR(4000),"name" VARCHAR(40) NOT NULL,"type" VARCHAR(255) NOT NULL,"version" TIMESTAMP NOT NULL,CONSTRAINT "StatementSource_PK" PRIMARY KEY("id"),CONSTRAINT "StatementSource_name_UNQ" UNIQUE("name"));
ALTER TABLE "statements"."StatementSource" ALTER COLUMN "id" RESTART WITH 0;
CREATE TABLE "statements"."StatementReader"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"description" VARCHAR(4000),"name" VARCHAR(40) NOT NULL,"properties" VARCHAR(2000),"readerType_id_OID" BIGINT NOT NULL,"version" TIMESTAMP NOT NULL,CONSTRAINT "StatementReader_PK" PRIMARY KEY("id"),CONSTRAINT "StatementReader_name_UNQ" UNIQUE("name"),CONSTRAINT "StatementReader_FK1" FOREIGN KEY("readerType_id_OID") REFERENCES "statements"."StatementReaderType"("id"));
ALTER TABLE "statements"."StatementReader" ALTER COLUMN "id" RESTART WITH 0;
CREATE INDEX "StatementReader_N49" ON "statements"."StatementReader"("readerType_id_OID");
CREATE TABLE "statements"."SubCategory"("id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL,"active" BOOLEAN DEFAULT TRUE,"description" VARCHAR(4000),"name" VARCHAR(40) NOT NULL,"version" TIMESTAMP NOT NULL,CONSTRAINT "SubCategory_PK" PRIMARY KEY("id"),CONSTRAINT "SubCategory_name_UNQ" UNIQUE("name"));
ALTER TABLE "statements"."SubCategory" ALTER COLUMN "id" RESTART WITH 0;

ALTER TABLE "statements"."Addon" ADD CONSTRAINT "Addon_FK1" FOREIGN KEY("addonType_id_OID") REFERENCES "statements"."AddonType"("id");
ALTER TABLE "statements"."Transaction" ADD CONSTRAINT "Transaction_FK3" FOREIGN KEY("subCategoryId") REFERENCES "statements"."SubCategory"("id");
ALTER TABLE "statements"."Transaction" ADD CONSTRAINT "Transaction_FK2" FOREIGN KEY("sourceId") REFERENCES "statements"."StatementSource"("id");
ALTER TABLE "statements"."Transaction" ADD CONSTRAINT "Transaction_FK1" FOREIGN KEY("categoryId") REFERENCES "statements"."Category"("id");

SET SCHEMA "isissecurity";
INSERT INTO "ApplicationUser" VALUES(0,'LOCAL','/',NULL,'$2a$10$ptgiTSyvLgOOQ6V7Je8Hb.T5MHjbEz6VI39181VE9XFgCtoDaK7v2',NULL,NULL,NULL,NULL,NULL,'ENABLED','isis-module-security-admin',1);
INSERT INTO "ApplicationUser" VALUES(1,'LOCAL','/',NULL,'$2a$10$ptgiTSyvLgOOQ6V7Je8Hb.ySj8/NzIq90TwKFbq71Fu8n2zDMhkru',NULL,NULL,NULL,NULL,NULL,'ENABLED','admin',3);
INSERT INTO "ApplicationTenancy" VALUES('/','Global',NULL,1);
INSERT INTO "ApplicationRole" VALUES(0,'Administer security','isis-module-security-admin');
INSERT INTO "ApplicationRole" VALUES(1,'Security module fixtures','isis-module-security-fixtures');
INSERT INTO "ApplicationRole" VALUES(2,'Regular user of the security module','isis-module-security-regular-user');
INSERT INTO "ApplicationRole" VALUES(3,'Access results of running Fixture Scripts','isis-applib-fixtureresults');
INSERT INTO "ApplicationRole" VALUES(4,'Super administrator','domainapp-super-admin');
INSERT INTO "ApplicationRole" VALUES(5,'Admin access to togglz module','isis-module-togglz-admin');
INSERT INTO "ApplicationPermission" VALUES(0,'org.isisaddons.module.security.app','PACKAGE','CHANGING',0,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(1,'org.isisaddons.module.security.dom','PACKAGE','CHANGING',0,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(2,'org.isisaddons.module.security.fixture','PACKAGE','CHANGING',1,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(3,'org.isisaddons.module.security.app.user.MeService#me','MEMBER','CHANGING',2,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(4,'org.isisaddons.module.security.dom.user.ApplicationUser','CLASS','VIEWING',2,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(5,'org.isisaddons.module.security.dom.user.ApplicationUser#updateName','MEMBER','CHANGING',2,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(6,'org.isisaddons.module.security.dom.user.ApplicationUser#updatePassword','MEMBER','CHANGING',2,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(7,'org.isisaddons.module.security.dom.user.ApplicationUser#updateEmailAddress','MEMBER','CHANGING',2,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(8,'org.isisaddons.module.security.dom.user.ApplicationUser#updatePhoneNumber','MEMBER','CHANGING',2,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(9,'org.isisaddons.module.security.dom.user.ApplicationUser#updateFaxNumber','MEMBER','CHANGING',2,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(10,'org.isisaddons.module.security.dom.user.ApplicationUser#filterPermissions','MEMBER','VIEWING',2,'VETO',1);
INSERT INTO "ApplicationPermission" VALUES(11,'org.isisaddons.module.security.dom.user.ApplicationUser#resetPassword','MEMBER','VIEWING',2,'VETO',1);
INSERT INTO "ApplicationPermission" VALUES(12,'org.isisaddons.module.security.dom.user.ApplicationUser#updateTenancy','MEMBER','VIEWING',2,'VETO',1);
INSERT INTO "ApplicationPermission" VALUES(13,'org.isisaddons.module.security.dom.user.ApplicationUser#lock','MEMBER','VIEWING',2,'VETO',1);
INSERT INTO "ApplicationPermission" VALUES(14,'org.isisaddons.module.security.dom.user.ApplicationUser#unlock','MEMBER','VIEWING',2,'VETO',1);
INSERT INTO "ApplicationPermission" VALUES(15,'org.isisaddons.module.security.dom.user.ApplicationUser#addRole','MEMBER','VIEWING',2,'VETO',1);
INSERT INTO "ApplicationPermission" VALUES(16,'org.isisaddons.module.security.dom.user.ApplicationUser#removeRole','MEMBER','VIEWING',2,'VETO',1);
INSERT INTO "ApplicationPermission" VALUES(17,'org.isisaddons.module.security.dom.role.ApplicationRole#name','MEMBER','VIEWING',2,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(18,'org.isisaddons.module.security.dom.role.ApplicationRole#description','MEMBER','VIEWING',2,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(19,'org.apache.isis.applib.fixturescripts.FixtureResult','CLASS','CHANGING',3,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(20,'domainapp','PACKAGE','CHANGING',4,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(21,'org','PACKAGE','CHANGING',4,'ALLOW',1);
INSERT INTO "ApplicationPermission" VALUES(22,'org.isisaddons.module.togglz.glue','PACKAGE','CHANGING',5,'ALLOW',1);
INSERT INTO "ApplicationUserRoles" VALUES(0,0);
INSERT INTO "ApplicationUserRoles" VALUES(4,1);
INSERT INTO "ApplicationUserRoles" VALUES(5,1);
