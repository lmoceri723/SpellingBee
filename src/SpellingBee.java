import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Spelling Bee
 *
 * This program accepts an input of letters. It prints to an output file
 * all English words that can be generated from those letters.
 *
 * For example: if the user inputs the letters "doggo" the program will generate:
 * do
 * dog
 * doggo
 * go
 * god
 * gog
 * gogo
 * goo
 * good
 *
 * It utilizes recursion to generate the strings, mergesort to sort them, and
 * binary search to find them in a dictionary.
 *
 * @author Zach Blick, [ADD YOUR NAME HERE]
 *
 * Written on March 5, 2023 for CS2 @ Menlo School
 *
 * DO NOT MODIFY MAIN OR ANY OF THE METHOD HEADERS.
 */
public class SpellingBee {

    private String letters;
    private ArrayList<String> words;
    public static final int DICTIONARY_SIZE = 143091;
    public static final String[] DICTIONARY = new String[DICTIONARY_SIZE];

    public SpellingBee(String letters) {
        this.letters = letters;
        words = new ArrayList<>();
    }

    // Calls the overloaded generate with the additional parameters
    public void generate() {
        generate("", letters);
    }

    // Generates every permutation of a string
    public void generate(String word, String letters) {
        // Adds the word the last call created
        words.add(word);

        // Base case for if there are no letters left to create words
        if (letters.equals(""))
        {
            return;
        }

        // Iterates through each usable letter
        for (int i = 0; i < letters.length(); i++)
        {
            // Appends the letter at the end of the word
            String newWord = word + letters.charAt(i);
            // Removes the letter from the available letters
            String newLetters = letters.substring(0, i) + letters.substring(i + 1);
            // Calls the function again with the new parameters
            generate(newWord, newLetters);
        }
    }

    // runs merge sort on words and sets words equal to the new sorted ArrayList
    public void sort()
    {
        words = mergeSort(words, 0, words.size() - 1);
    }

    // Uses merge sort to sort the ArrayList
    public ArrayList<String> mergeSort(ArrayList<String> arr, int low, int high)
    {
        // If there is only 1 index between high and low
        if (high - low == 0)
        {
            // returns a new array with only that index
            ArrayList<String> newArr = new ArrayList<>();
            newArr.add(arr.get(low));
            return newArr;
        }
        // Gets the middle point of the array
        int med = (high + low) / 2;
        // Runs merge sort on both halves
        ArrayList<String> arr1 = mergeSort(arr, low, med);
        ArrayList<String> arr2 = mergeSort(arr, med + 1, high);
        // Merges both halves, sorting them
        return merge(arr1, arr2);
    }

    public ArrayList<String> merge(ArrayList<String> arr1, ArrayList<String> arr2) {
        ArrayList<String> merged = new ArrayList<>();
        // Iterates through each element in both ArrayLists
        int i = 0, j = 0;
        while(j < arr1.size() && i < arr2.size()) {
            // Adds whichever String is "lower" alphabetically first
            if (arr1.get(j).compareTo(arr2.get(i)) < 0) {
                merged.add(arr1.get(j));
                j++;
            }
            else {
                merged.add(arr2.get(i));
                i++;
            }
        }
        // Once one ArrayList is empty, adds the remaining elements from both (only 1 will actually have elements)
        while(i < arr2.size()) {
            merged.add(arr2.get(i));
            i++;
        }
        while(j < arr1.size()) {
            merged.add(arr1.get(j));
            j++;
        }

        return merged;
    }

    // Removes duplicates from the sorted list.
    public void removeDuplicates() {
        int i = 0;
        while (i < words.size() - 1) {
            String word = words.get(i);
            if (word.equals(words.get(i + 1)))
                words.remove(i + 1);
            else
                i++;
        }
    }

    public void checkWords()
    {
        // Iterates through each word in words
        for (int i = 0; i < words.size(); i++) {
            // Checks if each word is absent from dictionary
            if (!search(words.get(i), 0, DICTIONARY_SIZE - 1))
            {
                // If so, removes it
                words.remove(i);
                // Decreases i, as the new element at index i will need to be checked also.
                i--;
            }
        }
    }

    // Uses binary search to search for a target string in DICTIONARY
    public boolean search(String target, int low, int high) {
        // Returns false if the
        if (low > high) {
            return false;
        }
        // Gets the index of the middle element
        int med = (high + low) / 2;
        // Checks if the target is at the middle, if so returns true
        if (DICTIONARY[med].equals(target)) {
            return true;
        }
        // If the string is "higher" alphabetically than the middle
        // Runs the method again with only the second half of DICTIONARY
        if (DICTIONARY[med].compareTo(target) < 0) {
            low = med + 1;
        }
        // Otherwise, runs it again with the first half
        else {
            high = med - 1;
        }
        // Runs again with the new parameters
        return search(target, low, high);
    }


    // Prints all valid words to wordList.txt
    public void printWords() throws IOException {
        File wordFile = new File("Resources/wordList.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(wordFile, false));
        for (String word : words) {
            writer.append(word);
            writer.newLine();
        }
        writer.close();
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    public SpellingBee getBee() {
        return this;
    }

    public static void loadDictionary() {
        Scanner s;
        File dictionaryFile = new File("Resources/dictionary.txt");
        try {
            s = new Scanner(dictionaryFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open dictionary file.");
            return;
        }
        int i = 0;
        while(s.hasNextLine()) {
            DICTIONARY[i++] = s.nextLine();
        }
    }

    public static void main(String[] args) {

        // Prompt for letters until given only letters.
        Scanner s = new Scanner(System.in);
        String letters;
        do {
            System.out.print("Enter your letters: ");
            letters = s.nextLine();
        }
        while (!letters.matches("[a-zA-Z]+"));

        // Load the dictionary
        SpellingBee.loadDictionary();

        // Generate and print all valid words from those letters.
        SpellingBee sb = new SpellingBee(letters);
        sb.generate();
        sb.sort();
        sb.removeDuplicates();
        sb.checkWords();
        try {
            sb.printWords();
        } catch (IOException e) {
            System.out.println("Could not write to output file.");
        }
        s.close();
    }
}
