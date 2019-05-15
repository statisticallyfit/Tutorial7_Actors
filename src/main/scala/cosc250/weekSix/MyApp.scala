package cosc250.weekSix


import akka.actor._
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

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

	//Create referee
	val referee = system.actorOf(Props[Referee], name="Referee")




	// Let's import the ask pattern, so we can get every player to reply when they have received the RefereeIs message
	implicit val timeout = Timeout(5.seconds)

	//note: algernon ? RefereeIs(referee) will produce a Future[Any] that only
	//completes when algernone responds.
	// Means bertie is only told who referee is after algernon responds
	// In this way, although these are all asynchronous , everything gets sequenced in order
	// Referee can't send the start message (containing sequence of players) until
	// all the players have been told who referee is.
	// If any players don't respond at all, this will hang.
	// See referee for how to handle it when you don't know how many players there are

	for {
		a <- algernon ? RefereeIs(referee)
		b <- bertie ? RefereeIs(referee)
		c <- cecily ? RefereeIs(referee)
	} yield {
		//Set players up in a circle and start the game
		referee ! PlayerList(Seq(algernon, bertie, cecily))
	}

	// Set the players up in a circle


	// Start the game by sending the first player the number 0


}

