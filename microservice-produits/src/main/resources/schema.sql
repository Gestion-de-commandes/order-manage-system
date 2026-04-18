DROP TABLE IF EXISTS product;
CREATE TABLE product
(
    id          INT PRIMARY KEY,
    titre       VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    image       VARCHAR(1000) NOT NULL,
    prix        DOUBLE PRECISION NOT NULL
);
