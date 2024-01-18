import java.io.*;
import java.util.*;

public class Main {

    static boolean[][] sparse;
    static int base = 139;
    static int mod = (int) 1e9+7;


    //generate needle hash
    public static long generate(String needle){
        long ans = 0;
        for(int i = 1; i <= needle.length(); ++i) {
            ans = (ans*base + needle.charAt(i-1)) % mod;
        }

        return ans;
    }

    //query sparse table
    public static boolean query(int L, int R){
        L = Math.max(L, 0); R = Math.min(R, sparse.length-5); //ensuring queries dont go out of bounds

	    int j = log2(R - L + 1);
	    return (sparse[L][j] || sparse[R - (1 << j) + 1][j]);
	}

    public static int log2(int n) {
		return (int)(Math.log(n)/Math.log(2));
	}

    //driver code
    public static void main(String[] args) {
        PrintWriter out = new PrintWriter(System.out);

        try (BufferedReader br = new BufferedReader(new FileReader("text.txt"))) {

            StringBuilder haystack = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                haystack.append(line);
            }

            StringBuilder original = haystack; 
            haystack = new StringBuilder(haystack.toString().toLowerCase());


            //haystack hash + pow array
            long[] hash = new long[haystack.length()+1];
            long[] pow = new long[haystack.length()+1];
            pow[0] = 1;
            
            for(int i = 1; i <= haystack.length(); ++i) {
                hash[i] = (hash[i-1]*base + haystack.charAt(i-1)) % mod;
                pow[i] = pow[i-1]*base % mod;
            }


            //define variables
            String[] keywords = {"rules", "laws", "democracy", "self-determination", "institution", "power", "authority", "system", "rez", "decision", "vote", "influence", "resolution", "reconciliation", "think", "reciprocity", "advocacy", "leadership", "chang", "volunteering", "inclusiveness", "equity", "imagin", "empathy", "respect", "rights", "responsibility", "freedom", "justice", "fairness", "truth", "citizenship", "collaboration", "cooperation", "know", "connected", "belief", "perspective", "community", "relation", " story", "stories", "culture"};
            String character = "hazel"; //hashes of all instances of character we're connecting keyword to
            long characterHash = generate(character);
            long myHash = generate("my");
            long meHash = generate("me");
            long weHash = generate("we");
            long iHash = generate("i");
            sparse = new boolean[haystack.length()+5][log2(haystack.length())+1];
            long score = 0;

            //find occurences of character
            HashSet<Integer> occurences = new HashSet<>();
            for(int l = 1, r = character.length(); r <= haystack.length(); ++l, ++r){
                if(
                    (hash[r] - hash[l-1]*pow[r-l+1] % mod + mod) % mod == characterHash ||
                    (hash[r] - hash[l-1]*pow[r-l+1] % mod + mod) % mod == myHash ||
                    (hash[r] - hash[l-1]*pow[r-l+1] % mod + mod) % mod == meHash ||
                    (hash[r] - hash[l-1]*pow[r-l+1] % mod + mod) % mod == iHash ||
                    (hash[r] - hash[l-1]*pow[r-l+1] % mod + mod) % mod == weHash
                ){
                    occurences.add(l);
                } 
            }

            //build sparse table
            for(int i = 1; i < haystack.length(); ++i){
                sparse[i][0] = occurences.contains(i);
            } 
            for(int j = 1; j <= log2(haystack.length()); ++j) {
                for(int i = 1; i + (1 << j) - 1 <= haystack.length(); i++) {
                    sparse[i][j] = (sparse[i][j - 1] || sparse[i + (1 << (j - 1))][j - 1]);
                }
            }


            System.out.println("THE FOLLOWING ARE COUNTING THE OCCURENCES OF KEYWORDS FOR INDIGENOUS LITERARY THEORY IN THE GIVEN TEXT: ");
            System.out.println("------------------------------------------------------------------------------------------------------");
            ArrayList<Integer> examples = new ArrayList<>();
            //perform queries
            for(int i = 0; i < keywords.length; ++i){
                String needle = keywords[i]; 
                long needleHash = generate(needle); //generates needle hash
                int range = 1000; //accepted number of characters the character name has to be within to 

                //find occurences of keyword (needle)
                int ans = 0;
                for(int l = 1, r = needle.length(); r <= haystack.length(); ++l, ++r){
                    if(
                    ((hash[r] - hash[l-1]*pow[r-l+1] % mod + mod) % mod == needleHash) && 
                    (query(l-range, l) || query(l, l+range))){ //if keyword is related to character
                        ++ans;
                        examples.add(l);
                    }
                }

                System.out.println("occurences of " + needle + ": " + ans);
                score += ans;


                if(i == 8){
                    out.println("final structures score of " +  character + ": " + (double)score/haystack.length() / ((double)9/keywords.length));
                    score = 0;
                }
                if(i == 8+11){
                    out.println("final active participation score of " +  character + ": " + (double)score/haystack.length() / ((double)11/keywords.length));
                    score = 0;
                }
                if(i == 8+11+15){
                    out.println("final attributes score of " +  character + ": " + (double)score/haystack.length() / ((double)15/keywords.length));
                    score = 0;
                }
                if(i == 8+11+15){
                    out.println("final identity score of " +  character + ": " + (double)score/haystack.length() / ((double)8/keywords.length));
                    score = 0;
                }
            }

            //print remaining
            out.flush();
            System.out.println("------------------------------------------------------------------------------------------------------");
            System.out.println("EXAMPLES OF USAGES OF KEYWORDS: ");
            System.out.println("------------------------------------------------------------------------------------------------------");
            for(int j = 0; j < examples.size(); ++j){ //accidentally use j instead of i on the outside vs inside cause i did as a foreach at first and realized i need index oops
                int next = examples.get(j);

                int l = 0;
                for(int i = next; i >= 0; --i){
                    l = i;
                    if(haystack.charAt(i) == '.') break; 
                }
                l += 2;

                int r = 0;
                for(int i = next; i < haystack.length(); ++i){
                    r = i;
                    if(haystack.charAt(i) == '.') break;
                }

                System.out.println("Example " + (j+1) + ": " + original.substring(l, r));
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}