package com.jrobot.commands;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.Method;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.jrobot.RobotActions;

public class ScriptInterpreter {
    private final RobotActions actions;
    private static final Pattern QUOTED_TEXT   = Pattern.compile("\"([^\"]*)\"");
    private static final Pattern VARIABLE_REF  = Pattern.compile("\\$([a-zA-Z][a-zA-Z0-9_]*)");
    private static final Pattern FUNCTION_CALL = Pattern.compile("^\\$([a-zA-Z][a-zA-Z0-9_]*)\\((.*)\\)$");
    private static final Map<String, String> variables = new HashMap<>();
    
    private final Stack<Integer> blockStack = new Stack<>();
    private final Stack<Boolean> conditionStack = new Stack<>();
    private final java.util.List<String> scriptLines = new ArrayList<>();
    private int currentLine = 0;
    private boolean breakLoop = false;
    private final ScriptEngine jsEngine;
    private volatile boolean forceStop = false;
    
    public ScriptInterpreter(RobotActions actions) {
        this.actions = actions;
        ScriptEngineManager manager = new ScriptEngineManager();
        this.jsEngine = manager.getEngineByName("js");
    }

    public void setForceStop(boolean value) {
        this.forceStop = value;
        if (value) {
            breakLoop = true;
            // Limpar as pilhas de controle
            blockStack.clear();
            conditionStack.clear();
        }
    }

