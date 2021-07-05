package DSPPCode.flink.email_assignment.impl;

import DSPPCode.flink.email_assignment.question.Department;
import DSPPCode.flink.email_assignment.question.EmailAssignment;
import DSPPCode.flink.email_assignment.question.Request;
import DSPPCode.flink.email_assignment.question.RequestType;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EmailAssignmentImpl extends EmailAssignment {

  private Set<Tuple2<Department,String>> dataset = new HashSet<>();

  @Override
  public DataStream<String> processRequest(DataStream<Request> requests) {

    DataStream<String> ret = requests
        .map(new MapFunction<Request, String>() {
          @Override
          public String map(Request request) throws Exception {
            if(checkNameAvailable(request.getAlias())){
              Tuple2<Department,String> req = new Tuple2<>(request.getDepart(),request.getAlias());
              if (request.getType() == RequestType.APPLY){
                if (dataset.contains(req)){
                  return "FAILURE";
                }
                else{
                  dataset.add(req);
                  return "SUCCESS";
                }

              }
              else{
                if (dataset.contains(req)){
                  dataset.remove(req);
                  return "SUCCESS";
                }
                else{
                  return "FAILURE";
                }


              }
            }
            return "FAILURE";
          }
        });
    return ret;
  }

  private boolean checkNameAvailable(String alias) {
    String reg = "^[_a-z0-9A-Z]+$";
    if(alias.length()>11 || alias.length()<5 || !alias.matches(reg))
      return false;
    return true;
  }
}
