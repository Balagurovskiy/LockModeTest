CREATE TABLE `library`.`authors` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `library`.`books` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `library`.`tags` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));

ALTER TABLE `library`.`books` 
ADD COLUMN `release_date` DATE NOT NULL AFTER `name`,
ADD COLUMN `version` INT NOT NULL AFTER `release_date`,
ADD UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE;
;

ALTER TABLE `library`.`authors` 
ADD COLUMN `version` INT NOT NULL AFTER `name`;

ALTER TABLE `library`.`tags` 
ADD COLUMN `version` INT NOT NULL AFTER `name`;

ALTER TABLE `library`.`tags` 
CHANGE COLUMN `id` `id` INT NOT NULL AUTO_INCREMENT ,
ADD UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE;
;


CREATE TABLE `library`.`author_book` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `author_id` INT NOT NULL,
  `book_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);
  
CREATE TABLE `library`.`book_tag` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `book_id` INT NOT NULL,
  `tag_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE);
  
INSERT INTO `library`.`books` (`name`, `release_date`, `version`) VALUES ('Head First Java', CURDATE(), '0');

INSERT INTO `library`.`authors` (`name`, `version`) VALUES ('Kathy Sierra', '0');
INSERT INTO `library`.`authors` (`name`, `version`) VALUES ('Bert Bates', '0');

INSERT INTO `library`.`tags` (`name`, `version`) VALUES ('programming', '0');
INSERT INTO `library`.`tags` (`name`, `version`) VALUES ('java', '0');

INSERT INTO `library`.`author_book` (`author_id`, `book_id`) VALUES ('1', '1');
INSERT INTO `library`.`author_book` (`author_id`, `book_id`) VALUES ('2', '1');

INSERT INTO `library`.`book_tag` (`book_id`, `tag_id`) VALUES ('1', '1');
INSERT INTO `library`.`book_tag` (`book_id`, `tag_id`) VALUES ('1', '2');
