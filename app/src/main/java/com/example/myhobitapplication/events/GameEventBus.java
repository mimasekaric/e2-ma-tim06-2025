package com.example.myhobitapplication.events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class GameEventBus {
    private static GameEventBus instance;
    private final MutableLiveData<GameEvent> _events = new MutableLiveData<>();

    private GameEventBus() {}

    public static synchronized GameEventBus getInstance() {
        if (instance == null) {
            instance = new GameEventBus();
        }
        return instance;
    }

    public void post(GameEvent event) {
        _events.postValue(event);
    }
    public LiveData<GameEvent> getEvents() {
        return _events;
    }
}
