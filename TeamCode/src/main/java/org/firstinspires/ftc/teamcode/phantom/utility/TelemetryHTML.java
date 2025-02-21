package org.firstinspires.ftc.teamcode.phantom.utility;

/**
 * Utility for Telemetry HTML
 */
public final class TelemetryHTML {
    /**
     * Create a string with a specified background color
     *
     * @param color Color in hex, ex: "#123456"
     * @param input String to format
     *
     * @return Formatted string, to be used by
     * {@link org.firstinspires.ftc.robotcore.external.Telemetry} in mode
     * {@link org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat#HTML}
     */
    public static String background_color(final String color, final String input) {
        return String.format("<span style=\"background-color:%s\">%s</span>", color, input);
    }

    /**
     * Create a string with a specified color
     *
     * @param color Color in hex, ex: "#123456"
     * @param input String to format
     *
     * @return Formatted string, to be used by
     * {@link org.firstinspires.ftc.robotcore.external.Telemetry} in mode
     * {@link org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat#HTML}
     */
    public static String foreground_color(final String color, final String input) {
        return String.format("<font color=\"%s\">%s</font>", color, input);
    }

    /**
     * Create a string with a foreground and background color
     *
     * @param foreground Foreground color
     * @param background Background color
     * @param input String to format
     *
     * @return Formatted string, to be used by
     * {@link org.firstinspires.ftc.robotcore.external.Telemetry} in mode
     * {@link org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat#HTML}
     */
    public static String color(final String foreground, final String background, final String input) {
        return String.format("<span style=\"color:%s; background-color:%s\">%s</span>", foreground, background, input);
    }

    /**
     * Create a bold string
     *
     * @param input String to format
     *
     * @return Formatted string, to be used by
     * {@link org.firstinspires.ftc.robotcore.external.Telemetry} in mode
     * {@link org.firstinspires.ftc.robotcore.external.Telemetry.DisplayFormat#HTML}
     */
    public static String bold(final String input) {
        return String.format("<b>%s</b>", input);
    }
}