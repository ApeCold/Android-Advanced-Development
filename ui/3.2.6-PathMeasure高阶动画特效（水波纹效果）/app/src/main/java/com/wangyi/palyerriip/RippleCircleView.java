package com.wangyi.palyerriip;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class RippleCircleView extends View {

    private RippleAnimationView rippleAnimationView;

    public RippleCircleView(RippleAnimationView rippleAnimationView) {
        this(rippleAnimationView.getContext(), null);
        this.rippleAnimationView = rippleAnimationView;
        this.setVisibility(View.INVISIBLE);
    }

    public RippleCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        int radius = (Math.min(getWidth(), getHeight())) / 2;
//        画一个水波纹
        canvas.drawCircle(radius, radius, radius - rippleAnimationView.getStrokWidth(), rippleAnimationView.paint);

    }
}