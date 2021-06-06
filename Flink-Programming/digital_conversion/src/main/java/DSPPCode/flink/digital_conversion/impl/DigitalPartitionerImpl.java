package DSPPCode.flink.digital_conversion.impl;
import DSPPCode.flink.digital_conversion.question.DigitalConversion;
import DSPPCode.flink.digital_conversion.question.DigitalWord;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;
import DSPPCode.flink.digital_conversion.question.DigitalPartitioner;

public class DigitalPartitionerImpl extends DigitalPartitioner {
  @Override
  public int partition(String key, int numPartitions) {
    float partition_key = Float.parseFloat(key);
    if(partition_key < 5){
      numPartitions = 0;
    }
    else{
      numPartitions = 1 ;
    }
    return numPartitions;
  }
}