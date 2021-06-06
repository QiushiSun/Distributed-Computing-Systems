package DSPPCode.mapreduce.consumer_statistics.impl;

import DSPPCode.mapreduce.consumer_statistics.question.Consumer;
import DSPPCode.mapreduce.consumer_statistics.question.ConsumerReducer;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// [ vip/non-vip, money]
public class ConsumerReducerImpl extends ConsumerReducer{
    @Override
    protected void reduce(Text key, Iterable<Consumer> values, Context context) throws IOException, InterruptedException {
        // 消费的人中有重复的用户，需要用Set集合去重后合并
        Set<String> consumerIds = new HashSet<>();
        long money_spent = 0;
        for (Consumer consumer:values){
            consumerIds.add(consumer.getId());
            money_spent += consumer.getMoney();
        }
        context.write(new Text(key.toString()+"\t"+consumerIds.size()+"\t"+money_spent), NullWritable.get());
    }
}