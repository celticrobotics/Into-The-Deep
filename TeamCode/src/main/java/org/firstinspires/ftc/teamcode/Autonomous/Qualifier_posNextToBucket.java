package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

// Auto for left red and right blue
// Score preload sample top bucket

@Disabled
@Autonomous(name = "Qualifier Pos Next to Bucket")
public class Qualifier_posNextToBucket extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        AutonomousTestV2withsigmaBrandonandOliver Auto = new AutonomousTestV2withsigmaBrandonandOliver(hardwareMap, telemetry, this::opModeIsActive, this::sleep);

        Auto.setup();

        // MUST START WITH BACK FACING BASKET

        // strafe left
        // turn left
        // Backwards
        // Extends slides
        // Rotate bucket

        waitForStart();

        Auto.Initialization();

        Auto.strafe(0.5, -16, 2);
        Auto.turn(0.5, -45, 2);
        Auto.strafe(0.5, -3, 1);
        Auto.fwd(0.5, -19, 2);
        Auto.moveVerticalLinearSlide(1, 4000);
        Auto.rotateThingyIntoBasket(2);
        Auto.origin(2);





    }
}
