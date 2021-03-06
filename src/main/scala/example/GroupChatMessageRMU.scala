package example

import org.apache.flink.table.api.Expressions.$
import org.apache.flink.table.api.{EnvironmentSettings, Table, TableEnvironment}

object GroupChatMessageRMU extends App {

  override def main(args: Array[String]): Unit = {
    println("\n\n\nMAIN FUNCTION EXECUTED\n\n\n")
    val settings = EnvironmentSettings.newInstance().build()
    val tEnv = TableEnvironment.create(settings)
    execute(tEnv)
  }

  private def report(messagesTable: Table): Table = {
    println("\n\n\nREPORT FUNCTION EXECUTED\n\n\n")
    messagesTable.select(
      $("message_id"),
      $("group_chat_id"),
      $("account_id"),
      $("sequence_no"),
      $("body"),
      $("create_at"))
      .groupBy($("message_id"), $("group_chat_id"), $("account_id"), $("body"))
      .select($("message_id"),
        $("group_chat_id"),
        $("account_id"),
        $("sequence_no").max().as("sequence_no"),
        $("body"),
        $("create_at").max().as("created_at")) //TODO???
  }

  private def execute(tEnv: TableEnvironment): Unit = {
    println("\n\n\nEXECUTE FUNCTION EXECUTED\n\n\n")
    val tableResult =  tEnv.executeSql("CREATE TABLE group_chat_messages (\n" +
      "    message_id  BIGINT,\n" +
      "    group_chat_id BIGINT,\n" +
      "    account_id  BIGINT,\n" +
      "    sequence_no  BIGINT,\n" +
      "    body        VARCHAR,\n" +
      "    create_at   TIMESTAMP(3)\n" +
      //"    WATERMARK FOR transaction_time AS transaction_time - INTERVAL '5' SECOND\n" +
      ") WITH (\n" +
      "    'connector' = 'kafka',\n" +
      "    'topic'     = 'group_chat_messages',\n" +
      "    'properties.bootstrap.servers' = 'kafka:9092',\n" +
      "    'format'    = 'csv'\n" +
      ")"
    )
    tableResult.print()

    val batchTableResult = tEnv.executeSql("CREATE TABLE group_chat_messages_batch (\n" +
      "    message_id  BIGINT PRIMARY KEY,\n" +
      "    group_chat_id BIGINT,\n" +
      "    account_id  BIGINT,\n" +
      "    sequence_no  BIGINT,\n" +
      "    body        VARCHAR(255),\n" +
      "    create_at   TIMESTAMP(3)\n" +
      ") WITH (\n" +
      "  'connector'  = 'jdbc',\n" +
      "  'url'        = 'jdbc:mysql://mysql:3306/flinksql',\n" +
      "  'table-name' = 'group_chat_messages_batch',\n" +
      "  'driver'     = 'com.mysql.jdbc.Driver',\n" +
      "  'username'   = 'flink',\n" + "  'password'   = 'secret'\n" + ")"
    )
    batchTableResult.print()

    val transactions = tEnv.from("group_chat_messages")
    val executeInsertResult = report(transactions).executeInsert("group_chat_messages_batch")
    executeInsertResult.print()
  }
}
