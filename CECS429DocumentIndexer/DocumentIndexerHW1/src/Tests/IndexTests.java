package Tests;

import junit.*;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import cecs429.documents.*;
import cecs429.indexes.*;
import cecs429.text.EnglishTokenProcessor;
import edu.csulb.InvertedIndexRunner;

public class IndexTests 
{
    //This should be redone
    @Test
    public void testIdenticalVocabularies()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get("C:\\Users\\tyler\\OneDrive\\Documents\\GitHub\\CECS-429-Search-Engine-Project\\CECS429DocumentIndexer\\DocumentIndexerHW1\\src\\Tests"));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);

        ArrayList<String> controlVocab = new ArrayList<>();
        controlVocab.add("a");
        controlVocab.add("anoth");
        controlVocab.add("bug");
        controlVocab.add("file");
        controlVocab.add("find");
        controlVocab.add("good");
        controlVocab.add("help");
        controlVocab.add("is");
        controlVocab.add("last");
        controlVocab.add("test");
        controlVocab.add("the");
        controlVocab.add("this");
        
        assertEquals(controlVocab, testIndex.getVocabulary());
    }

    @Test
    public void testVocabSizeEqual()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get("C:\\Users\\tyler\\OneDrive\\Documents\\GitHub\\CECS-429-Search-Engine-Project\\CECS429DocumentIndexer\\DocumentIndexerHW1\\src\\Tests"));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);

        int controlSize = 12;

        assertEquals(controlSize, testIndex.getVocabulary().size());
    }

    @Test
    public void testMultipleOccurancesOfTokenInSingleDocument()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get("C:\\Users\\tyler\\OneDrive\\Documents\\GitHub\\CECS-429-Search-Engine-Project\\CECS429DocumentIndexer\\DocumentIndexerHW1\\src\\Tests"));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);

        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPositions.add(3);
        controlPositions.add(4);
        ArrayList<Integer> testPositions = testIndex.getPostings("good").get(0).getPositions();

        assertEquals(controlPositions, testPositions);
    }

    @Test
    public void testMultiplePostingsOfToken1()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get("C:\\Users\\tyler\\OneDrive\\Documents\\GitHub\\CECS-429-Search-Engine-Project\\CECS429DocumentIndexer\\DocumentIndexerHW1\\src\\Tests"));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);

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
        testPostings.add(new Posting(0, testPositions1));
        testPostings.add(new Posting(1, testPositions2));
        testPostings.add(new Posting(2, testPositions3));
        testPostings.add(new Posting(3, testPositions4));
        testPostings.add(new Posting(4, testPositions5));

        assertEquals(testPostings.size(), testIndex.getPostings("test").size());
        for(int i = 0; i < testPostings.size(); i++)
        {
            assertEquals(testPostings.get(i).getDocumentId(), testIndex.getPostings("test").get(i).getDocumentId());
            assertEquals(testPostings.get(i).getPositions(), testIndex.getPostings("test").get(i).getPositions());
        }
    }

    @Test
    public void testMultiplePostingsOfToken2()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get("C:\\Users\\tyler\\OneDrive\\Documents\\GitHub\\CECS-429-Search-Engine-Project\\CECS429DocumentIndexer\\DocumentIndexerHW1\\src\\Tests"));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);

        ArrayList<Integer> thisPositions1 = new ArrayList<>();
        ArrayList<Integer> thisPositions2 = new ArrayList<>();
        ArrayList<Integer> thisPositions3 = new ArrayList<>();
        thisPositions1.add(1);
        thisPositions2.add(1);
        thisPositions3.add(1);
        ArrayList<Posting> thisPostings = new ArrayList<>();
        thisPostings.add(new Posting(0, thisPositions1));
        thisPostings.add(new Posting(1, thisPositions2));
        thisPostings.add(new Posting(4, thisPositions3));

        assertEquals(thisPostings.size(), testIndex.getPostings("this").size());
        for(int i = 0; i < thisPostings.size(); i++)
        {
            assertEquals(thisPostings.get(i).getDocumentId(), testIndex.getPostings("this").get(i).getDocumentId());
            assertEquals(thisPostings.get(i).getPositions(), testIndex.getPostings("this").get(i).getPositions());
        }
    }

    @Test
    public void testFindingWord()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get("C:\\Users\\tyler\\OneDrive\\Documents\\GitHub\\CECS-429-Search-Engine-Project\\CECS429DocumentIndexer\\DocumentIndexerHW1\\src\\Tests"));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);

        ArrayList<Integer> goodPositions1 = new ArrayList<>();
        goodPositions1.add(3);
        goodPositions1.add(4);
        ArrayList<Posting> goodPostings = new ArrayList<>();
        goodPostings.add(new Posting(2, goodPositions1));

        assertEquals(goodPostings.get(0).getDocumentId(), testIndex.getPostings("good").get(0).getDocumentId());
        assertEquals(goodPostings.get(0).getPositions(), testIndex.getPostings("good").get(0).getPositions());

    }
    @Test
    public void testFindingStemmedWord()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get("C:\\Users\\tyler\\OneDrive\\Documents\\GitHub\\CECS-429-Search-Engine-Project\\CECS429DocumentIndexer\\DocumentIndexerHW1\\src\\Tests"));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);

        ArrayList<Integer> anothPositions1 = new ArrayList<>();
        anothPositions1.add(3);
        ArrayList<Posting> anothPostings = new ArrayList<>();
        anothPostings.add(new Posting(1, anothPositions1));

        EnglishTokenProcessor processor = new EnglishTokenProcessor();
        String word = processor.stemSingleString("another");

        assertEquals(anothPostings.get(0).getDocumentId(), testIndex.getPostings(word).get(0).getDocumentId());
    }

    @Test
    public void testAddTermEmpty()
    {
        InvertedPositionalIndex testIndex = new InvertedPositionalIndex();
        testIndex.addTerm("test", 0 , 1);

        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPositions.add(1);

        assertEquals(1, testIndex.getVocabulary().size());
        assertEquals(0, testIndex.getPostings("test").get(0).getDocumentId());
        assertEquals(controlPositions, testIndex.getPostings("test").get(0).getPositions());
    }

    @Test
    public void testAddTermDuplicate()
    {
        InvertedPositionalIndex testIndex = new InvertedPositionalIndex();
        testIndex.addTerm("test", 0 , 1);
        testIndex.addTerm("test", 0 , 2);

        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPositions.add(1);
        controlPositions.add(2);

        assertEquals(1, testIndex.getVocabulary().size());
        assertEquals(0, testIndex.getPostings("test").get(0).getDocumentId());
        assertEquals(controlPositions, testIndex.getPostings("test").get(0).getPositions());
    }


    public InvertedPositionalIndex setupControlIndex()
    {
        HashMap<String, ArrayList<Posting>> controlMap = new HashMap<>();
        //"a": [0: 3]
        ArrayList<Integer> aPositions1 = new ArrayList<>();
        aPositions1.add(3);
        ArrayList<Posting> aPostings = new ArrayList<>();
        aPostings.add(new Posting(0, aPositions1));
        controlMap.put("a", aPostings);

        //"anoth": [1: 3]
        ArrayList<Integer> anothPositions1 = new ArrayList<>();
        anothPositions1.add(3);
        ArrayList<Posting> anothPostings = new ArrayList<>();
        anothPostings.add(new Posting(1, anothPositions1));
        controlMap.put("anoth", anothPostings);

        //"bug": [2: 8]
        ArrayList<Integer> bugPositions1 = new ArrayList<>();
        bugPositions1.add(8);
        ArrayList<Posting> bugPostings = new ArrayList<>();
        bugPostings.add(new Posting(2, bugPositions1));
        controlMap.put("bug", bugPostings);

        //"file": [4: 6]
        ArrayList<Integer> filePositions1 = new ArrayList<>();
        filePositions1.add(6);
        ArrayList<Posting> filePostings = new ArrayList<>();
        filePostings.add(new Posting(4, filePositions1));
        controlMap.put("file", filePostings);

        //"find": [2: 7]
        ArrayList<Integer> findPositions1 = new ArrayList<>();
        findPositions1.add(7);
        ArrayList<Posting> findPostings = new ArrayList<>();
        findPostings.add(new Posting(2, findPositions1));
        controlMap.put("find", findPostings);

        //"good": [2: 3, 4]
        ArrayList<Integer> goodPositions1 = new ArrayList<>();
        goodPositions1.add(3);
        goodPositions1.add(4);
        ArrayList<Posting> goodPostings = new ArrayList<>();
        goodPostings.add(new Posting(2, goodPositions1));
        controlMap.put("good", goodPostings);

        //"help": [2: 6]
        ArrayList<Integer> helpPositions1 = new ArrayList<>();
        helpPositions1.add(6);
        ArrayList<Posting> helpPostings = new ArrayList<>();
        helpPostings.add(new Posting(2, helpPositions1));
        controlMap.put("help", helpPostings);

        //"is": [0: 2], [1: 2], [2: 2], [4: 2]
        ArrayList<Integer> isPositions1 = new ArrayList<>();
        ArrayList<Integer> isPositions2 = new ArrayList<>();
        ArrayList<Integer> isPositions3 = new ArrayList<>();
        ArrayList<Integer> isPositions4 = new ArrayList<>();
        isPositions1.add(2);
        isPositions2.add(2);
        isPositions3.add(2);
        isPositions4.add(2);
        ArrayList<Posting> isPostings = new ArrayList<>();
        isPostings.add(new Posting(0, isPositions1));
        isPostings.add(new Posting(1, isPositions2));
        isPostings.add(new Posting(2, isPositions3));
        isPostings.add(new Posting(4, isPositions4));
        controlMap.put("is", isPostings);

        //"last": [4: 4]
        ArrayList<Integer> lastPositions1 = new ArrayList<>();
        lastPositions1.add(4);
        ArrayList<Posting> lastPostings = new ArrayList<>();
        lastPostings.add(new Posting(4, lastPositions1));
        controlMap.put("last", lastPostings);

        //"test": [0: 4], [1: 4], [2: 1, 5], [3: 1, 2, 3], [4: 5]
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
        testPostings.add(new Posting(0, testPositions1));
        testPostings.add(new Posting(1, testPositions2));
        testPostings.add(new Posting(2, testPositions3));
        testPostings.add(new Posting(3, testPositions4));
        testPostings.add(new Posting(4, testPositions5));
        controlMap.put("test", testPostings);

        //"the": [4: 3]
        ArrayList<Integer> thePositions1 = new ArrayList<>();
        thePositions1.add(3);
        ArrayList<Posting> thePostings = new ArrayList<>();
        thePostings.add(new Posting(4, thePositions1));
        controlMap.put("the", thePostings);

        //"this": [0: 1], [1: 1], [4: 1]
        ArrayList<Integer> thisPositions1 = new ArrayList<>();
        ArrayList<Integer> thisPositions2 = new ArrayList<>();
        ArrayList<Integer> thisPositions3 = new ArrayList<>();
        thisPositions1.add(1);
        thisPositions2.add(1);
        thisPositions3.add(1);
        ArrayList<Posting> thisPostings = new ArrayList<>();
        thisPostings.add(new Posting(0, thisPositions1));
        thisPostings.add(new Posting(1, thisPositions2));
        thisPostings.add(new Posting(4, thisPositions3));
        controlMap.put("this", thisPostings);

        //All done, finally
        return new InvertedPositionalIndex(controlMap);
    }
}
