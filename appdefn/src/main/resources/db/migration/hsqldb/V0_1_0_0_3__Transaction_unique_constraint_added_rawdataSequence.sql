-- DROP OLD UNIQUE CONSTRAINT ON TRANSACTION TABLE
ALTER TABLE "statements"."Transaction" DROP CONSTRAINT "Transaction_hash_UNQ";
-- ADD UNIQUE CONSTRAINT ON TRANSACTION TABLE TO INCLUDE rawdataSequence 
ALTER TABLE "statements"."Transaction" ADD CONSTRAINT "Transaction_hash_UNQ" UNIQUE("sourceId","rawdata", "rawdataSequence");
