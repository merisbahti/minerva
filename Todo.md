TODO
====

This is the todo list and working plan

- Create an indexer using Lucene and wikixmlj
````

                OR
XML             ||  XML
 |              ||   |
 |   wikixmlj   ||   |  WikiExtractor.py
 V              ||   v   
WikiPage        || bz2 Files
 |              ||   |
 |   Lucene     ||   |  Lucene
 V              ||   v
    Indexed database
       /\
       ||
       ||
Query abstraction (http request)
````

====================================================
                        MÖTE
====================================================
Att läsa:
* IBM - Finding the needle in the haystack.
* Pinchak - A Probabilistic Answer Type Model 

Att fixa:
* Baseline!
* Använd libshorttext för att predict category
* liblinear - logistikregression
* Reranking
