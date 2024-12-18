package site.liangbai.clyra.di;

import java.util.Map;

public interface InjectSourceProvider {
    Map<String, Object> getInjectSources(String originalCommand);
}
