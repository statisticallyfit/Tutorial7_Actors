package cosc250.actorsFizzBuzz.solution

import akka.actor.{Actor, ActorLogging, ActorRef}
import Exercise._

import scala.util.Random

/**
  * This Actor is shockingly awful at this game. It gets the number and increments it,
  * but
  */
class Terrible extends Actor with ActorLogging {

	var nextPlayer:Option[ActorRef] = None

	def getNextResponse(i:Int) = {
		Seq(
			i + 1,
			Fizz(i + 1),
			Buzz(i + 1),
			FizzBuzz(i + 1)
		)(Random.nextInt(4))
	}

	def respond(i:Int) = {
		val nextResp = getNextResponse(i)
		log.info("Terrible says " + nextResp)

		for { nextPly <- nextPlayer } nextPly ! nextResp
	}

	//'andthen' chains partial functions together.
	def receive = Exercise.exLog("Terrible") andThen {
		case NextPlayerIs(p) => {
			nextPlayer = Some(p)

			// Let's send a message back so there is some kind of response, so the set-up code can know we've done it.
			sender ! "Ok"
		}
		case i:Int => respond(i)
		case Fizz(i) => respond(i)
		case Buzz(i) => respond(i)
		case FizzBuzz(i) => respond(i)

		// Terrible also needs to handle the Wrong message
		case Wrong(x) => println("Someone just said I got it wrong.")
	}

}