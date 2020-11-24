package example

import java.time.LocalDateTime

import example.models.Message

import scala.annotation.tailrec
import scala.io.StdIn

object TablePlayground extends App with Terminal {

  private val broker = "localhost:9092"
  private val topicName = "group_chat_messages"
  val kafkaProducer = new Producer(broker, topicName)

  override def main(args: Array[String]): Unit = {
    //Runtime.getRuntime.addShutdownHook(new Thread(() => kafkaProducer.close()))
    commandLoop()
  }

  @tailrec
  private def commandLoop(): Unit = {
    Command(StdIn.readLine()) match {
      case Command.Message(groupChatId, accountId, messageId, sequenceNo, body) =>
        val m = Message(messageId = messageId, groupChatId = groupChatId, accountId = accountId,
          sequenceNo = sequenceNo, body = body, LocalDateTime.now())
        kafkaProducer.execute(m)
        println(s"created message $m")
        commandLoop()
      case Command.Quit =>
        println("Quit terminal")
        ()
      case Command.Unknown(command) =>
        println(s"$command is unknown command")
        commandLoop()
    }
  }

}
