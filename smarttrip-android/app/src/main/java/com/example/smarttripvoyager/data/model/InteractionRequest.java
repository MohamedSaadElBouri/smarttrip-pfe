package com.example.smarttripvoyager.data.model;

public class InteractionRequest {
    private String entiteType;
    private Long entiteId;
    private String typeSignal;

    public InteractionRequest(String entiteType, Long entiteId, String typeSignal) {
        this.entiteType = entiteType;
        this.entiteId = entiteId;
        this.typeSignal = typeSignal;
    }
}
