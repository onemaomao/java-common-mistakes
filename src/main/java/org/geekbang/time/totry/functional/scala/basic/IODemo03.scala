package org.geekbang.time.totry.functional.scala.basic

import scala.io.Source

object IODemo03 {
  def main(args: Array[String]): Unit = {
    println("文件内容为:" )

    Source.fromFile("pom.xml" ).foreach{
      print
    }
  }
}
