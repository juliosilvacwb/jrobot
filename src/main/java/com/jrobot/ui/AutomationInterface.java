package com.jrobot.ui;

/**
 * Interface for communication between the RobotController and the UI
 */
public interface AutomationInterface {
    /**
     * Updates the UI button states based on automation status
     * @param isRunning true if automation is running, false otherwise
     */
    void updateButtonStates(boolean isRunning);

    /**
     * Updates the visibility of buttons in the UI
     * @param visible true to show buttons, false to hide
     */
    void updateButtonVisibility(boolean visible);
} 