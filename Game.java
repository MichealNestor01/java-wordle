//Author: Micheal Nestor
//Email: sc21mpn@leeds.ac.uk
//Date completed: 03/04/2022

package comp1721.cwk1;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Game {
  private int gameNumber;
  private String target;
  private ArrayList<String> guesses = new ArrayList<String>();
  private boolean accessible;
  private boolean success = true;

  //=========================================//
  //             CONSTRUCTORS                //
  //=========================================//

  //constructor for the simple game with todays word
  public Game(boolean accessible_status, String filename) throws IOException {
    //initialise accessbile
    accessible = accessible_status;

    //initialise game number
    LocalDate inception = LocalDate.of(2021, 6, 19);
    LocalDate today = LocalDate.now();
    gameNumber = (int) ChronoUnit.DAYS.between(inception, today);

    //initialise target
    Scanner input = new Scanner(Paths.get(filename));

    // find the word that matches the game number
    int count = 0;
    while (input.hasNextLine()) {
      target = input.nextLine();
      if (count == gameNumber) {
        break;
      }
      count++;
    }
    
    //if the word isnt found throw an io exception
    if (count != gameNumber) {
      throw new IOException();
    }

    input.close();
  }

  // constructor for the simple game with a set word
  public Game(boolean accessible_status, int num, String filename) throws IOException {
    //initialise accessbile
    accessible = accessible_status;

    //initialise game number
    gameNumber = num;

    //initialise target
    Scanner input = new Scanner(Paths.get(filename));

    int count = 0;
    while (input.hasNextLine()) {
      target = input.nextLine();
      if (count == gameNumber) {
        break;
      }
      count++;
    }
    
    if (count != gameNumber) {
      throw new IOException();
    }

    input.close();
  }

  //=========================================//
  //              GAME METHODS               //
  //=========================================//

  public void play() throws IOException {
    //Show the current wordle game name
    System.out.printf("WORDLE %d\n\n", gameNumber);

    //run through the guesses for the player
    int totalGuesses = 7;
    for (int i = 1; i < 7; i++) {
      Guess currentGuess = new Guess(i);
      String output = currentGuess.compareWith(target, accessible);
      System.out.println(output);
      guesses.add(output);
      if (currentGuess.matches(target)) {
        totalGuesses = i;
        break;
      }
    }

    //select the final output based on the number of guesses used
    if (totalGuesses == 1) {
      System.out.println("Superb - Got it in one!");
    } else if (totalGuesses > 1 && totalGuesses < 6) {
      System.out.println("Well done!");
    } else if (totalGuesses == 6) {
      System.out.println("That was a close call!");
    } else {
      success = false;
      System.out.println("Nope - Better luck next time!");
      //If the user was not successful output the target word
      ArrayList<String> targetChars = new ArrayList<String>(Arrays.asList(target.toUpperCase().split("")));
      StringBuilder messageBuffer = new StringBuilder();
      //colour in the output
      for (int i = 0; i < 5; i++) {
        targetChars.set(i, String.format("\033[30;102m %s \033[0m", targetChars.get(i)));
        messageBuffer.append(targetChars.get(i));
      }
      String output = messageBuffer.toString();
      //show the output
      System.out.println(messageBuffer.toString());
    }

    //save the game file
    try {
      save("build/lastgame.txt");
    } catch (IOException ex) {
      System.err.println(ex);
    }

    //save game data
    saveHistory("build/history.txt");

    //Show history and statistics
    displayHistory();
  }

  public void displayHistory() throws IOException {
    //history data will store the data from the history file
    ArrayList<String> history_data = new ArrayList<String>();
    //these arrays will store the organised data from the history file
    ArrayList<String> game_numbers = new ArrayList<String>();
    ArrayList<String> successes = new ArrayList<String>();
    ArrayList<String> guess_counts = new ArrayList<String>();

    //Read in all of the data from history.txt and store it in the history_data array 
    Scanner input = new Scanner(Paths.get("build/history.txt"));
    
    while(input.hasNext()) {
      String record = input.next();
      history_data.add(record);
    }

    input.close();

    //parse the data from the file into  a useful format
    for (String record: history_data) { //loop over the history_data
      //Split the record into 3 arrays, at break point |
      String[] record_parts = record.split("\\|");
      //add each part to game_numbers, successes, guess_counts respectively
      game_numbers.add(record_parts[0]);
      successes.add(record_parts[1]);
      guess_counts.add(record_parts[2]);
    }

    //get the total games
    int number_of_games = history_data.size();

    //total wins will be used for win percentage
    int total_wins = Collections.frequency(successes, "S");
    
    //get a win percentage
    double win_percentage;
    if (total_wins > 0) {
      win_percentage = Math.round(100*total_wins/number_of_games);
    } else { //deal with the 0 wins situation
      win_percentage = 0;
    }

    //Current win streak
    int current_streak = 0;
    for (int i = 0; i < number_of_games; i++) {
      //The newest data is at the end of the file, so read through the list backwards to see the current streak
      if (successes.get(number_of_games - i - 1).equals("S")) {
        current_streak++;
      } else {
        break;
      }
    }

    //longest winning streak calc:
    int longest_streak = 0;
    int count = 0;
    for (String data: successes) {
      if (data.equals("S")) {
        count++;
        if (count > longest_streak) {
          longest_streak = count;
        }
      } else {
        count = 0;
      }
    }
    
    //output data
    System.out.print(String.format("===History and Summary Statistics===\n   Total Games played: %d\n   Win percentage:     %.0f%%\n   Current win streak: %d Games\n   Longest win streak: %d Games\n   Guess Distribution:\n", number_of_games, win_percentage, current_streak, longest_streak));


    //get max guesses of 1 type
    int max_guesses = 0;
    int current_count = 0;
    for (int i = 0; i < number_of_games; i++) {
      String i_str = Integer.toString(i);
      current_count = Collections.frequency(guess_counts, i_str);
      if (current_count > max_guesses) {
        max_guesses = current_count;
      }
    }


    //Histogram of guess distribution
    StringBuilder x_labels = new StringBuilder();
    for (int i = 0; i < max_guesses; i++) {
      x_labels.append(String.format(" %d ", i));
    }
    //output the histogram data:
    System.out.println(String.format("       %s", x_labels.toString()));
    for (int i = 1; i < 7; i++) {
      StringBuilder current_bar = new StringBuilder();
      for (String counts: guess_counts) {
         if (Integer.parseInt(counts) == i) {
           current_bar.append("\033[30;104m   \033[0m");
         }
      }
      System.out.println(String.format("    %d-|%s", i, current_bar.toString()));
    }
  }

  //Save function to save the guesses of the current game to a file
  public void save(String filename) throws IOException {
    PrintWriter out = new PrintWriter(filename);
    for (String guess: guesses) {
      out.println(guess);
    }
    out.close();
  }

  //save history adds the current games data to the data history stored in history.txt
  public void saveHistory(String filename) throws IOException {
    File file = new File(filename);
    if (!file.exists()) {
      file.createNewFile();
    } 
    FileWriter file_writer = new FileWriter(file, true);
    BufferedWriter out = new BufferedWriter(file_writer);
    String successful = "F";
    if (success) {
      successful = "S";
    }
    //My file format is game_number|whether the player won (S or F)|the total guesses used in this game
    out.write(String.format("%d|%s|%d\n", gameNumber, successful, guesses.size()));
    out.close();
  }
}