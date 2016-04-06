# Week 7 tutorial

This week, there's not (yet) a set of tests.
Let's put in a slightly higher level mission...

1. If you don't already know it, read up on what the game "FizzBuzz" is

2. In `MyApp`, set up a ring of `Terrible` players and start them playing. They will get it all wrong, but won't even know. But they should print it out to the console, so you'll see you have some Actors talking to each other.

   Q: If you do this naively (just sending the messages) it's theoretically possible for an actor to be slow responding to the `NextPlayerIs` message, and not actually have set themselves up when the game starts.
   How could you edit this so it's all still asynchronous, but you can *guaruntee* every player is ready before the game begins?

   Hint: Terrible will need to send a message in reply to the NextPlayerIs message, and then `MyApp` might want to use `?` instead of `!` and chain some futures together...

3. In `Exercise`, write a player who will play the game correctly. Don't worry yet about dealing with the Terrible players who get it wrong, just get your player to give the right next message (and print it out)

4. Now get your player to think about the message its received, and to send a Wrong message back to the previous player if they gave the wrong answer.

5. Add a Referee actor. Now, instead of sending a Wrong message back to the previous player, you send it to the Referee, who will check your claim and: if the player did indeed have it wrong, eliminate them from the game; but if it was right, eliminate you from the game.

(For #5, you'll possibly need to edit the Wrong message a little -- the Referee needs to know whose play it was AND who is shouting "Wrong")
