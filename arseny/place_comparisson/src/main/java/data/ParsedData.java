package data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParsedData {
    private Map<String, Set<String>> location;
    private Map<String, Set<String>> time;
    private Map<String, Set<String>> verb;
    private Map<String, Set<String>> weapons;

    public ParsedData() {
        location = new HashMap<>();
        time = new HashMap<>();
        verb = new HashMap<>();
        weapons = new HashMap<>();
    }

    public Map<String, Set<String>> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Set<String>> location) {
        this.location = location;
    }

    public Map<String, Set<String>> getTime() {
        return time;
    }

    public void setTime(Map<String, Set<String>> time) {
        this.time = time;
    }

    public Map<String, Set<String>> getVerb() {
        return verb;
    }

    public void setVerb(Map<String, Set<String>> verb) {
        this.verb = verb;
    }

    public Map<String, Set<String>> getWeapons() {
        return weapons;
    }

    public void setWeapons(Map<String, Set<String>> weapons) {
        this.weapons = weapons;
    }
}
