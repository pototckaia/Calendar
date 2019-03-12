Introdution in Kotlin
1. 

fun main(args: Array<String>) {
	println("Hello, world!")
}

fun max(a: Int, b: Int): Int {
	return if (a > b) a else b
}

fun max(a: Int, b: Int) = if (a > b) a else b

2. 
val answer: Int = 41;
val question = "Thhh"
val (value =) - неизменная ссылка -  final 
var (variable) - изменная ссылка

3. 

val name = if (args.size > 0) args[0] else "Kotlin" 
println("Hellow, $name!") 
println("Hellow, &{args[0]}")
println("Hellow, &{if (args.size > 0) args[0] else "someone"}")

4.

public class Person {
	private finale String name; 

	public Person(String name) {
		this.name = name;
	}

	public String getName() {
		return name; 
	}
}

class Person(val name: String) = value objects 
val = Неимзменное свойтво: только метод чтения
var = Изменяемое свойство: запись 

Java 
Person person = new Person("Bob", true)
System.out(person.getName())
System.out(person.isMarried())

Kotlin
val person = Person("Bob", true)
println(person.name)
println(person.isMarried)

5. 

class Rectangle(val height: Int, val width: Int) {
	val isSquare: Boolean
		get() {
			return height == width
		}
}

6.

package geometry.shapes

import java.util.Random

Любые классы и функции верхнего уравня


7. 

enum class Color {
	RED, ORANGE, YELLOW, BLUE
}

enum class  Color(
	val r: Int, val g: Int, val b: Int
) {
	RED(255, 0, 0), ... ;

	fun rgb() = (r * 256 + g) * 256 + b
}

println(Color.BLUE.rgb())

fun getMnemonic(color: Color) = 
	when(color) {
		Color.RED -> "   "
		Color.BLUE -> "   "
		Color.YELLOW, Color.RED -> 
		...
	}

fun min(c1: Color, c2: Color) = 
	when(setOf) {
		setOf(RED, YELLOW) -> ORANGE
		...
	}

fun mixOptimezed(c1: Color, c2: Color) = 
	when {
		(c1 == RED && c2 == YELLOW) || .. -> ORANGE
		() -> ...

		else -> ..
	}

8. Smart cast 

if (e is Num) {
	val n = e as Num <- Явное приведение излишее
	return n.value;
}
if (e is Sum) {
	return eval(e.right) + eval(e.left)
}

when (e) {
	is Num -> ...
	is Sum -> ...
	else -> ...
}

9. 

