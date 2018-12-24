package com.bilibili.magicasakura.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import android.util.AttributeSet;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedList;

/**
 * @author xyczero617@gmail.com
 * @time 16/2/22
 */
@SuppressWarnings("WeakerAccess")
public class ColorStateListUtils {

    static ColorStateList createColorStateList(Context context, int resId) {
        if (resId <= 0) {
            return null;
        }
        TypedValue value = new TypedValue();
        context.getResources().getValue(resId, value, true);
        ColorStateList cl = null;
        if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            //Assume that "color/theme_color_primary" and "color/theme_color_profile" have the same color value;
            //However, "color/theme_color_primary" need to replace by themeId, "color/theme_color_profile" not.
            //If use value.data may cause "color/theme_color_profile" still been replaced by themeId
            cl = ColorStateList.valueOf(ThemeUtils.replaceColorById(context, value.resourceId));
        } else {
            final String file = value.string.toString();
            //noinspection TryWithIdenticalCatches
            try {
                if (file.endsWith("xml")) {
                    final XmlResourceParser rp = context.getResources().getAssets().openXmlResourceParser(
                            value.assetCookie, file);
                    final AttributeSet attrs = Xml.asAttributeSet(rp);
                    int type;

                    //noinspection StatementWithEmptyBody
                    while ((type = rp.next()) != XmlPullParser.START_TAG
                            && type != XmlPullParser.END_DOCUMENT) {
                        // Seek parser to start tag.
                    }

                    if (type != XmlPullParser.START_TAG) {
                        throw new XmlPullParserException("No start tag found");
                    }

                    cl = createFromXmlInner(context, rp, attrs);
                    rp.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        return cl;
    }

    static ColorStateList createFromXmlInner(Context context, XmlPullParser parser, AttributeSet attrs)
            throws IOException, XmlPullParserException {
        final String name = parser.getName();
        if (!"selector".equals(name)) {
            throw new XmlPullParserException(
                    parser.getPositionDescription() + ": invalid color state list tag " + name);
        }

        return inflateColorStateList(context, parser, attrs);
    }

    static ColorStateList inflateColorStateList(Context context, XmlPullParser parser, AttributeSet attrs)
            throws IOException, XmlPullParserException {
        final int innerDepth = parser.getDepth() + 1;
        int depth;
        int type;

        LinkedList<int[]> stateList = new LinkedList<>();
        LinkedList<Integer> colorList = new LinkedList<>();

        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && ((depth = parser.getDepth()) >= innerDepth || type != XmlPullParser.END_TAG)) {
            if (type != XmlPullParser.START_TAG || depth > innerDepth
                    || !"item".equals(parser.getName())) {
                continue;
            }

            TypedArray a1 = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.color});
            final int value = a1.getResourceId(0, Color.MAGENTA);
            final int baseColor = value == Color.MAGENTA ? Color.MAGENTA : ThemeUtils.replaceColorById(context, value);
            a1.recycle();
            TypedArray a2 = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.alpha});
            final float alphaMod = a2.getFloat(0, 1.0f);
            a2.recycle();
            colorList.add(alphaMod != 1.0f
                    ? ColorUtils.setAlphaComponent(baseColor, Math.round(Color.alpha(baseColor) * alphaMod))
                    : baseColor);

            stateList.add(extractStateSet(attrs));
        }

        if (stateList.size() > 0 && stateList.size() == colorList.size()) {
            int[] colors = new int[colorList.size()];
            for (int i = 0; i < colorList.size(); i++) {
                colors[i] = colorList.get(i);
            }
            return new ColorStateList(stateList.toArray(new int[stateList.size()][]), colors);
        }
        return null;
    }

    protected static int[] extractStateSet(AttributeSet attrs) {
        int j = 0;
        final int numAttrs = attrs.getAttributeCount();
        int[] states = new int[numAttrs];
        for (int i = 0; i < numAttrs; i++) {
            final int stateResId = attrs.getAttributeNameResource(i);
            switch (stateResId) {
                case 0:
                    break;
                case android.R.attr.color:
                case android.R.attr.alpha:
                    // Ignore attributes from StateListDrawableItem and
                    // AnimatedStateListDrawableItem.
                    continue;
                default:
                    states[j++] = attrs.getAttributeBooleanValue(i, false)
                            ? stateResId : -stateResId;
                    break;
            }
        }
        states = StateSet.trimStateSet(states, j);
        return states;
    }
}
