package example

import java.time.LocalDateTime

import example.models.Message

import scala.annotation.tailrec
import scala.io.StdIn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object TablePlayground extends App with Terminal {

  override def main(args: Array[String]): Unit = {
    val broker = "localhost:9092"
    val topicName = "group_chat_messages"
    val kafkaProducer = new Producer(broker, topicName)
    //Runtime.getRuntime.addShutdownHook(new Thread(() => kafkaProducer.close()))

    @tailrec
    def commandLoop(): Unit = {
      Command(StdIn.readLine()) match {
        case Command.Message(groupChatId, accountId, messageId, sequenceNo, body) =>
          val m = Message(messageId = messageId, groupChatId = groupChatId, accountId = accountId,
            sequenceNo = sequenceNo, body = body, LocalDateTime.now())
          kafkaProducer.execute(m).onComplete {
            case Success(v) => println(s"created message $m")
            case Failure(e) => throw e
          }
          commandLoop()
        case Command.Quit =>
          println("Quit terminal")
          ()
        case Command.Unknown(command) =>
          println(s"$command is unknown command")
          commandLoop()
      }
    }

    commandLoop()
  }



}
