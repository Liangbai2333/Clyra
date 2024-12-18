package site.liangbai.clyra.chatmodel.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Node {
    String name;
    String args;
    List<ArgInfo> argsInfo;
    String desc;
}
