package org.geekbang.time.read.column.others.classes.annotation;

import java.lang.annotation.*;

/**
 * 新增的两种ElementType
 */
public class NewTypeDemo {
}

//------------------------------------------------
@Target(ElementType.TYPE_PARAMETER)
@interface Parameter{}

//TYPE_PARAMETER 标注在类型参数上
class D<@Parameter T> { }
//------------------------------------------------

@Target(ElementType.TYPE_USE)
@interface Rectangular{}

interface Shape{}
//TYPE_USE则可以用于标注任意类型(不包括class)
//用于父类或者接口
class Image implements @Rectangular Shape { }
//------------------------------------------------

//用于构造函数
//new @Path String("/usr/bin")

//用于强制转换和instanceof检查,注意这些注解中用于外部工具，它们不会对类型转换或者instanceof的检查行为带来任何影响。
//String path=(@Path String)input;
//if(input instanceof @Path String)

//用于指定异常
//public Person read() throws @Localized IOException

//用于通配符绑定
//List<@ReadOnly ? extends Person>
//List<? extends @ReadOnly Person>

//@NotNull String.class //非法，不能标注class
//import java.lang.@NotNull String //非法，不能标注import

