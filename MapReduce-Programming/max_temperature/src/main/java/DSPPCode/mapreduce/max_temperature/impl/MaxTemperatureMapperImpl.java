package DSPPCode.mapreduce.max_temperature.impl;

import DSPPCode.mapreduce.max_temperature.question.MaxTemperatureMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class MaxTemperatureMapperImpl extends MaxTemperatureMapper{

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String info = value.toString();
        String[] line = info.split(" ");
        String year = (line[0]);
        int temp = Integer.parseInt(line[1]);

        context.write(new Text(year),new IntWritable(temp));


    }
}
