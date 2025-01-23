package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

// Auto for best teams
// ONLY PARKING ON Red Right and Left Blue
// Start facing towards observation zone

@Disabled
@Autonomous
public class Qualifier_Park extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        AutonomousTestV2withsigmaBrandonandOliver Auto = new AutonomousTestV2withsigmaBrandonandOliver(hardwareMap, telemetry, this::opModeIsActive, this::sleep);

        Auto.setup();

        waitForStart();

        Auto.Initialization();

        Auto.fwd(0.5, 4, 2);

    }
}
