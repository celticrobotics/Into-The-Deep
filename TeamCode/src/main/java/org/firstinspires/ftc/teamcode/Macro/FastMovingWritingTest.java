package org.firstinspires.ftc.teamcode.Macro;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="Fast AF Moving and Writing Test")
public class FastMovingWritingTest extends LinearOpMode {
    public static final boolean DEBUG = true;
    public static final String MACRO_NAME = "test";

    // This OpMode works by saving each motor power every frame to a buffer, then and the
    // end of the OpMode, to save the buffer to a file to be read in during autonomous.
    // The way we try to make the movement replication as accurate as possible is to save
    // the data as often as possible, by optimizing the code so that the part of the game
    // loop that takes the most time is the actual hardware movement itself, (usually a
    // 5ms delay)

    byte[][] saved_hardware_commands_buffer;
    DcMotor FL, FR, BL, BR;

    @Override public void runOpMode() throws InterruptedException {
        setup_hardware();
        saved_hardware_commands_buffer = new byte[3_000][];  // we should have to save only up to 10,000 elements

        waitForStart();

        for (int saved_buffer_index = 0; opModeIsActive() && saved_buffer_index < saved_hardware_commands_buffer.length;) {
            saved_hardware_commands_buffer[saved_buffer_index++] = Serializer.encode_numbers('w', move());

            force_stop_check();

            if (DEBUG) { updateTelemetry(telemetry); }

            sleep(10);
        }

        // encode all of the data at this point
        Serializer.serialize(MACRO_NAME, saved_hardware_commands_buffer);
    }

    /**
     * Move the robot
     *
     * @return The individual motor powers for the robot encoded in { FL, FR, BL, BR }
     */
    private double[] move() {
        final Gamepad GAMEPAD = gamepad1;

        final double forward = -GAMEPAD.left_stick_y;
        final double strafe  =  GAMEPAD.right_stick_x;
        final double turn    =  GAMEPAD.right_trigger - GAMEPAD.left_trigger;

        if (DEBUG) {
            telemetry.addLine("≡≡≡≡≡≡ MOVEMENT COMMANDS ≡≡≡≡≡≡");
            telemetry.addLine(" - Left joystick moves robot vertically");
            telemetry.addLine(" - Right joystick moves robot laterally");
            telemetry.addLine(" - Triggers turn robot");
            telemetry.addLine();
        }

        double FL_power, FR_power, BL_power, BR_power;

        final double power = GAMEPAD.a ? 1.0 : 0.5; // first is turbo speed, second is normal speed

        /* traditional movement */ {
            FL_power = power * Range.clip(forward + strafe + turn, -1, 1);
            FR_power = power * Range.clip(forward - strafe - turn, -1, 1);
            BL_power = power * Range.clip(forward - strafe + turn, -1, 1);
            BR_power = power * Range.clip(forward + strafe - turn, -1, 1);
        }

//        /* circle movement */ {
//            final double theta = Math.atan2(forward, strafe);
//            final double hypot = Math.hypot(forward, strafe) * power;
//
//            final double sin = Math.sin(theta - (Math.PI / 4));
//            final double cos = Math.cos(theta - (Math.PI / 4));
//            final double max = Math.max(Math.abs(sin), Math.abs(cos));
//
//            FL_power = hypot * cos / max + turn;
//            FR_power = hypot * sin / max - turn;
//            BL_power = hypot * sin / max + turn;
//            BR_power = hypot * cos / max - turn;
//
//            if ((hypot + Math.abs(turn)) > 1) {
//                FL_power /= hypot + Math.abs(turn);
//                FR_power /= hypot + Math.abs(turn);
//                BL_power /= hypot + Math.abs(turn);
//                BR_power /= hypot + Math.abs(turn);
//            }
//        }

        FL.setPower(FL_power);
        FR.setPower(FR_power);
        BL.setPower(BL_power);
        BR.setPower(BR_power);

        return new double[]{ FL_power, FR_power, BL_power, BR_power };
    }

    /**
     * Force stop check
     */
    private void force_stop_check() {
        if (DEBUG) {
            telemetry.addLine("≡≡≡ EMERGENCY PAUSE COMMAND ≡≡≡");
            telemetry.addLine(" - Press the start button on any controller to shut down the robot");
            telemetry.addLine();
        }

        if (gamepad1.start || gamepad2.start) {
            requestOpModeStop();
        }
    }

    /**
     * Setup hardware devices
     */
    private void setup_hardware() {
        FL = hardwareMap.dcMotor.get("FL");
        FR = hardwareMap.dcMotor.get("FR");
        BL = hardwareMap.dcMotor.get("BL");
        BR = hardwareMap.dcMotor.get("BR");

        FL.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(ZeroPowerBehavior.BRAKE);

        FL.setDirection(Direction.REVERSE);
        BL.setDirection(Direction.REVERSE);
    }
}