fun evalWithLogging(e: Expr): Int =
	when(e) {
		is Num -> {
			println()
			e.value
		}
		is Sum -> {
			val left = ..
			val right = ..
			println()
			left + right
		}
		else -> throw ...

10. 

for (i in 1..100)
for (i in 100 downTo 1 step 2)
for (c in 'A'..'B')
val binaryReps = TreeMap<Char, String>()
for ((letter, binary) in binaryReps)
for (c !in 'A'..'B')

java.lang.Comparable 
println("Kotlin" in "Java".."Scala")
println("Koltin" in setOf("Java", "Scala"))

11.

val  percentage = 
	if (number in 0..100) 
		number
	else 
		throw IllegalArgumentException("... $number")

how Java
try {
	val line = reader.readLine()
	return Integer.parserInt(line)
} catch(e: NumberFormatException) {
	return null
}
finally {
	reader.close()
}

отсутсвие throws in сигнатуре 

val number = try {
	Integer.parserInt(reader.readline())
} catch(e: NumberFormatException) {
	return
}
println(number)

12. 

Именновынные аргументы 
joinToString(collection, separator=" ", prefix=" ")

Параментры по умолчанию
fun <T> joingToString(
	collection: Collection<T>,
	separator: String = ", ", 
	prefix: String = ""): String

Функции и свойства верхнего уравня

var opCount = 0

fun performOperation() {
	opCount++
}

fun reportOperationCount() {
	println("Operation perfomend $opCount times")
}

/* const */ val UNIC = "\n"
 const -> public static final (простые типы и String)

 13.

Функции-расширения
fun String.lastChar() : Char = this.get(this.length - 1)
Обращатья только к публичным методам
Ковертируется в static

import string.lastChar as last

Свойства-расширения

val String.lastChar: Char
	get() = get(length - 1)

var StringBuilder.lastChar: Char
	get() = get(length - 1)
	set(value: Char) {
		this.setCharAs(length - 1, value)
	}

14. 
Произвольное число аргументов
fun listOf<f>(vararg value: T): List<T> {...}
spread operator -> *
val list = listOf("args: ", *args)

Инфиксная форма записи вызова infix call
val map = mapOf(1 to "one",  7 to "seven", 53 to "fifty-three")

.to("one") -> to "one"


infix fun Any.to(other: Any) = Pair(this, other)

val (number, name) = 1 to "one"
destruturing declaration

15. 

"12.346-6.A".split("\\.|-".toRegex())
"12.346-6.A".split(".", "-")

fun parsePath(path: String) {
	val deriection = path.substringBeforeLast("/")
	val fullName = path.substringAfterLast("/")
	val fileName = fullName.substringBeforeLast(".")
	val extension = fullName.substringAfterLast(".")
	println("Dir: $deriection, name:  $fileName, ext: $extension")
}

fun parsePath(path: String) {
	val  regax = """(.+)/(.+)\.(.+)""".toRegex() <- не нужно экранировать 
	val matchResult = regax.matchEntire(path)
	if (matchResult != null) {
		val (direction, filename, extenstion) = matchResult.destructured
		println()
	}
}

// val kotlinlog = """| //
// 				   .|//
// 				   .|/ \"""


16. Ллокальные функции 

class User(val id: Int, val name: String, val address: String)

fun saveUser(user: User) {
	fun validate(user: User, 
				value: String,
				fieldName: String) {
		if (value.isEmpty()) {
			throw IllegalArgumentException(
				"Can't save user ${user.id}: empty $filedName")
		}
	}

	validate(user, user.name, "Name")
	validate(user.user.address, "Address")
}

validate имеет доступ к локальным переменным
fun valiadate(value: String, fieldName: String) {
	if (value.isEmpty()) {
		throw ...
	}
}
Функции расширения тоже можно сделать локальной

объектов-одиночек 
объектов-компаньенов
объектов-вырыжений


17.Интерфейсы

Не могут иметь состояние
interface Clickable {
	fun click()
	по-умолчанию
	fun showOff() = prinltn()
}

interface Focusable {
	fun setFocus(b: Boolean) =
		println("I ${if (b) "got" else "lost"} focus")
	fun showOff() = println()
}

class Button : Clickable, Focusable {
	override fun click() = println()
	
	определить реализацию showOff
	override fun showOff() {
		super<Clickable>.showOff();
		super<Focusable>.showOff();
		вместо Clickable.super.showOff in Java
	}
}

Button().click()

override - методы или свойства суперкласса и интерфейса, обязательно

18. Переопределение метов в Подклассе

Пролема с fragile base class for it final in base class 
Kotlin default final method and class
open class разрешенно наследование

open class RichButton : Clickable  {
	fun disable() {} Закрытая функция 
	open fun animate() {} 
	override fun click() {} Также является открытой
	final override fun click() {}
}

abstract class Animated {
	fun animate_() Абстрактная функция
	abstract fun animate()
	open fun stopAnimation() {}
	fun animateTwice() {}
}

Закрытые классы позволяют выполнять автоматическое приведение типой в 
больщенстве сценриев. Автоматическое приведение возможно только
для переменным, которые нельзя изменить после проверки типа. Эт можно применить
только к неизменным свойствам со стандартыми методами доступа - final

19. Модификаторы доступа

public, protected, private
public по умолчанию
Пакеты используются только для организации кода в пространства имен,
но не для управления видимостью

internal видимость в границах модуля
private к обявлениям верхнего уравня делает их доступными только в файле

Модуль - набора файлов, компилируемых вместе. Например, модуль Intelling, проект Eclipse ... 

internal open class TalkattiveButton {}

fun TalkattiveButton.giveSpeech() {}
Ошибка публичный член класса

20. Внутренние классы
Внешний класс не видит приватных членов внутренних классов
Вложенный класс не имеет доступа к экземпляру внешнего класса

interface State: Serializable

interface View {
	fun getCurrentState(): State
	fun restoreState(state: State) {}
}

Java
public class Button implements View {
	@Override 
	public State getCurrentState() {
		return new ButtonState();
	}

	@Override
	public void restoreState(State state) { ... }

	public class ButtonState implements State { ... }
	неявно хранит ссылку на внешний класс
	нужно объявить static
}

class Button: View {
	override fun getCurrentState(): State = ButtonState()
	override fun restoreState(state: State) { ... }
	class ButtonState: State { ...}
	Аналог статического вложенного класса 
	inner Внутренний класс со ссылкой на внешний класс 
}

this@Outer получить ссылку на внешний класс 
class Outer {
	inner class Inner { 
		fun getOuterReference(): Outer = this@Outer
	}
}

21. Запечатанные классы sealed
Все прямые подклассы долже быть вложены в суперкласса
sealed /* opem */ class Expr { Приватный коструктор, который может вызвать только внутрений класс
	class Num(val value: Int) : Expr()
	class Sum(val left: Expr, val right: Expr) : Expr()
}

fun eval(e: Expr): Int =
	when(e) {
		is Expr.Num ->
		is Expr.Sum ->
	}

22. Нетривиальный конструктор

основной конструктор - инициализация класса и обявляется вне тела класса
вторичный конструктор - в теле

class User(val nickname: String)

class User constructor(_nickname: String) {
	val nickname: String
	
	init {
		nickname = _nickname
	}
}

constructor - основной или вторичный
init - блок инициализация
код инициализация, выполняется при создании каждого экземпляра
можно обявить несколько блоков инициализации

В отсутсвии аннотации и модификатора
class User(_nickname: String) {
	val nickname = _nickname
}

val -> для параментра должно быть создано соответсвующее свойство

Если класс имеет супер класс, основной конструктор также должне
инициализировать свойства, унаследованные от суперкласса

open class User(val nickname: String) {...}
ckass TwitterUser(nickname: String) : User(nickname) {...}

class Sercetive private constructor() {}

open class View {
	constructor(ctx: Context) {

	}

	constructor(ctx: Context, attr: AttributeSet) {

	}
}

class MyButton: View {
	constructor(ctx: Context)
	: super(ctx) {
		...
	}

	constructor(ctx: Context, attr: AttributeSet)
	: super(ctx, attr) Делегирует выполнения
	this(ctx, MY_STYLE) Другой конструктор
	{
		...
	}

	Без основного конструктор каждый вторичный 
	обязатльно деленирует выполнения другому конструктору базового класса
}


23. Реализации свойств, обявленных в интерфейсе

interface User {
	val nickname: String
}

class PrivateUser(override val nickname: String) : User

class SubstriblingUser(val email: String) : User {
	override val nickname: String
		get() = email.substringBefore('@')
}

class FacebookUser(val accountId: Int) : User {
	override val nickname = getFacebookName(accountId)
	Инициализациия свойства
}


interface User {
	val email: String
	val nickname: String
		get() = email.substringBefore('@')
}



class User(val name: String) {
	var address: String = "unspecified"
		set(value: String) {
			println()
			field = value // изменение значение поля
			если не использовать отдельное поле не будет сгенерированно
		}
}