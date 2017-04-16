package parsers;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NERTaggerLocationParser {
    private static List<String> getLocationsFromSentence(Sentence sentence) {
        List<String> nerTags = sentence.nerTags();
        List<String> words = sentence.words();
        return IntStream.range(0, nerTags.size())
                .boxed()
                .filter(index -> nerTags.get(index).equals("LOCATION"))
                .map(words::get)
                .collect(Collectors.toList());

    }

    private static void storeResultsToFile(String filename, Map<Integer, List<String>> locationMap) {
        try {
            PrintWriter printWriter = new PrintWriter(filename);
            printWriter.println(locationMap.size());
            locationMap.entrySet().forEach(
                entry -> {
                    printWriter.println(entry.getKey());
                    entry.getValue().forEach(printWriter::println);
                }
            );
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        if (args.length == 3) {
            String path = args[0];
            Integer dataSetSize = Integer.valueOf(args[1]);
            String locationResultPath = args[2];
            MyFileReader myFileReader = new MyFileReader();
            Map<Integer, List<String>> locationMap = new HashMap<>();
            for (int i = 0; i < dataSetSize; i++) {
                String file = myFileReader.getFile(path + i + ".txt");
                if (!file.isEmpty()) {
                    Document document = new Document(file);
                    List<String> resultList = new ArrayList<>();
                    for (Sentence sentence : document.sentences()) {
                        resultList.addAll(getLocationsFromSentence(sentence));
                    }
                    locationMap.put(i, resultList);
                }
            }
            storeResultsToFile(locationResultPath, locationMap);
        }
    }


}
