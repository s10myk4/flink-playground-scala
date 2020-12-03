package example

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util
import java.util.Properties

import example.TransactionTopicProducer._
import example.models.Transaction
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{LongSerializer, StringSerializer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TransactionTopicProducer(broker: String) {
  private val kafkaProducer: KafkaProducer[Long, Transaction] = new KafkaProducer(getProperties(broker))

  def execute(tx: Transaction): Future[Unit] = Future {
    val millis = tx.timestamp.atZone(ZoneId.of("Asia/Tokyo")).toInstant.toEpochMilli
    val key = tx.accountId
    val record = new ProducerRecord(topic, null, millis, key, tx)
    kafkaProducer.send(record).get()
  }

}

object TransactionTopicProducer {

  private val topic = "transactions"

  import org.apache.kafka.common.serialization.Serializer

  class TransactionSerializer extends Serializer[Transaction] {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

    override def serialize(s: String, transaction: Transaction): Array[Byte] = {
      val csv = "%s, %s, %s".format(
        transaction.accountId, transaction.amount, transaction.timestamp.format(formatter)
      )
      csv.getBytes
    }

    override def close(): Unit = {}
  }

  private def getProperties(broker: String): Properties = {
    val props = new Properties
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.RETRIES_CONFIG, "0")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[LongSerializer])
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[TransactionSerializer])
    props
  }
}
