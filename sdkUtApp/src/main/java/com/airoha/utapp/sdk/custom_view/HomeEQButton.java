package com.airoha.utapp.sdk.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class HomeEQButton extends View {
    private Drawable icon;
    private String text;

    public HomeEQButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
        invalidate(); // Yêu cầu vẽ lại Custom View
    }

    public void setText(String text) {
        this.text = text;
        invalidate(); // Yêu cầu vẽ lại Custom View
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Vẽ hình dạng
        Paint shapePaint = new Paint();
        shapePaint.setColor(Color.parseColor("#EDEDED"));
        shapePaint.setStyle(Paint.Style.FILL);
        shapePaint.setAntiAlias(true);

        float radius = 40; // Radius của hình dạng
        RectF shapeRect = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(shapeRect, radius, radius, shapePaint);

        // Vẽ biểu đồ
        if (icon != null) {
            int iconWidth = icon.getIntrinsicWidth();
            int iconHeight = icon.getIntrinsicHeight();
            int iconLeft = (getWidth() - iconWidth) / 2;
            int iconTop = (getHeight() - iconHeight) / 2;
            icon.setBounds(iconLeft, iconTop, iconLeft + iconWidth, iconTop + iconHeight);
            icon.draw(canvas);
        }

        // Vẽ văn bản
        if (text != null) {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.BLACK); // Màu văn bản
            textPaint.setTextSize(30); // Kích thước văn bản
            textPaint.setAntiAlias(true);

            Rect bounds = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), bounds);
            int textWidth = bounds.width();
            int textHeight = bounds.height();
            int textX = (getWidth() - textWidth) / 2;
            int textY = (getHeight() - textHeight) / 2;

            canvas.drawText(text, textX, textY, textPaint);
        }
    }
}
