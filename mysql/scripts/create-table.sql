CREATE TABLE group_chat_messages_batch (
	message_id BIGINT NOT NULL PRIMARY KEY,
	group_chat_id BIGINT NOT NULL,
	account_id BIGINT NOT NULL,
	sequence_no BIGINT NOT NULL,
	body       VARCHAR(255) NOT NULL,
	amount	   BIGINT NOT NULL,
	create_at  TIMESTAMP(3) NOT NULL
);

CREATE TABLE spend_report (
	account_id BIGINT NOT NULL,
	log_ts     TIMESTAMP(3) NOT NULL,
	amount	   BIGINT NOT NULL,
	PRIMARY KEY (account_id, log_ts)
);