package com.jrobot.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum RobotCommand {
    // Mouse commands
    CLICK("click", "clique", 2),
    RIGHT_CLICK("rightclick", "cliquedireito", 2),
    DOUBLE_CLICK("doubleclick", "duploclick", 2),
    DRAG("drag", "arrastar", 4),
    MOVE("move", "mover", 2),
    
    // Keyboard commands
    TYPE("type", "digite", -1),
    PRESS("press", "pressione", 1),
    COPY("copy", "copie", 1),
    PASTE("paste", "cole", -1),
    
    // Special key combinations
    CONTROL("control", "control", 1),
    ALT("alt", "alt", 1),
    SHIFT("shift", "shift", 1),
    CONTROL_SHIFT("controlshift", "controlshift", 1),
    ALT_SHIFT("altshift", "altshift", 1),
    CONTROL_ALT("controlalt", "controlalt", 1),
    
    // Utility commands
    DELAY("delay", "espere", 1),
    REPEAT("repeat", "repita", 2),
    
    // Variable commands
    SET_VAR("set", "defina", 2),
    GET_VAR("get", "obter", 1),
    
    // Flow control commands
    IF("if", "se", 3), // comando se var1 == var2
    ELSE("else", "senao", 0),
    WHILE("while", "enquanto", 3), // comando enquanto var1 == var2
    END("end", "fim", 0), // fim do bloco if/while
    BREAK("break", "pare", 0); // para o loop atual

    private final String englishName;
    private final String portugueseName;
    private final int expectedArgs; // -1 means variable number of args

    RobotCommand(String englishName, String portugueseName, int expectedArgs) {
        this.englishName = englishName;
        this.portugueseName = portugueseName;
        this.expectedArgs = expectedArgs;
    }

    public static Optional<RobotCommand> fromString(String name) {
        String normalizedName = name.toLowerCase().replace("_", "").trim();
        return Arrays.stream(values())
                .filter(cmd -> cmd.englishName.equals(normalizedName) || 
                             cmd.portugueseName.equals(normalizedName))
                .findFirst();
    }

    public int getExpectedArgs() {
        return expectedArgs;
    }

    public static List<String> getDocumentation() {
        return Arrays.asList(
            "Comandos disponíveis / Available commands:",
            "",
            "Mouse:",
            "- click/clique x,y                    # Click at position / Clique na posição",
            "- rightclick/cliquedireito x,y        # Right click / Clique direito",
            "- doubleclick/duploclick x,y          # Double click / Clique duplo",
            "- drag/arrastar x1,y1,x2,y2           # Click and drag / Clique e arraste",
            "- move/mover x,y                      # Move mouse / Mover mouse",
            "",
            "Teclado / Keyboard:",
            "- type/digite \"text\"                  # Type text / Digite texto",
            "- type/digite $var                    # Type variable content / Digite conteúdo da variável",
            "- press/pressione KEY                 # Press key / Pressione tecla",
            "- copy/copie varname                  # Copy to variable / Copiar para variável",
            "- paste/cole                          # Paste clipboard / Colar área de transferência",
            "- paste/cole $var                     # Paste variable / Colar variável",
            "",
            "Variáveis / Variables:",
            "- set/defina varname \"value\"         # Set variable / Definir variável",
            "- get/obter varname                   # Get variable / Obter variável",
            "",
            "Controle de Fluxo / Flow Control:",
            "- if/se var1 == var2                 # If condition / Se condição",
            "- if/se var1 != var2                 # If not equal / Se diferente",
            "- if/se var1 > var2                  # If greater / Se maior",
            "- if/se var1 < var2                  # If less / Se menor",
            "- else/senao                         # Else block / Senão",
            "- while/enquanto var1 == var2        # While loop / Enquanto",
            "- end/fim                            # End block / Fim do bloco",
            "- break/pare                         # Break loop / Parar loop",
            "",
            "Combinações / Combinations:",
            "- control/control KEY                 # Ctrl+Key / Ctrl+Tecla",
            "- alt/alt KEY                        # Alt+Key / Alt+Tecla",
            "- shift/shift KEY                    # Shift+Key / Shift+Tecla",
            "- controlshift KEY                   # Ctrl+Shift+Key",
            "- altshift KEY                       # Alt+Shift+Key",
            "- controlalt KEY                     # Ctrl+Alt+Key",
            "",
            "Utilitários / Utilities:",
            "- delay/espere ms                    # Wait/Esperar (milliseconds)",
            "- repeat/repita N COMMAND            # Repeat command N times",
            "",
            "Exemplos / Examples:",
            "# Variáveis",
            "set contador \"0\"",
            "set max \"5\"",
            "",
            "# Loop com while",
            "while contador < max",
            "    click 100,200",
            "    set contador \"$contador + 1\"",
            "end",
            "",
            "# Condicionais",
            "if valor == \"teste\"",
            "    type \"É teste\"",
            "else",
            "    type \"Não é teste\"",
            "end",
            "",
            "# Loop com break",
            "while true == true",
            "    if contador == max",
            "        break",
            "    end",
            "    click 100,200",
            "end"
        );
    }
} 