package com.fdg.api;

public class GenerateRequest {
    private String modelId;
    private int count = 10;

    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
