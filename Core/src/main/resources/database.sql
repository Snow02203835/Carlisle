CREATE DATABASE IF NOT EXISTS carlisle DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

CREATE USER 'carlisleAdmin'@'localhost' IDENTIFIED BY '123456';
CREATE USER 'carlisleAdmin'@'%' IDENTIFIED BY '123456';

GRANT ALL ON carlisle.* TO 'carlisleAdmin'@'localhost';
GRANT ALL ON carlisle.* TO 'carlisleAdmin'@'%';

FLUSH PRIVILEGES;