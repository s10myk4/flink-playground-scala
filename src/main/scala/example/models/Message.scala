package example.models

import java.time.LocalDateTime

final case class Message(
                          messageId: Long,
                          groupChatId: Long,
                          accountId: Long,
                          sequenceNo: Long,
                          body: String,
                          createAt: LocalDateTime
                        )

