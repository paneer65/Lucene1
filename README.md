1)View Source Code
	Navigate to ~/Lucene/src/lucene
	There is a file called Main.java.
	It has the source code.

2)Build and Run the Code
	Navigate to ~/Lucene/bin
	Run the following command to generate an executable  jar file:
		jar cfm executable.jar manifest.txt lucene/Main.class

	Before running the executable jar verify that:
		results.txt does not exist in ~/Lucene/bin/resources/ 			and in  ~/Lucene/bin/resources/trec_eval-9.0.7/test
		
		Index folder at ~/Lucene/bin/resources/index is 		empty. 

	Run the executable file using the command:
		java -cp executable.jar lucene.Main
	After running the jar file:
		indexes will be created at ~/Lucene/bin/resources/index
		result of search will be at ~/Lucene/bin/resources/results.txt  and 
		also at ~/Lucene/bin/resources/trec_eval-9.0.7/test
	
3)Run trec_eval on results.txt
	A copy of QRelsCorrectedforTRECeval also resides in ~/Lucene/bin/resources/trec_eval-9.0.7/test 
	Navigate to ~/Lucene/bin/resources/trec_eval-9.0.7
	Run the following command:
	./trec_eval test/QRelsCorrectedforTRECeval test/results.txt

4)The folder ~/Lucene/bin/resources also contains the following dependencies for the project:
	crandb: collection of 1400 cranfield documents
	cran.qry: list of 225 queries, which are input to the search function
	stopwords.txt: list of stopwords
		
