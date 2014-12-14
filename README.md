EDAN70
======

Project in computer science.

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
$ cd lib/
$ java -cp liblinear-1.94.jar de.bwaldvogel.liblinear.Train -v 5 rcv1_train.binary
```
