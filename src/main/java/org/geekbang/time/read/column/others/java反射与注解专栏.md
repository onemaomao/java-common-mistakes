100例
18 | 当反射、注解和泛型遇到OOP时，会有哪些坑？
反射调用方法不是以传参决定重载
    使用反射的误区是，认为反射调用方法还是根据入参确定方法重载，其实要通过反射方法进行调用第一步就是通过方法签名来确定方法
泛型经过类型擦除多出桥接方法的坑
    使用javap -c XX.class观察
    使用jclasslib工具打开class
小结:
    getMethods和getDeclaredMethods有区别,前者可以查询到父类方法,后者只能查询到当前类
    反射进行方法调用时要注意过滤桥接方法
注解可以继承吗?
    子类以及子类的方法,无法自动继承父类和父类方法上的注解
    @Inherited只能实现类上的注解继承。
    Spring提供了AnnotatedElementUtils.findMergedAnnotation可以帮助我们找出父类和接口、父类方法和接口方法上的注解,并可以找到桥接方法，实现一键找到继承链的注解。
重点回顾
    第一，反射调用方法并不是通过调用时的传参确定方法重载，而是在获取方法的时候通过方法名和参数类型来确定的。遇到方法有包装类型和基本类型重载的时候，你需要特别注意这一点。
    第二，反射获取类成员，需要注意 getXXX 和 getDeclaredXXX 方法的区别，其中 XXX 包括 Methods、Fields、Constructors、Annotations。这两类方法，针对不同的成员类型 XXX 和对象，在实现上都有一些细节差异，详情请查看官方文档。今天提到的 getDeclaredMethods 方法无法获得父类定义的方法，而 getMethods 方法可以，只是差异之一，不能适用于所有的 XXX。
    第三，泛型因为类型擦除会导致泛型方法 T 占位符被替换为 Object，子类如果使用具体类型覆盖父类实现，编译器会生成桥接方法。这样既满足子类方法重写父类方法的定义，又满足子类实现的方法有具体的类型。使用反射来获取方法清单时，你需要特别注意这一点。
    第四，自定义注解可以通过标记元注解 @Inherited 实现注解的继承，不过这只适用于类。如果要继承定义在接口或方法上的注解，可以使用 Spring 的工具类 AnnotatedElementUtils，并注意各种 getXXX 方法和 findXXX 方法的区别，详情查看Spring 的文档。
    最后，我要说的是。编译后的代码和原始代码并不完全一致，编译器可能会做一些优化，加上还有诸如 AspectJ 等编译时增强框架，使用反射动态获取类型的元数据可能会和我们编写的源码有差异，这点需要特别注意。你可以在反射中多写断言，遇到非预期的情况直接抛异常，避免通过反射实现的业务逻辑不符合预期。


网文来源
反射机制
https://blog.csdn.net/javazejian/article/details/70768369
注解
https://blog.csdn.net/javazejian/article/details/71860633

