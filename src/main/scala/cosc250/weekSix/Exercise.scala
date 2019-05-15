package cosc250.weekSix

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import play.api.libs.json
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This week, let's play FizzBuzz with Actors.
  *
  * If you're not sure what FizzBuzz is, see https://en.wikipedia.org/wiki/Fizz_buzz
  *
  */
object Exercise {

	/*
	 * First, because we're working with Actors, we need some messages to pass
	 * Note: we can also just pass a bare Int! Normally we wouldn't -- it's nice to have a single trait
	 * or abstract class all our messages belong to -- but for this exercise let's show it's possible
	 */
	case class Fizz(i:Int)
	case class Buzz(i:Int)
	case class FizzBuzz(i:Int)
	case class RefereeIs(r:ActorRef)
	case class PlayerList(players:Seq[ActorRef])

	/*
	 * And we need a message so the next player can say "Wrong! You're out!"
	 *
	 * Note, I've made this message generic, so it can contain any of our messages
	 */
	case class Wrong[T](item:T, player:ActorRef)

	/*
	 * We're also going to need some "set-up" messages, to tell the Actors who is standing
	 * next to whom (if Alice goes "Fizz(3)", who goes next?
	 */
	case class NextPlayerIs(a:ActorRef)

	/**
	  * This is a little utility method I've made. It creates a PartialFunction that
	  * writes out whatever it received.
	  *
	  * PartialFunctions can be composed together using "andThen". So if you want a method
	  * that logs what it receives and then takes an action depending on what method it receives...
	  *
	  * log("MyActor") andThen { case ... }
	  */
	def log(name:String):PartialFunction[Any, Any] = {
		case m =>
			println(name + " received " + m)
			m
	}

	/*
	 * Now you need to define your FizzBuzz Actor...
	 */
	class FizzBuzzActor extends Actor {

		var nextPlayer:Option[ActorRef] = None
		var referee:Option[ActorRef] = None

		// Will return false if a received message was wrong
		def consider(msg:Any):Boolean = msg match {

			case i:Int => (i % 3 != 0) && (i % 5 != 0)
			case Fizz(i) => (i % 3 == 0) && (i % 5 != 0)
			case Buzz(i) => (i % 3 != 0) && (i % 5 == 0)
			case FizzBuzz(i) => i % 15  == 0
			case _ => false
		}

		def respond(msg:Any):Unit = {

			// Check if we've received a wrong message, and signal the referee
			if (!consider(msg)) {
				for { r <- referee } { r ! Wrong(msg, sender()) }
			} else {
				val i = msg match {
					case i:Int => i
					case Fizz(i) => i
					case Buzz(i) => i
					case FizzBuzz(i) => i
				}
				val n = i + 1
				for {
					p <- nextPlayer
				} if (n % 15 == 0) {
					p ! FizzBuzz(n)
				} else if (n % 5 == 0) {
					p ! Buzz(n)
				} else if (n % 3 == 0) {
					p ! Fizz(n)
				} else p ! n
			}
		}

		/*
		 * In class, I hadn't shown the type of the receive method. It's a PartialFunction!
		 * (Ah, the joy of being able to link back to earlier lectures...)
		 *
		 * Curiously, this means you can also change the def to a val and it will still work.
		 * I'll leave why that is as an exercise for the reader...
		 */
		def receive:PartialFunction[Any, Unit] = log(self.path.name) andThen {

			// Now decide how your actor is going to respond to the messages. Note, you might need to
			// Create member variables and functions...
			case NextPlayerIs(p) =>
				nextPlayer = Some(p)
				sender() ! "ok"

			case RefereeIs(r) =>
				referee = Some(r)
				sender() ! "ok"

			// We have to have at least one case statement to make this compile as a PartialFunction
			// with the types...
			case m => respond(m)
		}
	}


}
