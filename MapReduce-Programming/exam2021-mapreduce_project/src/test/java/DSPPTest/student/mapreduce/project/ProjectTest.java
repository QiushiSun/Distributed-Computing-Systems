package DSPPTest.student.mapreduce.project;

import DSPPCode.mapreduce.project.question.ProjectRunner;
import DSPPTest.student.TestTemplate;
import DSPPTest.util.Parser.KVParser;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Test;

import static DSPPTest.util.FileOperator.deleteFolder;
import static DSPPTest.util.FileOperator.readFile2String;
import static DSPPTest.util.Verifier.verifyKV;

public class ProjectTest extends TestTemplate {

  @Test
  public void test() throws Exception {
    // 设置路径
    String inputPath = root + "/mapreduce/project/input";
    String outputPath = outputRoot + "/mapreduce/project";
    String outputFile = outputPath + "/part-m-00000";
    String answerFile = root + "/mapreduce/project/answer";

    // 删除旧输出
    deleteFolder(outputPath);

    String[] args = {inputPath, outputPath};
    int exitCode = ToolRunner.run(new ProjectRunner(), args);

    // 检验结果
    verifyKV(readFile2String(outputFile), readFile2String(answerFile), new KVParser(","));

    System.out.println("恭喜通过~");
    System.exit(exitCode);
  }
}
