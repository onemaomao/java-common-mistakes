package org.geekbang.time.totry.functional.scala.basic

/**
 * 方法与函数2
 */
object MethodAndFunctionDemo02 {
  def ttt(f:Int => Int):Unit = {
    val r = f(10)
    println(r)
  }

  val f0 = (x : Int) => x * x

  //定义了一个方法
  def m0(x:Int) : Int = {
    //传递进来的参数乘以10
    x * 10
  }

  //将方法转换成函数，利用了神奇的下滑线
  val f1 = m0 _

  def main(args: Array[String]): Unit = {
    ttt(f0)

    //通过m0 _将方法转化成函数
    ttt(m0 _);

    //如果直接传递的是方法名称，scala相当于是把方法转成了函数
    ttt(m0)

    //通过x => m0(x)的方式将方法转化成函数,这个函数是一个匿名函数，等价：(x:Int) => m0(x)
    ttt(x => m0(x))
  }

//  方法和函数的区别
//  1、函数可作为一个参数传入到方法中，而方法不行。
//
//  2、在Scala中无法直接操作方法，如果要操作方法，必须先将其转换成函数。有两种方法可以将方法转换成函数：
//  val f1 = m _
//  val f1: (Int) => Int = m
//
//  3、函数必须要有参数列表，而方法可以没有参数列表
//
//  4、在函数出现的地方我们可以提供一个方法

  def m1(x:Int) = x+3

}
