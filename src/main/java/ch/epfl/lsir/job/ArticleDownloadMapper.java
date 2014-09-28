package ch.epfl.lsir.job;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.climate.utils.NewsArticle;
import com.climate.utils.NewsExtractor;

/**
 * Hadoop 2.0 
 * read urls list in following format
 * "url_id url"
 * fetch the corresponding content of url_id and emits json (title, content, short_url, expanded_url)
 * @author ashish
 *
 */

public class ArticleDownloadMapper  extends Mapper<LongWritable, Text, Text, Text> {
	
	public ArticleDownloadMapper()  {
	}

	@Override
	protected void map(LongWritable key, Text value,
			Context context)
			throws IOException, InterruptedException {
		
		String line = value.toString().trim();
		List<String> tweet_url = Arrays.asList(line.split(","));
//		
//		StringTokenizer tokenizer = new StringTokenizer(line);
//		String tweetId = tokenizer.nextToken();
//		String url = tokenizer.nextToken();
		/*
		NewsExtractor ext = new NewsExtractor();
		NewsArticle article = ext.content(url);
		
		ObjectWriter ow = new ObjectMapper().writer();
		String json = ow.writeValueAsString(article);
		*/
		try {
			String tweetId = tweet_url.get(0).trim();
			String url = tweet_url.get(1).trim();
			context.write(new Text(url), new Text(tweetId));
		} catch(Exception e) {
			return;
		}
	}
}
