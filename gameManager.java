import java.util.*;
// Said Sheck
// TA: Andrew Wang
// 01.31.2021
// The HangmanManager class is a class
// which manages our game of Evil Hangman
public class gameManager {
    private Set <String> possibleWords;
    private Set<Character> guessedLetters;
    private int guessLeft;
    private String currPattern;


    /* Creates an Instance of our HangmanManager game, for
     * our Evil Hangman Game
     * @param dictionary - a collection of all the words
     * are game will intially consider
     * @param length - the length of word our user will try to find
     * @param max - the maxium amount of times a user can get wrong before the game stops
     * @throws IllegalArgumentException if the length of
     * word we're finding is less than 1 or if maxium amount of times
     * a user can answer wrong is a non positive number
    */
    public gameManager(Collection <String> dictionary, int length, int max) {
        if (length < 1 || max < 0) {
            throw new IllegalArgumentException();
        }

        guessedLetters = new TreeSet<>();
        guessLeft = max;

        currPattern = "-"; //create blank guess
        for (int i = 0 ; i < length - 1; i++) {
            currPattern += " -";  //did this prevent space @ End
        }

        possibleWords = new TreeSet<>(); //They want us to have a TreeSet per Spec
        for (String word : dictionary) { // check through my collection of String for some possibleWords
            if (word.length() == length) {
                possibleWords.add(word); //we add these words to our set of possibleWords(which are String)
            }
        }
    }

    /* The following gets the possibleWords our
     * program is considering could be the correct word
     * @returns a set of words which can contains the word we're looking at
    */
    public Set<String> words() {
        return possibleWords;

    }

    /* The following method gets the amount of
     * guessess a user has before the game ends
     * @returns the amount of guesses a user has before
     * the game ends
    */
    public int guessesLeft() {
        return guessLeft;
    }

    /* The following gets the guessed letters a user has submitted in the game
     * @returns a Set of Characters which contains all the user's guesses
    */
    public Set<Character> guesses() {
        return guessedLetters;
    }

    /* The following method gets the pattern of correct letters
     * the user has guessed.
     * @returns the pattern
     * which contains the correct letters the user has guessed
    */
    public String pattern() {
        if (possibleWords.isEmpty()) {
             throw new IllegalStateException();
        }
        return currPattern;
    }

    /* The following method spacedString, rearranges a given word
     * by placing a space between each charecter of that said word
     * @Param word - a given word which we'll look to "space out"
     * @returns our given word with spaces between each charecter
    */
    private String spacedWord(String word) {
        String spacedString = "" + word.charAt(0);
        for (int i = 1; i < word.length(); i++) {
            spacedString += " " + word.charAt(i);
        }
        return spacedString;

    }


    /* The following method, creates a pattern which
     * includes our guessed Letter if our given word contains it.
     * In the same position as the word which conatins this given letter
     * @param char guess - charecter which represents a letter the user guessess
     * @Param word - the word in our set of possibleWords which we'll examine
     * @returns a pattern which has  our guessed letter in the
     * same position as a given word which includes this letter
    */
    private String updatePattern(String word, char guess) {
        word = spacedWord(word); // needed a spaced word for method
        String combinedString = "";
        //had a fencepost problem so i removed a step out of loop
        if (currPattern.charAt(0) != '-') {
            combinedString += currPattern.charAt(0);
        } else if (word.charAt(0) == guess) {
            combinedString += word.charAt(0);
        } else {
            combinedString += "-";
        }

        for (int i = 2; i < word.length(); i = i + 2) {
            if (currPattern.charAt(i) != '-') {
                combinedString += " " + currPattern.charAt(i);
            } else if (word.charAt(i) == guess ){
                combinedString += " " + word.charAt(i);
            } else {
                combinedString += " -";
            }
        }
        return combinedString;
    }


    /* The following method findLargeFam, looks for the pattern with the largest family of words
     * @param builtMap - map which includeds all patterns and their family of words
     * @returns a pattern with the largest family of words
    */
    private String findLargeFam(Map <String, Set<String> > builtMap) {
        String largeKey = currPattern; // I set intial largeKey to whatever is currPattern
        for (String pattern : builtMap.keySet()) { // I check with my Keys of patterns
            if (builtMap.get(pattern).size() > builtMap.get(largeKey).size()) {
                largeKey = pattern;
            } else if (builtMap.get(pattern).size() == builtMap.get(largeKey).size()) {
                 if ( pattern.compareTo(largeKey) < 0 ) {
                     largeKey = pattern;
                 }
            }
        }
        return largeKey;
    }



    /* The following counts the occurence of a letter in our current pattern
     * @Param guess - the letter we're looking to examine in our given pattern
     * @returns the number of times the said letter appears in our current pattern
     * of words within our game
    */
    private int charCount(char guess) {
        int charOccur = 0;
        for (int i = 0; i < currPattern.length(); i++) {
            if (currPattern.charAt(i) == guess) {
                charOccur++;
            }
        }

        if (charOccur == 0) {
            guessLeft = guessLeft - 1;
        }

        return charOccur;
    }


    /* The following methods determines a pattern's possible set
     * of words which match this very pattern
     * @Param guess - the letter we're looking to examine in our given pattern
     * @Param mapFamily - a given with one pattern possible family of word pair
     * @returns a collection of patterns and their family of matching words
    */
    private Map <String, Set<String>> mapBuilder(Map <String, Set<String>> mapFamily, char guess) {
        //build out map
        for (String words : possibleWords) { // Traverse through our set of possibleWords
            if (words.indexOf(guess) == -1 ) { // guess not in word
                mapFamily.get(currPattern).add(words); //add to default(currPattern)
            } else {
                String potenialPattern = updatePattern(words, guess);
                if (!mapFamily.containsKey(potenialPattern)) {
                    // if our map doesnt have that pattern as a key then make it
                    mapFamily.put(potenialPattern, new TreeSet<>());
                }
                mapFamily.get(potenialPattern).add(words); // add the word to its set
            }
        }
        return mapFamily;
    }



   /* The following method record determines if a letter exists
    * in our examined word, and if it does the method will return the number
    * of times the guess will appear in our word.
    * Now if the word does not exist then the number of times
    * you can guess a letter will be decremented by one
    * @throws IllegalStateException if we ran out of guesses or our
    * possible set of words is non existent
    * @throws IllegalArgumentException if we have already guessed a letter
    * @Param guess - our letter we guessed
    * @returns the number of times a letter appears in our word
   */
    public int record(char guess) {
        if (guessLeft < 1 || possibleWords.isEmpty()) {
            throw new IllegalStateException();
        } else if (guessedLetters.contains(guess)) {
            throw new IllegalArgumentException();
        }

        guessedLetters.add(guess); // add guess to our set of guessedLetters
        Map <String, Set<String>> mapFamily = new TreeMap<>(); // create a map
        mapFamily.put(currPattern, new TreeSet<>()); //add our default option

        // build out map
        mapFamily = mapBuilder(mapFamily, guess);
        // currPattern is the one with largest family of words
        currPattern = findLargeFam(mapFamily);
        possibleWords = mapFamily.get(currPattern); //change possibleWords
        return charCount(guess);

    }

}
