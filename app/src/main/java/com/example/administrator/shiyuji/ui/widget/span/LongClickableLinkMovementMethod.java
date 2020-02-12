package com.example.administrator.shiyuji.ui.widget.span;

import android.os.Handler;
import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

/**
 * Created by Administrator on 2019/7/1.
 */

public class LongClickableLinkMovementMethod extends ScrollingMovementMethod {
    private static final int CLICK = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private boolean mHasPerformedLongPress;
    private LongClickableLinkMovementMethod.CheckForLongPress mPendingCheckForLongPress;
    private boolean pressed;
    private Handler handler = new Handler();
    private boolean longClickable = true;
    private float[] lastEvent = new float[2];
    private static LongClickableLinkMovementMethod sInstance;
    private static Object FROM_BELOW = new NoCopySpan.Concrete();

    public LongClickableLinkMovementMethod() {
    }

    public void setLongClickable(boolean value) {
        this.longClickable = value;
    }

    protected boolean handleMovementKey(TextView widget, Spannable buffer, int keyCode, int movementMetaState, KeyEvent event) {
        switch(keyCode) {
            case 23:
            case 66:
                if(KeyEvent.metaStateHasNoModifiers(movementMetaState) && event.getAction() == 0 && event.getRepeatCount() == 0 && this.action(1, widget, buffer)) {
                    return true;
                }
            default:
                return super.handleMovementKey(widget, buffer, keyCode, movementMetaState, event);
        }
    }

    protected boolean up(TextView widget, Spannable buffer) {
        return this.action(2, widget, buffer)?true:super.up(widget, buffer);
    }

    protected boolean down(TextView widget, Spannable buffer) {
        return this.action(3, widget, buffer)?true:super.down(widget, buffer);
    }

    protected boolean left(TextView widget, Spannable buffer) {
        return this.action(2, widget, buffer)?true:super.left(widget, buffer);
    }

    protected boolean right(TextView widget, Spannable buffer) {
        return this.action(3, widget, buffer)?true:super.right(widget, buffer);
    }

