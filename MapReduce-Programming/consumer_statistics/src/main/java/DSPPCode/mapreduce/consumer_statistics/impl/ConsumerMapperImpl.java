package DSPPCode.mapreduce.consumer_statistics.impl;

import DSPPCode.mapreduce.consumer_statistics.question.Consumer;
import DSPPCode.mapreduce.consumer_statistics.question.ConsumerMapper;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

//3819	2021-03-27 21:30	357	vip
//3231	2021-03-27 21:30	77	non-vip
//5013	2021-03-27 21:30	491	vip

public class ConsumerMapperImpl extends ConsumerMapper {
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] fields = line.split("\t");
        Consumer consumer = new Consumer();
        consumer.setId(fields[0]);
        consumer.setMoney(Integer.parseInt(fields[2]));
        consumer.setVip(VIP.equals(fields[3]));
        if (consumer.isVip()){
            context.write(new Text(VIP),consumer);
        }else{
            context.write(new Text(NON_VIP),consumer);
        }
    }
}