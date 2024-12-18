package site.liangbai.clyra.chatmodel.service;

import site.liangbai.clyra.annotation.CommandParam;
import site.liangbai.clyra.chatmodel.pojo.ArgInfo;

import java.util.List;

public interface ParamService {
    String createArgsStr(List<CommandParam> commandParams);

    List<ArgInfo> createArgsInfo(List<CommandParam> commandParams);
}
