package cosc250.weekSix

import java.util.concurrent.TimeoutException

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.duration.FiniteDuration
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cosc250.weekSix.MyApp.referee

import scala.concurrent.ExecutionContext.Implicits.global

object MyApp extends App {

  import Exercise._

  // Create the actor system
  val system = ActorSystem("PingPongSystem")

  // Create three of your players
  val algernon = system.actorOf(Props[Terrible], name = "Algernon")
  val bertie = system.actorOf(Props[Terrible], name = "Bertie")
  val cecily = system.actorOf(Props[FizzBuzzActor], name = "Cecily")

  // Create a terrible player
  val hello = system.actorOf(Props[Terrible], name = "Terrible")

  val referee = system.actorOf(Props[Referee], name = "Referee")

  // Let's import the ask pattern, so we can get every player to reply when they have received the RefereeIs message
  import akka.pattern.ask
  import akka.util.Timeout
  import scala.concurrent.duration._
  implicit val timeout = Timeout(5.seconds)

  // This uses the ask pattern
  // algernon ? RefereeIs(referee) will produce a Future[Any] that will only complete when algernon responds
  // That means bertie will only be told who the referee is after algernon has responded.
  // In this way, although this is all asynchronous messages, everything gets sequenced in order.
  // The referee won't be send the start message (containing the sequence of players) until all players have been
  // told who the referee is.
  //
  // NB: If any of the players don't respond at all (including Terrible), this will hang.
  //
  // See the Referee for how to handle it when you don't know how many players there are.
  for {
    a <- algernon ? RefereeIs(referee)
    b <- bertie ? RefereeIs(referee)
    c <- cecily ? RefereeIs(referee)
  } yield {
    // Set the players up in a circle and start the game
    referee ! PlayerList(Seq(algernon, bertie, cecily))
  }



}

