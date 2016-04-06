package cosc250.weekSix

import akka.actor.{ActorRef, Actor}
import cosc250.weekSix.Exercise.{NextPlayerIs, Buzz, FizzBuzz, Fizz}

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

  def receive = Exercise.log("Terrible") andThen {
    case NextPlayerIs(p) => nextPlayer = Some(p)
    case i:Int => respond(i)
    case Fizz(i) => respond(i)
    case Buzz(i) => respond(i)
    case FizzBuzz(i) => respond(i)
  }

}
