package parsers;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.*;

import java.io.StringReader;
import java.sql.Time;
import java.util.*;

import static parsers.NERTaggerLocationParser.storeResultsToFile;

public class DependencyLocationParser {
    private static LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    private static TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");

    private static List<String> getLocationFromText(String text) {
        Tree tree = lp.apply(
                tokenizerFactory.getTokenizer(new StringReader(text))
                        .tokenize()
        );

        TreebankLanguagePack treeLanguagePack = new PennTreebankLanguagePack();
        GrammaticalStructureFactory factoryForGrammaticalStructure = treeLanguagePack.grammaticalStructureFactory();
        GrammaticalStructure grammaticalStructureOfSentence = factoryForGrammaticalStructure.newGrammaticalStructure(tree);
        Collection<TypedDependency> listOfDependencies = grammaticalStructureOfSentence.typedDependenciesCollapsed();

        DependencyTree dependencyTree = new DependencyTree(listOfDependencies);
        LocationParser locationParser = new LocationParser(listOfDependencies, dependencyTree);
        return locationParser.getLocation();
    }

    public static void main(String[] args) {
        if (args.length == 3) {
            String path = args[0];
            Integer dataSetSize = Integer.valueOf(args[1]);
            String locationResultPath = args[2];
            MyFileReader myFileReader = new MyFileReader();
            Map<Integer, List<String>> locationMap = new HashMap<>();
            long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < dataSetSize; i++) {
                if (i == 70 | i == 71)
                    continue;
                String file = MyFileReader.getFile(path + i + ".txt");
                if (!file.isEmpty()) {
                    Document document = new Document(file);
                    List<String> resultList = new ArrayList<>();
                    for (Sentence sentence : document.sentences()) {
                        resultList.addAll(getLocationFromText(sentence.text()));
                    }
                    locationMap.put(i, resultList);
                }
                System.out.println(i);
            }
            System.out.println(new Date(currentTimeMillis).toString() + new Date(System.currentTimeMillis()).toString());
            storeResultsToFile(locationResultPath, locationMap);
        }
    }
}
