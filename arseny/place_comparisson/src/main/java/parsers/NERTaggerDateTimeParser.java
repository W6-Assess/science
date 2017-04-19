package parsers;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static parsers.NERTaggerLocationParser.storeResultsToFile;

public class NERTaggerDateTimeParser {
    private static List<String> getDateTimeFromSentence(Sentence sentence) {
        List<String> nerTags = sentence.nerTags();
        List<String> words = sentence.words();
        return IntStream.range(0, nerTags.size())
                .boxed()
                .filter(index -> nerTags.get(index).equals("DATE")
                        || nerTags.get(index).equals("TIME"))
                .map(words::get)
                .collect(Collectors.toList());

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
                        resultList.addAll(getDateTimeFromSentence(sentence));
                    }
                    locationMap.put(i, resultList);
                }
            }
            storeResultsToFile(locationResultPath, locationMap);
        }
    }
}
