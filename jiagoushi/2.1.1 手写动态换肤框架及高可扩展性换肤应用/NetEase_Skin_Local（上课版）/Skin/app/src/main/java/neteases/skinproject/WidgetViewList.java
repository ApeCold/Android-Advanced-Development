package neteases.skinproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 理解 一个WidgetViewList 对应 多个控件
 * 理解 一个WidgetViewList 就是一个布局文件
 */
public class WidgetViewList {

    private final String TAG = WidgetViewList.class.getSimpleName();

    /**
     * 既然 一个WidgetViewList 对应 多个个控件
     * 那么这个WIDGET_VIEWS 就是对应 多个控件的
     * 注意⚠️：最终的结果多个WidgetView，是保存到这里的
     */
    private static final List<WidgetView> WIDGET_VIEWS = new ArrayList<>();

    // 认定以下定义的标记，就是需要换肤的
    private final List<String> attributeList = new ArrayList<>();

    {
        attributeList.add("background");
        attributeList.add("src");
        attributeList.add("textColor");
        attributeList.add("drawableLeft");
        attributeList.add("drawableTop");
        attributeList.add("drawableRight");
        attributeList.add("drawableBottom");
        attributeList.add("tint");
    }

    // private Typeface typeface;
    private Context context;

    /**
     * 用于缓存Typeface
     */
    private final static Map<WidgetViewList, Typeface> typefaceMap = new HashMap<>();

    private static int[] TYPEFACE_ATTR = {
            R.attr.custom_typeface
    };

    public WidgetViewList() {
    }

    public WidgetViewList(Activity activity) {
        this.context = activity;

        // 初始化字体
        typefaceMap.put(this, initTypeface());
    }

    /**
     * 此行为目的就是保存 WidgetView 到 静态集合中，因为所有的WidgetView都存放在静态集合中
     *
     * @param attributeSet
     * @param mView
     */
    public void saveWidgetView(AttributeSet attributeSet, View mView) {

        // 由于一个控件 有多个 属性名=属性值，所以需要把多个 属性名=属性值(对象) 保存到集合
        List<AttributeNameAndValue> attributeNameAndValues = new ArrayList<>();

        /**
         *      <TextView
         *         android:layout_width="wrap_content"
         *         android:layout_height="wrap_content"
         *         android:text="测试"
         *         android:textSize="30sp"
         *         android:textColor="@color/skin_my_textColor"
         *         />
         *
         *       遍历，就相当于要遍历上面的这种属性=属性值
         */
        for (int i = 0; i < attributeSet.getAttributeCount(); i++) {
            String attrName = attributeSet.getAttributeName(i);
            String attrValue = attributeSet.getAttributeValue(i);

            Log.d(TAG, attrName + " == " + attrValue);

            // 这种情况 不换肤
            if (attrValue.startsWith("#") || attrValue.startsWith("?")) {
                continue;
            }

            // 满足以上定义的标记，那就换呗
            if (attributeList.contains(attrName)) {
                // 符合就那获取Value
                // attributeSet.getAttributeValue(i);

                Log.d(TAG, "=============attrValue:" + attrValue);

                // 现在拿到的attrValue可能是 @46464345，所以需要把@给去掉
                attrValue = attrValue.substring(1);
                int attrValueInt = Integer.parseInt(attrValue);
                // 拿到🆔，就可以通过🆔去加载资源
                Log.d(TAG, "resId==============attrValueInt:" + attrValueInt);

                if (attrValueInt != 0) {
                    // 需要被替换的属性+属性值 【注意⚠️】
                    AttributeNameAndValue nameAndValue = new AttributeNameAndValue(attrName, attrValueInt);
                    attributeNameAndValues.add(nameAndValue); // 保存起来
                }
            }
        }

        /**
         * 最终要理解 保存好一个控件 WidgetView
         */
        WidgetView widgetView = new WidgetView(mView, attributeNameAndValues);
        WIDGET_VIEWS.add(widgetView);
    }

    /**
     * 点击换肤第四步：遍历所有保存的 控件(WidgetView)
     * 然后告诉每一个控件(WidgetView) 去换肤
     */
    public void skinChange() {
        for (WidgetView widgetView : WIDGET_VIEWS) {
            widgetView.skinChange();
        }
    }

