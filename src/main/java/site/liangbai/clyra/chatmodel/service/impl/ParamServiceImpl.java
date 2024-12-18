package site.liangbai.clyra.chatmodel.service.impl;

import site.liangbai.clyra.annotation.CommandParam;
import site.liangbai.clyra.chatmodel.pojo.ArgInfo;
import site.liangbai.clyra.chatmodel.service.ParamService;

import java.util.List;

public class ParamServiceImpl implements ParamService {
    @Override
    public String createArgsStr(List<CommandParam> commandParams) {
        return String.join(" ", commandParams.stream().map(it -> "<" + it.value() + ">").toList());
    }

    @Override
    public List<ArgInfo> createArgsInfo(List<CommandParam> commandParams) {
        return commandParams.stream().map(it -> new ArgInfo(it.value(), it.description())).toList();
    }
}
