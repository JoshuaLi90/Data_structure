import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CompressedSuffixTrie
{
    SuffixNode root;
    private String text;

    /**
     * For saving the nodes of the tree
     **/
    class SuffixNode {
        String text;
        SuffixNode subNodes[] = new SuffixNode[4];
        boolean terminal;
        int minStartIndex;

        SuffixNode() {
            this.text = "";
            //this.subNodes = new SuffixNode[4];
            this.minStartIndex = -1;
        }

        SuffixNode(String text) {
            this.text = text;
        }

        SuffixNode newChild(int i) {
            this.subNodes[i] = new SuffixNode();
            return this.subNodes[i];
        }
        void copySubNode(SuffixNode oldn, SuffixNode newn) {
            for (int i = 0; i < 4; i++) {
                if (oldn.subNodes[i] != null) {
                    newn.subNodes[i] = new SuffixNode(oldn.subNodes[i].text);
                    newn.subNodes[i].minStartIndex = oldn.subNodes[i].minStartIndex;
                    newn.subNodes[i].terminal = oldn.subNodes[i].terminal;
                }
            }
        }
    }

    public int translate(char c) {
        if (c == 'A') {
            return 0;
        } else if (c == 'C') {
            return 1;
        } else if (c == 'T') {
            return 2;
        } else if (c == 'G') {
            return 3;
        }
        return -1;
    }

    /**
     * For scan the file context
     * It returns the string with just ACGT
     **/
    public static String scanText(String f) throws FileNotFoundException {
        String s = new Scanner(new File(f)).useDelimiter("\\Z").next();
        return s.replaceAll("[^ACGT]", "");
    }

    /**
     * Tree construct function
     * Insert a string into the tree
     **/
    public void insert(SuffixNode currentNode, String text, int startIndex) {
        if (currentNode.minStartIndex == -1) {
            currentNode.text = text;
            currentNode.terminal = true;
            currentNode.minStartIndex = startIndex;
            return;
        }
        int match; // match from this place
        int shorter = Math.min(currentNode.text.length(), text.length());
        for(match = 0; match < shorter; match++){
            if(text.charAt(match) != currentNode.text.charAt(match)){
                break;
            }
        }
        // if no char matched
        // aaaa : bbbb    text : current
        if (match == 0) {
            System.out.println("ERROR: went into a wrong sub-tree.");
            return;
        }
        // all chars matched and 3 possibilities:
        // abcd : abcd    text : current
        //    abcd#
        //     /
        //  (old)
        else if (match == shorter && text.length() == currentNode.text.length()) {
            currentNode.terminal = true;
            currentNode.minStartIndex = Math.min(currentNode.minStartIndex, startIndex);
            return;
        }
        // abcde : abc    text : current
        //       abc
        //       / \
        //     de#  (old)
        //   (maybe)
        //     d
        //    / \
        //  ...  e#
        else if (match == shorter && text.length() > currentNode.text.length()) {
			/* No need here? Yes, because it must be earlier. */
            //currentNode.minStartIndex = Math.min(currentNode.minStartIndex, startIndex);
            String subText = text.substring(shorter); // always cut the smaller length from the longer string
            if (currentNode.subNodes[this.translate(subText.charAt(0))] == null) {
                insert(currentNode.newChild(this.translate(subText.charAt(0))), subText, startIndex + match - 1);
                return;
            }
            insert(currentNode.subNodes[this.translate(subText.charAt(0))], subText, startIndex + match - 1);
            return;
        }
        // abc : abcde    text : current
        //      abc#
        //      / \
        //(empty)  de
        //          \
        //          (old)
        else if (match == shorter && text.length() < currentNode.text.length()) {
            String subText = currentNode.text.substring(shorter); // always cut the smaller length from the longer string
            SuffixNode subNode = new SuffixNode(subText);
			/* This doesn't work for the instance of sub-nodes. */
            //subNode.subNodes = currentNode.subNodes;
            subNode.copySubNode(currentNode, subNode);
            subNode.terminal = currentNode.terminal;
            subNode.minStartIndex = Math.min(currentNode.minStartIndex, startIndex);

            currentNode.text = text;
            currentNode.subNodes = new SuffixNode[4]; // refresh all children
            currentNode.subNodes[this.translate(subNode.text.charAt(0))] = subNode;
            currentNode.terminal = true;
			/* No change here? Should be yes, because the current one always earlier than the new one. */
            currentNode.minStartIndex = Math.min(currentNode.minStartIndex, startIndex);

            return;
        }
        // the only possibility is math char is less than shorter length
        // abcde : abcxy    text : current
        //      abc
        //      / \
        //    de#  xy
        //(empty)   \
        //          (old)
        else if (match < shorter) {
            String subText1 = text.substring(match);
            SuffixNode subNode1 = new SuffixNode(subText1);
            subNode1.terminal = true;
			/* No change here? No, because it start from the new position. */
            subNode1.minStartIndex = startIndex;

            String subText2 = currentNode.text.substring(match);
            SuffixNode subNode2 = new SuffixNode(subText2);
			/* This doesn't work for the instance of sub-nodes. */
            //subNode2.subNodes = currentNode.subNodes;
            subNode2.copySubNode(currentNode, subNode2);
            subNode2.terminal = currentNode.terminal;
			/* No change here? Yes, because it comes from the old one. */
            subNode2.minStartIndex = currentNode.minStartIndex;

            currentNode.text = text.substring(0, match);
            currentNode.subNodes = new SuffixNode[4];
            currentNode.subNodes[this.translate(subNode1.text.charAt(0))] = subNode1;
            currentNode.subNodes[this.translate(subNode2.text.charAt(0))] = subNode2;
            currentNode.terminal = false;
			/* No change here? Yes, because it itself is the old one. */
            //currentNode.minStartIndex = itself;
            return;
        }
    }

    /**
     * Constructor
     * Create a compressed suffix trie from file f.
     **/
    public CompressedSuffixTrie(String f) throws FileNotFoundException {
        this.root = new SuffixNode();
        this.text = scanText(f + ".txt");

        // creating the sub string tree:
        for(int i = 0; i < this.text.length(); i++){
            String s = this.text.substring(i);
            if (root.subNodes[this.translate(s.charAt(0))] == null) {
                this.insert(root.newChild(this.translate(s.charAt(0))), s, i);
            }
            else {
                this.insert(root.subNodes[this.translate(s.charAt(0))], s, i);
            }
        }
    }
    /** Method for finding the first occurrence of a pattern s in the DNA sequence */
    public int findString(String input) {
        String s = input;
        SuffixNode sn = this.root;
        if (sn.subNodes[this.translate(s.charAt(0))] == null) {
            System.out.println("ERROR: Could not match this string");
            return -1;
        }
        else {
            sn = sn.subNodes[this.translate(s.charAt(0))];
        }
        // As here is only one loop without any recursion structure.     #<Analysis in here>#
        // The time complexity of this function is O(n).
        while(s.length() > 0) {
            if (sn.text.length() == s.length()) {
                if (sn.text.equals(s)) { return sn.minStartIndex; }
                break;
            }
            else if (sn.text.length() > s.length()) {
                if (sn.text.substring(0, s.length()).equals(s)) { return sn.minStartIndex; }
                break;
            }
            else if (sn.text.length() < s.length()) {
                if (s.substring(0, sn.text.length()).equals(sn.text)) {
                    s = s.substring(sn.text.length());
                    sn = sn.subNodes[this.translate(s.charAt(0))];
                    if (sn == null) { break; }
                }
            }
        }
        //System.out.println("ERROR: Could not match this string");
        return -1;
    }
    /** Method for computing the degree of similarity of two DNA sequences stored
     *  in the text files f1 and f2
     **/
    public static float similarityAnalyser(String f1, String f2, String f3) throws IOException {
        String s1 = scanText(f1 + ".txt");
        String s2 = scanText(f2 + ".txt");

        char[] c1 = s1.toCharArray();
        char[] c2 = s2.toCharArray();

        int z1 = c1.length;
        int z2 = c2.length;

        int[][] lcs = new int[z1 + 1][z2 + 1];
        for (int i = 0; i < z1 + 1; i++) {
            for (int j = 0; j < z2 + 1; j++) {
    			/* initiator */
                if (i * j == 0) { lcs[i][j] = 0; }
    			/* found a matched char here */
                else if (c1[i - 1] == c2[j - 1]) { lcs[i][j] = lcs[i - 1][j - 1] + 1; }
    			/* just choose a longer one and jump */
                else { lcs[i][j] = lcs[i - 1][j] > lcs[i][j - 1]? lcs[i - 1][j] : lcs[i][j - 1]; }
            }
        }

        String result = backtrack(lcs, c1, c2, z1 - 1, z2 - 1);
        FileWriter out = new FileWriter(f3 + ".txt");
        out.write(result);
        out.close();
        int max = z1 > z2? z1 : z2;
        return (float)(lcs[z1][z2]) / max;
    }

    /**
     * back track is simpler then forwarding
     * the time complexity of this function is O(z1 + z2) (z means the length of the string of f)  #<Analysis in here>#
     */
    private static String backtrack(int[][] lcs, char[] c1, char[] c2, int i, int j) {
    	/* reached ending */
        if (i * j == 0) { return ""; }
     	/* found a matched char here */
        else if (c1[i - 1] == c2[j - 1]) { return backtrack(lcs, c1, c2, i - 1, j - 1) + c1[i - 1]; }
        /* just for jump over a char */
        else if (lcs[i][j - 1] < lcs[i - 1][j]) { return backtrack(lcs, c1, c2, i - 1, j); }
        /* lcs[i][j - 1] >= lcs[i - 1][j] */
        return backtrack(lcs, c1, c2, i, j - 1);
    }

    /**
     * Function of finding maximum sub-string:
     *//*
		String max = s1.length() > s2.length()? s1 : s2;
		String min = s1.length() > s2.length()? s2 : s1;

		int maxLen = 1;
		String result = "";
		// The time complexity here is 2 for loop for the smaller string   #<Analysis in here>#
		// So it is O( (min(m,n) - 1) ^ 2 ) which is less than O(m * n)
		for (int i = 0; i < min.length(); i++) {
			for (int j = min.length(); j > i; j--){ // from long to short
				if (max.contains(min.substring(i,j))) {
					if (j - i > maxLen) {
						result = min.substring(i,j);
						maxLen = j - i;
					}
					break; // this is the longest in the current i
				}
				if (j - i <= maxLen) {
					break; // others are even shorter
				}
			}
		}
		//System.out.println("Max similar string is: " + result);
		FileWriter out = new FileWriter(f3 + ".txt");
		out.write(result);
		out.close();
		return ("Max similar string is: " + result);
		*/

    public static void main(String args[]) throws Exception {
        /** Construct a trie named trie1
         */
    	/* only for test: */
        //CompressedSuffixTrie trie0 = new CompressedSuffixTrie("file0");print(0, trie0.root);
        //System.out.println("ACTG is at: " + trie0.findString("ACTG"));
        //System.out.println("ACT is at: " + trie0.findString("ACT"));
        //System.out.println("CTG is at: " + trie0.findString("CTG"));
        //System.out.println("CTGA is at: " + trie0.findString("CTGA"));
        //System.out.println("ACTGACTG is at: " + trie0.findString("ACTGACTG"));

        CompressedSuffixTrie trie1 = new CompressedSuffixTrie("file1");
        //print(0, trie1.root);
        System.out.println("ACTTCGTAAG is at: " + trie1.findString("ACTTCGTAAG"));

        System.out.println("AAAACAACTTCG is at: " + trie1.findString("AAAACAACTTCG"));

        System.out.println("ACTTCGTAAGGTT : " + trie1.findString("ACTTCGTAAGGTT"));
        //if (!trie1.text.contains("ACTTCGTAAGGTT")) { System.out.println("Finished!"); }

        System.out.println(CompressedSuffixTrie.similarityAnalyser("file2", "file3", "file4"));
    }

    /** " " and "+" means level, "$" means ending mark
     *  Only for testing
     *//*
    private static void print(int level, SuffixNode node){
        for (int i = 0; i < level; i++) {
            System.out.format(" ");
        }
        System.out.format("|");
        for (int i = 0; i < level; i++) {
            System.out.format("+");
        }

        if (node.terminal)
            System.out.format("%s[%s]$%n", node.text, node.minStartIndex);
        else
            System.out.format("%s[%s]%n", node.text, node.minStartIndex);
        for (int i = 0; i < 4; i++) {
        	if (node.subNodes[i] != null) {
        		print(level + 1, node.subNodes[i]);
        	}
        }
    }*/
}