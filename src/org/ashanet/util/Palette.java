
package org.ashanet.util;

import org.ashanet.R;

public class Palette {
    public enum COLOR {
        PINK(R.color.pink1, R.color.pink2, R.color.pink3),
        ORANGE(R.color.orange1, R.color.orange2, R.color.orange3),
        BLUE(R.color.blue1, R.color.blue2, R.color.blue3),
        GREEN(R.color.green1, R.color.green2, R.color.green3),
        PURPLE(R.color.purple1, R.color.purple2, R.color.purple3),
        TEAL(R.color.teal1, R.color.teal2, R.color.teal3),
        GREY(R.color.grey1, R.color.grey2, R.color.grey3),
        DARK_BLUE(R.color.dark_blue1, R.color.dark_blue2, R.color.dark_blue3),
        LIGHT_BLUE(R.color.light_blue1, R.color.light_blue2, R.color.light_blue3),
        RED(R.color.red1, R.color.red2, R.color.red3),
        BROWN(R.color.brown1, R.color.brown2, R.color.brown3);

        public int light;
        public int medium;
        public int dark;

        private COLOR(int light, int medium, int dark) {
            this.light = light;
            this.medium = medium;
            this.dark = dark;
        }
    }

    static public COLOR getColor(String colorName) {
        COLOR c;
        try {
            c = COLOR.valueOf(colorName.toUpperCase().replace(" ", "_"));
        }
        catch (IllegalArgumentException e) {
            c = COLOR.GREY;
        }
        return c;
    }
}
