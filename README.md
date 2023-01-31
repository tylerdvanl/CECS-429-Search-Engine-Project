# CECS-429-Search-Engine-Project
A big ol' search engine done over the summer of 2022 for a Search Engines course

This project was developed over the Summer 2022 semester for the CECS 429: Search Engine Technology course at California State University Long Beach.
The project deliverables were distinct iterations utilizing different, often more advanced, techniques as the course progressed.  At the beginning, we were provided
with basic interfaces and objects from which to launch from, as well as a small text document corpus (the chapters of Moby Dick) and a large corpus that would serve 
as our primary corpus (~37,000 .json files scraped from the National Parks Service public websites).  Some of the language processing methods were found on the web, 
and will be cited.

The iterations of the project are reflected in the three "runner" files found in edu/csulb/, "InvertedIndexRunner.java", "FileWritingRunner.java", and
"BayesianClassificationRunner.java", described below.

I.  "InvertedIndexRunner.java"
    The most basic of the iterations, this runner builds an Inverted Positional Index in memory and allows a user to search Boolean and Phrase queries.  Once documents
    are returned, the user may choose to read the contents of one of them.
    
II. "FileWritingRunner.java"
    Contains all the features of the previous iteration, but with additonal features.  Instead of writing an index in memory, the user has the option to write it to a
    file or to use a previously saved index.  Also, the user can performed Ranked Retrieval, using a scoring system to return the top 10 documents from a query.  
    
III.  "BayesianClassificationRunner.java"
      This iteration performs a different purpose than the other two.  This one utilizes Bayesian Classification techniques to determine the top discriminating terms 
      from the Federalist Papers written by Alexander Hamilton, James Madison, and John Jay, an uses this classification to attempt to determine the authors of the 
      11 disputed articles.
      
 In all above iterations, the stemming portion of language processing was performed using Snowball Stemmer, found here: https://snowballstem.org/
 
 The disk positional index was structured using a B-Tree fround from JDBM, here: https://jdbm.sourceforge.net/V1.0/doc/api/overview-summary.html
