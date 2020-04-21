package parsers;

import java.util.ArrayList;
import java.util.List;


public class LocationParser {

    private DependencyTree dependencyTree;
    
    public LocationParser(DependencyTree dependencyTree)
    {
        this.dependencyTree = dependencyTree;
    }
    
    public  List<String> getLocation() {
        List<String> listOfLocations = new ArrayList<>();
        
        dependencyTree.getCollectionsByTag("nmod:in").forEach((collection) -> {
                                        listOfLocations.add(collection.getCollectionAsString());
                                    });
        
        
        return listOfLocations;
    }
    
    
}