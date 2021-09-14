# JAVASS
A Java playable version of the swiss card game called Jass with the possibility to play against ai-players and remote players - First year project in 2019 

# How to play
Launch the src/ch/epfl/javass/launcher.java file.  
There you will have to select the different types of players:  
* Human: the person that is playing on this computer, you can choose the name and if you want the game to give you some hint or not.  
* Remote: A player playing on an other computer (or on the same for local multiplayer), check the multiplayer section below.  
* Simulated: Ai-player, you can choose the number of iterations (ai-players become better the more iterations you put but it also becomes slower).

You can also add a seed to always play the same game.

# Rules
Find all the rules of the Jass game here: https://www.swisslos.ch/en/jass/informations/jass-rules/principles-of-jass.html

# Remote Player
To be able to play with someone on an other computer make sur to have completly opens NATs or it won't work. The easiest way is to both play on the same computer.
In any case to play with a remote player do as follow:  
  - On the second computer (or the same computer for local multiplayer) launch the RemoteMain.java file. 
  - On the main computer now launch Launcher.java as usual and choose a RemotePlayer, fill its IP or leave it blank if playing on the same computer.  
  - Salect the other players and start the game. It should now open a window for the remote player.  
  - Have fun with your friends!  

# AI Player
To be able to have some simulated players to play against you, we use the Monte Carlo Tree Search algorithm in order to find the best play possible for the ai-player.
Since the algorithm works with iterations, it becomes better as the number of iterations increases but also slower. By default it does 10 000 iterations.  
  
    
      
Done for the "Practice of object-oriented programming" course of Schinz Michel.  
**ENJOY THE GAME!**
