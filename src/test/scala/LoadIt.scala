import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.apache.kafka.clients.producer.ProducerConfig
import ru.tinkoff.gatling.kafka.Predef._

import scala.concurrent.duration.DurationDouble

class LoadIt extends Simulation {

  val json =
    """{
      |"product_type": "other",
      |"model_version": "1.0",
      |"event_type": "customer_preferences_specified",
      |"occurred_at": "2019-08-07",
      |"message_body": "{\"metadata\":{\"kafka.offset\":\"43046322\",\"kafka.partition\":\"0\",\"service\":\"customeraccountpreferences/1.0.0\",\"eventName\":\"CustomerPreferencesSpecified\",\"occurredAt\":\"2019-08-07T11:09:53.014Z\",\"eventId\":\"c6839a54-49a4-42b2-b6e0-458d705b6242\",\"sessionId\":\"303df063-022f-471d-8649-e34e382d7a60\",\"correlationId\":\"87f36643-9fc0-40bc-9752-460da985d87f\",\"updatedBy\":\"home\",\"submittedAt\":\"2019-08-07T11:09:53.016Z\",\"eventSink\":{\"receivedAt\":\"2019-08-07T11:09:53.090Z\",\"sourceIP\":\"52.208.96.190, 52.208.96.190\"}},\"payload\":{\"emailAddress\":\"christian.mendes@sa.com\",\"preferences\":[{\"context\":\"global\",\"namespace\":\"marketing\",\"key\":\"email\",\"value\":false,\"updatedAt\":\"2019-08-07T11:09:53.014Z\",\"updatedBy\":\"home\"},{\"context\":\"global\",\"namespace\":\"marketing\",\"key\":\"post\",\"value\":false,\"updatedAt\":\"2019-08-07T11:09:53.014Z\",\"updatedBy\":\"home\"},{\"context\":\"global\",\"namespace\":\"marketing\",\"key\":\"sms\",\"value\":false,\"updatedAt\":\"2019-08-07T11:09:53.014Z\",\"updatedBy\":\"home\"},{\"context\":\"global\",\"namespace\":\"marketing\",\"key\":\"telephone\",\"value\":false,\"updatedAt\":\"2019-08-07T11:09:53.014Z\",\"updatedBy\":\"home\"},{\"context\":\"home\",\"namespace\":\"marketing\",\"key\":\"indicative-quotes\",\"value\":true,\"updatedAt\":\"2019-08-07T11:09:53.014Z\",\"updatedBy\":\"home\"}]}}"
      |}""".stripMargin

  val kafkaConf = kafka
    .topic("raw")
    .properties(Map(
      ProducerConfig.ACKS_CONFIG -> "1",
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer",
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringSerializer"))

  val scn: ScenarioBuilder = scenario("Basic")
//  .feed(jsonFile("test.json"))
    .exec(
      kafka("KafkaRequest")
        .send[String](json))

  setUp(
    scn.inject(constantUsersPerSec(100) during (60 seconds))
      //          .throttle(jumpToRps(10), holdFor(1 minute))
      .protocols(kafkaConf)
  ).assertions(global.responseTime.percentile3.lt(1000), global.successfulRequests.percent.gt(99))
    .maxDuration(60 seconds)
}
