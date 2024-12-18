package site.liangbai.clyra;

import jakarta.annotation.Resource;
import site.liangbai.clyra.dispatcher.CommandDispatcher;

public class DispatcherTest {
    @Resource
    private CommandDispatcher dispatcher;

    public void dispatchTest() {
        dispatcher.dispatch("5666");
    }
}
