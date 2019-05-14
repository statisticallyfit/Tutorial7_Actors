package cosc250.weekSix

import akka.actor.{Actor, ActorRef}
import cosc250.weekSix.Exercise._

import scala.util.Random

/**
  * This Actor is shockingly awful at this game. It gets the number and increments it,
  * but
  */
class Terrible extends Actor {

  var nextPlayer:Option[ActorRef] = None

  def nextResponse(i:Int) = {
    Seq(
      i + 1,
      Fizz(i + 1),
      Buzz(i + 1),
      FizzBuzz(i + 1)
    )(Random.nextInt(4))
  }

  def respond(i:Int) = {
    val n = nextResponse(i)
    println("Terrible says " + n)
    for { p <- nextPlayer } p ! n
  }

  def receive = Exercise.log(self.path.name) andThen {


    // Note - we have to respond to this, or the starting logic will never complete
    case RefereeIs(r) =>
      sender() ! "ok"

    // Again, we have to respond so that the ask pattern in the Referee will complete successfully
    case NextPlayerIs(p) =>
      nextPlayer = Some(p)
      sender() ! "Created"

    case i:Int => respond(i)
    case Fizz(i) => respond(i)
    case Buzz(i) => respond(i)
    case FizzBuzz(i) => respond(i)
    case _ => {}
  }

}
