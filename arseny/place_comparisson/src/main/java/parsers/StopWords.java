package parsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StopWords {
    private final String filename = "/home/arseny/stop_words.txt";
    private Set<String> stopWords;
    public StopWords() {
        stopWords = new HashSet<>();
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);
            Integer number = Integer.parseInt(bufferedReader.readLine());
            while (number > 0) {
                stopWords.add(bufferedReader.readLine());
                number--;
            }
            bufferedReader.close();
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getStopWords() {
        return stopWords;
    }

    public void setStopWords(Set<String> stopWords) {
        this.stopWords = stopWords;
    }
}
