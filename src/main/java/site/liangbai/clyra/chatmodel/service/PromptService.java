package site.liangbai.clyra.chatmodel.service;

public interface PromptService {
    String generateModelList();
    String generateCommandDetails(String command);
}
