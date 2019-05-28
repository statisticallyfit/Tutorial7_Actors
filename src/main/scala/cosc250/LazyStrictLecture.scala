package cosc250.roboScala
import scala.collection.immutable

/**
  *
  */

object Temp {

	val theval1: Int = {
		println("I was called")
		3
	}

	def theval2: Int = {
		println("I was called")
		3
	}

	def main(args: Array[String]) {
		println("test: println(theval1)")
		println(theval1) //3

		println("test: println(theval1) TWICE")
		println(theval1) //3
		println(theval1) //3

		println("empty the val1")
		val theval11: Int = {
			println("I was called")
			3
		}
		//note: prints "I was called" since a value type is evaluated immediately (strict)


		println("-----------")
		println("test: println(theval2)")
		println(theval2) //I was called and 3

		println("test: println(theval2) TWICE")
		println(theval2) // i was valled, and 3
		println(theval2) // i was called, and 3

		println("empty theval2")
		def theval22: Int = {
			println("I was called")
			3
		}
		//prints nothing since this is lazy evaluation, function does not evaluate immediately
		// only when it is called


		println("--------")
		println("Testing lazy val evaluation")
		lazy val theval3: Int = {
			println("I was called")
			3
		}
		println("See it wasn't called immediately!")
		println("test call it now: ")
		println(theval3)


		println("-----------BY NAME VS BY VALUE -------")
		def myValue = {
			println("myValue was calculate")
			3
		}

		println("BY value function args")
		def myFunctionByVal (i: Int)= {
			println(s"$i + 1 == ${i + 1}") //myValue arg println is done immediately before i is passed into  body
			println(s"$i + 2 == ${i + 2}")
		}
		myFunctionByVal(myValue )

		println("\nBy name function args:")
		def myFunctionByName (i: => Int)= {
			println(s"$i + 1 == ${i + 1}") //myValue arg println is done twice here for each time i is called
			println(s"$i + 2 == ${i + 2}") //done twice here again since i is called twice
		}
		myFunctionByName(myValue)

		println("\nBy name with lazy val assignment")
		def myFunctionByNameLazy(givenI: => Int) = {
			lazy val i = givenI //

			println(s"$i + 1 == ${i + 1}") // myValue arg println done only once total at both lines since lazy, only
			// done when needed
			println(s"$i + 2 == ${i + 2}")
		}
		myFunctionByNameLazy(myValue)

		println("\nBy name with lazy val assignment --- and nothing else")
		def myFunctionByNameLazyNothing(givenI: => Int) = {
			lazy val i = givenI // myValue arg println not done since "i" is never called

			//nothing else
		}
		myFunctionByNameLazyNothing(myValue)

		/*
BY value function args
myValue was calculate
3 + 1 == 4
3 + 2 == 5

By name function args:
myValue was calculate
myValue was calculate
3 + 1 == 4
myValue was calculate
myValue was calculate
3 + 2 == 5

By name with lazy val assignment
myValue was calculate
3 + 1 == 4
3 + 2 == 5

By name with lazy val assignment --- and nothing else
		 */
	}
}


object Example2 extends App {

	//pg 216 odersky
	var assertionsEnabled = true

	//note byname parameter predicate: lazy evaluation, non-strict
	//predicate parameter is by name passed so its value is not evaluated
	// before the call to the function. Instead, a function value is created
	// whose apply method evaluates predicate value and this
	// function value is passed to byNameAssert
	def byNameAssert(predicate: => Boolean) = {
		if(assertionsEnabled && !predicate){
			throw new AssertionError
		}
		//println(s"IN BYNAME: predicate = $predicate")

	}

	// by value: parameter predicate is just boolean so the argument is
	// evaluated before it goes into the body of the method
	def boolAssert(predicate: Boolean) = {
		if(assertionsEnabled && !predicate){
			throw new AssertionError
		}
		//println(s"IN BYVALUE: predicate = $predicate")
	}

	//note: the difference between the approaches:
	// If assertions are disabled, any side effects that the argument has
	// are only visible in boolAssert (by value passing) and not in byname passing
	val x = 5
	assertionsEnabled = false

	try{
		//by value: should throw error (byvalue: side effects of argument are visible before entering method)
		boolAssert(x / 0 == 0)
	} catch {
		//case e: Exception => e.getMessage
		case _: Throwable => println("Assertion error was thrown")

	}
	// by name: function args are not evaluated in argument position, only in method
	byNameAssert(x / 0 == 0)
}



