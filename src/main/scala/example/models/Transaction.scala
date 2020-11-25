package example.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util

case class Transaction(
                        accountId: Long,
                        amount: Int,
                        timestamp: LocalDateTime
                      )

object Transaction {


}