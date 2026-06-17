-- 팜터레스트 DB/계정 생성 (root 권한으로 실행)
CREATE DATABASE IF NOT EXISTS farmterest
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'farmterest'@'localhost'
  IDENTIFIED WITH caching_sha2_password BY 'farmterest123!';

GRANT ALL PRIVILEGES ON farmterest.* TO 'farmterest'@'localhost';
FLUSH PRIVILEGES;
