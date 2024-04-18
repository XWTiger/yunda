package com.tiger.yunda.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GreenBorderDrawable extends Drawable {
    private Paint paint;
    private Rect rect;

    public GreenBorderDrawable() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rect = new Rect();
        // 设置绿色
        paint.setColor(Color.GREEN);
        // 设置线宽为3dp
        paint.setStrokeWidth(3f);
        // 设置为描边模式
        paint.setStyle(Paint.Style.STROKE);
        // 设置圆角
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        rect.set(canvas.getClipBounds());
        // 画圆角矩形边框
        canvas.drawRoundRect((float) rect.left, (float) rect.top, (float) rect.right, (float) rect.bottom, 10f, 10f, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
