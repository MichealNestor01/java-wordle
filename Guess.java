//Author: Micheal Nestor
//Email: sc21mpn@leeds.ac.uk
//Date completed: 03/04/2022

package comp1721.cwk1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



public class Guess {
  private static final Scanner INPUT = new Scanner(System.in);
  private String chosenWord;
  private int guessNumber;

  public Guess(int num) {
    //If guess is not valid throw a game exception
    if (num < 1 || num > 6) {
      throw new GameException("Guess expects a number between 1 and 6 as a parameter");
    }

    //initialise guess number
    guessNumber = num;
    
    //initialise the chosen word using readFromPlayer()
    readFromPlayer();
  }

  public Guess(int num, String word) {
    //If guess is not valid throw a game exception 
    if (num < 1 || num > 6) {
      throw new GameException("Guess expects a number between 1 and 6 as a parameter");
    }
    
    //Validate word length and alphabetic characters
    if (word.length() != 5) {
      throw new GameException("Guess expects a word of length 5");
    } else if (!(word.matches("^[a-zA-Z]*$") && word != null)) {
      throw new GameException("Guess expects alphabetic characters");
    }

    //initialise chosen word in upper case and guess number
    chosenWord = word.toUpperCase();
    guessNumber = num;
  }

  //getter function for guessNumber
  public int getGuessNumber() {
    return guessNumber;
  }

  //getter function for chosenWord
  public String getChosenWord() {
    return chosenWord;
  }

  //Read a guess from the user 
  public void readFromPlayer() {
    //used to store the players word
    String candidate;
    //run this loop till player enters a valid word:
    while (true) {
      System.out.printf("Enter a guess (%d/6): ", guessNumber);
      candidate = INPUT.next();
      if ((candidate.matches("^[a-zA-Z]*$") && candidate != null && candidate.length() == 5)) {
        break;
      }
      System.out.println("That guess is not valid guess: alphabetic string of length 5");
    }
    chosenWord = candidate.toUpperCase();
  }

  //checks if the guess logically matches a given string 
  public Boolean matches(String target) {
    return target.toUpperCase().equals(chosenWord);
  } 

