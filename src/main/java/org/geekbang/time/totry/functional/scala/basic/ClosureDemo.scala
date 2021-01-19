package org.geekbang.time.totry.functional.scala.basic

/**
 * 闭包
 */
object ClosureDemo {
//  闭包是一个函数，返回值依赖于声明在函数外部的一个或多个变量。
//  闭包通常来讲可以简单的认为是可以访问一个函数里面局部变量的另外一个函数。

  def main(args: Array[String]) {
    println( "muliplier(1) value = " +  multiplier(1) )
    println( "muliplier(2) value = " +  multiplier(2) )
  }
  var factor = 3
  val multiplier = (i:Int) => i * factor
}
