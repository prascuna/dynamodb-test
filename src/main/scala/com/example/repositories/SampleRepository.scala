package com.example.repositories

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.example.models.Sample

import scala.concurrent.ExecutionContext

trait SampleRepository extends DynamoDBRepository[Sample] {


}

object SampleRepository {
  def apply(endpoint: String)(implicit ec: ExecutionContext) = {
    val client = new AmazonDynamoDBAsyncClient().withEndpoint[AmazonDynamoDBAsyncClient](endpoint)
    new SampleRepositoryImpl(client)
  }
}

class SampleRepositoryImpl(client: AmazonDynamoDBAsyncClient)(implicit ec: ExecutionContext)
  extends DynamoDBRepositoryImpl[Sample](Table("sample", 'key), client)
    with SampleRepository {

}