  //this compares the guess with a given target string, using the wordle words
  public String compareWith(String target, boolean accessible) {
    //return message is the list of chars that will be returned as a string
    ArrayList<String> returnMessageChars = new ArrayList<String>();
    for (int index = 0; index < 5; index++) {
      returnMessageChars.add("");
    }
    //targetChars is the target string turned into characters
    ArrayList<String> targetChars = new ArrayList<String>(Arrays.asList(target.toUpperCase().split("")));
    
    //chosenChars is the chosenWord string turned into characters
    ArrayList<String> chosenChars = new ArrayList<String>(Arrays.asList(chosenWord.split("")));
    
    //guess objects are a list of guess objects representing correct or partial correct char placements
    ArrayList<guessCharObject> charObjects = new ArrayList<guessCharObject>();

    //Check if any possitions match:
    for (int index = 0; index < 5; index++) {
      //get the guess character at index i
      String currentChosenChar = chosenChars.get(index);

      //check if current char already has a charObject object in the array:
      Boolean charObjectExists = false;
      int charObjectIndex = -1;
      for (int i = 0; i < charObjects.size(); i++) {
        if (charObjects.get(i).value.matches(currentChosenChar)) {
          charObjectExists = true;
          charObjectIndex = i;
          break;
        }
      }

      //find total occurences of current char in target word:
      int currentCharOccurences = 0;
      if (!charObjectExists) {
        for (String c: targetChars) {
          if (c.matches(currentChosenChar)) {
            currentCharOccurences++;
          } 
        }
      }

      //check where it should go
      if (currentChosenChar.equals(targetChars.get(index))) {
        //the guess's character at index matches the target's character at index 
        if (charObjectExists) {
          charObjects.get(charObjectIndex).addCorrectIndex(index);
        } else {
          charObjects.add(new guessCharObject(currentChosenChar, index, currentCharOccurences, true));
        }
      } else if (targetChars.contains(currentChosenChar)) {
        //the guess's character at index exists in the target's character set
        if (charObjectExists) {
          charObjects.get(charObjectIndex).addWrongIndex(index);
        } else {
          charObjects.add(new guessCharObject(currentChosenChar, index, currentCharOccurences, false));
        }
      } else {
        //the guess's character at index does not exist in the target's character set
        returnMessageChars.set(index, String.format("\033[30;107m %s \033[0m", currentChosenChar));
      }
    }

    //initialise return string:
    String returnMessage;

    if (accessible) { //CREATE ACCESSIBLE RETURN MESSAGE
      ArrayList<Integer> correctIndexes = new ArrayList<Integer>();
      ArrayList<Integer> wrongIndexes = new ArrayList<Integer>();
      //see how many letters in the chosen word are in the correct place and in the wrong place such that they would be coloured yellow
      int count;
      for (guessCharObject c: charObjects) {
        count = 0;
        int occurences = c.occurencesInTarget;
        for (int index: c.correctIndexes) { 
          //These would be coloured green
          correctIndexes.add(index);
          count++;
        }
        for (int index: c.wrongIndexes) {
          if (occurences - count > 0) {
            //these would be coloured yellow
            wrongIndexes.add(index);
          }
        }
      }
      if (wrongIndexes.size() + correctIndexes.size() == 0) {
        returnMessage = "No letters in common with word";
      } else {        
        //CREATE THE ACCESSIBLE STRING
        StringBuilder messageBuffer = new StringBuilder();
        String curr_char;
        for (int i = 0; i < wrongIndexes.size(); i++) {
          //All this logic is just to create the correct format
          if (wrongIndexes.get(i) == 0) {
            messageBuffer.append("1st");
          } else if (wrongIndexes.get(i) == 1) {
            messageBuffer.append("2nd");
          } else if (wrongIndexes.get(i) == 2) {
            messageBuffer.append("3rd");
          } else {
            messageBuffer.append(String.format("%dth", wrongIndexes.get(i) + 1));
          }
          if (i == wrongIndexes.size() - 2) {
            messageBuffer.append(" and ");
          } else if (i != wrongIndexes.size()-1) {
            messageBuffer.append(", ");
          }
        }
        if (wrongIndexes.size() > 0) {
          messageBuffer.append(" correct but in the wrong place, ");
        }
        for (int i = 0; i < correctIndexes.size(); i++) {
          if (correctIndexes.get(i) == 0) {
            messageBuffer.append("1st");
          } else if (correctIndexes.get(i) == 1) {
            messageBuffer.append("2nd");
          } else if (correctIndexes.get(i) == 2) {
            messageBuffer.append("3rd");
          } else {
            messageBuffer.append(String.format("%dth", correctIndexes.get(i) + 1));
          }
          if (i == correctIndexes.size() - 2) {
            messageBuffer.append(" and ");
          } else if (i != correctIndexes.size()-1) {
            messageBuffer.append(", ");
          }
        }
        if (correctIndexes.size() > 0) {
          messageBuffer.append(" perfect");
        }
        returnMessage = messageBuffer.toString();
      }
    } else { //RETURN COLOURFUL RETURN MESSAGE
      //now colour the return statement using the corrct colours (for letters not existing in the target's character set)
      for (guessCharObject c: charObjects) {
        //get these values now to improve efficiency
        int correctIndexesLength = c.correctIndexes.size();
        int wrongIndexesLength = c.wrongIndexes.size();
        int occurencesInTarget = c.occurencesInTarget;
        int totalIndexes = c.occurences;
        //now to loop over all occurences of the current charObjects's char
        for (int i = 0; i < c.occurences; i++) {
          if (i < occurencesInTarget) {
            //if the letter appears in the target, check if it is in the right place
            if (i < correctIndexesLength) {
              //it is in the right place
              returnMessageChars.set(c.correctIndexes.get(i), String.format("\033[30;102m %s \033[0m", c.value));
            } else if (i - correctIndexesLength < wrongIndexesLength) {
              //it is in the wrong place
              returnMessageChars.set(c.wrongIndexes.get(i-correctIndexesLength), String.format("\033[30;103m %s \033[0m", c.value));
            }
          } else {
            //it is in the wrong place at it does not appear in the target
            returnMessageChars.set(c.wrongIndexes.get(i-correctIndexesLength), String.format("\033[30;107m %s \033[0m", c.value));
          }
        }
      }
      //join the array back into a single string
      StringBuilder messageBuffer = new StringBuilder();
      for (int i = 0; i < returnMessageChars.size(); i++) {
        messageBuffer.append(returnMessageChars.get(i));
      }

      returnMessage = messageBuffer.toString();
    }

    return returnMessage;
  }
}

//GuessCharObjects are used in my implementation of the compares with function
class guessCharObject {
  //initialise class attributes
  public String value;
  //this will store the indexes in the guess where value is in the wrong place
  public ArrayList<Integer> wrongIndexes = new ArrayList<Integer>();
  //this will store the indexes in the guess where the value is in the wright place
  public ArrayList<Integer> correctIndexes = new ArrayList<Integer>();
  public int occurencesInTarget;
  //this refers to occurences in guess
  public int occurences = 1;

  //constructor
  public guessCharObject(String val, int index, int nOccurences, boolean correct) {
    value = val;

    if (correct) {
      correctIndexes.add(index);
    } else {
      wrongIndexes.add(index);
    }

    occurencesInTarget = nOccurences;
  }

  //setter for correctIndexes
  public void addCorrectIndex(int index) {
    correctIndexes.add(index);
    occurences++;
  }

  //setter for wrongIndexes
  public void addWrongIndex(int index) {
    wrongIndexes.add(index);
    occurences++;
  }
}