深入理解Class对象
    RRTI的概念以及Class对象作用
        Java中用来表示运行时类型信息的对应类就是Class类，Class类也是一个实实在在的类
        Java中每个类都有一个Class对象，每当我们编写并且编译一个新创建的类就会产生一个对应Class对象并且这个Class对象会被保存在同名.class文件里(编译后的字节码文件保存的就是Class对象)
        几点信息：
            Class类也是类的一种，与class关键字是不一样的。
            手动编写的类被编译后会产生一个Class对象，其表示的是创建的类的类型信息，而且这个Class对象保存在同名.class的文件中(字节码文件)，比如创建一个Shapes类，编译Shapes类后就会创建其包含Shapes类相关类型信息的Class对象，并保存在Shapes.class字节码文件中。
            每个通过关键字class标识的类，在内存中有且只有一个与之对应的Class对象来描述其类型信息，无论创建多少个实例对象，其依据的都是用一个Class对象。
            Class类只存私有构造函数，因此对应Class对象只能有JVM创建和加载
            Class类的对象作用是运行时提供或获得某个对象的类型信息，这点对于反射技术很重要(关于反射稍后分析)。
    Class对象的加载及其获取方式
        Class对象的加载
            代码演示: SweetShop
        Class.forName方法
            代码演示: ClassForNameDemo
            需要捕获一个名称为ClassNotFoundException的异常
    Class字面常量
        Class clazz = Gum.class;
        更加简单，更安全。
        它在编译器就会受到编译器的检查同时由于无需调用forName方法效率也会更高，因为通过字面量的方法获取Class对象的引用不会自动初始化该类。
        字面常量的获取Class对象引用方式不仅可以应用于普通的类，也可以应用用接口，数组以及基本数据类型，这点在反射技术应用传递参数时很有帮助。
        由于基本数据类型还有对应的基本包装类型，其包装类型有一个标准字段TYPE，而这个TYPE就是一个引用，指向基本数据类型的Class对象。
        一般情况下更倾向使用.class的形式，这样可以保持与普通类的形式统一。
        代码演示: 见PrimitiveTypeDemo
    类加载的过程
        加载: 类加载过程的一个阶段：通过一个类的完全限定查找此类字节码文件，并利用字节码文件创建一个Class对象
        连接: 包含验证、准备、解析。验证字节码的安全性和完整性，准备阶段正式为静态域分配存储空间，注意此时只是分配静态成员变量的存储空间，不包含实例成员变量，如果必要的话，解析这个类创建的对其他类的所有引用。
        初始化: 类加载最后阶段，若该类具有超类，则对其进行初始化，执行静态初始化器和静态初始化成员变量。
        代码演示: ClassLoadProcessDemo。文稿的解释。
        小结论：
            获取Class对象引用的方式3种，通过继承自Object类的getClass方法，Class类的静态方法forName以及字面常量的方式”.class”。
            其中实例类的getClass方法和Class类的静态方法forName都将会触发类的初始化阶段，而字面常量获取Class对象的方式则不会触发初始化。
            初始化是类加载的最后一个阶段，也就是说完成这个阶段后类也就加载到内存中(Class对象在加载阶段已被创建)，此时可以对类进行各种必要的操作了（如new对象，调用静态成员等），注意在这个阶段，才真正开始执行类中定义的Java程序代码或者字节码。
        关于类加载的初始化阶段，在虚拟机规范严格规定了有且只有5种场景必须对类进行初始化：
            使用new关键字实例化对象时、读取或者设置一个类的静态字段(不包含编译期常量)以及调用静态方法的时候，必须触发类加载的初始化过程(类加载过程最终阶段)。
            使用反射包(java.lang.reflect)的方法对类进行反射调用时，如果类还没有被初始化，则需先进行初始化，这点对反射很重要。
            当初始化一个类的时候，如果其父类还没进行初始化则需先触发其父类的初始化。
            当Java虚拟机启动时，用户需要指定一个要执行的主类(包含main方法的类)，虚拟机会先初始化这个主类
            当使用JDK 1.7 的动态语言支持时，如果一个java.lang.invoke.MethodHandle 实例最后解析结果为REF_getStatic、REF_putStatic、REF_invokeStatic的方法句柄，并且这个方法句柄对应类没有初始化时，必须触发其初始化(这点看不懂就算了，这是1.7的新增的动态语言支持，其关键特征是它的类型检查的主体过程是在运行期而不是编译期进行的，这是一个比较大点的话题，这里暂且打住)
    理解泛化的Class对象引用
        代码演示: ClassGenericDemo
        Class<T>
        //以下编译无法通过
        Class<Number> numberClass=Integer.class;
        困惑:Integer不就是Number的子类吗？
        然而事实并非这般简单，毕竟Integer的Class对象并非Number的Class对象的子类，
        所有的Class对象都只来源于Class类，看来事实确实如此。
        //以下编译通过！
        Class<? extends Number> clazz = Integer.class;
        //赋予其他类型
        clazz = double.class;
        clazz = Number.class;
        extends关键字的作用是告诉编译器，只要是Number的子类都可以赋值。这点与前面直接使用Class<Number>是不一样的。实际上，应该时刻记住向Class引用添加泛型约束仅仅是为了提供编译期类型的检查从而避免将错误延续到运行时期。
    关于类型转换的问题
        利用Class对象的cast方法
        代码演示: TypeCastDemo
    instanceof 关键字与isInstance方法
        代码演示: InstanceOfDemo

理解反射技术
Constructor类及其用法
    查API
    代码演示: ConstructorDemo
Field类及其用法
    如果我们不期望获取其父类的字段，则需使用Class类的getDeclaredField/getDeclaredFields方法来获取字段即可，倘若需要连带获取到父类的字段，那么请使用Class类的getField/getFields，但是也只能获取到public修饰的的字段，无法获取父类的私有字段。
    代码演示: FieldDemo
