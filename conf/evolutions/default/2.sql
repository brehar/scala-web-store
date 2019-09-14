# --- !Ups
CREATE TABLE Image
(
    ID         INT NOT NULL AUTO_INCREMENT,
    PRODUCT_ID INT NOT NULL,
    URL        VARCHAR(250),
    PRIMARY KEY (ID)
);

# --- !Downs
DROP TABLE "Image";
