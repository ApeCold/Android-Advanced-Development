package com.wangyi.pingxinview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.wangyi.pingxinview.R;

import java.lang.reflect.Constructor;

public class ParallaxLayoutInflater  extends LayoutInflater {
    private static final String TAG = "kongjian";
    private ParallaxFragment fragment;
    protected ParallaxLayoutInflater(Context context) {
        super(context);
    }
    protected ParallaxLayoutInflater(LayoutInflater original, Context newContext,ParallaxFragment fragment) {
        super(original, newContext);
        this.fragment = fragment;
        setFactory2(new ParallaxFactory(this));
    }

        @Override
    public LayoutInflater cloneInContext(Context newContext) {
            return new ParallaxLayoutInflater(this,newContext,fragment);
        }
    class ParallaxFactory implements Factory2{
        private LayoutInflater inflater;
        private final String[] sClassPrefix = {
                "android.widget.",
                "android.view."
        };
        public ParallaxFactory(LayoutInflater inflater) {
            this.inflater = inflater;
        }
        int[] attrIds = {
                R.attr.a_in,
                R.attr.a_out,
                R.attr.x_in,
                R.attr.x_out,
                R.attr.y_in,
                R.attr.y_out};
//很多次  name=ImageView           钱    ----》 女朋友  RelativeLayout  ----->  能不能   com.wangyi.pingxinview.view.MyView
        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

            Log.i(TAG, "onCreateView: "+name);
            //2.系统视图需要加上前缀
            View view= null;
            if (name.contains(".")) {
               view= createMyView(name, context, attrs);

            }else {
                for (String prefix : sClassPrefix) {
                   view = createMyView(prefix + name, context, attrs);
                    if (view != null) {
                        break;
                    }
                }
            }
//        将 AttributeSet取出自定义属性    封装成javabean   设置到Viewtag
            //获取
            TypedArray a = context.obtainStyledAttributes(attrs, attrIds);
            if (a != null && a.length() > 0) {
                //获取自定义属性的值
                ParallaxViewTag tag = new ParallaxViewTag();
                tag.alphaIn = a.getFloat(0, 0f);
                tag.alphaOut = a.getFloat(1, 0f);
                tag.xIn = a.getFloat(2, 0f);
                tag.xOut = a.getFloat(3, 0f);
                tag.yIn = a.getFloat(4, 0f);
                tag.yOut = a.getFloat(5, 0f);
                //index
                view.setTag(R.id.parallax_view_tag,tag);
            }
            a.recycle();
            fragment.getParallaxViews().add(view);
            return view;
        }

        private View createMyView(String s, Context context, AttributeSet attrs) {
//            反射
            try {
                Class clazz = Class.forName(s);
                Constructor<View> constructor = clazz.getConstructor(Context.class, AttributeSet.class);
               return  constructor.newInstance(context, attrs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return null;
        }
    }
}

