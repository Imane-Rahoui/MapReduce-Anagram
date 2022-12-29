package org.mbds;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

// Driver class (implements the main method).
public class Anagrams {

    // Mapper class.
    // The 4 Generic types correspond to:
    // 1 - Object: the input key (a line number - not used)
    // 2 - Text: the input value (a word).
    // 3 - Text: the output key (the word sorted alphabetically).
    // 4 - Text: the output value (the word).
    public static class AnagramsMap extends Mapper<Object, Text, Text, Text> {

        private static Text sortedWord = new Text();
        private static Text word = new Text();

        // The map method receives one line of text at the time (by default).
        // The `key` argument consists of the line number (not used).
        // The `value` argument consists of the line of text (a word).
        // The `context` argument let us emit key/value pairs.
        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            word.set(value.toString());
            char[] charArray = word.toString().toCharArray();
            Arrays.sort(charArray);
            sortedWord.set(String.valueOf(charArray));
            context.write(sortedWord, word);
        }
    }

    // Reducer class.
    // The 4 Generic types correspond to:
    // 1 - Text: the input key (a word sorted alphabetically)
    // 2 - Text: the input value (the anagrams).
    // 3 - Text: the output key (the word sorted alphabetically).
    // 4 - Text: the output value (the comma separated list of anagrams).
    public static class AnagramsReduce extends Reducer<Text, Text, Text, Text> {

        private Text result = new Text();

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuilder anagrams = new StringBuilder();
            Iterator<Text> iterator = values.iterator();
            anagrams.append(iterator.next().toString());
            boolean isAnagram = iterator.hasNext();
            while (iterator.hasNext()) {
                anagrams.append(", ");
                anagrams.append(iterator.next().toString());
            }
            if (isAnagram) {
                result.set(anagrams.toString());
                context.write(key, result);
            }
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        // Instantiate the Hadoop Configuration.
        Configuration conf = new Configuration();

        // Parse command-line arguments.
        // The GenericOptionParser takes care of Hadoop-specific arguments.
        String[] ourArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        // Check input arguments.
        if (ourArgs.length != 2) {
            System.err.println("Usage: anagrams <in> <out>");
            System.exit(2);
        }

        // Get a Job instance.
        Job job = Job.getInstance(conf, "Anagrams");
        // Setup the Driver/Mapper/Reducer classes.
        job.setJarByClass(Anagrams.class);
        job.setMapperClass(AnagramsMap.class);
        job.setReducerClass(AnagramsReduce.class);
        // Indicate the key/value output types we are using in our Mapper & Reducer.
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // Indicate from where to read input data from HDFS.
        FileInputFormat.addInputPath(job, new Path(ourArgs[0]));
        // Indicate where to write the results on HDFS.
        FileOutputFormat.setOutputPath(job, new Path(ourArgs[1]));

        // We start the MapReduce Job execution (synchronous approach).
        // If it completes with success we exit with code 0, else with code 1.
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
