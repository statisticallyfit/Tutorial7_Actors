package cosc250.weekSix

import akka.actor.{Actor, ActorRef}
import cosc250.weekSix.Exercise._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This Actor is shockingly awful at this game. It gets the number and increments it,
  * but
  */
class Referee extends Actor {

  var players:Seq[ActorRef] = Seq.empty

  // Will return false if a received message was wrong
  def consider(msg:Any):Boolean = msg match {
    case i:Int => (i % 3 != 0) && (i % 5 != 0)
    case Fizz(i) => (i % 3 == 0) && (i % 5 != 0)
    case Buzz(i) => (i % 3 != 0) && (i % 5 == 0)
    case FizzBuzz(i) => i % 15  == 0
    case _ => false
  }

  /**
    * We need a way of setting up the circle of players.
    * As this is the sample solution, let's do this including the ask pattern.
    *
    * The return type here is a Future containing a Seq of the responses the players made to the NextPlayerIs message
    */
  def setNextPlayers():Future[Seq[Any]] = {

    // Are there multiple players? If not, a player has won
    if (players.length > 1) {
      // This should give us a List of tuples (player, nextPlayer)
      // eg, if our players are a, b, c we should get (a, b), (b, c), (c, a)
      val pairings = players.zip(players.tail) :+ players.last -> players.head


      // Let's import the ask pattern, so we can get every player to reply when they have received the NextPlayerIs message
      import akka.pattern.ask
      import akka.util.Timeout
      import scala.concurrent.duration._
      implicit val timeout = Timeout(5.seconds)

      // We also need an Execution context for combining Futures together
      import scala.concurrent.ExecutionContext.Implicits.global

      // This should now give us a Seq[Future[Any]] containing whether each actor has responded
      val responded = pairings.map({ case (p, n) => p ? NextPlayerIs(n) })

      // And this will produce a Future[Seq[Any]] that will only complete when they have all responded.
      // Future.sequence isn't an examinable method, but is a useful one to know
      Future.sequence(responded)
    } else {
      println("And the game has finished!")
      Future.successful(Seq.empty)
    }
  }

  /** A function for getting all the players' names */
  def playerNames = for { p <- players } yield p.path.name

  /**
    * To eliminate players, we're just going to filter them out of the sequence of players. ActorRefs are comparable
    * on the path (ie, the name they were given when you created them).
    */
  def eliminate(p:ActorRef):Unit = {
    println("elminating " + p.path.name)
    players = players.filterNot(_ == p)
    println("players are " + playerNames)
    startGame()
  }

  /**
    * To start a game, we tell them all who the next player is, and when they've all responded, send FizzBuzz(0) to
    * the first player. Because zero is divisible by 15.
    */
  def startGame():Unit = {
    for (responses <- setNextPlayers()) {
      if (players.length > 1) {
        players.head ! FizzBuzz(0)
      } else {
        // If we only have 1 player left, exit
        MyApp.system.terminate()
      }
    }
  }

  def receive = Exercise.log("Referee") andThen {

    // We start the game when we first receive a PlayerList from MyApp
    case PlayerList(p) => {
      players = p
      startGame()
    }

    // The only other messages the Referee needs to handle (the way we've set it up) is the calls of Wrong!
    case Wrong(msg, player) => {
      if (consider(msg)) {
        println("The Wrong call was wrong!")
        eliminate(sender())
      } else {
        println("Thr Wrong call was right!")
        eliminate(player)
      }

    }
  }

}
