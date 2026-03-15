# TeleOp — Movimentação com Rodas Mecanum

**Arquivo:** `TeleOpMovimentacao.java`  
**Pacote:** `org.firstinspires.ftc.teamcode`  
**Time:** Full-Metal  

---

## Descrição

OpMode de TeleOp responsável exclusivamente pela **movimentação do robô** durante o período controlado. Utiliza um **drivetrain de 4 motores com rodas Mecanum**, permitindo movimento em qualquer direção (frente, ré, lateral e diagonal) sem precisar girar o robô.

---

## Configuração de Hardware

Os motores devem estar cadastrados no **Driver Hub** com exatamente estes nomes:

| Nome no Driver Hub | Posição no robô      | Variável no código       |
|--------------------|----------------------|--------------------------|
| `frontRight`       | Frente Direito       | `motorFrenteDireito`     |
| `frontLeft`        | Frente Esquerdo      | `motorFrenteEsquerdo`    |
| `backRight`        | Trás Direito         | `motorTrasDireito`       |
| `backLeft`         | Trás Esquerdo        | `motorTrasEsquerdo`      |

> ⚠️ Os motores do lado esquerdo são invertidos via `setDirection(REVERSE)` para compensar o espelhamento físico da montagem.

---

## Controles (Gamepad 1)

| Entrada                | Ação                     |
|------------------------|--------------------------|
| `left_stick_y` (↑↓)   | Avançar / Recuar         |
| `left_stick_x` (←→)   | Mover lateralmente (strafe) |
| `right_stick_x` (←→)  | Girar no próprio eixo    |

---

## Lógica de Movimentação

### Leitura dos eixos

```java
double avancarRecuar = -gamepad1.left_stick_y;  // Y invertido no controle
double girarEsqDir   =  gamepad1.right_stick_x;
double moverLateral  =  gamepad1.left_stick_x;
```

> O eixo Y do joystick é naturalmente invertido no hardware — empurrar para frente retorna `-1`. Por isso o sinal é negado.

### Fórmula Mecanum

Cada roda recebe uma combinação dos três eixos de movimento:

```java
potenciaFrenteDireito  = avancarRecuar - girarEsqDir - moverLateral;
potenciaFrenteEsquerdo = avancarRecuar + girarEsqDir + moverLateral;
potenciaTrasDireito    = avancarRecuar - girarEsqDir + moverLateral;
potenciaTrasEsquerdo   = avancarRecuar + girarEsqDir - moverLateral;
```

Tabela de referência de direção por roda:

| Movimento     | Frente Dir | Frente Esq | Trás Dir | Trás Esq |
|---------------|:----------:|:----------:|:--------:|:--------:|
| Avançar       | +          | +          | +        | +        |
| Recuar        | -          | -          | -        | -        |
| Strafe Direita| -          | +          | +        | -        |
| Strafe Esquerda| +         | -          | -        | +        |
| Girar Direita | -          | +          | -        | +        |
| Girar Esquerda| +          | -          | +        | -        |

### Normalização de potência

Garante que nenhuma roda ultrapasse o limite de `1.0` ou `-1.0`, mantendo a proporção entre elas:

```java
double maiorPotencia = Math.max(1.0, Math.max(
    Math.max(Math.abs(potenciaFrenteDireito), Math.abs(potenciaFrenteEsquerdo)),
    Math.max(Math.abs(potenciaTrasDireito),   Math.abs(potenciaTrasEsquerdo))
));

motorFrenteDireito.setPower(potenciaFrenteDireito   / maiorPotencia);
motorFrenteEsquerdo.setPower(potenciaFrenteEsquerdo / maiorPotencia);
motorTrasDireito.setPower(potenciaTrasDireito       / maiorPotencia);
motorTrasEsquerdo.setPower(potenciaTrasEsquerdo     / maiorPotencia);
```

> Se todas as potências estiverem dentro de `[-1.0, 1.0]`, o divisor é `1.0` e nada muda. Se alguma ultrapassar, todas são reduzidas proporcionalmente.

---

## Configurações dos Motores

| Configuração              | Valor                          | Motivo                                        |
|---------------------------|--------------------------------|-----------------------------------------------|
| `RunMode`                 | `RUN_WITHOUT_ENCODER`          | Controle direto de potência sem PID de encoder|
| `ZeroPowerBehavior`       | `BRAKE`                        | Trava o robô ao soltar o joystick             |
| Direção (lado esquerdo)   | `REVERSE`                      | Compensa o espelhamento físico dos motores    |

---

## Telemetria

Apenas uma mensagem de status é exibida antes do START:

```
Status: Pronto! Aguardando START...
```

Nenhuma telemetria é exibida durante o loop — mantendo a comunicação limpa durante a partida.

---

## Possíveis Melhorias Futuras

- **Modo preciso:** segurar um botão (ex: `gamepad1.right_bumper`) para reduzir a velocidade máxima a ~40%, útil para manobras delicadas.
- **Turbo:** segurar `gamepad1.right_trigger` para manter 100% de potência sem normalização extra.
- **Telemetria de debug:** adicionar leitura de potências no loop apenas durante testes, removendo em competição.
