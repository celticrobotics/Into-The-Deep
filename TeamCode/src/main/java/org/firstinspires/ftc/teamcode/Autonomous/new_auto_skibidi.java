package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


@Disabled
@Autonomous
public class new_auto_skibidi extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        AutonomousTestV2withsigmaBrandonandOliver Auto = new AutonomousTestV2withsigmaBrandonandOliver(hardwareMap, telemetry, this::opModeIsActive, this::sleep);

        Auto.setup();

        waitForStart();

        Auto.Initialization();

        Auto.strafe(0.5, -16, 1.5);
        Auto.turn(0.5, -45, 1);
        Auto.strafe(0.5, -3, 1);
        Auto.fwd(0.5, -19, 2);
        Auto.moveVerticalLinearSlide(1, 4000);
        Auto.rotateThingyIntoBasket(2);
        Auto.origin(2);

        Auto.fwd(0.5, 8.2, 1);
        Auto.turn(0.5, -57, 1);
        Auto.fwd(0.5, 9, 1);
        Auto.moveHorizontalLinearSlide(0.5, 300, 2);
        Auto.ClawMove(AutonomousTestV2withsigmaBrandonandOliver.ClawPosition.CLOSED, 0.7);
        Auto.ElbowMove(AutonomousTestV2withsigmaBrandonandOliver.ElbowPos.UP, 0.7);
        Auto.ClawMove(AutonomousTestV2withsigmaBrandonandOliver.ClawPosition.OPEN, 0.7);
        Auto.ElbowMove(AutonomousTestV2withsigmaBrandonandOliver.ElbowPos.DOWN, 0.7);
        Auto.fwd(0.5, -13.8, 2);
        Auto.turn(0.5, 45, 1);
        Auto.strafe(0.5, -3, 1);
        Auto.fwd(0.5, -4, 1);
        Auto.moveVerticalLinearSlide(1, 4000);
        Auto.rotateThingyIntoBasket(2);
        Auto.origin(3);

    }
}
