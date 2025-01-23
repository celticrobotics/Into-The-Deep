package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Autonomous(name = "Quicker Auto")
public class FastAuto extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        AutonomousTestV2withsigmaBrandonandOliver Auto = new AutonomousTestV2withsigmaBrandonandOliver(hardwareMap, telemetry, this::opModeIsActive, this::sleep);

        Auto.setup();

        waitForStart();

        Auto.Initialization();

        // Pre-Load Sample
        Auto.moveVerticalLinearSlide(1, 300);
        Auto.strafe(0.5, -10, 0.8);
        Auto.fwd(0.5, -8, 0.8);
        Auto.turn(0.5, -62.069, 0.7);
        Auto.fwd(0.5, -8.5, 1);
        Auto.moveVerticalLinearSlide(1, 4000);
        Auto.rotateThingyIntoBasket(2);
        Auto.origin(2);

        // 1st Sample
        Auto.fwd(0.5, 14.3, 1);
        Auto.turn(0.5, -42.069, 1);
        Auto.fwd(0.2, 8, 1);
        Auto.ClawMove(AutonomousTestV2withsigmaBrandonandOliver.ClawPosition.CLOSED, 0.7);
        Auto.ElbowMove(AutonomousTestV2withsigmaBrandonandOliver.ElbowPos.UP, 0.7);
        Auto.ClawMove(AutonomousTestV2withsigmaBrandonandOliver.ClawPosition.OPEN, 0.7);
        Auto.ElbowMove(AutonomousTestV2withsigmaBrandonandOliver.ElbowPos.DOWN, 0.7);
        Auto.fwd(0.5, -10, 1);
        Auto.turn(0.5, 45, 1);
        Auto.fwd(0.5, -12.2, 1);
        Auto.moveVerticalLinearSlide(1, 4000);
        Auto.rotateThingyIntoBasket(2);
        Auto.origin(2);
        Auto.reset();

        //sets bucket & upslide for TeleOp
    }
}
