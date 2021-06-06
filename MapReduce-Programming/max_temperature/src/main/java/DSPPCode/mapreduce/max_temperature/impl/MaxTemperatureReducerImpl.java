package DSPPCode.mapreduce.max_temperature.impl;

import DSPPCode.mapreduce.max_temperature.question.MaxTemperatureReducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.spark.sql.execution.columnar.DOUBLE;

import java.io.IOException;

public class MaxTemperatureReducerImpl extends MaxTemperatureReducer{
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int temp = Integer.MIN_VALUE;
        for (IntWritable value:values){
            if (value.get()>temp){
                temp = value.get();
            }
        }
        IntWritable MAX_TEMP = new IntWritable(temp);
        context.write(key,MAX_TEMP);
    }
}