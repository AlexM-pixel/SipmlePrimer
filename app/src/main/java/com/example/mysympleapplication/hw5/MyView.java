package com.example.mysympleapplication.hw5;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.mysympleapplication.R;

import java.util.ArrayList;

public class MyView extends View implements SetValues {
    private float startX;
    private int interval;
    private Paint background;
    private Paint linePaint;
    private RectF backgroundRect;
    ArrayList<Float> arrayListGrafic;
    Context context1;

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        context1 = context;
        arrayListGrafic = new ArrayList<>();
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(ContextCompat.getColor(context, R.color.colorGrafik_line));
        linePaint.setStrokeWidth(4f);
        backgroundRect = new RectF();
        background = new Paint(Paint.ANTI_ALIAS_FLAG);
        background.setColor(ContextCompat.getColor(context, R.color.colorGrafik_fon));
    }
    protected void onDraw(Canvas canvas) {
        final float canvasWidth = getWidth();
        final float canvasHeight = getHeight();
        backgroundRect.set(0f, 0f, canvasWidth, canvasHeight);              //фон
        canvas.drawRect(backgroundRect, background);

        final float baselineY1 = canvasHeight * 0.2f;
        float baselineX = canvasWidth / interval;                      //  вертикальные  полоски
        float baseLineY = canvasHeight / 10;
        for (int i = 0; i < interval; i++) {
            startX = baselineX + startX;
            canvas.drawLine(startX, canvasHeight, startX, baselineY1, linePaint);

        }
        int count = 0;
        int countLines = (arrayListGrafic.size() - 2);
        for (int i = 0; i < countLines; ) {                    //сам график
            canvas.drawLine(count * baselineX, canvasHeight - arrayListGrafic.get(i + 1) * baseLineY, baselineX * ++count, canvasHeight - (arrayListGrafic.get(i + 3)) * baseLineY, linePaint);
            i = i + 2;
        }
        startX = 0;
    }

    @Override
    public void setInterval(int val) {
        this.interval = val;
        invalidate();
    }

    @Override
    public void setArray(ArrayList<Float> listGrafic) {
        arrayListGrafic = listGrafic;
        invalidate();
    }


}
