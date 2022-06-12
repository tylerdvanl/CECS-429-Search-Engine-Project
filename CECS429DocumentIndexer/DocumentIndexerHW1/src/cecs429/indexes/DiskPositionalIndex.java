package cecs429.indexes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import jdbm.recman.*;

public class DiskPositionalIndex implements Index{

    @Override
    public List<Posting> getPostings(String term) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getVocabulary() throws IOException {
        
        RecordManager recordManager = RecordManagerFactory.createRecordManager("Terms");
        long bTreeId = recordManager.getNamedObject("TermsAndPositions");
        BTree tree;
        ArrayList<String> terms = new ArrayList<>();

        if(bTreeId == 0)
        {
            System.out.println("Could not find vocabulary from index");
        }
        else
        {
            Tuple browsedTuple = new Tuple();
            tree = BTree.load(recordManager, bTreeId);
            System.out.println("Debug: Loaded tree with nodes: " + tree.size());
            TupleBrowser browser = tree.browse();
            while(browser.getNext(browsedTuple))
            {
                terms.add((String) browsedTuple.getKey()); //I hate casting, but I think I have to do it here.
            }
        }

        return terms;
    }
    
}
