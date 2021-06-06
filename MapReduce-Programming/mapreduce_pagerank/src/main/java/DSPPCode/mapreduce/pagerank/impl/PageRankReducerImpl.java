package DSPPCode.mapreduce.pagerank.impl;

import DSPPCode.mapreduce.pagerank.question.PageRankReducer;
import DSPPCode.mapreduce.pagerank.question.PageRankRunner;
import DSPPCode.mapreduce.pagerank.question.ReducePageRankWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class PageRankReducerImpl extends PageRankReducer{
    private static final double D = 0.85;

    @Override
    public void reduce(Text key, Iterable<ReducePageRankWritable> values, Context context)
            throws IOException, InterruptedException {

        String[] pageInfo = null;
        // 从配置项读取网页总数
        int totalPage = context.getConfiguration().getInt(PageRankRunner.TOTAL_PAGE,0);
        // 从配置项读取当前迭代步数
        int iteration = context.getConfiguration().getInt(PageRankRunner.ITERATION,0);
        double sum = 0;
        for (ReducePageRankWritable value:values) {
            String tag = value.getTag();
            // 如果是贡献值则求和，否则以空格为分隔符切分后保存到pageInfo
            if (tag.equals(ReducePageRankWritable.PR_L)) {
                sum += Double.parseDouble(value.getData());
            }else if (tag.equals(ReducePageRankWritable.PAGE_INFO)){
                pageInfo = value.getData().split(" ");
            }
        }
        // 根据公式计算排名值
        double pageRank = (1-D)/totalPage + D*sum;
        // 更新网页信息中的排名值
        pageInfo[1] = String.valueOf(pageRank);
        // 最后一次迭代输出网页名以及排名值，而其余迭代输出网页信息
        StringBuilder result = new StringBuilder();
        if (iteration==(PageRankRunner.MAX_ITERATION-1)){
            result.append(pageInfo[0]).append(" ").append(String.format("%.5f",pageRank));
        }else{
            for (String data : pageInfo){
                result.append(data).append(" ");
            }
        }
        context.write(new Text(result.toString()), NullWritable.get());
    }
}
