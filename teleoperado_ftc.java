package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "TeleOp - Movimentação", group = "FullMetal")
public class TeleOpMovimentacao extends LinearOpMode {

    // --- Motores da base ---
    private DcMotor motorFrenteDireito;
    private DcMotor motorFrenteEsquerdo;
    private DcMotor motorTrasDireito;
    private DcMotor motorTrasEsquerdo;

    @Override
    public void runOpMode() {

        // --- Inicialização dos motores pelo nome configurado no Driver Hub ---
        motorFrenteDireito  = hardwareMap.get(DcMotor.class, "frontRight");
        motorFrenteEsquerdo = hardwareMap.get(DcMotor.class, "frontLeft");
        motorTrasDireito    = hardwareMap.get(DcMotor.class, "backRight");
        motorTrasEsquerdo   = hardwareMap.get(DcMotor.class, "backLeft");

        // --- Inversão dos motores do lado esquerdo (estão espelhados fisicamente) ---
        motorFrenteEsquerdo.setDirection(DcMotorSimple.Direction.REVERSE);
        motorTrasEsquerdo.setDirection(DcMotorSimple.Direction.REVERSE);

        // --- Comportamento ao soltar o controle: trava os motores no lugar ---
        motorFrenteDireito.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFrenteEsquerdo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorTrasDireito.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorTrasEsquerdo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // --- Modo de controle direto (sem encoder) ---
        motorFrenteDireito.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrenteEsquerdo.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorTrasDireito.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorTrasEsquerdo.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Pronto! Aguardando START...");
        telemetry.update();

        waitForStart();

        // =====================================================================
        //  LOOP PRINCIPAL
        // =====================================================================
        while (opModeIsActive()) {

            // --- Leitura dos eixos do joystick esquerdo e direito ---
            double avancarRecuar   = -gamepad1.left_stick_y;  // eixo Y invertido no controle
            double girarEsqDir     =  gamepad1.right_stick_x; // rotação
            double moverLateral    =  gamepad1.left_stick_x;  // strafe (deslizar para os lados)

            // --- Cálculo das potências para cada roda (fórmula Mecanum) ---
            double potenciaFrenteDireito  = avancarRecuar - girarEsqDir - moverLateral;
            double potenciaFrenteEsquerdo = avancarRecuar + girarEsqDir + moverLateral;
            double potenciaTrasDireito    = avancarRecuar - girarEsqDir + moverLateral;
            double potenciaTrasEsquerdo   = avancarRecuar + girarEsqDir - moverLateral;

            // --- Normalização: garante que nenhuma potência passe de 1.0 ou -1.0 ---
            // Substitua o bloco de normalização por este:
double maiorPotencia = Math.max(1.0, Math.max(
    Math.max(Math.abs(potenciaFrenteDireito), Math.abs(potenciaFrenteEsquerdo)),
    Math.max(Math.abs(potenciaTrasDireito),   Math.abs(potenciaTrasEsquerdo))
));

            motorFrenteDireito.setPower(potenciaFrenteDireito   / maiorPotencia);
            motorFrenteEsquerdo.setPower(potenciaFrenteEsquerdo / maiorPotencia);
            motorTrasDireito.setPower(potenciaTrasDireito       / maiorPotencia);
            motorTrasEsquerdo.setPower(potenciaTrasEsquerdo     / maiorPotencia);
        }
    }
}
