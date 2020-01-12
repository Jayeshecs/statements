-- increase length of password column from 40 to 1024
ALTER TABLE "statements"."MailConnectionProfile" ALTER COLUMN "password" VARCHAR(1024);
