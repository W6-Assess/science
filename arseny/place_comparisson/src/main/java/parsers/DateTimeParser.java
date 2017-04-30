package parsers;

import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.TypedDependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DateTimeParser {

    private DependencyTree dependencyTree;

    public DateTimeParser(DependencyTree dependencyTree)
    {
        this.dependencyTree = dependencyTree;
    }


    public List<String> parseDateAndTime()
    {

        List<String> timeAndDate = new ArrayList<>();

        dependencyTree.getCollectionsByTag("nmod:tmod").forEach((collection) -> {
            timeAndDate.add(collection.getCollectionAsString());
        });

        dependencyTree.getCollectionsByTag("nmod:since").forEach((collection) -> {
            timeAndDate.add(collection.getCollectionAsString());
        });

        dependencyTree.getCollectionsByTag("nmod:on").forEach((collection) -> {
            timeAndDate.add(collection.getCollectionAsString());
        });


        return timeAndDate;
    }
}