Method类及其用法
    在通过getMethods方法获取Method对象时，会把父类的方法也获取到，如上的输出结果，把Object类的方法都打印出来了。而getDeclaredMethod/getDeclaredMethods方法都只能获取当前类的方法。
    getReturnType方法/getGenericReturnType方法都是获取Method对象表示的方法的返回类型，只不过前者返回的Class类型后者返回的Type(前面已分析过)，Type就是一个接口而已，在Java8中新增一个默认的方法实现，返回的就参数类型信息
    代码演示: FieldDemo
反射包中的Array类
    代码演示: ArrayDemo
    无法创建泛型数组，有了Array的动态创建数组的方式这个问题也就迎刃而解

理解Java注解
实际上Java注解与普通修饰符(public、static、void等)的使用方式并没有多大区别
基本语法
@Retention用来约束注解的生命周期，分别有三个值，源码级别（source），类文件级别（class）或者运行时级别（runtime）
    SOURCE：注解将被编译器丢弃（该类型的注解信息只会保留在源码里，源码经过编译后，注解信息会被丢弃，不会保留在编译好的class文件里）
    CLASS：注解在class文件中可用，但会被VM丢弃（该类型的注解信息会保留在源码里和class文件里，在执行的时候，不会加载到虚拟机中），请注意，当注解未定义Retention值时，默认值是CLASS，如Java内置注解，@Override、@Deprecated、@SuppressWarnning等
    RUNTIME：注解信息将在运行期(JVM)也保留，因此可以通过反射机制读取注解的信息（源码、class文件和执行的时候都有注解的信息），如SpringMvc中的@Controller、@Autowired、@RequestMapping等。
@Target 用来约束注解可以应用的地方（如方法、类或字段），其中ElementType是枚举类型
    TYPE:标明该注解可以用于类、接口（包括注解类型）或enum声明
    FIELD:标明该注解可以用于字段(域)声明，包括enum实例
    METHOD:标明该注解可以用于方法声明
    PARAMETER:标明该注解可以用于参数声明
    CONSTRUCTOR:标明注解可以用于构造函数声明
    LOCAL_VARIABLE:标明注解可以用于局部变量声明
    ANNOTATION_TYPE:标明注解可以用于注解声明(应用于另一个注解上)
    PACKAGE:标明注解可以用于包声明
    TYPE_PARAMETER:标明注解可以用于类型参数声明since 1.8
    TYPE_USE:类型使用声明since 1.8

注解元素及其数据类型
注解支持的元素数据类型
    所有基本类型（int,float,boolean,byte,double,char,long,short）
    String
    Class
    enum
    Annotation
    上述类型的数组
编译器对默认值的限制
    元素必须要么具有默认值，要么在使用注解时提供元素的值
    对于非基本类型的元素，无论是在源代码中声明，还是在注解接口中定义默认值，都不能以null作为值
    代码演示: AnnotationSupportTypeDemo
注解不支持继承
    反编译注解class可以发现注解类自动继承java.lang.annotation.Annotation
    Java的接口可以实现多继承，但定义注解时依然无法使用extends关键字继承@interface
快捷方式
    注解中定义了名为value的元素，并且在使用该注解时，如果该元素是唯一需要赋值的一个元素，那么此时无需使用key=value的语法，而只需在括号内给出value元素所需的值即可。这可以应用于任何合法类型的元素，记住，这限制了元素名必须为value
    代码演示: QuicklyDemo
Java内置注解与其它元注解
    主要有3个:@Override,@Deprecated,@SuppressWarnnings
    另外两种元注解，@Documented和@Inherited
        @Documented 被修饰的注解会生成到javadoc中
        @Inherited 可以让注解被继承，但这并不是真的继承，只是通过使用@Inherited，可以让子类Class对象使用getAnnotations()获取父类被@Inherited修饰的注解
注解与反射机制
    代码演示: AnnotatedElementDemo
运行时注解处理器
    代码演示: org.geekbang.time.read.column.others.classes.annotation.runtime下的文件
    解析注解生成sql
Java 8中注解增强
    @Repeatable
    代码演示: RepeatableDemo
新增的两种ElementType
TYPE_PARAMETER 可以用于标注类型参数
TYPE_USE 可以用于标注任意类型(不包括class)
代码演示: NewTypeDemo















