SHELL:=/bin/bash
DC_CMD := docker-compose exec
ZOOKEEPER_PORT := 2181
KAFKA_CONTAINER_NAME := kafka
KAFKA_BIN_DIR := /opt/kafka/bin
MYSQL_CMD := /usr/bin/mysql -usql-demo -pdemo-sql -Dsql-demo
TOPIC = group_chat_messages

args = `arg="$(filter-out $@,$(MAKECMDGOALS))" && echo $${arg:-${1}}`

.DEFAULT_GOAL := help
help:
	@grep -E '^[a-zA-Z/_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?##"}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

up: ## docker-compose up [TARGET_NAME]
	@docker-compose up -d $(call args, )

ps: ## docker-compose ps
	@docker-compose ps

build: ## docker-compose build
	docker-compose build

kafka/topic-list: ## list kafka topics
	$(DC_CMD) $(KAFKA_CONTAINER_NAME) $(KAFKA_BIN_DIR)/kafka-topics.sh --list --zookeeper zookeeper:$(ZOOKEEPER_PORT)

kafka/topic-describe: ## kafka/topic-describe TOPIC=[TARGET_NAME]
	$(DC_CMD) $(KAFKA_CONTAINER_NAME) $(KAFKA_BIN_DIR)/kafka-topics.sh --describe --zookeeper zookeeper:$(ZOOKEEPER_PORT) --topic $(TOPIC)

kafka/topic-create: ## kafka/topic-create [NAME]
	$(DC_CMD) $(KAFKA_CONTAINER_NAME) $(KAFKA_BIN_DIR)/kafka-topics.sh --create --zookeeper zookeeper:$(ZOOKEEPER_PORT) --topic $(call args, ) --partitions 1 --replication-factor 1

kafka/topic-delete: ## kafka/topic-delete [TARGET_NAME]
	$(DC_CMD) $(KAFKA_CONTAINER_NAME) $(KAFKA_BIN_DIR)/kafka-topics.sh --delete --zookeeper zookeeper:$(ZOOKEEPER_PORT) --topic $(call args, )

mysql/show-tables: ## show db tables
	$(DC_CMD) mysql $(MYSQL_CMD) -e 'show tables'

mysql/prompt: ## start mysql prompt
	$(DC_CMD) mysql $(MYSQL_CMD)

flink/job-list: ## job list
	$(DC_CMD) jobmanager flink list

.PHONY: help up ps build kafka/topic-list kafka/topic-describe kafka/topic-create kafka/topic-delete mysql/prompt mysql/show-tables flink/job-list

