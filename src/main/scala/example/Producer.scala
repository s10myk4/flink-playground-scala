package example

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Properties

import example.Producer.getProperties
import example.models.Message
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Producer(broker: String, topic: String){

  private val kafkaProducer: KafkaProducer[String, Message] = new KafkaProducer(getProperties(broker))

  def execute(msg: Message): Future[Unit] = Future {
    val millis = msg.createAt.atZone(ZoneId.of("Asia/Tokyo")).toInstant.getEpochSecond
    val key = s"${msg.messageId}-${msg.accountId}"
    val record = new ProducerRecord(topic, null, millis, key, msg)
    kafkaProducer.send(record).get()
  }

  def close():Unit = kafkaProducer.close()
}

object Producer {

  import org.apache.kafka.common.serialization.Serializer


  class MessageSerializer extends Serializer[Message] {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override def serialize(topic: String, data: Message): Array[Byte] = {
      val csv = "%s, %s, %s, %s, %s, %s".format(
        data.messageId, data.groupChatId, data.accountId, data.sequenceNo, data.body, data.createAt.format(formatter)
      )
      csv.getBytes
    }

    override def close(): Unit = {}

    override def configure(x$1: java.util.Map[String, _],x$2: Boolean): Unit = {}
  }

  private def getProperties(broker: String): Properties = {
    val props = new Properties
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.RETRIES_CONFIG, "0")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[MessageSerializer])
    props
  }

}