    /**
     * 初始化字体对象相关
     */
    private Typeface initTypeface() {
        int[] resIds = new int[TYPEFACE_ATTR.length];
        Log.d(TAG, "context>>>>>>>>>>>>>>:" + context);
        TypedArray typedArray = context.obtainStyledAttributes(TYPEFACE_ATTR);
        for (int i = 0; i < typedArray.length(); i++) {
            resIds[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();

        Typeface typeface = SkinResources.getInstance().getTypeface(resIds[0]);
        return typeface;
    }

    /**
     * 换字体
     *
     * @param view
     */
    private void changeTypeface(View view) {
        if (view instanceof TextView /*&& typeface != null*/) {
            // 做缓存优化，提高下性能
            /*Typeface typefaceSave = typefaceMap.get(WidgetViewList.this);
            if (typefaceSave == null) {
                Typeface typeface = initTypeface();
                ((TextView) view).setTypeface(typeface);
                typefaceMap.put(WidgetViewList.this, typeface);
            } else {
                ((TextView) view).setTypeface(typefaceSave);
            }*/

            // 这样会有点卡
            ((TextView) view).setTypeface(initTypeface());

            // ...
        }
    }

    /**
     * 由于最终要保存 属性名 = 资源ID(Int型)attrValueInt
     * 类似于：android:textColor=
     * 所以定义JavaBean
     */
    class AttributeNameAndValue {
        String attrName;
        int attrValueInt;

        public AttributeNameAndValue(String attrName, int attrValueInt) {
            this.attrName = attrName;
            this.attrValueInt = attrValueInt;
        }
    }

    /**
     * 由于 一个TextView对应一个View，一个View中 有 多个AttributeNameAndValue
     * 所以需要描述这个对象，这个对象可以抽象理解为 TextView == WidgetView
     * 类似于：
     * <TextView
     * android:layout_width="wrap_content"
     * android:layout_height="wrap_content"
     * android:text="测试"
     * android:textSize="30sp"
     * android:textColor="@color/skin_my_textColor"
     * />
     * <p>
     * 所以定义成JavaBean
     */
    class WidgetView {

        View mView;
        List<AttributeNameAndValue> attributeNameAndValues;

        public WidgetView(View mView, List<AttributeNameAndValue> attributeNameAndValues) {
            this.mView = mView;
            this.attributeNameAndValues = attributeNameAndValues;
        }

        /**
         * 点击换肤第五步：遍历当前这个控件(WidgetView==TextView) 里面的属性(AttributeNameAndValue)，属性例如如下：
         * android:layout_width="wrap_content"
         * android:layout_height="wrap_content"
         * android:text="测试"
         * android:textSize="30sp"
         * android:textColor="@color/skin_my_textColor"
         */
        @SuppressLint("RestrictedApi")
        public void skinChange() {

            // 符合TextView的控件，我就换字体
            changeTypeface(mView);

            for (AttributeNameAndValue attributeNameAndValue : attributeNameAndValues) {
                switch (attributeNameAndValue.attrName) {
                    case "background":
                        Object background = SkinResources.getInstance().getBackground(attributeNameAndValue.attrValueInt);
                        if (background instanceof Integer) {
                            mView.setBackgroundColor((Integer) background);
                        } else {
                            // mView.setBackground((Drawable) background);
                            // 用兼容包的
                            ViewCompat.setBackground(mView, (Drawable) background);
                        }
                        break;

                    case "textColor":
                        TextView textView = (TextView) mView;
                        textView.setTextColor(SkinResources.getInstance().getColorStateList(attributeNameAndValue.attrValueInt));
                        break;

                    case "src":
                        Object src = SkinResources.getInstance().getBackground(attributeNameAndValue.attrValueInt);
                        if (src instanceof Integer) {
                            ((ImageView) mView).setImageDrawable(new ColorDrawable((Integer) src));
                        } else {
                            ((ImageView) mView).setImageDrawable((Drawable) src);
                        }
                    case "tint":
                        SkinResources skinRes = SkinResources.getInstance();
                        Log.d(TAG, "tint>>>>>>>>>>>" + attributeNameAndValue.attrValueInt + "  -- " + skinRes.getDefaultSkin());
                        break;
                }
            }
        }
    }
}
