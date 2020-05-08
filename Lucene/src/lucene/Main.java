package lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Main {
	public static final String indexPath = "resources/index";
	
	public static void main(String args[]) throws Exception
	{
		index();
		search();
	}
	
	public static void index() throws Exception
	{
		int i=0;
		int j=0;
		int count =0;
		String data="";
		String name="";
		StringBuffer content;
		StringBuffer title;
		StringBuffer author;
		StringBuffer bib;
		
		content = new StringBuffer();
		title = new StringBuffer();
		author = new StringBuffer();
		bib = new StringBuffer();
		File idx = new File("resources/index");

		if(!(Files.list(idx.toPath()).count()==0))
		{
			String[]entries = idx.list();
			  for(String s: entries){
				  File currentFile = new File(idx.getPath(),s);
			      currentFile.delete();
			  }
			  
		}
			
		Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
		Analyzer analyzer = new EnglishAnalyzer(); 
		IndexWriterConfig indexWriterConfig= new IndexWriterConfig(analyzer);
		indexWriterConfig.setSimilarity(new BM25Similarity(1.2f, 0.76f));
		IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);		

		File crandb = new File("resources/crandb");
		BufferedReader br = new BufferedReader(new FileReader(crandb));
		data= br.readLine();
		
		
		for(i=1;i<=1400;i++)
		{
			
			data = br.readLine();			
			title.delete(0, title.length());
			j=0;
			do		
			{	if(j!=0)
				{
					title.append(" ");
				}
				title.append(data);
				j++;
			}while((data= br.readLine())!=null && !data.substring(0,2).matches(".A"));
			
			
			author.delete(0, author.length());
			j=0;
			do		
			{	
				if(j!=0)
				{
					author.append(" ");
				}
				author.append(data);
				j++;
			}while((data= br.readLine())!=null && !data.substring(0,2).matches(".B"));
			
			bib.delete(0, bib.length());
			j=0;
			do		
			{	
				if(j!=0)
				{
					bib.append(" ");
				}

				bib.append(data);
				j++;
			}while((data= br.readLine())!=null && !data.substring(0,2).matches(".W"));
			
			content.delete(0, content.length());
			do		
			{	
				content.append(data);
				content.append("\n");
			}while((data= br.readLine())!=null && !data.substring(0,2).matches(".I"));
			
			name = "docs/" + Integer.toString(i)+".txt";
			Document document = new Document();  
			
		    document.add(new TextField("title",title.toString().substring(2),Field.Store.YES));
		    document.add(new TextField("author",author.toString().substring(2),Field.Store.YES));
		    document.add(new TextField("bibliography",bib.toString().substring(2),Field.Store.YES));
		    document.add(new TextField("contents", content.toString().substring(2),Field.Store.YES));
		    document.add(new StringField("filename", name, Field.Store.YES));
		    indexWriter.addDocument(document);
		    
			count++;
		}
		
		br.close();
		indexWriter.close();
		System.out.println("\t\t\t----------------------------------------Indexing-------------------------------");
		System.out.println("\t\t\tInput File: resources/crandb");
		System.out.println("\t\t\tOutput: Indexes in resources/index");
		System.out.println("\t\t\tTotal Documents Indexed: "+ count);
	}
	
	  public static void search()throws Exception
	  {

		  String data,line;
		  StringBuffer query = new StringBuffer();
		  int[] arr =  new int[225];
		  int count=0;
		  int i,idx;
		  String[] props = {"user.title","user.author","user.bibliography"};
		  
	      File cranqry = new File("resources/cran.qry");
	      File results = new File("resources/results.txt");
	      File res = new File("resources/trec_eval-9.0.7/test/results.txt");
	
		  				  
		  BufferedReader br = new BufferedReader(new FileReader(cranqry));
		  BufferedWriter bw = new BufferedWriter(new FileWriter(results));
		  BufferedWriter bw2 = new BufferedWriter(new FileWriter(res));
		
		  i=0;
		  while((data = br.readLine())!=null)
		  {
			  if(data.substring(0,2).matches(".I"))
			  {	  if(query.length()>0)
			  	  {
				  	line = searchFiles(count,"contents",query.substring(3).replace("?", " "));
				  	bw.write(line);
				  	bw2.write(line);
				  	i++;
				  	
			  	  }
			  	 	
				  count++;
				  query.setLength(0);
			  }
			  else
			  {
				  query.append(data);
				  query.append(" ");
			  }
		  }
		  line = searchFiles(count,"contents",query.substring(3).replace("?", ""));
		  bw.write(line);bw2.write(line);
		  br.close();
		  bw.close();
		  bw2.close();

		  
		  System.out.println("\t\t\t----------------------------------------Searching-------------------------------");
		  System.out.println("\t\t\tInput Query File: resources/cran.qry");
		  System.out.println("\t\t\tOutput File for Trec Eval: resources/results.txt");
		  System.out.println("\t\t\tTotal Queries: "+ count);
		  
		  }
	  
	  public static String searchFiles(int query_id, String inField, String queryString) throws Exception 
	       {
		    
		  	int end,i;
		    
			
		   	String qs = removeStopWords(queryString);
			
		    
		    StringBuffer sb= new StringBuffer();
			
			Analyzer analyzer = new EnglishAnalyzer(); 

		    HashMap<String,Float> boosts = new HashMap<String,Float>();
		    boosts.put("title", 18f);
		    boosts.put("contents", 35f);
		    boosts.put("bibliography", 13f);
		   
		    Query query = new MultiFieldQueryParser(new String[]{"title", "contents","bibliography"},analyzer,boosts).parse(qs);
		    Similarity sim = new BM25Similarity(1.2f, 0.76f);
		    Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));
		    IndexReader indexReader = DirectoryReader.open(indexDirectory);
		    IndexSearcher searcher = new IndexSearcher(indexReader);
		    searcher.setSimilarity(sim);
		    TopDocs topDocs = searcher.search(query,50);
		    
		 
		    
		    ScoreDoc[] hits = topDocs.scoreDocs;   
		    int numTotalHits = Math.toIntExact(topDocs.totalHits.value);
		   
		    
		    int start = 0;
		    sb.setLength(0);
		    end = Math.min(numTotalHits, 50);
		    for (i = start; i < end; i++) {
			    Document doc = searcher.doc(hits[i].doc);
			  sb.append(query_id+" Q0 "+doc.get("filename").replace(".txt","").replace("docs/", "")+" "+ i+1 +" "+hits[i].score+" STANDARD\n");
			    
		    } 
		    return sb.toString();
		}
	  public static String removeStopWords(String str)throws Exception
	  {
		  
		  List<String> stopwords = Files.readAllLines(Paths.get("resources/stopwords.txt"));
		  
		  ArrayList<String> allWords = Stream.of(str.toLowerCase().split(" ")).collect(Collectors.toCollection(ArrayList<String>::new));
		  allWords.removeAll(stopwords);
		  String result = allWords.stream().collect(Collectors.joining(" "));
		  
		  
		  return result;
	  }
	
	
	
	

}
