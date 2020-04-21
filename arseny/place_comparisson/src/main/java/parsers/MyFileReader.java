package parsers;

import data.ParsedData;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.Levenshtein;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class MyFileReader {
    static String getFile(String fileName) {

        StringBuilder result = new StringBuilder("");
        String line;
        try {
            FileReader fileReader =
                    new FileReader(fileName);

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return result.toString();

    }

    public static ParsedData getMappedDataSet(String filename) {
        ParsedData parsedData = new ParsedData();
        Integer numberOfResults = null;
        try {
            FileReader fileReader =
                    new FileReader(filename);

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            numberOfResults = Integer.parseInt(bufferedReader.readLine());

            while (numberOfResults > 0) {
                String[] numbers = bufferedReader.readLine().split("\\s+");
                String file = numbers[0];
                //System.out.println(file);
                parsedData.getLocation().put(file, getData(Integer.valueOf(numbers[1]), bufferedReader));
                parsedData.getTime().put(file, getData(Integer.valueOf(numbers[2]), bufferedReader));
                parsedData.getVerb().put(file, getData(Integer.valueOf(numbers[3]), bufferedReader));
                parsedData.getWeapons().put(file, getData(Integer.valueOf(numbers[4]), bufferedReader));
                parsedData.getDoers().put(file, getData(Integer.valueOf(numbers[5]), bufferedReader));
                parsedData.getVictims().put(file, getData(Integer.valueOf(numbers[6]), bufferedReader));
                numberOfResults--;
            }
            bufferedReader.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return parsedData;
    }

    private static Set<String> getData(Integer number, BufferedReader bufferedReader) throws IOException {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < number; i++) {
            result.addAll(
                    Arrays.asList(
                            bufferedReader.readLine().split("\\s+")
                    )
            );
        }
        return result;
    }

    private static Map<String, Set<String>> getResultsFromAlgorithm(String filename) {
        Map<String, Set<String>> resultMap = new HashMap<>();
        try {
            FileReader fileReader =
                    new FileReader(filename);

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);
            Integer numberOfResults = Integer.parseInt(bufferedReader.readLine());

            while (numberOfResults > 0) {
                String name = bufferedReader.readLine();
                Integer numberOfLines = Integer.parseInt(bufferedReader.readLine().split("\\s+")[0]);
                Set<String> values = getData(numberOfLines, bufferedReader);
                resultMap.put(name, values);
                numberOfResults--;
            }
            bufferedReader.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return resultMap;
    }

    private static Double getMetrics(Map<String, Set<String>> correct, Map<String, Set<String>> result) {
        Double metric = 0D;
        Integer numberOfNotNullDocs = 0;
        for (String filename : correct.keySet()) {
            Integer allResults = correct.get(filename).size();
            if (allResults > 0) {
                numberOfNotNullDocs++;
                Set<String> correctSet = new HashSet<>(correct.get(filename));
                Set<String> resultSet = new HashSet<>(result.get(filename));
                correctSet.removeAll(resultSet);
                System.out.println(correctSet.toString());
                Integer diff = correctSet.size();
                metric += (allResults - diff) / allResults;
            }
        }
        return 1 - metric / numberOfNotNullDocs;
    }

    private static Double getLevenshteinDistance(Map<String, Set<String>> correct, Map<String, Set<String>> result) {
        Double metric = 0D;
        Integer numberOfNotNullDocs = 0;
        for (String filename : correct.keySet()) {
            Integer allResults = correct.get(filename).size();
            if (allResults > 0 && !filename.equals("99")) {
                numberOfNotNullDocs++;
                int distance = FuzzySearch.tokenSetRatio(correct.get(filename).toString(),
                        result.get(filename).toString());
                metric += distance;
                //System.out.println(distance);
            }
        }
        return metric / (100 * numberOfNotNullDocs);

    }

    private static Double getJaroWinklerDistance(JaroWinkler jaroWinkler, Map<String, Set<String>> correct, Map<String, Set<String>> result) {
        Double metric = 0D;
        Integer numberOfNotNullDocs = 0;
        for (String filename : correct.keySet()) {
            Integer allResults = correct.get(filename).size();
            if (allResults > 0 && !filename.equals("99")) {
                numberOfNotNullDocs++;
                Double distance = jaroWinkler.similarity(correct.get(filename).toString(), result.get(filename).toString());
                metric += distance;
            }
        }
        return metric / (numberOfNotNullDocs);
    }

    private static Map<String, Set<String>> lemmatizeData(Map<String, Set<String>> nonLemmatized,
                                                          WordLemmatization wordLemmatization,
                                                          StopWords stopWords) {
        Map<String, Set<String>> result = new HashMap<>();
        for (String key : nonLemmatized.keySet()) {
            Set<String> lemmatizedWords = new HashSet<>(
                    wordLemmatization.lemmatize(fromSetToString(nonLemmatized.get(key)))
            );
            lemmatizedWords.removeAll(stopWords.getStopWords());
            result.put(key, lemmatizedWords);
        }
        return result;
    }

    private static String fromSetToString(Set<String> set) {
        StringBuilder stringBuilder = new StringBuilder("");
        set.forEach(str -> stringBuilder.append(str.toLowerCase()).append(" "));
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        WordLemmatization wordLemmatization = new WordLemmatization();
        StopWords stopWords = new StopWords();
        ParsedData mappedDataSet = getMappedDataSet("/home/arseny/metrics.txt");
        Map<String, Set<String>> mappedTimeMap = lemmatizeData(mappedDataSet.getTime(), wordLemmatization, stopWords);
        Map<String, Set<String>> mappedLocationMap = lemmatizeData(mappedDataSet.getLocation(), wordLemmatization, stopWords);
        Map<String, Set<String>> mappedDoersMap = lemmatizeData(mappedDataSet.getDoers(), wordLemmatization, stopWords);
        Map<String, Set<String>> mappedVictimsMap = lemmatizeData(mappedDataSet.getVictims(), wordLemmatization, stopWords);

        System.out.println("Levenstein distance");

        Map<String, Set<String>> locationsFromNER = lemmatizeData(
                getResultsFromAlgorithm("/home/arseny/ner_loc.txt"),
                wordLemmatization,
                stopWords
        );
        System.out.println("NER LOC");
        System.out.println(getLevenshteinDistance(mappedLocationMap,locationsFromNER));
        Map<String, Set<String>> dateTimeFromNER = lemmatizeData(
                getResultsFromAlgorithm("/home/arseny/ner_dt.txt"),
                wordLemmatization,
                stopWords
        );
        System.out.println("NER DT");
        System.out.println(getLevenshteinDistance(mappedTimeMap, dateTimeFromNER));
        Map<String, Set<String>> locationsFromDependencies = lemmatizeData(
                getResultsFromAlgorithm("/home/arseny/dep_loc.txt"),
                wordLemmatization,
                stopWords
        );
        System.out.println("DEP LOC");
        System.out.println(getLevenshteinDistance(mappedLocationMap, locationsFromDependencies));
        Map<String, Set<String>> dateTimeFromDependencies = lemmatizeData(
                getResultsFromAlgorithm("/home/arseny/dep_dt.txt"),
                wordLemmatization,
                stopWords
        );
        System.out.println("DEP DT");
        System.out.println(getLevenshteinDistance(mappedTimeMap, dateTimeFromDependencies));
        Map<String, Set<String>> doersFromDependencies = lemmatizeData(
                getResultsFromAlgorithm("/home/arseny/dep_do.txt"),
                wordLemmatization,
                stopWords
        );
        System.out.println("DEP DOERS");
        System.out.println(getLevenshteinDistance(mappedDoersMap, doersFromDependencies));
        Map<String, Set<String>> victimsFromDependencies = lemmatizeData(
                getResultsFromAlgorithm("/home/arseny/dep_vic.txt"),
                wordLemmatization,
                stopWords
        );
        System.out.println("DEP VICTIMS");
        System.out.println(getLevenshteinDistance(mappedVictimsMap, victimsFromDependencies));


        System.out.println();
        System.out.println("Jaro-Winkler distance");
        JaroWinkler jw = new JaroWinkler();

        System.out.println("NER LOC");
        System.out.println(getJaroWinklerDistance(jw, mappedLocationMap,locationsFromNER));

        System.out.println("NER DT");
        System.out.println(getJaroWinklerDistance(jw, mappedTimeMap, dateTimeFromNER));

        System.out.println("DEP LOC");
        System.out.println(getJaroWinklerDistance(jw, mappedLocationMap, locationsFromDependencies));

        System.out.println("DEP DT");
        System.out.println(getJaroWinklerDistance(jw, mappedTimeMap, dateTimeFromDependencies));

        System.out.println("DEP DOERS");
        System.out.println(getJaroWinklerDistance(jw, mappedDoersMap, doersFromDependencies));

        System.out.println("DEP VICTIMS");
        System.out.println(getJaroWinklerDistance(jw, mappedVictimsMap, victimsFromDependencies));

    }

}
