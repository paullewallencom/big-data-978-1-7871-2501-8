package com.tomekl007.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSuite, Suite}

import scala.util.Random

trait SparkSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfterEach {
  this: Suite =>
  implicit var spark: SparkContext = _

  def appName: String

  override def beforeAll() {
    spark = SparkSuite.createTestSparkContext(SparkSuite.createTestSparkConfig(appName))
    spark.setLogLevel("ERROR")
  }
}

object SparkSuite {
  def createTestSparkContext(sparkConf: SparkConf): SparkContext = {
    SparkContext.getOrCreate(sparkConf)
  }

  def createTestSparkConfig(appName: String): SparkConf = {
    SparkContextInitializer.basicSparkConf(appName)
      .setMaster("local[*]")
      .set("spark.driver.allowMultipleContexts", "true")
      .set("spark.app.id", Random.nextInt(1000).toString)
  }
}
