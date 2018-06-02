package gmsis.diagrep.components;

import javafx.scene.paint.Color;

import java.util.Random;

public class Util {

    public static Color getUniqueColorFromString(String str, double alpha) {
        Random random = new Random();
        random.setSeed(str.hashCode() * 27595); // Multiply by random number
        float hue = random.nextInt(360);
        float saturation = 0.5f + random.nextFloat()*0.1f;
        float value = 0.8f + random.nextFloat()*0.1f;

        return Color.hsb(hue, saturation, value, alpha);
    }

}
