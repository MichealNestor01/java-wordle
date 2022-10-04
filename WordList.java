//Author: Micheal Nestor
//Email: sc21mpn@leeds.ac.uk
//Date completed: 03/04/2022

package comp1721.cwk1;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Paths;

public class WordList {
  private List<String> words = new ArrayList<String>();  

  //constructor
  public WordList(String filename) throws IOException {
    //read in all of the words from the given file
    Scanner input = new Scanner(Paths.get(filename));

    while(input.hasNext()) {
      String word = input.next();
      words.add(word);
    }

    input.close();
  }

  //simple getter for size
  public int size() {
    return words.size();
  }

  //simple getter for the word at index n
  public String getWord(int n) {
    if (n >= words.size() || n < 0) {
      throw new GameException("Game number is out of range");
    }
    return words.get(n);
  }
}
