package parsers;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

public class DependencyDateTimeParser {
    private static LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    private static TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");

    private static List<String> getDateTimeFromText(String text) {
        Tree tree = lp.apply(
                tokenizerFactory.getTokenizer(new StringReader(text))
                        .tokenize()
        );

        TreebankLanguagePack treeLanguagePack = new PennTreebankLanguagePack();
        GrammaticalStructureFactory factoryForGrammaticalStructure = treeLanguagePack.grammaticalStructureFactory();
        GrammaticalStructure grammaticalStructureOfSentence = factoryForGrammaticalStructure.newGrammaticalStructure(tree);
        Collection<TypedDependency> listOfDependencies = grammaticalStructureOfSentence.typedDependenciesCollapsed();

        DependencyTree dependencyTree = new DependencyTree(listOfDependencies);
        DateTimeParser dateTimeParser = new DateTimeParser(dependencyTree);
        return dateTimeParser.parseDateAndTime();
    }
}
