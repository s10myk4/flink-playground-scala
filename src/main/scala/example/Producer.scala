package example

import java.time.ZoneId
import java.util.Properties

import example.Producer.getProperties
import example.models.Message
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.LongSerializer

class Producer(broker: String, topic: String){

  private val kafkaProducer: KafkaProducer[String, Message] = new KafkaProducer(getProperties(broker))

  def execute(msg: Message): Unit = {
    val millis = msg.createAt.atZone(ZoneId.of("Asia/Tokyo")).toInstant.toEpochMilli
    val key = s"${msg.messageId}-${msg.accountId}"
    val record = new ProducerRecord(topic, null, millis, key, msg)
    kafkaProducer.send(record)
  }

  def close():Unit = kafkaProducer.close()
}

object Producer {

  import org.apache.kafka.common.serialization.Serializer

  class MessageSerializer extends Serializer[Message] {
    override def serialize(topic: String, data: Message): Array[Byte] = {
      val csv = "%s, %s, %s, %s, %s, %s".format(
        data.messageId, data.groupChatId, data.accountId, data.sequenceNo, data.body, data.createAt
      )
      csv.getBytes
    }
  }

  private def getProperties(broker: String): Properties = {
    val props = new Properties
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker)
    props.put(ProducerConfig.ACKS_CONFIG, "all")
    props.put(ProducerConfig.RETRIES_CONFIG, "0")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[LongSerializer])
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[MessageSerializer])
    props
  }

}
