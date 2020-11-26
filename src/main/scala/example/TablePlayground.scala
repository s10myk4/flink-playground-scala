package example

import java.time.LocalDateTime

import example.models.{Message, Transaction}

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn
import scala.util.{Failure, Success}

object TablePlayground extends App with Terminal {

  override def main(args: Array[String]): Unit = {
    val broker = "localhost:9092"
    val messageProducer = new Producer(broker, "group_chat_messages")
    val transactionProducer = new TransactionTopicProducer(broker)
    //Runtime.getRuntime.addShutdownHook(new Thread(() => kafkaProducer.close()))

    @tailrec
    def commandLoop(): Unit = {
      Command(StdIn.readLine()) match {
        case Command.Message(groupChatId, accountId, messageId, sequenceNo, body) =>
          val m = Message(messageId = messageId, groupChatId = groupChatId, accountId = accountId,
            sequenceNo = sequenceNo, body = body, LocalDateTime.now())
          messageProducer.execute(m).onComplete {
            case Success(v) => println(s"created message $v")
            case Failure(e) => throw e
          }
          commandLoop()
        case Command.Transaction(accountId, amount) =>
          val tx = Transaction(accountId = accountId, amount = amount, LocalDateTime.now())
          transactionProducer.execute(tx).onComplete {
            case Success(v) => println(s"created transaction $v")
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