    public void executeFile(String filename) throws IOException {
        scriptLines.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scriptLines.add(line.trim());
            }
        }
        executeScript();
    }

    public void executeScript(String script) {
        scriptLines.clear();
        scriptLines.addAll(Arrays.asList(script.split("\n")));
        executeScript();
    }

    private void executeScript() {
        currentLine = 0;
        while (currentLine < scriptLines.size() && !forceStop) {
            String line = scriptLines.get(currentLine).trim();
            if (!line.isEmpty() && !line.startsWith("//") && !line.startsWith("#")) {
                executeLine(line);
            }
            if (!breakLoop) {
                currentLine++;
            } else {
                // Se forceStop estiver ativo, sair do loop
                if (forceStop) {
                    break;
                }
                // Encontrar o fim do loop atual
                while (currentLine < scriptLines.size() && 
                       !scriptLines.get(currentLine).trim().matches("(?i)^(end|fim)\\s*$")) {
                    currentLine++;
                }
                breakLoop = false;
            }
        }
        // Resetar o estado se foi forçado a parar
        if (forceStop) {
            forceStop = false;
            breakLoop = false;
            blockStack.clear();
            conditionStack.clear();
        }
    }

    private void executeLine(String line) {
        try {
            String[] parts = splitCommand(line);
            if (parts.length == 0) return;

            String commandName = parts[0].toLowerCase();
            java.util.List<String> args = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                args.add(parts[i]);
            }

            RobotCommand.fromString(commandName).ifPresentOrElse(
                command -> executeCommand(command, args),
                () -> System.err.println("Comando desconhecido: " + commandName)
            );
        } catch (Exception e) {
            System.err.println("Erro ao executar linha: " + line);
            e.printStackTrace();
        }
    }

    private String[] splitCommand(String line) {
        java.util.List<String> parts = new ArrayList<>();
        Matcher m = QUOTED_TEXT.matcher(line);
        
        // Primeiro, extrair o comando
        String command = line.split("\\s+")[0];
        parts.add(command);
        
        // Remover o comando da linha
        line = line.substring(command.length()).trim();
        
        // Depois, extrair texto entre aspas
        m = QUOTED_TEXT.matcher(line);
        if (m.find()) {
            parts.add(m.group(1));
        } else {
            // Se não houver texto entre aspas, adicionar o resto como argumentos
            String[] remaining = line.trim().split("\\s+");
            for (String part : remaining) {
                if (!part.isEmpty()) {
                    parts.add(part);
                }
            }
        }
        
        return parts.toArray(new String[0]);
    }

    private String resolveVariables(String text) {
        if (text == null || !text.contains("$")) {
            return text;
        }

        Matcher m = VARIABLE_REF.matcher(text);
        StringBuffer result = new StringBuffer();
        
        while (m.find()) {
            String varName = m.group(1);
            String value = variables.getOrDefault(varName, "");
            m.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        m.appendTail(result);
        
        return result.toString();
    }

    private void executeCommand(RobotCommand command, java.util.List<String> args) {
        try {
            switch (command) {
                case CLICK:
                    executeClick(args);
                    break;
                case RIGHT_CLICK:
                    executeRightClick(args);
                    break;
                case DOUBLE_CLICK:
                    executeDoubleClick(args);
                    break;
                case DRAG:
                    executeDrag(args);
                    break;
                case MOVE:
                    executeMove(args);
                    break;
                case TYPE:
                    executeType(args);
                    break;
                case PRESS:
                    executePress(args);
                    break;
                case COPY:
                    // Apenas executa Ctrl+C
                    actions.pressControlKey(KeyEvent.VK_C);
                    actions.delay(100); // Pequeno delay para o sistema processar
                    break;
                case PASTE:
                    // Apenas executa Ctrl+V
                    actions.pressControlKey(KeyEvent.VK_V);
                    break;
                case CONTROL:
                    executeControl(args);
                    break;
                case ALT:
                    executeAlt(args);
                    break;
                case SHIFT:
                    executeShift(args);
                    break;
                case CONTROL_SHIFT:
                    executeControlShift(args);
                    break;
                case ALT_SHIFT:
                    executeAltShift(args);
                    break;
                case CONTROL_ALT:
                    executeControlAlt(args);
                    break;
                case DELAY:
                    executeDelay(args);
                    break;
                case REPEAT:
                    executeRepeat(args);
                    break;
                case SET_VAR:
                    setVar(args);
                    break;
                case GET_VAR:
                    String getVarName = args.get(0);
                    String getVarValue = variables.getOrDefault(getVarName, "");
                    System.out.println(getVarValue);
                    break;
                case IF:
                    handleIf(args);
                    break;
                case ELSE:
                    handleElse();
                    break;
                case WHILE:
                    handleWhile(args);
                    break;
                case END:
                    handleEnd();
                    break;
                case BREAK:
                    breakLoop = true;
                    break;
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar comando " + command + ": " + e.getMessage());
        }
    }

    private void setVar(java.util.List<String> args) {
        String varName = args.get(0);
        // Junta a expressão mantendo espaços e aspas
        String expression = String.join(" ", args.subList(1, args.size())).trim();
        String value;

        Matcher funcMatcher = FUNCTION_CALL.matcher(expression);
        if (funcMatcher.matches()) {
            // É chamada de função: $nomeFunc(arg1,arg2,...)
            String funcName = funcMatcher.group(1);
            String paramsStr = funcMatcher.group(2);
            String[] tokens = paramsStr.split(",");
            Object[] paramsArr = new Object[tokens.length];
            Class<?>[] paramTypes = new Class<?>[tokens.length];
            
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                // Se for variável ($x), resolve antes
                if (token.startsWith("$")) {
                    token = variables.getOrDefault(token.substring(1), "");
                }
                int intVal = Integer.parseInt(token);
                paramsArr[i] = intVal;
                paramTypes[i] = int.class;
            }
            
            try {
                Method m = actions.getClass().getMethod(funcName, paramTypes);
                Object result = m.invoke(actions, paramsArr);
                // Preserva o resultado exatamente como retornado
                value = (result != null ? result.toString() : "");
            } catch (Exception e) {
                System.err.println("Erro ao chamar função " + funcName + ": " + e.getMessage());
                e.printStackTrace();
                value = "";
            }
        } else {
            // Não é chamada de função
            String resolvedExpr = resolveVariables(expression);
            
            // Se começa com # é uma cor, preserva exatamente como está
            if (resolvedExpr.startsWith("#")) {
                value = resolvedExpr;
            } else {
                // Remove aspas apenas se não for cor
                resolvedExpr = resolvedExpr.replaceAll("^\"|\"$", "");
                try {
                    value = evaluateMathExpression(resolvedExpr);
                } catch (Exception e) {
                    System.err.println("Erro ao avaliar expressão: " + resolvedExpr);
                    e.printStackTrace();
                    value = "";
                }
            }
        }
        
        // Armazena o valor sem modificar
        variables.put(varName, value);
    }

    private void executeClick(java.util.List<String> args) {
        Point point = parsePoint(args.get(0));
        actions.moveMouseTo(point.x, point.y);
        actions.clickMouse();
    }

    private void executeRightClick(java.util.List<String> args) {
        Point point = parsePoint(args.get(0));
        actions.moveMouseTo(point.x, point.y);
        actions.rightClickMouse();
    }

    private void executeDoubleClick(java.util.List<String> args) {
        Point point = parsePoint(args.get(0));
        actions.moveMouseTo(point.x, point.y);
        actions.doubleClickMouse();
    }

    private void executeDrag(java.util.List<String> args) {
        String[] coords = String.join(",", args).split(",");
        if (coords.length != 4) {
            throw new IllegalArgumentException("Drag requer 4 coordenadas: x1,y1,x2,y2");
        }
        int x1 = Integer.parseInt(coords[0].trim());
        int y1 = Integer.parseInt(coords[1].trim());
        int x2 = Integer.parseInt(coords[2].trim());
        int y2 = Integer.parseInt(coords[3].trim());
        actions.dragMouse(x1, y1, x2, y2);
    }

    private void executeMove(java.util.List<String> args) {
        Point point = parsePoint(args.get(0));
        actions.moveMouseTo(point.x, point.y);
    }

    private void executeType(java.util.List<String> args) {
        String text = args.get(0);
        if (text.startsWith("$")) {
            text = variables.getOrDefault(text.substring(1), "");
        }
        actions.typeText(text);
    }

    private void executePress(java.util.List<String> args) {
        actions.pressKey(parseKey(args.get(0)));
    }

    private void executeControl(java.util.List<String> args) {
        actions.pressControlKey(parseKey(args.get(0)));
    }

    private void executeAlt(java.util.List<String> args) {
        actions.pressAltKey(parseKey(args.get(0)));
    }

    private void executeShift(java.util.List<String> args) {
        actions.pressShiftKey(parseKey(args.get(0)));
    }

    private void executeControlShift(java.util.List<String> args) {
        actions.pressControlShiftKey(parseKey(args.get(0)));
    }

    private void executeAltShift(java.util.List<String> args) {
        actions.pressAltShiftKey(parseKey(args.get(0)));
    }

    private void executeControlAlt(java.util.List<String> args) {
        actions.pressControlAltKey(parseKey(args.get(0)));
    }

    private void executeDelay(java.util.List<String> args) {
        actions.delay(Integer.parseInt(args.get(0)));
    }

    private void executeRepeat(java.util.List<String> args) {
        if (args.size() < 2) {
            throw new IllegalArgumentException("Repeat requer pelo menos 2 argumentos: número de repetições e comando");
        }

        int times = Integer.parseInt(args.get(0));
        // Reconstruir o comando removendo o número de repetições
        String command = String.join(" ", args.subList(1, args.size()));
        
        // Substituir palavras em português por inglês para comandos básicos
        command = command
            .replace("clique", "press")
            .replace("digite", "type")
            .replace("espere", "delay");

        for (int i = 0; i < times && !forceStop; i++) {
            executeLine(command);
            // Pequeno delay entre repetições para evitar problemas
            actions.delay(50);
        }
    }

    private static Point parsePoint(String coord) {
        String[] parts = coord.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Coordenada deve ser x,y");
        }
        return new Point(
            Integer.parseInt(parts[0].trim()),
            Integer.parseInt(parts[1].trim())
        );
    }

    private static int parseKey(String key) {
        // Tentar encontrar a tecla na classe KeyEvent
        try {
            return KeyEvent.class.getField("VK_" + key.toUpperCase()).getInt(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Tecla inválida: " + key);
        }
    }

    private static class Point {
        final int x;
        final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private void handleIf(java.util.List<String> args) {
        if (args.size() < 3) {
            throw new IllegalArgumentException("If requer 3 argumentos: var1 operador var2");
        }

        String var1 = resolveVariables(args.get(0));
        String operator = args.get(1);
        String var2 = resolveVariables(args.get(2));

        boolean condition = evaluateCondition(var1, operator, var2);
        conditionStack.push(condition);
        blockStack.push(currentLine);

        if (!condition) {
            // Pular para else ou end
            while (currentLine < scriptLines.size()) {
                currentLine++;
                String line = scriptLines.get(currentLine).trim().toLowerCase();
                if (line.matches("^(else|senao)\\s*$") || line.matches("^(end|fim)\\s*$")) {
                    break;
                }
            }
            currentLine--; // Compensar o incremento no loop principal
        }
    }

    private void handleElse() {
        if (conditionStack.isEmpty()) {
            throw new IllegalStateException("Else sem if correspondente");
        }

        if (conditionStack.peek()) {
            // Se a condição do if era verdadeira, pular o bloco else
            while (currentLine < scriptLines.size()) {
                currentLine++;
                if (scriptLines.get(currentLine).trim().matches("(?i)^(end|fim)\\s*$")) {
                    break;
                }
            }
            currentLine--; // Compensar o incremento no loop principal
        }
    }

    private void handleWhile(java.util.List<String> args) {
        if (args.size() < 3) {
            throw new IllegalArgumentException("While requer 3 argumentos: var1 operador var2");
        }

        String var1 = resolveVariables(args.get(0));
        String operator = args.get(1);
        String var2 = resolveVariables(args.get(2));

        boolean condition = evaluateCondition(var1, operator, var2);
        conditionStack.push(condition);
        blockStack.push(currentLine);

        if (!condition) {
            // Pular para end
            while (currentLine < scriptLines.size()) {
                currentLine++;
                if (scriptLines.get(currentLine).trim().matches("(?i)^(end|fim)\\s*$")) {
                    break;
                }
            }
        }
    }

    private void handleEnd() {
        if (blockStack.isEmpty()) {
            throw new IllegalStateException("End sem bloco correspondente");
        }

        int blockStart = blockStack.pop();
        boolean condition = conditionStack.pop();

        // Se for um while e a condição ainda é verdadeira, voltar ao início
        if (scriptLines.get(blockStart).trim().toLowerCase().startsWith("while") ||
            scriptLines.get(blockStart).trim().toLowerCase().startsWith("enquanto")) {
            String[] parts = splitCommand(scriptLines.get(blockStart));
            java.util.List<String> args = new ArrayList<>(Arrays.asList(parts).subList(1, parts.length));
            
            String var1 = resolveVariables(args.get(0));
            String operator = args.get(1);
            String var2 = resolveVariables(args.get(2));

            if (evaluateCondition(var1, operator, var2)) {
                currentLine = blockStart - 1; // -1 para compensar o incremento no loop principal
            }
        }
    }

    private boolean evaluateCondition(String var1, String operator, String var2) {
        // Remover aspas se presentes
        var1 = var1.replaceAll("^\"|\"$", "");
        var2 = var2.replaceAll("^\"|\"$", "");

        // Avaliar expressões matemáticas se necessário
        try {
            if (containsMathOperators(var1)) {
                var1 = evaluateMathExpression(var1);
            }
            if (containsMathOperators(var2)) {
                var2 = evaluateMathExpression(var2);
            }

            // Tentar converter para números
            try {
                double num1 = Double.parseDouble(var1);
                double num2 = Double.parseDouble(var2);
                return evaluateNumericCondition(num1, operator, num2);
            } catch (NumberFormatException e) {
                // Se não for número, comparar como strings
                return evaluateStringCondition(var1, operator, var2);
            }
        } catch (Exception e) {
            System.err.println("Erro ao avaliar condição: " + e.getMessage());
            return false;
        }
    }

    private boolean evaluateNumericCondition(double num1, String operator, double num2) {
        switch (operator) {
            case "==": return num1 == num2;
            case "!=": return num1 != num2;
            case ">": return num1 > num2;
            case "<": return num1 < num2;
            case ">=": return num1 >= num2;
            case "<=": return num1 <= num2;
            default: throw new IllegalArgumentException("Operador inválido: " + operator);
        }
    }

    private boolean evaluateStringCondition(String str1, String operator, String str2) {
        switch (operator) {
            case "==": return str1.equals(str2);
            case "!=": return !str1.equals(str2);
            case ">": return str1.compareTo(str2) > 0;
            case "<": return str1.compareTo(str2) < 0;
            case ">=": return str1.compareTo(str2) >= 0;
            case "<=": return str1.compareTo(str2) <= 0;
            default: throw new IllegalArgumentException("Operador inválido: " + operator);
        }
    }

    private boolean containsMathOperators(String expression) {
        return expression.matches(".*[+\\-*/%].*");
    }

    private String evaluateMathExpression(String expression) throws Exception {
        // Remover aspas se presentes
        expression = expression.replaceAll("^\"|\"$", "");
        
        // Substituir variáveis por seus valores
        Matcher m = VARIABLE_REF.matcher(expression);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String varName = m.group(1);
            String varValue = variables.getOrDefault(varName, "0");
            // Se o valor contiver aspas, removê-las
            varValue = varValue.replaceAll("^\"|\"$", "");
            m.appendReplacement(sb, Matcher.quoteReplacement(varValue));
        }
        m.appendTail(sb);
        expression = sb.toString();

        // Se após substituir variáveis ainda houver operadores matemáticos
        if (containsMathOperators(expression)) {
            // Avaliar a expressão
            Object result = jsEngine.eval(expression);
            
            // Converter para número se possível
            if (result instanceof Number) {
                double numResult = ((Number) result).doubleValue();
                // Se o resultado for um número inteiro, remover o .0
                if (numResult == Math.floor(numResult)) {
                    return String.format("%.0f", numResult);
                }
                return String.valueOf(numResult);
            }
            return result.toString();
        }
        
        // Se não houver operadores, retornar a expressão como está
        return expression;
    }

    public static String getVariableValue(String name) {
        return variables.get(name);
    }
} 