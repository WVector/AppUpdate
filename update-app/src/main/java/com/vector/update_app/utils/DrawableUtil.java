package com.vector.update_app.utils;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.TextView;

import static com.vector.update_app.utils.ColorUtil.getColorStateList;
import static com.vector.update_app.utils.ColorUtil.getRandomColor;


/**
 * Created by Vector
 * on 2017/6/26 0026.
 */

public class DrawableUtil {
    private DrawableUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 得到实心的drawable, 一般作为选中，点中的效果
     *
     * @param cornerRadius 圆角半径
     * @param solidColor   实心颜色
     * @return 得到实心效果
     */
    public static GradientDrawable getSolidRectDrawable(int cornerRadius, int solidColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        // 设置矩形的圆角半径
        gradientDrawable.setCornerRadius(cornerRadius);
        // 设置绘画图片色值
        gradientDrawable.setColor(solidColor);
        // 绘画的是矩形
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        return gradientDrawable;
    }

    /**
     * 得到空心的效果，一般作为默认的效果
     *
     * @param cornerRadius 圆角半径
     * @param solidColor   实心颜色
     * @param strokeColor  边框颜色
     * @param strokeWidth  边框宽度
     * @return 得到空心效果
     */
    public static GradientDrawable getStrokeRectDrawable(int cornerRadius, int solidColor, int strokeColor, int strokeWidth) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        gradientDrawable.setColor(solidColor);
        gradientDrawable.setCornerRadius(cornerRadius);
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        return gradientDrawable;

    }

    /**
     * 背景选择器
     *
     * @param pressedDrawable 按下状态的Drawable
     * @param normalDrawable  正常状态的Drawable
     * @return 状态选择器
     */
    public static StateListDrawable getStateListDrawable(Drawable pressedDrawable, Drawable normalDrawable) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, pressedDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, normalDrawable);
        //设置不能用的状态
        //默认其他状态背景
        GradientDrawable gray = getSolidRectDrawable(10, Color.GRAY);
        stateListDrawable.addState(new int[]{}, gray);
        return stateListDrawable;
    }

    /**
     * 实体  状态选择器
     *
     * @param cornerRadius 圆角半径
     * @param pressedColor 按下颜色
     * @param normalColor  正常的颜色
     * @return 状态选择器
     */
    public static StateListDrawable getDrawable(int cornerRadius, int pressedColor, int normalColor) {
        return getStateListDrawable(getSolidRectDrawable(cornerRadius, pressedColor), getSolidRectDrawable(cornerRadius, normalColor));
    }

    /**
     * 得到 正常空心， 按下实体的状态选择器
     *
     * @param cornerRadiusPX 圆角半径
     * @param strokeWidthPX  边框宽度
     * @param subColor       表框颜色
     * @param mainColor      实心颜色
     * @return 状态选择器
     */
    public static StateListDrawable getStrokeSolidDrawable(int cornerRadiusPX, int strokeWidthPX, int subColor, int mainColor) {
        //一般solidColor 为透明
        return getStateListDrawable(
                //实心
                getSolidRectDrawable(cornerRadiusPX, subColor),
                //空心
                getStrokeRectDrawable(cornerRadiusPX, mainColor, subColor, strokeWidthPX));
    }

    /**
     * 得到 正常空心， 按下实体的状态选择器
     *
     * @param cornerRadiusPX 圆角半径
     * @param strokeWidthPX  边框宽度
     * @param subColor       表框颜色
     * @param mainColor      实心颜色
     * @return 状态选择器
     */
    public static StateListDrawable getSolidStrokeDrawable(int cornerRadiusPX, int strokeWidthPX, int subColor, int mainColor) {
        //一般solidColor 为透明
        return getStateListDrawable(
                //空心
                getStrokeRectDrawable(cornerRadiusPX, subColor, mainColor, strokeWidthPX),
                //实心
                getSolidRectDrawable(cornerRadiusPX, mainColor));
    }

    /**
     * 实体 按下的颜色加深
     *
     * @param cornerRadius 圆角半径
     * @param normalColor  正常的颜色
     * @return 状态选择器
     */

    public static StateListDrawable getDrawable(int cornerRadius, int normalColor) {
        return getDrawable(cornerRadius, ColorUtil.colorDeep(normalColor), normalColor);
    }

    /**
     * 实体 得到随机色 状态选择器
     *
     * @param cornerRadius 圆角半径
     * @return 状态选择器
     */

    public static StateListDrawable getDrawable(int cornerRadius) {
        return getDrawable(cornerRadius, getRandomColor());
    }

    /**
     * 实体 得到随机色 状态选择器 默认10px
     *
     * @return 状态选择器
     */

    public static StateListDrawable getDrawable() {
        return getDrawable(10);
    }


    /**
     * 实心 得到 随机背景色并且带选择器, 并且可以设置圆角
     *
     * @param cornerRadius 圆角
     * @return 状态选择器
     */
    public static StateListDrawable getRandomColorDrawable(int cornerRadius) {
        return getDrawable(cornerRadius, getRandomColor(), getRandomColor());

    }

    /**
     * 实心 得到随机背景色并且带选择器, 并且可以设置圆角
     *
     * @return 状态选择器
     */
    public static StateListDrawable getRandomColorDrawable() {
        return getRandomColorDrawable(10);

    }

    /**
     * 空心，按下实心 得到随机背景色并且带选择器, 并且可以设置圆角
     *
     * @return 状态选择器
     */
    public static StateListDrawable getStrokeRandomColorDrawable() {
        return getStrokeSolidDrawable(10, 4, getRandomColor(), Color.TRANSPARENT);
    }

    /**
     * 默认空心 设置TextView 主题，
     *
     * @param textView     textView
     * @param strokeWidth  边框宽度 px
     * @param cornerRadius 圆角
     * @param color        颜色
     */
    public static void setTextStrokeTheme(TextView textView, int strokeWidth, int cornerRadius, int color) {
        textView.setBackgroundDrawable(getStrokeSolidDrawable(cornerRadius, strokeWidth, color, Color.WHITE));
        textView.setTextColor(getColorStateList(Color.WHITE, color));
        textView.getPaint().setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
    }

    /**
     * 默认空心 设置TextView 主题，随机颜色
     *
     * @param textView     textView
     * @param strokeWidth  边框宽度 px
     * @param cornerRadius 圆角
     */
    public static void setTextStrokeTheme(TextView textView, int strokeWidth, int cornerRadius) {
        setTextStrokeTheme(textView, strokeWidth, cornerRadius, ColorUtil.getRandomColor());
    }

    /**
     * 默认空心 设置TextView 主题，随机颜色
     *
     * @param textView textView
     */
    public static void setTextStrokeTheme(TextView textView) {
        setTextStrokeTheme(textView, 6, 10);
    }

    /**
     * 默认空心 设置TextView 主题，随机颜色
     * @param textView 文本控件
     * @param color 颜色
     */
    public static void setTextStrokeTheme(TextView textView, int color) {
        setTextStrokeTheme(textView, 6, 10, color);
    }

    /**
     * 默认实心 设置TextView 主题，
     *
     * @param textView     textView
     * @param strokeWidth  边框宽度 px
     * @param cornerRadius 圆角
     * @param color        颜色
     */
    public static void setTextSolidTheme(TextView textView, int strokeWidth, int cornerRadius, int color) {
        textView.setBackgroundDrawable(getSolidStrokeDrawable(cornerRadius, strokeWidth, Color.WHITE, color));
        textView.setTextColor(getColorStateList(color, Color.WHITE));
        textView.getPaint().setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
    }

    /**
     * 默认实心 设置TextView 主题，随机颜色
     *
     * @param textView     textView
     * @param strokeWidth  边框宽度 px
     * @param cornerRadius 圆角
     */
    public static void setTextSolidTheme(TextView textView, int strokeWidth, int cornerRadius) {
        setTextSolidTheme(textView, strokeWidth, cornerRadius, ColorUtil.getRandomColor());
    }

    /**
     * 默认实心 设置TextView 主题，随机颜色
     *
     * @param textView textView
     */
    public static void setTextSolidTheme(TextView textView) {
        setTextSolidTheme(textView, 6, 10);
    }

}
