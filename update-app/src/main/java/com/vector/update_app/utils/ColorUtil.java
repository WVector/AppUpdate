package com.vector.update_app.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;

import java.util.Random;

/**
 * Created by Vector
 * on 2017/6/29 0029.
 */

public class ColorUtil {


    /**
     * 颜色选择器
     *
     * @param pressedColor 按下的颜色
     * @param normalColor  正常的颜色
     * @return 颜色选择器
     */
    public static ColorStateList getColorStateList(int pressedColor, int normalColor) {
        //其他状态默认为白色
        return new ColorStateList(
                new int[][]{{android.R.attr.state_enabled, android.R.attr.state_pressed}, {android.R.attr.state_enabled}, {}},
                new int[]{pressedColor, normalColor, Color.WHITE});
    }

    /**
     * 加深颜色
     *
     * @param color 原色
     * @return 加深后的
     */
    public static int colorDeep(int color) {

        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        float ratio = 0.8F;

        red = (int) (red * ratio);
        green = (int) (green * ratio);
        blue = (int) (blue * ratio);

        return Color.argb(alpha, red, green, blue);
    }

    /**
     * @param color 背景颜色
     * @return 前景色是否深色
     */
    public static boolean isTextColorDark(int color) {
        float a = (Color.red(color) * 0.299f + Color.green(color) * 0.587f + Color.blue(color) * 0.114f);
        return a > 180;
    }

    /**
     * 按条件的到随机颜色
     *
     * @param alpha 透明
     * @param lower 下边界
     * @param upper 上边界
     * @return 颜色值
     */

    public static int getRandomColor(int alpha, int lower, int upper) {
        return new RandomColor(alpha, lower, upper).getColor();
    }

    /**
     * @return 获取随机色
     */
    public static int getRandomColor() {
        return new RandomColor(255, 80, 200).getColor();
    }

    /**
     * 随机颜色
     */
    public static class RandomColor {
        int alpha;
        int lower;
        int upper;

        public RandomColor(int alpha, int lower, int upper) {
            if (upper <= lower) {
                throw new IllegalArgumentException("must be lower < upper");
            }
            setAlpha(alpha);
            setLower(lower);
            setUpper(upper);
        }

        public int getColor() {

            //随机数是前闭  后开

            int red = getLower() + new Random().nextInt(getUpper() - getLower() + 1);
            int green = getLower() + new Random().nextInt(getUpper() - getLower() + 1);
            int blue = getLower() + new Random().nextInt(getUpper() - getLower() + 1);


            return Color.argb(getAlpha(), red, green, blue);
        }

        public int getAlpha() {
            return alpha;
        }

        public void setAlpha(int alpha) {
            if (alpha > 255) alpha = 255;
            if (alpha < 0) alpha = 0;
            this.alpha = alpha;
        }

        public int getLower() {
            return lower;
        }

        public void setLower(int lower) {
            if (lower < 0) lower = 0;
            this.lower = lower;
        }

        public int getUpper() {
            return upper;
        }

        public void setUpper(int upper) {
            if (upper > 255) upper = 255;
            this.upper = upper;
        }
    }
}
