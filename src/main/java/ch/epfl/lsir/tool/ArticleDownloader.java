package ch.epfl.lsir.tool;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import ch.epfl.lsir.job.ArticleDownloadMapper;
import ch.epfl.lsir.job.ArticleDownloadReducer;

public class ArticleDownloader extends Configured implements Tool{

	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		if (args.length != 2) {
			System.err.println("Usage: ArticleDownloader <in> <out>");
			System.exit(2);
		}
		
		Job job = Job.getInstance(getConf(), "Web-Article Downloader");
		job.setJarByClass(getClass());
		
		job.setMapperClass(ArticleDownloadMapper.class);
		job.setReducerClass(ArticleDownloadReducer.class);
		
		
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(500);
//		FileInputFormat.addInputPath(job, new Path("input"));//"///user/hdfs/url_input"));
//		FileOutputFormat.setOutputPath(job, new Path("output"));//"/user/hdfs/url_output"));
//		FileInputFormat.addInputPath(job, new Path("/user/hdfs/url_input"));
//		FileOutputFormat.setOutputPath(job, new Path("/user/hdfs/url_output"));
		//System.out.println(args[0]+"\n"+args[1]);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		return job.waitForCompletion(true) ? 0: 1;
//		job.submit();
//		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new ArticleDownloader(), args);
		System.exit(exitCode);
	}
}
