package jifei.dachuang.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;
/*
描边这么重要的效果，安卓里的TextView竟然不自带
太失望了！
本类非原创
*/
public class StrokeTextView extends AppCompatTextView
{
    private TextView outlineTextView;

    public StrokeTextView(Context context)
    {
        super(context);
        outlineTextView = new TextView(context);
    }

    public StrokeTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        outlineTextView = new TextView(context, attrs);
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        outlineTextView = new TextView(context, attrs, defStyle);
    }

    public void setStroke(float width,String color)
    {
        TextPaint paint = outlineTextView.getPaint();
        paint.setStrokeWidth(width);// 描边宽度
        paint.setStyle(Paint.Style.STROKE);
        outlineTextView.setTextColor(Color.parseColor(color));// 描边颜色
        outlineTextView.setGravity(getGravity());
    }

    @Override
    public void setLayoutParams (ViewGroup.LayoutParams params)
    {
        super.setLayoutParams(params);
        outlineTextView.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 设置轮廓文字
        CharSequence outlineText = outlineTextView.getText();
        if (outlineText == null || !outlineText.equals(this.getText()))
        {
            outlineTextView.setText(getText());
            postInvalidate();
        }
        outlineTextView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        outlineTextView.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        outlineTextView.draw(canvas);
        super.onDraw(canvas);
    }
}