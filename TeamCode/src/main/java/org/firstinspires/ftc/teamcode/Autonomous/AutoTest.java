package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "reset")
public class AutoTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        AutonomousTestV2withsigmaBrandonandOliver Auto = new AutonomousTestV2withsigmaBrandonandOliver(hardwareMap, telemetry, this::opModeIsActive, this::sleep);

        Auto.setup();

        waitForStart();

        Auto.Initialization();
        Auto.moveVerticalLinearSlide(1, 300);

        Auto.rotateThingyIntoBasket(100000);

        Auto.reset();


    }
}
