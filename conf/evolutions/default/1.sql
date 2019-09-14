# --- !Ups
CREATE TABLE Product
(
    ID      INT          NOT NULL AUTO_INCREMENT,
    NAME    VARCHAR(100) NOT NULL,
    DETAILS VARCHAR(250),
    PRICE   DOUBLE       NOT NULL,
    PRIMARY KEY (ID)
);

# --- !Downs
DROP TABLE "Product";
