package com.jrobot;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ToastNotification extends JWindow {
    private static final int DISPLAY_TIME = 2000; // 2 segundos
    private static final int FADE_TIME = 500; // 0.5 segundos
    private static final float MAX_OPACITY = 0.8f;
    
    public ToastNotification(String message) {
        this(message, null);
    }

    public ToastNotification(String message, Color previewColor) {
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());
        
        // Criar label com a mensagem
        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Adicionar label ao painel com fundo escuro
        JPanel panel = new JPanel();

        Color bg = previewColor != null
                    ? previewColor
                    : new Color(0, 0, 0, 220);
        panel.setBackground(bg);

        panel.add(label);
        add(panel);

        // Configurar tamanho e posição
        pack();
        setLocationRelativeTo(null);
        
        // Arredondar bordas
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
    }

    public void display() {
        new Thread(() -> {
            try {
                // Fade in
                for (float opacity = 0.0f; opacity <= MAX_OPACITY; opacity += 0.1f) {
                    setOpacity(opacity);
                    Thread.sleep(FADE_TIME / 10);
                }

                // Esperar
                Thread.sleep(DISPLAY_TIME);

                // Fade out
                for (float opacity = MAX_OPACITY; opacity >= 0.0f; opacity -= 0.1f) {
                    setOpacity(opacity);
                    Thread.sleep(FADE_TIME / 10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                dispose();
            }
        }).start();
    }
} 