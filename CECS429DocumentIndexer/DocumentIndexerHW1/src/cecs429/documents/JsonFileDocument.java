package cecs429.documents;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.*;


public class JsonFileDocument implements FileDocument
{

    private int mDocumentId;
    private Path mFilePath;

    public JsonFileDocument(int docID, Path absoluteFilePath)
    {
        mDocumentId = docID;
        mFilePath = absoluteFilePath;
    }
    
    @Override
    public Path getFilePath() 
    {
        return mFilePath;
    }

    @Override
    public int getId() 
    {
        return mDocumentId;
    }

    @Override
    public Reader getContent() 
    {
        try 
        {
            FileReader reader = new FileReader(mFilePath.toFile());
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject json = new JSONObject(tokener);
            //This will throw an JSONException if the key does not exist in the file.
            return new StringReader(json.getString("body"));
        } 
        catch (FileNotFoundException e) 
        {
            throw new RuntimeException(e);
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTitle() 
    {
        try 
        {
            FileReader reader = new FileReader(mFilePath.toFile());
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject json = new JSONObject(tokener);
            //This will throw an JSONException if the key does not exist in the file.
            return json.getString("title");
        } 
        catch (FileNotFoundException e) 
        {
            throw new RuntimeException(e);
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static FileDocument loadJSONFileDocument(Path absolutePath, int docId)
   {
       return new JsonFileDocument(docId, absolutePath);
   }

    @Override
    public Path getPath() 
    {
        return mFilePath;
    }
}
