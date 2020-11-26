package example.models

import java.time.LocalDateTime

case class Transaction(
                        accountId: Long,
                        amount: Int,
                        timestamp: LocalDateTime
                      )

