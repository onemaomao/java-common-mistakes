package org.geekbang.time.totry.functional.scala.basic

import scala.util.matching.Regex

object PatternDemo {
  def main(args: Array[String]): Unit = {
//    val pattern = "Scala".r
//    val str = "Scala is Scalable and cool"
//    println(pattern findFirstIn str)


//    val pattern = new Regex("(S|s)cala")  // 首字母可以是大写 S 或小写 s
//    val str = "Scala is scalable and cool"
//    println((pattern findAllIn str).mkString(","))   // 使用逗号 , 连接返回结果

    val pattern = "(S|s)cala".r
    val str = "Scala is scalable and cool"
    println(pattern replaceFirstIn(str, "Java"))
  }
}

//Scala 的正则表达式继承了 Java 的语法规则，Java 则大部分使用了 Perl 语言的规则。
