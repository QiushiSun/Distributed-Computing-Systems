package DSPPCode.mapreduce.pagerank.impl;

import DSPPCode.mapreduce.pagerank.question.PageRankMapper;
import DSPPCode.mapreduce.pagerank.question.PageRankReducer;
import DSPPCode.mapreduce.pagerank.question.PageRankRunner;
import DSPPCode.mapreduce.pagerank.question.ReducePageRankWritable;
import DSPPCode.mapreduce.pagerank.question.utils.Rank;
import DSPPCode.mapreduce.pagerank.question.utils.RanksOperation;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageRankMapperImpl extends PageRankMapper{
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        List<Rank> ranks =  new ArrayList<>();

        String[] pageInfo;
        if (PageRankRunner.iteration == 0){
            String[] data = value.toString().split(" ");
            String pagerank = context.getCacheFiles()[0].toString();
            ranks = RanksOperation.getRanks(pagerank,false);
            String rankValue = "";
            String pageName = data[0];
            // 连接网页名与网页排名
            for (int i = 0;i<ranks.size();i++){
                if (ranks.get(i).getPageName().equals(pageName)){
                    rankValue = String.valueOf(ranks.get(i).getRank());
                    break;
                }
            }
            pageInfo = new String[data.length*2];
            for (int i = 0;i<data.length;i++){
                pageInfo[i*2] = data[i];
                if (i==0){
                    pageInfo[i*2+1] = rankValue;
                }else{
                    pageInfo[i*2+1] = "1.0";
                }
            }
        }
        else{pageInfo = value.toString().split(" ");
        }

        int outlink = (pageInfo.length-2)/2;
        double pageRank = Double.parseDouble(pageInfo[1]);
        ReducePageRankWritable writable;
        writable = new ReducePageRankWritable();
        // 计算贡献值
        writable.setData(String.valueOf(pageRank/outlink));
        // 设置对应标识
        writable.setTag(ReducePageRankWritable.PR_L);
        // 对于每个出站链接，输出贡献值
        for (int i = 2;i<pageInfo.length;i+=2){
            context.write(new Text(pageInfo[i]),writable);
        }
        writable = new ReducePageRankWritable();
        // 保存网页信息并标识
        StringBuilder pageInfoResult = new StringBuilder();
        if (PageRankRunner.iteration == 0){
            for (String data:pageInfo){
                pageInfoResult.append(data).append(" ");
            }
            writable.setData(pageInfoResult.toString());
        }else{
            writable.setData(value.toString());
        }

        writable.setTag(ReducePageRankWritable.PAGE_INFO);
        // 以输入网页信息的网页名为key进行输出
        context.write(new Text(pageInfo[0]),writable);

    }
}