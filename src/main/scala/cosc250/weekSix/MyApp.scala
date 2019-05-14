package cosc250.weekSix

import java.util.concurrent.TimeoutException

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.duration.FiniteDuration
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global

object MyApp extends App {

  import Exercise._

  // Create the actor system
  val system = ActorSystem("PingPongSystem")

  // Create three of your players
  val algernon = system.actorOf(Props[FizzBuzzActor], name = "Algernon")
  val bertie = system.actorOf(Props[FizzBuzzActor], name = "Bertie")
  val cecily = system.actorOf(Props[FizzBuzzActor], name = "Cecily")

  // Create a terrible player
  val hello = system.actorOf(Props[Terrible], name = "Terrible")

  // Set the players up in a circle


  // Start the game by sending the first player the number 0


}

