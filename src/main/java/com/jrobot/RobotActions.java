package com.jrobot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import com.jrobot.commands.ScriptInterpreter;

public class RobotActions {
    private final Robot robot;
    private static final Map<Character, int[]> SPECIAL_CHARS;

    static {
        SPECIAL_CHARS = new HashMap<>();
        // Vogais acentuadas minúsculas
        SPECIAL_CHARS.put('á', new int[] { KeyEvent.VK_DEAD_ACUTE, KeyEvent.VK_A });
        SPECIAL_CHARS.put('à', new int[] { KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_A });
        SPECIAL_CHARS.put('ã', new int[] { KeyEvent.VK_DEAD_TILDE, KeyEvent.VK_A });
        SPECIAL_CHARS.put('â', new int[] { KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_A });
        SPECIAL_CHARS.put('é', new int[] { KeyEvent.VK_DEAD_ACUTE, KeyEvent.VK_E });
        SPECIAL_CHARS.put('è', new int[] { KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_E });
        SPECIAL_CHARS.put('ê', new int[] { KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_E });
        SPECIAL_CHARS.put('í', new int[] { KeyEvent.VK_DEAD_ACUTE, KeyEvent.VK_I });
        SPECIAL_CHARS.put('ì', new int[] { KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_I });
        SPECIAL_CHARS.put('î', new int[] { KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_I });
        SPECIAL_CHARS.put('ó', new int[] { KeyEvent.VK_DEAD_ACUTE, KeyEvent.VK_O });
        SPECIAL_CHARS.put('ò', new int[] { KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_O });
        SPECIAL_CHARS.put('õ', new int[] { KeyEvent.VK_DEAD_TILDE, KeyEvent.VK_O });
        SPECIAL_CHARS.put('ô', new int[] { KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_O });
        SPECIAL_CHARS.put('ú', new int[] { KeyEvent.VK_DEAD_ACUTE, KeyEvent.VK_U });
        SPECIAL_CHARS.put('ù', new int[] { KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_U });
        SPECIAL_CHARS.put('û', new int[] { KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_U });

        // Vogais acentuadas maiúsculas
        SPECIAL_CHARS.put('Á', new int[] { KeyEvent.VK_DEAD_ACUTE, KeyEvent.VK_A });
        SPECIAL_CHARS.put('À', new int[] { KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_A });
        SPECIAL_CHARS.put('Ã', new int[] { KeyEvent.VK_DEAD_TILDE, KeyEvent.VK_A });
        SPECIAL_CHARS.put('Â', new int[] { KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_A });
        SPECIAL_CHARS.put('É', new int[] { KeyEvent.VK_DEAD_ACUTE, KeyEvent.VK_E });
        SPECIAL_CHARS.put('È', new int[] { KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_E });
        SPECIAL_CHARS.put('Ê', new int[] { KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_E });
        SPECIAL_CHARS.put('Í', new int[] { KeyEvent.VK_DEAD_ACUTE, KeyEvent.VK_I });
        SPECIAL_CHARS.put('Ì', new int[] { KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_I });
        SPECIAL_CHARS.put('Î', new int[] { KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_I });
        SPECIAL_CHARS.put('Ó', new int[] { KeyEvent.VK_DEAD_ACUTE, KeyEvent.VK_O });
        SPECIAL_CHARS.put('Ò', new int[] { KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_O });
        SPECIAL_CHARS.put('Õ', new int[] { KeyEvent.VK_DEAD_TILDE, KeyEvent.VK_O });
        SPECIAL_CHARS.put('Ô', new int[] { KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_O });
        SPECIAL_CHARS.put('Ú', new int[] { KeyEvent.VK_DEAD_ACUTE, KeyEvent.VK_U });
        SPECIAL_CHARS.put('Ù', new int[] { KeyEvent.VK_DEAD_GRAVE, KeyEvent.VK_U });
        SPECIAL_CHARS.put('Û', new int[] { KeyEvent.VK_DEAD_CIRCUMFLEX, KeyEvent.VK_U });

        // Caracteres especiais
        SPECIAL_CHARS.put('ç', new int[] { KeyEvent.VK_DEAD_CEDILLA, KeyEvent.VK_C });
        SPECIAL_CHARS.put('Ç', new int[] { KeyEvent.VK_DEAD_CEDILLA, KeyEvent.VK_C });
        SPECIAL_CHARS.put('ñ', new int[] { KeyEvent.VK_DEAD_TILDE, KeyEvent.VK_N });
        SPECIAL_CHARS.put('Ñ', new int[] { KeyEvent.VK_DEAD_TILDE, KeyEvent.VK_N });

        // Outros caracteres especiais
        SPECIAL_CHARS.put('!', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_1 });
        SPECIAL_CHARS.put('@', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_2 });
        SPECIAL_CHARS.put('#', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_3 });
        SPECIAL_CHARS.put('$', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_4 });
        SPECIAL_CHARS.put('%', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_5 });
        SPECIAL_CHARS.put('¨', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_6 });
        SPECIAL_CHARS.put('&', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_7 });
        SPECIAL_CHARS.put('*', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_8 });
        SPECIAL_CHARS.put('(', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_9 });
        SPECIAL_CHARS.put(')', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_0 });
        SPECIAL_CHARS.put('_', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS });
        SPECIAL_CHARS.put('+', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_EQUALS });
        SPECIAL_CHARS.put('=', new int[] { -1, KeyEvent.VK_EQUALS });
        SPECIAL_CHARS.put('-', new int[] { -1, KeyEvent.VK_MINUS });
        SPECIAL_CHARS.put('§', new int[] { -1, KeyEvent.VK_UNDEFINED }); // Adicione o tratamento específico
        SPECIAL_CHARS.put('¢', new int[] { -1, KeyEvent.VK_UNDEFINED }); // Adicione o tratamento específico
        SPECIAL_CHARS.put('£', new int[] { -1, KeyEvent.VK_UNDEFINED }); // Adicione o tratamento específico
        SPECIAL_CHARS.put('³', new int[] { -1, KeyEvent.VK_UNDEFINED }); // Adicione o tratamento específico
        SPECIAL_CHARS.put('²', new int[] { -1, KeyEvent.VK_UNDEFINED }); // Adicione o tratamento específico
        SPECIAL_CHARS.put('¹', new int[] { -1, KeyEvent.VK_UNDEFINED }); // Adicione o tratamento específico
        SPECIAL_CHARS.put('´', new int[] { -1, KeyEvent.VK_DEAD_ACUTE });
        SPECIAL_CHARS.put('`', new int[] { -1, KeyEvent.VK_BACK_QUOTE });
        SPECIAL_CHARS.put('[', new int[] { -1, KeyEvent.VK_OPEN_BRACKET });
        SPECIAL_CHARS.put('{', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET });
        SPECIAL_CHARS.put('ª', new int[] { -1, KeyEvent.VK_UNDEFINED }); // Adicione o tratamento específico
        SPECIAL_CHARS.put('~', new int[] { -1, KeyEvent.VK_DEAD_TILDE });
        SPECIAL_CHARS.put('^', new int[] { -1, KeyEvent.VK_DEAD_CIRCUMFLEX });
        SPECIAL_CHARS.put(']', new int[] { -1, KeyEvent.VK_CLOSE_BRACKET });
        SPECIAL_CHARS.put('}', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET });
        SPECIAL_CHARS.put('º', new int[] { -1, KeyEvent.VK_UNDEFINED }); // Adicione o tratamento específico
        SPECIAL_CHARS.put(';', new int[] { -1, KeyEvent.VK_SEMICOLON });
        SPECIAL_CHARS.put(':', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON });
        SPECIAL_CHARS.put('.', new int[] { -1, KeyEvent.VK_PERIOD });
        SPECIAL_CHARS.put(',', new int[] { -1, KeyEvent.VK_COMMA });
        SPECIAL_CHARS.put('>', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD });
        SPECIAL_CHARS.put('<', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_COMMA });
        SPECIAL_CHARS.put('/', new int[] { -1, KeyEvent.VK_SLASH });
        SPECIAL_CHARS.put('?', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH });
        SPECIAL_CHARS.put('°', new int[] { -1, KeyEvent.VK_UNDEFINED }); // Adicione o tratamento específico
        SPECIAL_CHARS.put('|', new int[] { KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH });
        SPECIAL_CHARS.put('\\', new int[] { -1, KeyEvent.VK_BACK_SLASH });
    }

    public RobotActions() throws AWTException {
        this.robot = new Robot();
        robot.setAutoDelay(50); // Add small delay between actions
    }

    // Mouse actions
    public void moveMouseTo(int x, int y) {
        robot.mouseMove(x, y);
    }

    public void clickMouse() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void rightClickMouse() {
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }

    public void doubleClickMouse() {
        clickMouse();
        delay(50);
        clickMouse();
    }

    public void dragMouse(int fromX, int fromY, int toX, int toY) {
        moveMouseTo(fromX, fromY);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        moveMouseTo(toX, toY);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    // Keyboard actions
    public void pressKey(int keyCode) {
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
    }

    public void holdKey(int keyCode) {
        robot.keyPress(keyCode);
    }

    public void releaseKey(int keyCode) {
        robot.keyRelease(keyCode);
    }

    public void typeText(String text) {
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);

            // Verificar sequência de escape
            if (c == '\\' && i + 1 < text.length()) {
                char nextChar = text.charAt(i + 1);
                if (nextChar == 'n' || nextChar == 'r') {
                    // Pressionar Enter para quebra de linha
                    pressKey(KeyEvent.VK_ENTER);
                    i += 2; // Pular os dois caracteres (\n ou \r)
                    continue;
                }
            }

            // Verificar se é uma variável
            if (c == '$' && i + 1 < text.length()) {
                StringBuilder varName = new StringBuilder();
                int j = i + 1;
                // Capturar nome da variável (letras, números e _)
                while (j < text.length() && (Character.isLetterOrDigit(text.charAt(j)) || text.charAt(j) == '_')) {
                    varName.append(text.charAt(j));
                    j++;
                }

                if (varName.length() > 0) {
                    // Se a variável existe no ScriptInterpreter, use seu valor
                    String value = ScriptInterpreter.getVariableValue(varName.toString());
                    if (value != null) {
                        // Digitar o valor da variável
                        for (char vc : value.toCharArray()) {
                            typeChar(vc);
                        }
                    } else {
                        // Se a variável não existe, digitar como texto
                        typeChar('$');
                        for (char vc : varName.toString().toCharArray()) {
                            typeChar(vc);
                        }
                    }
                    i = j; // Avançar o índice para depois da variável
                    continue;
                }
            }

            typeChar(c);
            i++;
        }
    }

    private void typeChar(char character) {
        try {
            if (SPECIAL_CHARS.containsKey(character)) {
                int[] keys = SPECIAL_CHARS.get(character);
                if (keys[0] != -1) {
                    robot.keyPress(keys[0]);
                }
                robot.keyPress(keys[1]);
                robot.keyRelease(keys[1]);
                if (keys[0] != -1) {
                    robot.keyRelease(keys[0]);
                }
            } else {
                int keyCode = KeyEvent.getExtendedKeyCodeForChar(character);
                if (keyCode != KeyEvent.VK_UNDEFINED) {
                    robot.keyPress(keyCode);
                    robot.keyRelease(keyCode);
                } else {
                    System.err.println("Unable to type character: " + character);
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Unable to type character: " + character);
        }
    }

    // Combination keys
    public void pressControlKey(int keyCode) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void pressAltKey(int keyCode) {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    public void pressShiftKey(int keyCode) {
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    public void pressControlShiftKey(int keyCode) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void pressAltShiftKey(int keyCode) {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    public void pressControlAltKey(int keyCode) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    // Utility methods
    public void delay(int ms) {
        robot.delay(ms);
    }

    public Point getMousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }

    public Color getColorAtMouse() {
        Point mousePos = getMousePosition();
        return robot.getPixelColor(mousePos.x, mousePos.y);
    }
    
    public Color getColorAt(int x, int y) {
        return robot.getPixelColor(x, y);
    }
    
       
    /**
     * Alias para permitir chamadas $getColor(x,y) nos scripts.
     * Retorna o valor hexadecimal #RRGGBB do pixel em (x,y).
     */
    public String getColor(int x, int y) {
        return getHexaColorAt(x, y);
    }

    /**
     * Retorna a cor em formato hexadecimal (#RRGGBB) para a posição especificada
     * 
     * @param x coordenada X
     * @param y coordenada Y
     * @return String no formato "#RRGGBB"
     */
    public String getHexaColorAt(int x, int y) {
        Color color = getColorAt(x, y);
        return String.format("#%02x%02x%02x",
                color.getRed(),
                color.getGreen(),
                color.getBlue());
    }

}