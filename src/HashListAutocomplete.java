import java.util.*;

public class HashListAutocomplete implements Autocompletor {    

    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap = new HashMap<>();
    private int mySize;

    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
            throw new NullPointerException("invalid args");
        } 
        
        if (terms.length != weights.length) {
            throw new IllegalArgumentException("Term array and weight array are different sizes");
        }

        initialize(terms, weights);

    }


    @Override
    public List<Term> topMatches(String prefix, int k) {
        if (k == 0) {
            return new LinkedList<>();
        }

        if (prefix.length() > MAX_PREFIX) {
            prefix = prefix.substring(0, MAX_PREFIX);

        }

        List<Term> all = myMap.get(prefix);

        if (all == null){
            return new ArrayList<>();
        }

        List<Term> list = all.subList(0, Math.min(k, all.size()));
        
        return list;
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        myMap = new HashMap<>();
        mySize = 0;

        for (int k =0; k<terms.length; k++) {
            Term t = new Term(terms[k], weights[k]);
            mySize += BYTES_PER_CHAR * t.getWord().length();
            mySize += BYTES_PER_DOUBLE;

            for(int i = 0; i <= Math.min(MAX_PREFIX, terms[k].length()); i++){
                
                String prefix = terms[k].substring(0, i);
                
                if(!myMap.containsKey(prefix)){
                    myMap.put(prefix, new ArrayList<>());
                    mySize += prefix.length() * BYTES_PER_CHAR;
                }
            
                myMap.get(prefix).add(t);
            }
        }

        for (String key : myMap.keySet()) {
            List<Term> list = myMap.get(key);
            Collections.sort(list, Comparator.comparing(Term::getWeight).reversed());
        }
    }

    @Override
    public int sizeInBytes() {
        return mySize;
    }
    
}

