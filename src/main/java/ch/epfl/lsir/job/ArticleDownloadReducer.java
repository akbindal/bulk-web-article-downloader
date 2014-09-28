package ch.epfl.lsir.job;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.climate.utils.NewsArticle;
import com.climate.utils.NewsExtractor;

public class ArticleDownloadReducer extends Reducer<Text, Text, NullWritable, Text> {
	  
	public ArticleDownloadReducer()  {
		
	}
	
	public static enum URL_COUNTER {
		RESOLVED_URL,
		UNRESOLVED_URL
	}
	
	 @Override
	 public void reduce(Text key, Iterable<Text> values, Context context)
	            throws IOException, InterruptedException {
		 String url = key.toString();
		 try {
			 NewsExtractor ext = new NewsExtractor();
			 NewsArticle article = ext.content(url);
			 Set<String> tweetids = article.tweets;
			 for(Text id:values) {
				 tweetids.add(id.toString());
			 }
			 ObjectWriter ow = new ObjectMapper().writer();
			 String json = ow.writeValueAsString(article);
			 context.write(NullWritable.get(), new Text(json));
			 if (article.title!=null) 
				 context.getCounter(URL_COUNTER.RESOLVED_URL).increment(1);
			 else
				 context.getCounter(URL_COUNTER.UNRESOLVED_URL).increment(1);
		 } catch (Exception e) {
			 System.err.println(e);
			 context.getCounter(URL_COUNTER.UNRESOLVED_URL).increment(1);
		 }
		 System.out.println("done");
		 //Reduce task gets killed if it doesn't output or report anythin for 10 minutes(changed to 30min) 
		 context.setStatus("working");
		 context.progress();
	 }
}