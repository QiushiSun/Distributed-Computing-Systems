package DSPPCode.mapreduce.project.impl;

import DSPPCode.mapreduce.project.question.ProjectMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import java.io.IOException;

//     0,Alice,Shanghai
//     1,Bob,Nanjing
//     2,Mark,Beijing
//     3,Tom,Shanghai
public class ProjectMapperImpl extends ProjectMapper{

  @Override
  public void map(Object key, Text value, Context context)
      throws IOException, InterruptedException {

    String raw_data = value.toString();
    System.out.println(raw_data);
    String[] each_data = raw_data.split(",");
    // System.out.println(each_data[1]);
    String name = each_data[1];
    long new_id = Long.parseLong(each_data[0])+1;
    // System.err.println(new_id);
    String ans = new_id+","+name;
    context.write(new Text(ans), NullWritable.get());
  }
}
