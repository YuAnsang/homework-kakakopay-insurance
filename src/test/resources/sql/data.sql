INSERT INTO PRODUCT(id, name, min_duration_in_months, max_duration_in_months, created_at, updated_at)
VALUES (1, '여행자 보험', 1, 3, now(), now());

INSERT INTO COVERAGE(id, name, insured_amount, base_amount, product_id, created_at, updated_at)
VALUES (1, '상해치료비', 1000000, 100, 1, now(), now());

INSERT INTO COVERAGE(id, name, insured_amount, base_amount, product_id, created_at, updated_at)
VALUES (2, '항공기 지연도착 시 보상금', 500000, 100, 1, now(), now());

INSERT INTO PRODUCT(id, name, min_duration_in_months, max_duration_in_months, created_at, updated_at)
VALUES (2, '휴대폰 보험', 1, 12, now(), now());

INSERT INTO COVERAGE(id, name, insured_amount, base_amount, product_id, created_at, updated_at)
VALUES (3, '부분손실', 750000, 38, 2, now(), now());

INSERT INTO COVERAGE(id, name, insured_amount, base_amount, product_id, created_at, updated_at)
VALUES (4, '전체손실', 1570000, 40, 2, now(), now());


INSERT INTO CONTRACT (id, DURATION_IN_MONTHS, END_DATE, START_DATE, TOTAL_PREMIUM, PRODUCT_ID, STATUS)
VALUES (10000, 1, '2023-12-03', '2023-11-03', 15000.00, 1, 'NORMAL');
INSERT INTO CONTRACT_COVERAGE (CONTRACT_ID, COVERAGE_ID) VALUES (10000, 1);
INSERT INTO CONTRACT_COVERAGE (CONTRACT_ID, COVERAGE_ID) VALUES (10000, 2);

INSERT INTO CONTRACT (id, DURATION_IN_MONTHS, END_DATE, START_DATE, TOTAL_PREMIUM, PRODUCT_ID, STATUS)
VALUES (10001, 2, '2024-01-03', '2023-11-03', 30000.00, 1, 'EXPIRED');
INSERT INTO CONTRACT_COVERAGE (CONTRACT_ID, COVERAGE_ID) VALUES (10000, 1);
INSERT INTO CONTRACT_COVERAGE (CONTRACT_ID, COVERAGE_ID) VALUES (10000, 2);
