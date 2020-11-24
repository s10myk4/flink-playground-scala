package example

import scala.util.parsing.combinator.RegexParsers

trait Terminal {

  protected sealed trait Command

  protected object Command {

    case object Quit extends Command

    case class Message(
                        groupChatId: Long,
                        accountId: Long,
                        messageId: Long,
                        sequenceNo: Long,
                        body: String
                      ) extends Command

    case class Unknown(command: String) extends Command

    def apply(command: String): Command = CommandParser.parseAsCommand(command)

  }

  private val parser: CommandParser.Parser[Command] = CommandParser.createMessage | CommandParser.quit

  private object CommandParser extends RegexParsers {

    def parseAsCommand(arg: String): Command = {
      parseAll(parser, arg) match {
        case Success(command, _) => command
        case _ => Command.Unknown(arg)
      }
    }

    def createMessage: Parser[Command.Message] = {
      ("message|m".r ~> long ~ long ~ long ~ long ~ "[0-9a-zA-Z]+".r) ^^ {
        case groupChatId ~ accountId ~ messageId ~ sequenceNo ~ body=>
          Command.Message(groupChatId, accountId, messageId, sequenceNo, body)
      }
    }

    def quit: Parser[Command.Quit.type] = "quit|q".r ^^ (_ => Command.Quit)

    def long: Parser[Long] = """\d+""".r ^^ (_.toLong)
  }


}
