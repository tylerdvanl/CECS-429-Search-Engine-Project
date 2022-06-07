package Tests;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import cecs429.documents.*;
import cecs429.indexes.*;
import cecs429.queries.*;
import cecs429.text.EnglishTokenProcessor;
import edu.csulb.InvertedIndexRunner;

public class QueryTests 
{

    String testPath = "C:\\Users\\tyler_rdl\\Documents\\GitHub\\CECS-429-Search-Engine-Project\\CECS429DocumentIndexer\\DocumentIndexerHW1\\src\\Tests";

    //AND queries
    @Test
    public void testAndSingleTerm()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new TermLiteral("help"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());    
    }

    @Test
    public void testAndSingleTermStemmed()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new TermLiteral("helps"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());   
    }

    @Test
    public void testAndTwoTerms()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new TermLiteral("test"));
        testComponents.add(new TermLiteral("help"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPositions.add(6);
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }

    @Test
    public void testAndTwoTermsStemmed()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new TermLiteral("tests"));
        testComponents.add(new TermLiteral("helping"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPositions.add(6);
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }

    @Test
    public void testAndManyTerms()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new TermLiteral("test"));
        testComponents.add(new TermLiteral("help"));
        testComponents.add(new TermLiteral("good"));
        testComponents.add(new TermLiteral("bug"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPositions.add(6);
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }

    @Test
    public void testAndManyTermsStemmed()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new TermLiteral("tests"));
        testComponents.add(new TermLiteral("helping"));
        testComponents.add(new TermLiteral("good"));
        testComponents.add(new TermLiteral("bugs"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPositions.add(6);
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }

    //OR Queries
    @Test
    public void testOrTwoTerms()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new TermLiteral("bug"));
        testComponents.add(new TermLiteral("help"));
        OrQuery testQuery = new OrQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPositions.add(6);
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }

    @Test
    public void testOrTwoTermsStemmed()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new TermLiteral("helping"));
        testComponents.add(new TermLiteral("bugs"));
        OrQuery testQuery = new OrQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPositions.add(6);
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
        //No positions get returned from my AND query implementation
    }

    @Test
    public void testOrManyTerms()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new TermLiteral("test"));
        testComponents.add(new TermLiteral("help"));
        testComponents.add(new TermLiteral("good"));
        testComponents.add(new TermLiteral("bug"));
        OrQuery testQuery = new OrQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions0 = new ArrayList<>();
        ArrayList<Integer> controlPositions1 = new ArrayList<>();
        ArrayList<Integer> controlPositions2 = new ArrayList<>();
        ArrayList<Integer> controlPositions3 = new ArrayList<>();
        ArrayList<Integer> controlPositions4 = new ArrayList<>();
        controlPostings.add(new Posting(0, controlPositions0));
        controlPostings.add(new Posting(1, controlPositions1));
        controlPostings.add(new Posting(2, controlPositions2));
        controlPostings.add(new Posting(3, controlPositions3));
        controlPostings.add(new Posting(4, controlPositions4));

        assertEquals(controlPostings.size(), resultPostings.size());
        for(int i = 0; i < controlPostings.size(); i++)
        {
            assertEquals(controlPostings.get(i).getDocumentId(), resultPostings.get(i).getDocumentId());  
        }
    }

    @Test
    public void testOrManyTermsStemmed()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new TermLiteral("tests"));
        testComponents.add(new TermLiteral("helping"));
        testComponents.add(new TermLiteral("good"));
        testComponents.add(new TermLiteral("bugs"));
        OrQuery testQuery = new OrQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions0 = new ArrayList<>();
        ArrayList<Integer> controlPositions1 = new ArrayList<>();
        ArrayList<Integer> controlPositions2 = new ArrayList<>();
        ArrayList<Integer> controlPositions3 = new ArrayList<>();
        ArrayList<Integer> controlPositions4 = new ArrayList<>();
        controlPostings.add(new Posting(0, controlPositions0));
        controlPostings.add(new Posting(1, controlPositions1));
        controlPostings.add(new Posting(2, controlPositions2));
        controlPostings.add(new Posting(3, controlPositions3));
        controlPostings.add(new Posting(4, controlPositions4));

        assertEquals(controlPostings.size(), resultPostings.size());
        for(int i = 0; i < controlPostings.size(); i++)
        {
            assertEquals(controlPostings.get(i).getDocumentId(), resultPostings.get(i).getDocumentId());  
        }
    }

    //PhraseQueries
    //TODO:
    @Test
    public void testPhraseSingleTerm()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new PhraseLiteral("help"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }
    //TODO:
    @Test
    public void testPhraseSingleTermStemmed()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new PhraseLiteral("helps"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }
    //TODO:
    @Test
    public void testPhraseTwoTerms()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new PhraseLiteral("tests help"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }
    //TODO:
    @Test
    public void testPhraseTwoTermsStemmed()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new PhraseLiteral("testing helping"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPositions.add(6);
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }
    //TODO:
    @Test
    public void testPhraseManyTerms()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new PhraseLiteral("Good tests help find bugs."));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }
    //TODO:
    @Test
    public void testPhraseManyTermsStemmed()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new PhraseLiteral("Goods testing helping finding bug"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPostings.add(new Posting(2, controlPositions));

        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }

    //TODO: 
    @Test
    public void testHyphenQuery()
    {
        DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
        Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
        ArrayList<QueryComponent> testComponents = new ArrayList<>();
        EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
        testComponents.add(new TermLiteral("test-help"));
        AndQuery testQuery = new AndQuery(testComponents);
        ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));

        ArrayList<Posting> controlPostings = new ArrayList<>();
        ArrayList<Integer> controlPositions = new ArrayList<>();
        controlPostings.add(new Posting(0, controlPositions));
        controlPostings.add(new Posting(1, controlPositions));
        controlPostings.add(new Posting(2, controlPositions));
        controlPostings.add(new Posting(3, controlPositions));
        controlPostings.add(new Posting(4, controlPositions));


        assertEquals(controlPostings.size(), resultPostings.size());
        assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
    }

        //TODO: 
        @Test
        public void testHyphenQueryStemmed()
        {
            DocumentCorpus testCorpus = DirectoryCorpus.loadDirectory(Paths.get(testPath));
            Index testIndex = InvertedIndexRunner.indexCorpus(testCorpus);
            ArrayList<QueryComponent> testComponents = new ArrayList<>();
            EnglishTokenProcessor testProcessor = new EnglishTokenProcessor();
            testComponents.add(new TermLiteral("testing-helping"));
            AndQuery testQuery = new AndQuery(testComponents);
            ArrayList<Posting> resultPostings = new ArrayList<>(testQuery.getPostings(testIndex, testProcessor));
    
            ArrayList<Posting> controlPostings = new ArrayList<>();
            ArrayList<Integer> controlPositions = new ArrayList<>();
            controlPostings.add(new Posting(0, controlPositions));
            controlPostings.add(new Posting(1, controlPositions));
            controlPostings.add(new Posting(2, controlPositions));
            controlPostings.add(new Posting(3, controlPositions));
            controlPostings.add(new Posting(4, controlPositions));
    
    
            assertEquals(controlPostings.size(), resultPostings.size());
            assertEquals(controlPostings.get(0).getDocumentId(), resultPostings.get(0).getDocumentId());  
        }
}
