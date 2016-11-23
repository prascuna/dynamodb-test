package com.example

import com.example.models.Sample
import com.example.repositories.SampleRepository

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object DynamoTest {
  def main(args: Array[String]): Unit = {
    val repo = SampleRepository("http://127.0.0.1:8000")
    (for {
//          _ <- repo.createTable
//          tables <- repo.listTables
      result <- repo.put(Sample("bleah", "popo"))
    } yield result)
      .onComplete {
        case Success(result) =>
          println("Created")
          println(result)
        case Failure(e) =>
          println("failure")
          println(e)
      }
  }
}