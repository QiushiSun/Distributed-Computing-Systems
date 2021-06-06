package DSPPCode.mapreduce.average_score.impl;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.Tool;
import java.io.IOException;
import DSPPCode.mapreduce.average_score.question.ScoreMapper;
import DSPPCode.mapreduce.average_score.question.Util;

import static DSPPCode.mapreduce.average_score.question.Util.getCourseName;

public class ScoreMapperImpl extends ScoreMapper {
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String[] strs = value.toString().split(",");
//        System.out.println(strs[1]);
        for (int i = 1;i<strs.length;i++){
            Text course = new Text(getCourseName(i-1));
            IntWritable score = new IntWritable(Integer.parseInt(strs[i]));
            context.write(course,score);
        }
    }
}
