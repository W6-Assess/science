package data;

public class WordOfCollections {
    
    private final String value;
    private final Integer position;
    private final CollectionOfWords collection;
    private final String tag;

    public WordOfCollections(String content, String tag) {
        this.value = content;
        this.tag = tag;
        this.position = 0;
        this.collection = null;
    }
    
    public WordOfCollections(String content,int position, CollectionOfWords collection)
    {
        this.value = content;
        this.position = position;
        this.collection = collection;
        this.tag = "";
    }
    
    public Integer getPosition()
    {
        return position;
    }
    
    public String getValue()
    {
        return value;
    }
    
    @Override
    public boolean equals(Object object)
    {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        
        WordOfCollections word = (WordOfCollections)object;
        
        if(word.value.equals(this.value) && word.position.equals(this.position))
        {
            return true;
        }
        
        return false;
    }
}