    private boolean action(int what, TextView widget, Spannable buffer) {
        Layout layout = widget.getLayout();
        int padding = widget.getTotalPaddingTop() + widget.getTotalPaddingBottom();
        int areatop = widget.getScrollY();
        int areabot = areatop + widget.getHeight() - padding;
        int linetop = layout.getLineForVertical(areatop);
        int linebot = layout.getLineForVertical(areabot);
        int first = layout.getLineStart(linetop);
        int last = layout.getLineEnd(linebot);
        MyURLSpan[] candidates = (MyURLSpan[])buffer.getSpans(first, last, MyURLSpan.class);
        int a = Selection.getSelectionStart(buffer);
        int b = Selection.getSelectionEnd(buffer);
        int selStart = Math.min(a, b);
        int selEnd = Math.max(a, b);
        if(selStart < 0 && buffer.getSpanStart(FROM_BELOW) >= 0) {
            selStart = selEnd = buffer.length();
        }

        if(selStart > last) {
            selEnd = 2147483647;
            selStart = 2147483647;
        }

        if(selEnd < first) {
            selEnd = -1;
            selStart = -1;
        }

        int beststart;
        int bestend;
        int i;
        int start;
        switch(what) {
            case 1:
                if(selStart == selEnd) {
                    return false;
                }

                MyURLSpan[] link = (MyURLSpan[])buffer.getSpans(selStart, selEnd, MyURLSpan.class);
                if(link.length != 1) {
                    return false;
                }

                link[0].onClick(widget);
                break;
            case 2:
                beststart = -1;
                bestend = -1;

                for(i = 0; i < candidates.length; ++i) {
                    start = buffer.getSpanEnd(candidates[i]);
                    if((start < selEnd || selStart == selEnd) && start > bestend) {
                        beststart = buffer.getSpanStart(candidates[i]);
                        bestend = start;
                    }
                }

                if(beststart >= 0) {
                    Selection.setSelection(buffer, bestend, beststart);
                    return true;
                }
                break;
            case 3:
                beststart = 2147483647;
                bestend = 2147483647;

                for(i = 0; i < candidates.length; ++i) {
                    start = buffer.getSpanStart(candidates[i]);
                    if((start > selStart || selStart == selEnd) && start < beststart) {
                        beststart = start;
                        bestend = buffer.getSpanEnd(candidates[i]);
                    }
                }

                if(bestend < 2147483647) {
                    Selection.setSelection(buffer, beststart, bestend);
                    return true;
                }
        }

        return false;
    }

    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();
        if(action != 1 && action != 0) {
            if(action == 2) {
                float[] position1 = new float[]{event.getX(), event.getY()};
                byte slop1 = 6;
                float xInstance1 = Math.abs(this.lastEvent[0] - position1[0]);
                float yInstance1 = Math.abs(this.lastEvent[1] - position1[1]);
                double instance1 = Math.sqrt(Math.hypot((double)xInstance1, (double)yInstance1));
                if(instance1 > (double)slop1) {
                    this.pressed = false;
                }
            } else if(action == 3) {
                this.pressed = false;
                this.lastEvent = new float[2];
            } else {
                this.pressed = false;
                this.lastEvent = new float[2];
            }
        } else {
            int position = (int)event.getX();
            int slop = (int)event.getY();
            position -= widget.getTotalPaddingLeft();
            slop -= widget.getTotalPaddingTop();
            position += widget.getScrollX();
            slop += widget.getScrollY();
            Layout xInstance = widget.getLayout();
            int yInstance = xInstance.getLineForVertical(slop);
            int instance = xInstance.getOffsetForHorizontal(yInstance, (float)position);
            MyURLSpan[] link = (MyURLSpan[])buffer.getSpans(instance, instance, MyURLSpan.class);
            if(link.length != 0) {
                if(action == 1) {
                    if(!this.mHasPerformedLongPress) {
                        link[0].onClick(widget);
                    }

                    this.pressed = false;
                    this.lastEvent = new float[2];
                } else if(action == 0) {
                    this.pressed = true;
                    this.lastEvent[0] = event.getX();
                    this.lastEvent[1] = event.getY();
                    this.checkForLongClick(link, widget);
                }

                return true;
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    private void checkForLongClick(MyURLSpan[] spans, View widget) {
        this.mHasPerformedLongPress = false;
        this.mPendingCheckForLongPress = new LongClickableLinkMovementMethod.CheckForLongPress(spans, widget);
        this.handler.postDelayed(this.mPendingCheckForLongPress, (long) ViewConfiguration.getLongPressTimeout());
    }

    public void removeLongClickCallback() {
        if(this.mPendingCheckForLongPress != null) {
            this.handler.removeCallbacks(this.mPendingCheckForLongPress);
            this.mPendingCheckForLongPress = null;
        }

    }

    private void performLongClick() {
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public void initialize(TextView widget, Spannable text) {
        Selection.removeSelection(text);
        text.removeSpan(FROM_BELOW);
    }

    public void onTakeFocus(TextView view, Spannable text, int dir) {
        Selection.removeSelection(text);
        if((dir & 1) != 0) {
            text.setSpan(FROM_BELOW, 0, 0, 34);
        } else {
            text.removeSpan(FROM_BELOW);
        }

    }

    public static LongClickableLinkMovementMethod getInstance() {
        if(sInstance == null) {
            sInstance = new LongClickableLinkMovementMethod();
        }

        return sInstance;
    }

    class CheckForLongPress implements Runnable {
        MyURLSpan[] spans;
        View widget;

        public CheckForLongPress(MyURLSpan[] spans, View widget) {
            this.spans = spans;
            this.widget = widget;
        }

        public void run() {
            if(LongClickableLinkMovementMethod.this.isPressed() && LongClickableLinkMovementMethod.this.longClickable) {
                this.spans[0].onLongClick(this.widget);
                LongClickableLinkMovementMethod.this.mHasPerformedLongPress = true;
            }

        }
    }
}
