package org.firstinspires.ftc.teamcode.phantom.utility;

import android.util.Pair;

import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Consumer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class Hardware {
    /**
     * Flags for initializing hardware
     */
    public enum HardwareFlag {
        DRIVETRAIN_ONLY,
        SLIDES_ONLY,
        ARM_ONLY,
        SPECIMEN_ONLY,
        ALL_HARDWARE,
    }

    /**
     * Constructor
     *
     * @param map hardware map
     */
    public Hardware(final HardwareMap map, final EnumSet<HardwareFlag> flags) {
        this.flags = flags;

        if (flags.contains(HardwareFlag.DRIVETRAIN_ONLY) || flags.contains(HardwareFlag.ALL_HARDWARE)) {
            FL = (DcMotorEx) map.dcMotor.get("FL");
            FR = (DcMotorEx) map.dcMotor.get("FR");
            BL = (DcMotorEx) map.dcMotor.get("BL");
            BR = (DcMotorEx) map.dcMotor.get("BR");

            FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            FL.setDirection(DcMotorSimple.Direction.REVERSE);
            BL.setDirection(DcMotorSimple.Direction.REVERSE);

            FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

//            FL.setTargetPosition(0);
//            FR.setTargetPosition(0);
//            BL.setTargetPosition(0);
//            BR.setTargetPosition(0);

            FL.setPower(1);
            FR.setPower(1);
            BL.setPower(1);
            BR.setPower(1);

            // see https://docs.google.com/document/d/1tyWrXDfMidwYyP_5H4mZyVgaEswhOC35gvdmP-V-5hA/mobilebasic#h.61g9ixenznbx
            FL.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(1.172326693, 0.1172326693, 0, 11.72326693));
            FR.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(1.172326693, 0.1172326693, 0, 11.72326693));
            BL.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(1.172326693, 0.1172326693, 0, 11.72326693));
            BR.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(1.172326693, 0.1172326693, 0, 11.72326693));
        }

        if (flags.contains(HardwareFlag.SLIDES_ONLY) || flags.contains(HardwareFlag.ALL_HARDWARE)) {
            SS = map.dcMotor.get("sideSlide");
            US = map.dcMotor.get("upSlide");

            SS.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            US.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            SS.setDirection(DcMotorSimple.Direction.REVERSE);
            US.setDirection(DcMotorSimple.Direction.REVERSE);

            SS.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            US.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            SS.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            US.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            SS.setPower(1);
            US.setPower(1);

            SS.setTargetPosition(0);
            US.setTargetPosition(0);
            SS.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            US.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            SS_sensor = map.get(RevTouchSensor.class, "Touch");
        }

        if (flags.contains(HardwareFlag.ARM_ONLY) || flags.contains(HardwareFlag.ALL_HARDWARE)) {
            bucket = map.servo.get("Thing2");
            claw   = map.servo.get("Thing 1");
            claw_elbow = map.servo.get("Claw Elbow");
            elbow  = map.servo.get("Elbow");
            wrist  = map.servo.get("Claw Wrist");

            bucket.setPosition(0);
            claw.setPosition(0.2);
            claw_elbow.setPosition(0.17);
            elbow.setPosition(0.151);
            wrist.setPosition(0);
        }

        if (flags.contains(HardwareFlag.SPECIMEN_ONLY) || flags.contains(HardwareFlag.ALL_HARDWARE)) {
            claw_specimen = map.servo.get("Specimen Claw");
            elbow_specimen = map.servo.get("Specimen Elbow");

            claw_specimen.setPosition(0.2);
            elbow_specimen.setPosition(1);
        }
    }

    /**
     * Move the robot based on a command block
     *
     * @param command_block Command to execute
     * @param timer Time since start of frame
     * @param sleep Sleep function
     */
    public void execute_command_block(
            final List<Pair<Character, double[]>> command_block,
            final ElapsedTime timer,
            final Consumer<Long> sleep
    ) {
        for (final Pair<Character, double[]> command : command_block) {
            final double[] powers = command.second;

            switch (command.first) {
                case 'w': {
                    FL.setPower(powers[4]);
                    FR.setPower(powers[4]);
                    BL.setPower(powers[4]);
                    BR.setPower(powers[4]);

                    FL.setTargetPosition((int) powers[0]);
                    FR.setTargetPosition((int) powers[1]);
                    BL.setTargetPosition((int) powers[2]);
                    BR.setTargetPosition((int) powers[3]);

                    FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    break;
                }
                case 's': {
                    SS.setTargetPosition((int) Math.round(powers[0]));
                    US.setTargetPosition((int) Math.round(powers[1]));
                    SS.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    US.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                    break;
                }
                case 'a': {
                    bucket.setPosition(powers[0]);
                    claw.setPosition(powers[1]);
                    claw_elbow.setPosition(powers[2]);
                    elbow.setPosition(powers[3]);
                    wrist.setPosition(powers[4]);
                    claw_specimen.setPosition(powers[5]);
                    elbow_specimen.setPosition(powers[6]);

                    break;
                }
                case 't': {
                    sleep.accept(Math.round(Math.abs(powers[0] - timer.milliseconds())));
                    timer.reset();

                    break;
                }
                default: {
                    throw new RuntimeException(String.format(
                            Locale.ROOT, "Invalid character prefix, %c, contains %s",
                            command.first, Arrays.toString(command.second)
                    ));
                }
            }
        }
    }

    /**
     * Get Hardware Flags as a String
     *
     * @return Hardware Flags as a String
     */
    public String flags_to_string() {
        return flags.toString();
    }

    /**
     * Get String of Flags to a Hardware instance
     *
     * @param map Hardware Map
     * @param input Hardware Flags as input
     *
     * @return Hardware with flags
     */
    public static Hardware string_to_hardware(final HardwareMap map, final String input) {
        final EnumSet<HardwareFlag> flags = Arrays.stream(input.replaceAll("\\[", "").replace("]", "").split(", "))
                 .map(item -> HardwareFlag.valueOf(HardwareFlag.class, item))
                 .collect(Collectors.toCollection(() -> EnumSet.noneOf(HardwareFlag.class)));

        return new Hardware(map, flags);
    }

    /**
     * Drivetrain motors
     */
    public DcMotorEx FL, FR, BL, BR;

    /**
     * Touch sensor, enabled when slides are
     */
    public RevTouchSensor SS_sensor;

    /**
     * Linear slide motors
     */
    public DcMotor SS, US;

    /**
     * Intake and outtake servos
     */
    public Servo bucket, claw, claw_elbow, elbow, wrist;

    /**
     * Specimen Servos
     */
    public Servo claw_specimen, elbow_specimen;

    private EnumSet<HardwareFlag> flags;
}