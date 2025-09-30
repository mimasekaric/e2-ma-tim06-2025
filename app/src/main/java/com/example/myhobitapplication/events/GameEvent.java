package com.example.myhobitapplication.events;

import com.example.myhobitapplication.services.AllianceMissionService;

public class GameEvent {

    private final AllianceMissionService.MissionEventType eventType;
    private final String userId;

    public GameEvent(AllianceMissionService.MissionEventType eventType, String userId) {
        this.eventType = eventType;
        this.userId = userId;
    }

    public AllianceMissionService.MissionEventType getEventType() {
        return eventType;
    }

    public String getUserId() {
        return userId;
    }
}
