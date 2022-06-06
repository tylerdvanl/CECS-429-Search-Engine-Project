package Tests;

import junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import cecs429.indexes.InvertedPositionalIndex;
import cecs429.indexes.Posting;

public class IndexTests 
{


    private InvertedPositionalIndex setupIndex()
    {
        HashMap<String, ArrayList<Posting>> controlMap = new HashMap<>();
        //"a": [1: 3]
        ArrayList<Integer> aPositions1 = new ArrayList<>();
        aPositions1.add(1);
        ArrayList<Posting> aPostings = new ArrayList<>();
        aPostings.add(new Posting(1, aPositions1));
        controlMap.put("a", aPostings);

        //"another": [2: 3]
        ArrayList<Integer> anotherPositions1 = new ArrayList<>();
        anotherPositions1.add(3);
        ArrayList<Posting> anotherPostings = new ArrayList<>();
        anotherPostings.add(new Posting(2, anotherPositions1));
        controlMap.put("another", anotherPostings);

        //"bug": [3: 8]
        ArrayList<Integer> bugPositions1 = new ArrayList<>();
        bugPositions1.add(8);
        ArrayList<Posting> bugPostings = new ArrayList<>();
        bugPostings.add(new Posting(3, bugPositions1));
        controlMap.put("bug", bugPostings);

        //"file": [5: 6]
        ArrayList<Integer> filePositions1 = new ArrayList<>();
        filePositions1.add(6);
        ArrayList<Posting> filePostings = new ArrayList<>();
        filePostings.add(new Posting(5, filePositions1));
        controlMap.put("file", filePostings);

        //"find": [3: 7]
        ArrayList<Integer> findPositions1 = new ArrayList<>();
        findPositions1.add(3);
        ArrayList<Posting> findPostings = new ArrayList<>();
        findPostings.add(new Posting(7, findPositions1));
        controlMap.put("find", findPostings);

        //"good": [3: 3, 7]
        ArrayList<Integer> goodPositions1 = new ArrayList<>();
        goodPositions1.add(3);
        goodPositions1.add(7);
        ArrayList<Posting> goodPostings = new ArrayList<>();
        goodPostings.add(new Posting(3, goodPositions1));
        controlMap.put("good", goodPostings);

        //"help": [3: 6]
        ArrayList<Integer> helpPositions1 = new ArrayList<>();
        helpPositions1.add(6);
        ArrayList<Posting> helpPostings = new ArrayList<>();
        helpPostings.add(new Posting(3, helpPositions1));
        controlMap.put("help", helpPostings);

        //"is": [1: 2], [2: 2], [3: 2], [5: 2]
        ArrayList<Integer> isPositions1 = new ArrayList<>();
        ArrayList<Integer> isPositions2 = new ArrayList<>();
        ArrayList<Integer> isPositions3 = new ArrayList<>();
        ArrayList<Integer> isPositions4 = new ArrayList<>();
        isPositions1.add(2);
        isPositions2.add(2);
        isPositions3.add(2);
        isPositions4.add(2);
        ArrayList<Posting> isPostings = new ArrayList<>();
        isPostings.add(new Posting(1, isPositions1));
        isPostings.add(new Posting(2, isPositions2));
        isPostings.add(new Posting(3, isPositions3));
        isPostings.add(new Posting(5, isPositions4));
        controlMap.put("is", isPostings);

        //"last": [5: 4]
        ArrayList<Integer> lastPositions1 = new ArrayList<>();
        lastPositions1.add(4);
        ArrayList<Posting> lastPostings = new ArrayList<>();
        lastPostings.add(new Posting(5, lastPositions1));
        controlMap.put("last", lastPostings);

        //"test": [1: 4], [2: 4], [3: 1, 5], [4: 1, 2, 3], [5: 5]
        ArrayList<Integer> testPositions1 = new ArrayList<>();
        ArrayList<Integer> testPositions2 = new ArrayList<>();
        ArrayList<Integer> testPositions3 = new ArrayList<>();
        ArrayList<Integer> testPositions4 = new ArrayList<>();
        ArrayList<Integer> testPositions5 = new ArrayList<>();
        testPositions1.add(4);
        testPositions2.add(4);
        testPositions3.add(1);
        testPositions3.add(5);
        testPositions4.add(1);
        testPositions4.add(2);
        testPositions4.add(3);
        testPositions5.add(5);
        ArrayList<Posting> testPostings = new ArrayList<>();
        testPostings.add(new Posting(1, testPositions1));
        testPostings.add(new Posting(2, testPositions2));
        testPostings.add(new Posting(3, testPositions3));
        testPostings.add(new Posting(4, testPositions4));
        testPostings.add(new Posting(5, testPositions5));
        controlMap.put("test", testPostings);

        //"the": [5: 3]
        ArrayList<Integer> thePositions1 = new ArrayList<>();
        thePositions1.add(3);
        ArrayList<Posting> thePostings = new ArrayList<>();
        thePostings.add(new Posting(5, thePositions1));
        controlMap.put("the", thePostings);

        //"this": [1: 1], [2: 1], [3: 1]
        ArrayList<Integer> thisPositions1 = new ArrayList<>();
        ArrayList<Integer> thisPositions2 = new ArrayList<>();
        ArrayList<Integer> thisPositions3 = new ArrayList<>();
        thisPositions1.add(1);
        thisPositions2.add(1);
        thisPositions3.add(1);
        ArrayList<Posting> thisPostings = new ArrayList<>();
        thisPostings.add(new Posting(1, thisPositions1));
        thisPostings.add(new Posting(2, thisPositions2));
        thisPostings.add(new Posting(3, thisPositions3));
        controlMap.put("this", thisPostings);

        //All done, finally
        return new InvertedPositionalIndex(controlMap);
    }
}
