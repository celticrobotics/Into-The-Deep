package org.firstinspires.ftc.teamcode.phantom.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.phantom.utility.Keyboard;

@TeleOp(name="Keyboard Test")
public class KeyboardTest extends LinearOpMode {
    public void runOpMode() {
        keyboard = new Keyboard(telemetry, () -> gamepad1, this::opModeIsActive);

        waitForStart();

        final String input = keyboard.input("Enter anything using the keyboard: ");

        if (input == null) { return; }

        telemetry.addData("Named", input);
        telemetry.update();

        while (opModeIsActive() && !gamepad1.start) { idle(); }
    }

    Keyboard keyboard;
}
