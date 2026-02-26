package com.maskedsyntax.lockfile.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.util.Duration;

public class IdleManager {
    private final Timeline timeline;
    private final Runnable onIdle;
    private Scene currentScene;
    private final EventHandler<Event> activityHandler;

    public IdleManager(Duration idleTime, Runnable onIdle) {
        this.onIdle = onIdle;
        this.timeline = new Timeline(new KeyFrame(idleTime, e -> onIdle.run()));
        this.timeline.setCycleCount(1);
        
        this.activityHandler = event -> reset();
    }

    public void start(Scene scene) {
        stop();
        this.currentScene = scene;
        if (currentScene != null) {
            currentScene.addEventFilter(Event.ANY, activityHandler);
            timeline.playFromStart();
        }
    }

    public void stop() {
        timeline.stop();
        if (currentScene != null) {
            currentScene.removeEventFilter(Event.ANY, activityHandler);
            currentScene = null;
        }
    }

    public void reset() {
        if (timeline.getStatus() == Timeline.Status.RUNNING) {
            timeline.playFromStart();
        }
    }
}
