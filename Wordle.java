// Main program for COMP1721 Coursework 1
// DO NOT CHANGE THIS!

//Author: Micheal Nestor
//Email: sc21mpn@leeds.ac.uk
//Date completed: 03/04/2022

package comp1721.cwk1;

import java.io.IOException;


public class Wordle {
  public static void main(String[] args) throws IOException {
    Game game;
    boolean accessible = false;

    if (args.length > 1) {
      String game_num = args[0];
      if (args[0].equals("-a")) {
        accessible = true;
        if (args.length > 1) {
          game_num = args[1];
        }
      }
      // Player wants to specify the game
      game = new Game(accessible, Integer.parseInt(game_num), "data/words.txt");
    } 
    else {
      // Play today's game
      game = new Game(accessible, "data/words.txt");
    }

    game.play();
    game.save("build/lastgame.txt");
  }
}
