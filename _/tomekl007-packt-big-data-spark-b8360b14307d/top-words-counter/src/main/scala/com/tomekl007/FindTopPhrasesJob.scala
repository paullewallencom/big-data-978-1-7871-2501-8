package com.tomekl007

import com.tomekl007.spark.SparkContextInitializer
import org.apache.spark.rdd.RDD


/**
  * job that finds top occurrences of words across given text files (paths to text files are given as argument), result is printed to standard output
  */
object FindTopPhrasesJob {
  private val NumberOfTopPhrases = 10
  private val SplitRegexp = "[^a-zA-Z0-9']"

  def main(args: Array[String]) {
    val paths = args.slice(1, args.length)
    println(paths)

    val sparkContext = SparkContextInitializer.createSparkContext("find-top-phrases")
    val contentOfTextFiles = sparkContext.textFile(paths.mkString(","))

    val result = findTopPhrases(contentOfTextFiles, NumberOfTopPhrases)
    println(s"result: $result")
  }

  /**
    *
    * @param contentOfTextFiles RDD of string, that will be tokenize and top phrase occurrences will be found for that input RDD
    * @param numberOfTopPhrases - number of top phrases that will be found
    * @return List of pairs (String,Int) - String is a word that is in top occurrences, Int is number of those occurrences
    */
  def findTopPhrases(contentOfTextFiles: RDD[String], numberOfTopPhrases: Int): List[(String, Int)] = {
    contentOfTextFiles
      .flatMap(_.split(SplitRegexp))
      .map(_.replaceAll("\\s", ""))
      .filter(!_.isEmpty)
      .map(_.toLowerCase)
      .map(phrase => (phrase, 1))
      .reduceByKey(_ + _)
      .takeOrdered(numberOfTopPhrases)(Ordering[Int].reverse.on(x => x._2))
      .toList
  }
}
