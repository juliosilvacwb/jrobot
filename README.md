# JRobot - Automation Tool

[:us: English](#english) | [:brazil: Português](#português)

<div id="english">

A Java application for mouse and keyboard automation with GUI and script support.

## 🚀 Features

- Simple GUI with Play/Stop buttons
- Global shortcuts (work even without application focus)
- Mouse position capture with F8
- Script system with variables and flow control support
- Support for commands in Portuguese and English

## 📋 Available Commands

### Mouse
```
click x,y                 clique x,y                # Clique na posição
rightclick x,y           cliquedireito x,y         # Clique direito
doubleclick x,y         duploclick x,y            # Clique duplo
drag x1,y1,x2,y2        arrastar x1,y1,x2,y2      # Clique e arraste
move x,y                 mover x,y                 # Mover mouse
```

### Keyboard
```
type "texto"             digite "texto"            # Digite texto
press TECLA             clique TECLA              # Pressione tecla
copy                    copie                     # Executa Ctrl+C
paste                   cole                      # Executa Ctrl+V

# Combinações de teclas / Key combinations
control TECLA                                     # Ctrl+Tecla
alt TECLA                                        # Alt+Tecla
shift TECLA                                      # Shift+Tecla
controlshift TECLA                               # Ctrl+Shift+Tecla
altshift TECLA                                   # Alt+Shift+Tecla
controlalt TECLA                                 # Ctrl+Alt+Tecla
```

### Variables
```
set NOME VALOR          defina NOME VALOR         # Define variável
get NOME               obter NOME                # Obtém valor da variável
```

### Flow Control
```
if VAR1 == VAR2        se VAR1 == VAR2           # Se condição
else                   senao                     # Senão
while VAR1 == VAR2     enquanto VAR1 == VAR2     # Enquanto condição
end                    fim                       # Fim do bloco
break                  pare                      # Para o loop
```

### Utilities
```
delay MS               espere MS                 # Espera em milissegundos
repeat N COMANDO       repita N COMANDO          # Repete comando N vezes
```

## 🔍 Comparison Operators

- `==` : Equal
- `!=` : Not equal
- `>` : Greater than
- `<` : Less than
- `>=` : Greater or equal
- `<=` : Less or equal

## 📝 Script Examples

### Basic Example
```
# Click and type
click 100,200
type "Hello, world!"
delay 1000
control a
copy text
click 300,300
paste $text
```

### Counter Loop
```
# Initialize counter
set counter "0"
set max "5"

# While loop
while $counter < $max
    click 100,200
    delay 500
    set counter "$counter + 1"
end
```

### Conditionals
```
# Check value
if $result == "success"
    type "Operation successful!"
else
    type "Operation failed"
end
```

### Loop with Break
```
# Infinite loop with break condition
while true == true
    click 100,200
    copy result
    
    if $result == "found"
        type "Item found!"
        break
    end
    
    delay 1000
end
```

## ⌨️ Global Shortcuts

- `F6`: Start automation
- `F7`: Stop automation
- `F8`: Capture mouse position

## 🛠️ Installation

1. Make sure you have Java 11 or higher installed
2. Clone the repository
3. Build with Maven:
```bash
mvn clean package
```
4. Run the generated JAR:
```bash
java -jar target/jrobot-1.0-SNAPSHOT.jar
```

## 📌 Notes

- Scripts can be saved in .txt files
- Comments start with # or //
- Variables are case-sensitive
- Strings must be in double quotes
- Indentation is optional but recommended
- Simple mathematical expressions are supported in variables

## 🤝 Contributing

Feel free to contribute with improvements through pull requests.

## 📄 License

This project is under the MIT license.

---

<div id="português">

# JRobot - Ferramenta de Automação

Um aplicativo Java para automação de mouse e teclado com interface gráfica e suporte a scripts.

## 🚀 Recursos

- Interface gráfica simples com botões Play/Stop
- Atalhos globais (funcionam mesmo sem foco na aplicação)
- Captura de posição do mouse com F8
- Sistema de scripts com suporte a variáveis e controle de fluxo
- Suporte a comandos em português e inglês

## 📋 Comandos Disponíveis

### Mouse
```
clique x,y                    # Clique na posição
cliquedireito x,y            # Clique direito
duploclick x,y               # Clique duplo
arrastar x1,y1,x2,y2         # Clique e arraste
mover x,y                    # Mover mouse
```

### Teclado
```
digite "texto"               # Digite texto
digite $variavel            # Digite conteúdo da variável
pressione TECLA            # Pressione tecla
copie nome_var            # Copiar para variável
cole                      # Colar área de transferência
cole $variavel           # Colar variável
```

### Combinações de Teclas
```
control TECLA              # Ctrl+Tecla
alt TECLA                 # Alt+Tecla
shift TECLA               # Shift+Tecla
controlshift TECLA        # Ctrl+Shift+Tecla
altshift TECLA            # Alt+Shift+Tecla
controlalt TECLA          # Ctrl+Alt+Tecla
```

### Variáveis
```
definir nome_var "valor"  # Definir variável
obter nome_var           # Obter valor da variável
```

### Controle de Fluxo
```
se var1 == var2          # Se condição
senao                    # Senão
enquanto var1 == var2    # Enquanto condição
fim                      # Fim do bloco
pare                     # Parar loop
```

### Utilitários
```
espere ms               # Esperar milissegundos
repita N comando        # Repetir comando N vezes
```

## 🔍 Operadores de Comparação

- `==` : Igual
- `!=` : Diferente
- `>` : Maior que
- `<` : Menor que
- `>=` : Maior ou igual
- `<=` : Menor ou igual

## 📝 Exemplos de Scripts

### Exemplo Básico
```
# Clicar e digitar
clique 100,200
digite "Olá, mundo!"
espere 1000
control a
copie texto
clique 300,300
cole $texto
```

### Loop com Contador
```
# Inicializar contador
definir contador "0"
definir max "5"

# Loop com while
enquanto $contador < $max
    clique 100,200
    espere 500
    definir contador "$contador + 1"
fim
```

### Condicionais
```
# Verificar valor
se $resultado == "sucesso"
    digite "Operação bem sucedida!"
senao
    digite "Falha na operação"
fim
```

### Loop com Break
```
# Loop infinito com condição de parada
enquanto true == true
    clique 100,200
    copie resultado
    
    se $resultado == "encontrado"
        digite "Item encontrado!"
        pare
    fim
    
    espere 1000
fim
```

## ⌨️ Atalhos Globais

- `F6`: Iniciar automação
- `F7`: Parar automação
- `F8`: Capturar posição do mouse

## 🛠️ Instalação

1. Certifique-se de ter Java 11 ou superior instalado
2. Clone o repositório
3. Execute o Maven para construir:
```bash
mvn clean package
```
4. Execute o JAR gerado:
```bash
java -jar target/jrobot-1.0-SNAPSHOT.jar
```

## 📌 Notas

- Os scripts podem ser salvos em arquivos .txt
- Comentários começam com # ou //
- Variáveis são case-sensitive
- Strings devem estar entre aspas dupras
- A indentação é opcional mas recomendada
- Expressões matemáticas simples são suportadas em variáveis

## 🤝 Contribuindo

Sinta-se à vontade para contribuir com melhorias através de pull requests.

## 📄 Licença

Este projeto está sob a licença MIT. 