# JRobot - Documentação para Desenvolvedores

## Visão Geral
JRobot é uma aplicação Java para automação de tarefas através de scripts. Ela permite controlar o mouse e teclado programaticamente, com uma interface gráfica minimalista e atalhos globais.

## Arquitetura

### Componentes Principais

#### 1. MainApp (Interface Gráfica)
- Classe principal que estende `JFrame`
- Implementa `NativeKeyListener` para atalhos globais e `AutomationInterface` para comunicação com o controlador
- Gerencia a interface gráfica minimalista com:
  - Botão de seleção de script
  - Botão Play/Stop
  - Capacidade de arrastar a janela
  - Atalhos globais (F6, F7, F8)

#### 2. RobotController
- Controlador principal que gerencia a automação
- Implementa `NativeKeyListener` para atalhos globais
- Responsável por:
  - Carregar e executar scripts
  - Gerenciar o estado da automação (running/stopped)
  - Comunicar mudanças de estado para a UI através do `AutomationInterface`
  - Tratar atalhos globais F6 (play) e F7 (stop)

#### 3. RobotActions
- Encapsula todas as ações de baixo nível do `java.awt.Robot`
- Implementa operações como:
  - Movimentação do mouse
  - Cliques (simples, duplo, direito)
  - Digitação de texto
  - Combinações de teclas (Ctrl, Alt, Shift)
  - Drag and drop

#### 4. ScriptInterpreter
- Parser e executor de scripts
- Suporta comandos em inglês e português
- Funcionalidades:
  - Variáveis
  - Estruturas de controle (if/else, while)
  - Comandos de mouse e teclado
  - Delays e repetições

### Interface de Comunicação

#### AutomationInterface
- Interface que define o contrato entre `RobotController` e UI
- Métodos:
  - `updateButtonStates(boolean isRunning)`: Atualiza estado dos botões
  - `updateButtonVisibility(boolean visible)`: Controla visibilidade dos botões

## Fluxo de Execução

### 1. Inicialização
```java
MainApp -> RobotController -> RobotActions
```
- `MainApp` inicializa a interface gráfica
- Cria instância de `RobotController` passando a si mesmo como `AutomationInterface`
- `RobotController` inicializa `RobotActions` e configura listeners globais

### 2. Carregamento de Script
```java
MainApp.selectFile() -> RobotController.loadScriptFromFile() -> currentScript
```
- Usuário seleciona arquivo de script
- Caminho do arquivo é armazenado no controlador

### 3. Execução de Script
```java
F6/Play Button -> RobotController.startAutomation() -> ScriptInterpreter.executeFile()
```
- Execução pode ser iniciada via botão Play ou F6 global
- Script é executado em thread separada
- UI é atualizada conforme estado da execução

### 4. Parada de Script
```java
F7/Stop Button -> RobotController.stopAutomation() -> ScriptInterpreter.setForceStop()
```
- Script pode ser parado via botão Stop ou F7 global
- Força a parada do interpretador
- UI é atualizada para estado inicial

## Formato do Script

### Comandos Suportados
```
// Mouse
click x,y (ou clique x,y)
rightclick x,y
doubleclick x,y
drag x1,y1,x2,y2
move x,y

// Teclado
type "texto" (ou digite "texto")
press TECLA (ou clique TECLA)
control TECLA
alt TECLA
shift TECLA
copy (executa Ctrl+C)
paste (executa Ctrl+V)

// Utilitários
delay MILISSEGUNDOS (ou espere MILISSEGUNDOS)
repeat N comando (repete o comando N vezes)

// Variáveis
set NOME VALOR (ou defina NOME VALOR)
get NOME (ou obter NOME)

// Controle de Fluxo
if VAR1 OPERADOR VAR2
else
while VAR1 OPERADOR VAR2
end
break
```

### Exemplos de Script
```
// Move o mouse e clica
move 100,200
delay 500
click 100,200

// Copiar e colar
move 100,200    // Move para o texto a ser copiado
click 100,200   // Clica para selecionar
copy            // Executa Ctrl+C
move 300,200    // Move para onde vai colar
click 300,200   // Clica no destino
paste           // Executa Ctrl+V

// Loop com variável em português
defina contador 0
enquanto $contador < 5
    digite "Linha "
    clique ENTER
    defina contador $contador + 1
fim

// Repetições
repeat 3 press K
repeat 5 type "Olá"
repeat 2 click 100,200

// Comandos em português
clique K
digite "Texto de exemplo"
espere 1000
repita 3 clique K
```

## Atalhos Globais

- **F6**: Inicia a execução do script carregado
- **F7**: Para a execução do script atual
- **F8**: Captura a posição atual do mouse
- **F9**: Captura a cor na posição atual do mouse

## Considerações de Desenvolvimento

### Thread Safety
- Todas as operações de UI são executadas via `SwingUtilities.invokeLater`
- Script roda em thread separada para não bloquear a UI
- Estado de execução é controlado por variável volatile

### Tratamento de Erros
- Erros de script são logados mas não interrompem a execução
- Exceções de Robot são propagadas até o construtor
- Falhas de registro de atalhos globais são tratadas apropriadamente

### Extensibilidade
- Novos comandos podem ser adicionados ao enum `RobotCommand`
- Interface `AutomationInterface` pode ser expandida para mais funcionalidades
- Sistema de interpretação de script pode ser estendido para mais estruturas de controle

## Dependências

- **JNativeHook**: Para captura de teclas globalmente
- **Java AWT Robot**: Para automação de mouse e teclado
- **Swing**: Para interface gráfica

## Compilação e Execução

```bash
# Compilar com Maven
mvn clean package

# Executar o JAR gerado
java -jar target/jrobot-1.0-SNAPSHOT.jar
``` 