MINERVA
======

Project in computer science.
![System overview](Question-answering-system.png)

## Dependencies

- [Stagger.tar.bz2](http://mumin.ling.su.se/projects/stagger/snapshot.tar.bz2)  
- [Stagger swedish model](http://mumin.ling.su.se/projects/stagger/swedish.bin.bz2)
- [libshorttext](http://www.csie.ntu.edu.tw/~cjlin/libshorttext/)
- [liblinear-java](http://liblinear.bwaldvogel.de/)
- [Apache Lucene](http://apache.mirrors.spacedump.net/lucene/java/4.10.2)  
- [Wikipedia Extractor](http://medialab.di.unipi.it/Project/SemaWiki/Tools/WikiExtractor.py)

## Guides
- [Lucene in 5 minutes](http://www.lucenetutorial.com/lucene-in-5-minutes.html)  
- [Wikipedia Extractor usage](http://medialab.di.unipi.it/wiki/Wikipedia_Extractor)


```
Cross validation:
$ java -cp liblinear-1.94.jar de.bwaldvogel.liblinear.Train -s 7 -v 10 ".scale file"
Train model:
$ java -cp liblinear-1.94.jar de.bwaldvogel.liblinear.Train -s 7 ".scale file" "model file"
```

## Packages
- lucene  
  Contains classes used with the lucene library:
  - CustomAnalyzer  
    Same as SwedishAnalyzer, but without stemming.
  - Indexer  
    Class for creating indexes from bz2 files, either devided into paragraphs or entire atricles
  - QueryPassager  
    Parses a query and retrieves the desired number of documents
- minerva
- ranker
- tagging
- test
- util
- web
