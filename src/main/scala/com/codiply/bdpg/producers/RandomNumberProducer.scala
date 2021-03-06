package com.codiply.bdpg.producers

import com.codiply.bdpg.constants.KafkaCluster.Topics
import com.codiply.bdpg.kafka.StringProducer
import com.codiply.bdpg.util.DelayedFuture

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Random

object RandomNumberProducer {
  import scala.concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]): Unit = {
    val topic = Topics.RandomNumbers

    val random = new Random
    val producer = new StringProducer(topic)

    Await.result(loop(random, producer), Duration.Inf)
  }

  private def produceNext(random: Random, producer: StringProducer): Future[Unit] = {
    val key = s"key-${random.nextInt(100)}"
    val value = random.nextInt(1000).toString
    println(key, value)
    Future.successful(())
    producer.send(key, value).map(_ => ())
  }

  private def loop(random: Random, producer: StringProducer): Future[Unit] = {
    produceNext(random, producer)
      .flatMap { _ => DelayedFuture(1.seconds)(()) }
      .flatMap(_ => loop(random, producer))
  }
}