object AboutStreams extends App {

	//strict version of Stream
	trait Lst[+T] {
		def tail: Lst[T]
		def head: T

		def apply(n: Int): T = {
			if (n < 0 || this == Nl) {
				throw new IndexOutOfBoundsException
			} else if (n == 0){
				head
			} else {
				tail.apply(n-1)
			}
		}
	}

	//Nothing is a bottom type so we can have just a single object represent
	// all empty Lsts
	object Nl extends Lst[Nothing] {
		def tail = throw new NoSuchElementException("empty list has no tail")
		def head = throw new NoSuchElementException("empty list has no head")

		override def toString = "Nl"
	}


	// BY VALUE (Strict) implementation of Lst
	{

		class Cons[T](val head: T, val tail: Lst[T]) extends Lst[T] {
			override def toString = s"($head, $tail)"
		}

		//testing this with a range function to generate natraul numbers
		// and print then out when they are calculated
		def range(from: Int, to: Int): Lst[Int] = {
			if(to < from) {
				Nl
			} else {
				println(s"Created element for $from")
				new Cons(from, range(from + 1, to))
			}
		}
		println("TESTING BY VALUE LST")
		val bnRange: Lst[Int] = range(1, 6) //help lecture has Lst.range(1,6)
		println(bnRange.apply(1)) //get the value at index 1 = 2
	}



	// BY NAME (Non-Strict) implementation of Lst
	{

		//note: val params cannot be called by name
		class Cons[T](val head: T,  byNameTail: => Lst[T]) extends Lst[T] {
			def tail = byNameTail //trait function "tail" is returning the byname tail version
			override def toString = s"($head, $tail)"
		}

		//testing this with a range function to generate natraul numbers
		// and print then out when they are calculated
		def range(from: Int, to: Int): Lst[Int] = {
			if(to < from) {
				Nl
			} else {
				println(s"Created element for $from")
				new Cons(from, range(from + 1, to))
			}
		}
		println("TESTING BY NAME CONS")
		val bnRange: Lst[Int] = range(1, 6) //help lecture has Lst.range(1,6)
		println(bnRange.apply(1)) //get the value at index 1 = 2

		//second argument tail is by name so bnRange.tail.tail was never asked for

		//problem: we are recalculating tail every time we ask for it
		println(bnRange.apply(1))
	}



	// BY NAME (Non-Strict) with LAZY val implementation of Lst
	//fixes the "recalc of tail" problem
	{

		//note: val params cannot be called by name
		class Cons[T](val head: T,  byNameTail: => Lst[T]) extends Lst[T] {
			lazy val tail = byNameTail //trait function "tail" is returning the byname tail version
			override def toString = s"($head, $tail)"
		}

		//testing this with a range function to generate natraul numbers
		// and print then out when they are calculated
		def range(from: Int, to: Int): Lst[Int] = {
			if(to < from) {
				Nl
			} else {
				println(s"Created element for $from")
				new Cons(from, range(from + 1, to))
			}
		}
		println("TESTING BY NAME CONS - lazy")
		val bnRange: Lst[Int] = range(1, 6) //help lecture has Lst.range(1,6)
		println(bnRange.apply(1)) //get the value at index 1 = 2

		//second argument tail is by name so bnRange.tail.tail was never asked for

		//the tail is a lazy val so is calculated once and only if asked for
		println(bnRange.apply(1))

		//another problem: printnl causes everything to be generated
		println(bnRange)
	}



	//STREAMS: lazy list with Stream.empty[T] like Nl and Stream.cons[T](head: T, tail: => T)
	// lazy (caches results) and println does not force calculation)
	println("\n\nStreams Intro:")
	println(Stream.range(1, 5000))


	println("\n\nInfinite streams")

	def f(i: Int) = i % 60 == 0
	val naturals: immutable.Seq[Int] = Stream.from(1)
	//val n: immutable.Seq[Int] = naturals.filter(f)
	println(naturals.filter(f)(1)) //filter by f and get first value
	println(naturals) //now the results mod 60 are caches in the stream by the above filtering. The rest are
	// question marked

	/*
	Infinite streams
120
Stream(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, ?)
	 */
}
