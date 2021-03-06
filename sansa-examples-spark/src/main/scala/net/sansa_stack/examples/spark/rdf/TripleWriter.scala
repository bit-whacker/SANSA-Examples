package net.sansa_stack.examples.spark.rdf

import java.io.File
import java.net.URI

import scala.collection.mutable
import org.apache.spark.sql.SparkSession
import org.apache.jena.riot.Lang
import net.sansa_stack.rdf.spark.io.rdf._

object TripleWriter {

  def main(args: Array[String]) {
    parser.parse(args, Config()) match {
      case Some(config) =>
        run(config.in, config.out)
      case None =>
        println(parser.usage)
    }
  }

  def run(input: String, output: String): Unit = {

    val spark = SparkSession.builder
      .appName(s"Triple writer example ( $input )")
      .master("local[*]")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .getOrCreate()

    println("======================================")
    println("|        Triple writer example       |")
    println("======================================")

    val lang = Lang.NTRIPLES
    val triples = spark.rdf(lang)(input)

    triples.saveAsNTriplesFile(output)

    spark.stop

  }

  case class Config(
    in:  String = "",
    out: String = "")

  // the CLI parser
  val parser = new scopt.OptionParser[Config]("Triple writer example ") {

    head("Triple writer example ")

    opt[String]('i', "input").required().valueName("<path>").
      action((x, c) => c.copy(in = x)).
      text("path to file that contains the data (in N-Triples format)")

    opt[String]('o', "out").required().valueName("<directory>").
      action((x, c) => c.copy(out = x)).
      text("the output directory")

    help("help").text("prints this usage text")
  }

}