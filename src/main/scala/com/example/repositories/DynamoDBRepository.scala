package com.example.repositories


import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model._
import com.gu.scanamo.{DynamoFormat, ScanamoAsync, Table}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future, Promise}


trait DynamoDBRepository[T] {

  def createTable: Future[CreateTableResult]

  def listTables: Future[ListTablesResult]

  def put(item: T): Future[PutItemResult]

}

case class Table(tableName: String, partitionKey: Symbol, sortKey: Option[Symbol] = None, attributes: List[(Symbol, ScalarAttributeType)] = List.empty)


class DynamoDBRepositoryImpl[T: DynamoFormat](table: Table, client: AmazonDynamoDBAsyncClient)
                                             (implicit ec: ExecutionContext)
  extends DynamoDBRepository[T] {

  override def createTable: Future[CreateTableResult] = {

    def attributeDef(a: (Symbol, ScalarAttributeType)): AttributeDefinition =
      new AttributeDefinition(a._1.name, a._2)

    val keySchemaElement = (new KeySchemaElement(table.partitionKey.name, KeyType.HASH) ::
      table.sortKey.fold {
        List.empty[KeySchemaElement]
      } { sortKey =>
        List(new KeySchemaElement(sortKey.name, KeyType.RANGE))
      }).asJava

    val attributeDefinitions = table.sortKey.fold {
      attributeDef(table.partitionKey, ScalarAttributeType.S) :: table.attributes.map(attributeDef)
    } { sortKey =>
      attributeDef(table.partitionKey, ScalarAttributeType.S) :: attributeDef(sortKey, ScalarAttributeType.S) :: table.attributes.map(attributeDef)
    }.asJava

    val createTableRequest = new CreateTableRequest()
      .withTableName(table.tableName)
      .withKeySchema(keySchemaElement)
      .withAttributeDefinitions(attributeDefinitions)
      .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L))

    wrapAsyncMethod[CreateTableRequest, CreateTableResult](client.createTableAsync, createTableRequest)
  }

  override def put(item: T): Future[PutItemResult] =
    ScanamoAsync.put(client)(table.tableName)(item)

  override def listTables: Future[ListTablesResult] =
    wrapAsyncMethod[ListTablesRequest, ListTablesResult](client.listTablesAsync, new ListTablesRequest())



}
