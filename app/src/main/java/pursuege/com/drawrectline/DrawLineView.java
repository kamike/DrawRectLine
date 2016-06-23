package pursuege.com.drawrectline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/6/23.
 */
public class DrawLineView extends View {
    private final int crossSpace;
    public int colorX, colorY;
    private Bitmap bmpBule, bmpYello, bmpGreen;
    public ArrayList<Float[]> listPoint;
    private int viewWidth;
    private int viewHeight;
    //选中哪个坐标点
    private int currentPointIndex;

    private Paint paint;

    public DrawLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
        listPoint = new ArrayList<Float[]>();
        colorX = Color.argb(255, 0, 255, 0);
        colorY = Color.argb(255, 0, 0, 255);
        bmpGreen = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_cross_green);
        bmpBule = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_cross_bule);
        bmpYello = BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_cross_yello);
        crossSpace = bmpYello.getWidth();
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(1f);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    private float moveX, moveY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveX = event.getX();
                moveY = event.getY();
                if (listPoint.size() < 3) {
                    listPoint.add(new Float[]{event.getX(), event.getY()});
                } else {
                    getCurrentPoint(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_MOVE:

                listPoint.get(currentPointIndex)[0] += event.getX(0)
                        - moveX;
                listPoint.get(currentPointIndex)[1] += event.getY(0)
                        - moveY;

                moveX = event.getX();
                moveY = event.getY();

                // isUp = false;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStrokeWidth(1f);
        for (int i = 0; i < listPoint.size(); i++) {
            if (currentPointIndex >= 0) {
                switch (i) {
                    case 0:
                        canvas.drawBitmap(bmpBule, listPoint.get(i)[0] - crossSpace
                                / 2, listPoint.get(i)[1] - crossSpace / 2, paint);
                        break;
                    case 1:
                        canvas.drawBitmap(bmpGreen, listPoint.get(i)[0]
                                - crossSpace / 2, listPoint.get(i)[1] - crossSpace
                                / 2, paint);
                        break;
                    default:
                        canvas.drawBitmap(bmpYello, listPoint.get(i)[0]
                                - crossSpace / 2, listPoint.get(i)[1] - crossSpace
                                / 2, paint);
                        break;
                }
            } else {
                switch (i) {
                    case 0:
                        canvas.drawBitmap(bmpBule, listPoint.get(i)[0] - crossSpace
                                / 2, listPoint.get(i)[1] - crossSpace / 2, paint);
                        break;

                    case 1:
                        canvas.drawBitmap(bmpGreen, listPoint.get(i)[0]
                                - crossSpace / 2, listPoint.get(i)[1] - crossSpace
                                / 2, paint);
                        break;
                    default:
                        canvas.drawBitmap(bmpYello, listPoint.get(i)[0]
                                - crossSpace / 2, listPoint.get(i)[1] - crossSpace
                                / 2, paint);
                        break;
                }
            }
        }
        if (listPoint.size() < 3) {
            return;
        }
        float leftY = getLeftScreenFocusY(listPoint.get(0)[0], listPoint.get(0)[1], listPoint.get(2)[0], listPoint.get(2)[1]);
        float rightY = getRightScreenFocusY(listPoint.get(0)[0], listPoint.get(0)[1], listPoint.get(2)[0], listPoint.get(2)[1]);
        canvas.drawLine(0, leftY, viewWidth, rightY, paint);
    }

    /**
     * 计算当前是哪个店
     *
     * @param x
     * @param y
     */
    private void getCurrentPoint(float x, float y) {
        HashMap<Float, Integer> map = new HashMap<Float, Integer>();
        for (int i = 0; i < listPoint.size(); i++) {
            float space = getSpace(listPoint.get(i), x, y);
            if (space <= 100) {
                map.put(space, i);
            }
        }
        if (map.isEmpty()) {
            return;
        }
        Float min = Collections.min(map.keySet());
        currentPointIndex = map.get(min);
    }

    private float getSpace(Float[] floats, float x, float y) {
        float space = (float) Math.sqrt(Math.pow(x - floats[0], 2)
                + Math.pow(y - floats[1], 2));
        return space;
    }

    /**
     * 得到两条直线焦点
     */
    private float[] getLineFocus(float x1, float y1, float x2, float y2, float x3,
                                 float y3, float x4, float y4) {
        float[] resoult = new float[2];
        resoult[0] = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4)
                * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        resoult[1] = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1)
                * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
        return resoult;

    }

    private float getLeftScreenFocusY(float x3,
                                      float y3, float x4, float y4) {
        float x1 = 0, y1 = 0, x2 = 0, y2 = viewHeight;
        float[] resoult = new float[2];
        resoult[0] = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4)
                * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        resoult[1] = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1)
                * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
        return resoult[1];

    }

    private float getRightScreenFocusY(float x1, float y1, float x2, float y2) {
        float x3 = viewWidth, y3 = 0, x4 = viewWidth, y4 = viewHeight;
        float[] resoult = new float[2];
        resoult[0] = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4)
                * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        resoult[1] = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1)
                * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
        return resoult[1];

    }


}
