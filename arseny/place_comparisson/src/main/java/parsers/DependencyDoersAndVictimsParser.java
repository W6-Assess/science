package parsers;

import data.ParsedData;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.*;
import javafx.util.Pair;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static parsers.MyFileReader.getMappedDataSet;
import static parsers.NERTaggerLocationParser.storeResultsToFile;

public class DependencyDoersAndVictimsParser {
    private static LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    private static TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
    private static ViolentVerbsParser violentVerbsParser;

    public DependencyDoersAndVictimsParser() {
        try {
            violentVerbsParser = new ViolentVerbsParser(lp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pair<List<String>, List<String>> getDoersAndVictimsFromText(String text) {
        Tree tree = lp.apply(
                tokenizerFactory.getTokenizer(new StringReader(text))
                        .tokenize()
        );

        TreebankLanguagePack treeLanguagePack = new PennTreebankLanguagePack();
        GrammaticalStructureFactory factoryForGrammaticalStructure = treeLanguagePack.grammaticalStructureFactory();
        GrammaticalStructure grammaticalStructureOfSentence = factoryForGrammaticalStructure.newGrammaticalStructure(tree);
        Collection<TypedDependency> listOfDependencies = grammaticalStructureOfSentence.typedDependenciesCollapsed();

        DependencyTree dependencyTree = new DependencyTree(listOfDependencies);
        GetDoerAndVictim getDoerAndVictim = new GetDoerAndVictim(dependencyTree);
        return new Pair<>(getDoerAndVictim.getObjectsOfViolence(violentVerbsParser.getAllViolentVerbs(tree)),
                getDoerAndVictim.getSubjectsOfViolence(violentVerbsParser.getAllViolentVerbs(tree)));
    }

    public static void main(String[] args) {
        DependencyDoersAndVictimsParser dependencyDoersAndVictimsParser = new DependencyDoersAndVictimsParser();
        if (args.length == 3) {
            String path = args[0];
            String doersResultPath = args[1];
            String victimsResultPath = args[2];
            Map<Integer, List<String>> doersMap = new HashMap<>();
            Map<Integer, List<String>> victimsMap = new HashMap<>();
            ParsedData mappedDataSet = getMappedDataSet("/home/arseny/metrics.txt");
            for (String i : mappedDataSet.getVictims().keySet()) {
                System.out.println(i);
                String file = MyFileReader.getFile(path + i + ".txt");
                if (!file.isEmpty() && !i.equals("99")) {
                    Document document = new Document(file);
                    List<String> doersList = new ArrayList<>();
                    List<String> victimsList = new ArrayList<>();

                    for (Sentence sentence : document.sentences()) {
                        Pair<List<String>, List<String>> doersAndVictims =
                                dependencyDoersAndVictimsParser.getDoersAndVictimsFromText(sentence.text());
                        doersList.addAll(doersAndVictims.getKey());
                        victimsList.addAll(doersAndVictims.getValue());
                    }

                    doersMap.put(Integer.parseInt(i), doersList);
                    victimsMap.put(Integer.parseInt(i), victimsList);
                }
            }
            storeResultsToFile(doersResultPath, doersMap);
            storeResultsToFile(victimsResultPath, victimsMap);
        }
    }
}
