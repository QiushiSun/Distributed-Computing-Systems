package DSPPCode.mapreduce.project.question;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

abstract public class ProjectMapper extends Mapper<Object, Text, Text, NullWritable> {

  /**
   * TODO 请完成该方法
   * <p>
   * 输入:
   * <p>
   * value中包含三个字段："序号"、"姓名"和"城市"，各字段之间按逗号分隔。
   * <p>
   * 例如，0,Alice,Shanghai 代表一条"序号"为0、"姓名"为Alice、"城市"为Shanghai的记录。
   */
  @Override
  abstract public void map(Object key, Text value, Context context)
      throws IOException, InterruptedException;
}



