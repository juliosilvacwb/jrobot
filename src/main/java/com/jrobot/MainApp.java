package com.jrobot;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.jrobot.ui.AutomationInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends JFrame implements NativeKeyListener, AutomationInterface {
    private static MainApp instance;
    private final RobotController robotController;
    private final JButton playButton;
    private final JButton stopButton;
    private final JButton selectFileButton;
    private final JLabel scriptFileLabel;
    private Point initialClick; 

    public MainApp() {
        super("JRobot");
        instance = this;
        
        // Set Windows Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize robot controller
        try {
            robotController = new RobotController(this);
        } catch (AWTException e) {
            throw new RuntimeException("Failed to initialize Robot", e);
        }

        // Configure window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);
        setUndecorated(true); // Remove a decoração padrão da janela

        // Set custom icon
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/chatbot.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Erro ao carregar o ícone: " + e.getMessage());
        }

        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Create top panel for close button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JButton closeButton = new JButton("×");
        closeButton.setForeground(Color.RED);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> {
            // Cleanup and exit
            if (robotController != null) {
                robotController.stopAutomation();
            }
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException ex) {
                System.err.println("Error unregistering native hook");
            }
            System.exit(0);
        });
        topPanel.add(closeButton);
        topPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
        mainPanel.add(topPanel);

        // Adicionar capacidade de arrastar a janela
        mainPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });

        mainPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                // Get current location
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                // Determine how much the mouse moved
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Move window to this position
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });

        // Create script file label with fixed height
        scriptFileLabel = new JLabel("Nenhum script selecionado");
        scriptFileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scriptFileLabel.setPreferredSize(new Dimension(120, 20));
        scriptFileLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
        
        // Create buttons
        selectFileButton = new JButton("Selecionar Script");
        playButton = new JButton("Play (F6)");
        stopButton = new JButton("Stop (F7)");
        stopButton.setEnabled(false);

        // Style buttons
        Dimension buttonSize = new Dimension(120, 25);
        selectFileButton.setPreferredSize(buttonSize);
        playButton.setPreferredSize(buttonSize);
        stopButton.setPreferredSize(buttonSize);
        selectFileButton.setMaximumSize(buttonSize);
        playButton.setMaximumSize(buttonSize);
        stopButton.setMaximumSize(buttonSize);
        
        // Add action listeners
        selectFileButton.addActionListener(this::selectFile);
        playButton.addActionListener(this::startAutomation);
        stopButton.addActionListener(this::stopAutomation);

        // Add components with minimal spacing
        mainPanel.add(scriptFileLabel);
        mainPanel.add(Box.createVerticalStrut(3));
        mainPanel.add(selectFileButton);
        mainPanel.add(Box.createVerticalStrut(3));
        mainPanel.add(playButton);
        mainPanel.add(Box.createVerticalStrut(3));
        mainPanel.add(stopButton);

        // Add panel to frame
        add(mainPanel);
        
        // Pack and position the window at the bottom of the screen
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width - getWidth() - 10, screenSize.height - getHeight() - 50);

        // Setup global hotkey
        try {
            // Turn off logging
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            
            // Register this instance as a key listener
            GlobalScreen.addNativeKeyListener(this);
        } catch (Exception ex) {
            System.err.println("Failed to register native hook: " + ex.getMessage());
            System.exit(1);
        }
    }

    private void selectFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }
            public String getDescription() {
                return "Arquivos de Script (*.txt)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            robotController.loadScriptFromFile(selectedFile.getAbsolutePath());
            scriptFileLabel.setText(selectedFile.getName());
            playButton.setEnabled(true);
        }
    }

    private void startAutomation(ActionEvent e) {
        if (!robotController.hasScriptLoaded()) {
            selectFile(null);
            return;
        }
        
        updateButtonStates(true);
        updateButtonVisibility(false);
        robotController.startAutomation();
    }

    public void stopAutomation(ActionEvent e) {
        updateButtonStates(false);
        updateButtonVisibility(true);
        robotController.stopAutomation();
    }

    private void captureMousePosition() {
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        String position = mousePos.x + "," + mousePos.y;
        
        // Copiar para a área de transferência
        StringSelection selection = new StringSelection(position);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        
        // Mostrar notificação
        SwingUtilities.invokeLater(() -> {
            ToastNotification toast = new ToastNotification("Posição copiada: " + position);
            toast.setVisible(true);
            toast.display();
        });
    }

    private void captureScreenColor() {
        Color color = robotController.getActions().getColorAtMouse();
        String hexColor = String.format("#%02x%02x%02x", 
            color.getRed(), color.getGreen(), color.getBlue());
        
        // Copiar para a área de transferência
        StringSelection selection = new StringSelection(hexColor);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        
        // Mostrar notificação com preview da cor
        SwingUtilities.invokeLater(() -> {
            ToastNotification toast = new ToastNotification("Cor copiada: " + hexColor, color);
            toast.setVisible(true);
            toast.display();
        });
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F8) {
            SwingUtilities.invokeLater(this::captureMousePosition);
        } else if (e.getKeyCode() == NativeKeyEvent.VC_F9) {
            SwingUtilities.invokeLater(this::captureScreenColor);
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

    @Override
    public void updateButtonStates(boolean isRunning) {
        SwingUtilities.invokeLater(() -> {
            playButton.setEnabled(!isRunning);
            stopButton.setEnabled(isRunning);
            selectFileButton.setEnabled(!isRunning);
        });
    }

    @Override
    public void updateButtonVisibility(boolean visible) {
        SwingUtilities.invokeLater(() -> {
            playButton.setVisible(visible);
            selectFileButton.setVisible(visible);
            pack(); // Reajusta o tamanho da janela
        });
    }

    public static MainApp getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
} 