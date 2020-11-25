DC_CMD := docker-compose exec
KAFKA_TOPIC := group_chat_messages
ZOOKEEPER_PORT := 2181
KAFKA_CONTAINER_NAME := kafka
KAFKA_BIN_DIR := /opt/kafka/bin

.DEFAULT_GOAL := help

help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

topic-list: ## list kafka topic
	$(DC_CMD) $(KAFKA_CONTAINER_NAME) $(KAFKA_BIN_DIR)/kafka-topics.sh --list --zookeeper zookeeper:$(ZOOKEEPER_PORT)

topic-describe: ## describe $(KAFKA_TOPIC) kafka topic
	$(DC_CMD) $(KAFKA_CONTAINER_NAME) $(KAFKA_BIN_DIR)/kafka-topics.sh --describe --zookeeper zookeeper:$(ZOOKEEPER_PORT) --topic $(KAFKA_TOPIC)

.PHONY: topic-list topic-describe
