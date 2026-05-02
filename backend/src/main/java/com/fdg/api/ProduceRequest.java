package com.fdg.api;

public class ProduceRequest {
    private String modelId;
    private int count = 10;
    private String topic;

    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
}
