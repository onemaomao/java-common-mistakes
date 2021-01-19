package org.geekbang.time.totry.functional.scala.basic

import scala.io.StdIn

/**
 * IO
 */
object IODemo02 {
  def main(args: Array[String]): Unit = {
    print("google : " )
    val line = StdIn.readLine()

    println("tks: " + line)
  }
}
