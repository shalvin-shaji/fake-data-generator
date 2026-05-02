package com.fdg.api;

public class ProduceResult {
    private String modelId;
    private String topic;
    private int sent;

    public ProduceResult(String modelId, String topic, int sent) {
        this.modelId = modelId;
        this.topic = topic;
        this.sent = sent;
    }

    public String getModelId() { return modelId; }
    public String getTopic() { return topic; }
    public int getSent() { return sent; }
}
