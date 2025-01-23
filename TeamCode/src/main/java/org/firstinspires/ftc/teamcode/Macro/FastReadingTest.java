package org.firstinspires.ftc.teamcode.Macro;

import android.util.Pair;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.ArrayList;

@Autonomous
public class FastReadingTest extends LinearOpMode {
    public static final boolean DEBUG = FastMovingWritingTest.DEBUG;
    public static final String MACRO_NAME = FastMovingWritingTest.MACRO_NAME;


    ArrayList<Pair<Character, double[]>> saved_hardware_commands_buffer;
    DcMotor FL, FR, BL, BR;

    @Override public void runOpMode() throws InterruptedException {
        read_macro();
        setup_hardware();

        waitForStart();

        for (final Pair<Character, double[]> command_buffer :  saved_hardware_commands_buffer) {
            final double[] powers = command_buffer.second;

            if (command_buffer.first == 'w') {
                FL.setPower(powers[0]);
                FR.setPower(powers[1]);
                BL.setPower(powers[2]);
                BR.setPower(powers[3]);
            }

            sleep(10);
        }
    }

    /**
     * Read the macro
     */
    private void read_macro() throws InterruptedException {
        if (DEBUG) {
            telemetry.addLine("≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡ READING MACRO ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡");
            updateTelemetry(telemetry);
        }

        final byte[][] encoded_hardware_commands_buffers = Serializer.deserialize(MACRO_NAME);
        saved_hardware_commands_buffer = new ArrayList<>();

        for (final byte[] encoded_buffer : encoded_hardware_commands_buffers) {
            saved_hardware_commands_buffer.add(Serializer.decode_to_numbers(encoded_buffer));
        }

        if (DEBUG) {
            sleep(500);

            telemetry.addLine("≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡ MACRO READ SUCCESSFULLY ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡");
            updateTelemetry(telemetry);
        }

        System.gc();
    }

    /**
     * Setup hardware devices
     */
    private void setup_hardware() {
        FL = hardwareMap.dcMotor.get("FL");
        FR = hardwareMap.dcMotor.get("FR");
        BL = hardwareMap.dcMotor.get("BL");
        BR = hardwareMap.dcMotor.get("BR");

        FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        BL.setDirection(DcMotorSimple.Direction.REVERSE);
    }
}