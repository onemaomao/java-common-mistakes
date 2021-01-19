package org.geekbang.time.totry.functional.scala.basic

import java.io.{File, PrintWriter}

/**
 * IO
 */
object IODemo01 {
  def main(args: Array[String]): Unit = {
    val writer = new PrintWriter(new File("xyz.txt" ))
    writer.write("google")
    writer.close()
  }
}
