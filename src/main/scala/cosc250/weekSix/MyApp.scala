package cosc250.weekSix

import java.util.concurrent.TimeoutException

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.ws.ahc.AhcWSClient
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.duration.FiniteDuration
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global

object MyApp extends App {

  import Exercise._

  // Create the actor system
  val system = ActorSystem("PingPongSystem")

  // Let's create five Terrible players.
  // Each of these returns an ActorRef
  val algernon = system.actorOf(Props[Terrible], name = "Algernon")
  val bertie = system.actorOf(Props[Terrible], name = "Bertie")
  val cecily = system.actorOf(Props[Terrible], name = "Cecily")
  val daliah = system.actorOf(Props[Terrible], name = "Dahlia")
  val earnest = system.actorOf(Props[Terrible], name = "Earnest")

  // To set the players up in a circle, we tell each player who the next one is
  // (In the code for Terrible, there's a field that keeps who the next player is. And Terrible
  // updates it on receiving a NextPlayerIs(player) message.  
  algernon ! NextPlayerIs(bertie)
  bertie ! NextPlayerIs(cecily)
  cecily ! NextPlayerIs(daliah)
  daliah ! NextPlayerIs(earnest)
  earnest ! NextPlayerIs(algernon)

  // And we can start the game by sending the first player the number 0
  // (Because if we look at the Actors, they then send the next message on for the next number in
  // sequence)
  algernon ! 0

}

