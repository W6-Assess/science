package parsers;

import edu.stanford.nlp.trees.TypedDependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class LocationParser {
    
    private Collection<TypedDependency> listOfDependencies;
    private DependencyTree dependencyTree;
    
    public LocationParser(Collection<TypedDependency> listOfDependencies, DependencyTree dependencyTree)
    {
        this.listOfDependencies = listOfDependencies;
        this.dependencyTree = dependencyTree;
    }
    
    public  List<String> getLoationOfViolence(List<String> violenceVerbs) {
        List<String> listOfLocations = new ArrayList<>();
        
        dependencyTree.getCollectionsFromWordsByTag(violenceVerbs, "nmod:in").forEach((collection) -> {
                                        listOfLocations.add(collection.getCollectionAsString());
                                    });
        
        
        return listOfLocations;
    }
    
    public  List<String> getLocation() {
        List<String> listOfLocations = new ArrayList<>();
        
        dependencyTree.getCollectionsByTag("nmod:in").forEach((collection) -> {
                                        listOfLocations.add(collection.getCollectionAsString());
                                    });
        
        
        return listOfLocations;
    }
    
    
}