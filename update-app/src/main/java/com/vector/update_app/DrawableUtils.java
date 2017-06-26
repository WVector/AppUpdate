package com.vector.update_app;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.util.Random;

/**
 * Created by Vector
 * on 2017/6/26 0026.
 */

public class DrawableUtils {
    private DrawableUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
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
     * 按条件的到随机颜色
     *
     * @param alpha 透明
     * @param lower 下边界
     * @param upper 上边界
     * @return 颜色值
     */

    public static int getRandomArgb(int alpha, int lower, int upper) {
        if (upper > 255) upper = 255;

        if (lower < 0) lower = 0;

        //随机数是前闭  后开

        int red = lower + new Random().nextInt(upper - lower + 1);
        int green = lower + new Random().nextInt(upper - lower + 1);
        int blue = lower + new Random().nextInt(upper - lower + 1);


        return Color.argb(alpha, red, green, blue);
    }

    /**
     * 根据颜色和圆角
     *
     * @param rgb 颜色
     * @param r   圆角
     * @return 圆角矩形
     */
    public static Drawable getRectDrawable(int rgb, int r) {
        //
        GradientDrawable gradientDrawable = new GradientDrawable();
        // 设置绘画图片色值
        gradientDrawable.setColor(rgb);
        // 绘画的是矩形
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        // 设置矩形的圆角半径
        gradientDrawable.setCornerRadius(r);

        return gradientDrawable;
    }

    /**
     * 背景选择器
     *
     * @param pressDrawable  按下状态的Drawable
     * @param normalDrawable 正常状态的Drawable
     * @return 状态选择器
     */
    public static StateListDrawable getStateListDrawable(Drawable pressDrawable, Drawable normalDrawable) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, pressDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, normalDrawable);
        stateListDrawable.addState(new int[]{}, normalDrawable);
        return stateListDrawable;
    }

    /**
     * @param cornerRadius 圆角半径
     * @param normalArgb   正常的颜色
     * @return 状态选择器
     */

    public static StateListDrawable getDrawable(int cornerRadius, int normalArgb) {
        return getDrawable(cornerRadius, colorDeep(normalArgb), normalArgb);
    }

    /**
     * @param cornerRadius 圆角半径
     * @param pressedArgb  按下颜色
     * @param normalArgb   正常的颜色
     * @return 状态选择器
     */
    public static StateListDrawable getDrawable(int cornerRadius, int pressedArgb, int normalArgb) {

        return getStateListDrawable(getRectDrawable(pressedArgb, cornerRadius), getRectDrawable(normalArgb, cornerRadius));
    }

    /**
     * 得到随机背景色并且带选择器, 并且可以设置圆角
     *
     * @param cornerRadius 圆角
     * @param alpha        随机颜色的透明度
     * @param lower        随机颜色的下边界
     * @param upper        随机颜色的上边界
     * @param normalArgb   正常的颜色
     * @return 状态选择器
     */
    public static StateListDrawable getRandomDrawable(int cornerRadius, int alpha, int lower, int upper, int normalArgb) {

        return getDrawable(cornerRadius, new RandomColor(alpha, lower, upper).getColor(), normalArgb);

    }

    /**
     * 得到随机背景色并且带选择器, 并且可以设置圆角
     *
     * @param cornerRadius 圆角
     * @param randomColor  随机颜色
     * @param normalArgb   正常的颜色
     * @return 状态选择器
     */
    public static StateListDrawable getRandomDrawable(int cornerRadius, RandomColor randomColor, int normalArgb) {

        return getDrawable(cornerRadius, randomColor.getColor(), normalArgb);

    }

    /**
     * 颜色选择器
     *
     * @param pressedArgb 按下的颜色
     * @param normalArgb  正常的颜色
     * @return 颜色选择器
     */
    public static ColorStateList getColorStateList(int pressedArgb, int normalArgb) {

        return new ColorStateList(
                new int[][]{{android.R.attr.state_enabled, android.R.attr.state_pressed}, {android.R.attr.state_enabled}},
                new int[]{pressedArgb, normalArgb});
    }

    /**
     * 得到空心的效果，一般作为默认的效果
     *
     * @param cornerRadius 圆角半径
     * @param solidArgb    实心颜色
     * @param strokeArgb   边框颜色
     * @param strokeWidth  边框宽度
     * @return 得到空心效果
     */
    public static GradientDrawable getStrokeRectDrawable(int cornerRadius, int solidArgb, int strokeArgb, int strokeWidth) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(strokeWidth, strokeArgb);
        gradientDrawable.setColor(solidArgb);
        gradientDrawable.setCornerRadius(cornerRadius);
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        return gradientDrawable;

    }

    /**
     * 得到实心的drawable, 一般作为选中，点中的效果
     *
     * @param cornerRadius 圆角半径
     * @param solidArgb    实心颜色
     * @return 得到实心效果
     */
    public static GradientDrawable getSolidRectDrawable(int cornerRadius, int solidArgb) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(cornerRadius);
        gradientDrawable.setColor(solidArgb);
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        return gradientDrawable;
    }

    /**
     * 得到带边框的状态选择器
     *
     * @param cornerRadiusPX 圆角半径
     * @param strokeWidthPX  边框宽度
     * @param strokeArgb     表框颜色
     * @param solidArgb      实心颜色
     * @return 状态选择器
     */
    public static StateListDrawable getDrawable(int cornerRadiusPX, int strokeWidthPX, int strokeArgb, int solidArgb) {
        return getStateListDrawable(getSolidRectDrawable(cornerRadiusPX, strokeArgb),
                getStrokeRectDrawable(cornerRadiusPX, solidArgb, strokeArgb, strokeWidthPX));
    }

    public static SpannableString getColorSpannableString(String msg, int color) {
        SpannableString spannableString = new SpannableString(msg);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        spannableString.setSpan(colorSpan, 0, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
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
