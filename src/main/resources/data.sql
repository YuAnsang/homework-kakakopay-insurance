INSERT INTO PRODUCT(name, min_duration_in_months, max_duration_in_months, created_at, updated_at)
VALUES ('여행자 보험', 1, 3, now(), now());

INSERT INTO COVERAGE(name, insured_amount, base_amount, product_id, created_at, updated_at)
VALUES ('상해치료비', 1000000, 100, 1, now(), now());

INSERT INTO COVERAGE(name, insured_amount, base_amount, product_id, created_at, updated_at)
VALUES ('항공기 지연도착 시 보상금', 500000, 100, 1, now(), now());

INSERT INTO PRODUCT(name, min_duration_in_months, max_duration_in_months, created_at, updated_at)
VALUES ('휴대폰 보험', 1, 12, now(), now());

INSERT INTO COVERAGE(name, insured_amount, base_amount, product_id, created_at, updated_at)
VALUES ('부분손실', 750000, 38, 2, now(), now());

INSERT INTO COVERAGE(name, insured_amount, base_amount, product_id, created_at, updated_at)
VALUES ('전체손실', 1570000, 40, 2, now(), now());
