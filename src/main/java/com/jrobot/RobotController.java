package com.jrobot;

import java.awt.AWTException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.jrobot.commands.ScriptInterpreter;
import com.jrobot.ui.AutomationInterface;
import javax.swing.SwingUtilities;

public class RobotController implements NativeKeyListener {
    private final RobotActions actions;
    private final ScriptInterpreter interpreter;
    private final AutomationInterface automationInterface;
    private Thread automationThread;
    private boolean isRunning = false;
    private String currentScript = null;

    public RobotController(AutomationInterface automationInterface) throws AWTException {
        this.automationInterface = automationInterface;
        this.actions = new RobotActions();
        this.interpreter = new ScriptInterpreter(actions);
        setupKeyListener();
    }

    private void setupKeyListener() {
        try {
            // Desabilitar log do JNativeHook
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            logger.setUseParentHandlers(false);
            
            // Garantir que o hook global está registrado
            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook();
            }
            
            // Remover listeners existentes e adicionar este controller
            GlobalScreen.removeNativeKeyListener(this);
            GlobalScreen.addNativeKeyListener(this);
        } catch (NativeHookException e) {
            System.err.println("Error registering key listener: " + e.getMessage());
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F7) {
            SwingUtilities.invokeLater(this::stopAutomation);
        } else if (e.getKeyCode() == NativeKeyEvent.VC_F6 && !isRunning) {
            SwingUtilities.invokeLater(this::startAutomation);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Not needed
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Not needed
    }

    public void loadScriptFromFile(String filePath) {
        currentScript = filePath;
    }

    public boolean hasScriptLoaded() {
        return currentScript != null;
    }

    public void startAutomation() {
        if (!hasScriptLoaded() || isRunning) {
            return;
        }
        
        interpreter.setForceStop(false);
        isRunning = true;
        automationInterface.updateButtonStates(true);
        
        automationThread = new Thread(() -> {
            try {
                interpreter.executeFile(currentScript);
            } catch (IOException e) {
                System.err.println("Error executing script file: " + e.getMessage());
            } finally {
                isRunning = false;
                automationInterface.updateButtonStates(false);
                automationInterface.updateButtonVisibility(true);
            }
        });
        automationThread.start();
    }

    public void stopAutomation() {
        if (isRunning) {
            interpreter.setForceStop(true);
            isRunning = false;
            automationInterface.updateButtonStates(false);
            automationInterface.updateButtonVisibility(true);
        }
    }

    public void cleanup() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            System.err.println("Error unregistering key listener: " + e.getMessage());
        }
    }

    // Getter para acesso às ações do robot (caso necessário)
    public RobotActions getActions() {
        return actions;
    }
} 