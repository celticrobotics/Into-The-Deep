package org.firstinspires.ftc.teamcode;

import androidx.annotation.ColorInt;
import android.graphics.Color;
import android.graphics.ColorSpace;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Arrays;

@Disabled
public class Sensor {
    public Sensor(RevColorSensorV3 sensor, Telemetry t) {
        _sensor = sensor;
        telemetry = t;

    // Create class, create constructor, every function needed outside the class is PUBLIC
    }

    /**
     * Color values for the color sensor
     */
    public enum CColor {
        MOIST_CYAN,
        RED,
        YELLOW,
        BLUE,
    }

    /**
     * Check what color the color sensor detects.
     * <br>
     * This uses the Euclidean distance formula <a href="https://en.wikipedia.org/wiki/Color_difference#sRGB">wikipedia.org/Color_difference</a>
     *
     * @return What color was detected
     */
    public CColor get_detected_color() {
        // use the Euclidean distance formula to find the closest matching color

        double closest_color_distance = Double.MAX_VALUE;
        CColor closest_color = null;

        @ColorInt int current_color = _sensor.getNormalizedColors().toColor();
        float[] hsv = new float[3];
        Color.colorToHSV(current_color, hsv);

//        telemetry.addLine(Arrays.toString(hsv));

        // get the color closest matching the color we are seeing
        for (CColor indexed_color : CColor.values()) {
            float[] hsv_color = to_argb(indexed_color);

            final double distance = Math.sqrt(Math.pow(hsv[0] - hsv_color[0] , 2) + Math.pow(hsv[1] - hsv_color[1] , 2) + Math.pow(hsv[2] - hsv_color[2] , 2));

            if (distance < closest_color_distance) {
                closest_color_distance = distance;
                closest_color = indexed_color;
            }
        }
//            if (indexed_color == Color.MOIST_CYAN) continue;
//
//            // get the distance between these colors
//            final int green_difference = ((current_color >> 8 ) & 0xFF) - ((to_argb(indexed_color) >> 8 ) & 0xFF);
//            final int red_difference   = ((current_color >> 16) & 0xFF) - ((to_argb(indexed_color) >> 16) & 0xFF);
//            final int blue_difference  = ( current_color        & 0xFF) - ( to_argb(indexed_color)        & 0xFF);
//
//            final double total_distance = Math.sqrt(red_difference   * red_difference   +
//                    green_difference * green_difference +
//                    blue_difference  * blue_difference);
//
//
//            // check if the color is the closest color found
//            if (total_distance < closest_color_distance) {
//                closest_color_distance = total_distance;
//                closest_color = indexed_color;
//            }
//        }

        return closest_color;
    }

    /**
     * Convert a color enum to an int array of argb
     *
     * @param color the Color enum to convert
     *
     * @return The argb colors
     */
    private float[] to_argb(CColor color) throws IllegalArgumentException {
        // these color values are taken from a website. Get the hex values of the colors you want
        // to replace in ARGB format or use this formula: (a << 24) | (r << 16) | (g << 8) | b
        switch (color) {
            case RED:       return new float[]{30, 1, 0.89f};
            case YELLOW:    return new float[]{60, 1, 1};
            case BLUE:      return new float[]{225, 1, 1};
            case MOIST_CYAN:return new float[]{0, .94f, .4f};
        }

        throw new IllegalArgumentException("invalid color value");
    }

    private RevColorSensorV3 _sensor;
    final Telemetry telemetry;
}
