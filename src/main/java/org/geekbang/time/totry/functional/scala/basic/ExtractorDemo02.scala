package org.geekbang.time.totry.functional.scala.basic

/**
 * 提取器
 */
object ExtractorDemo02 {

  def main(args: Array[String]): Unit = {
    val x = ExtractorDemo02(5)
    println(x)

    x match
    {
      case ExtractorDemo02(num) => println(x + " 是 " + num + " 的两倍！")
      //unapply 被调用
      case _ => println("无法计算")
    }
  }
  def apply(x: Int) = x*2
  def unapply(z: Int): Option[Int] = if (z%2==0) Some(z/2) else None
}
