package pursuege.com.drawrectline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                if (isFileRect) {
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isFileRect) {
                    return true;
                }
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
        paint.setStyle(Paint.Style.STROKE);
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
        allXFocusPoint = new ArrayList<>();
        allYFocusPoint = new ArrayList<>();
        float leftY = getLeftScreenFocusY(listPoint.get(0)[0], listPoint.get(0)[1], listPoint.get(2)[0], listPoint.get(2)[1]);
        float rightY = getRightScreenFocusY(listPoint.get(0)[0], listPoint.get(0)[1], listPoint.get(2)[0], listPoint.get(2)[1]);
        canvas.drawLine(0, leftY, viewWidth, rightY, paint);
        pointD[0] = listPoint.get(1)[0] + listPoint.get(2)[0] - listPoint.get(0)[0];
        pointD[1] = listPoint.get(1)[1] + listPoint.get(2)[1] - listPoint.get(0)[1];
        float A2y = getLeftScreenFocusY(listPoint.get(1)[0], listPoint.get(1)[1], pointD[0], pointD[1]);
        float right = getRightScreenFocusY(0, A2y, listPoint.get(1)[0], listPoint.get(1)[1]);
        paint.setColor(colorY);
        Line lineX = new Line(0, A2y, viewWidth, right);
        canvas.drawLine(lineX.startX, lineX.startY, lineX.endX, lineX.endY, paint);
        allXFocusPoint.add(lineX);
        currentSpaceY = Math.abs(A2y - leftY);
        for (int i = 1; i < 100; i++) {
            Line line1 = new Line(0, A2y + currentSpaceY * i, viewWidth, right + currentSpaceY * i);
            Line line2 = new Line(0, A2y - currentSpaceY * i, viewWidth, right - currentSpaceY * i);
            canvas.drawLine(line1.startX, line1.startY, line1.endX, line1.endY, paint);
            canvas.drawLine(line2.startX, line2.startY, line2.endX, line2.endY, paint);
            allXFocusPoint.add(line1);
            allXFocusPoint.add(line2);
        }
        //gener
        float topY = getTopScreenFocusY(listPoint.get(0)[0], listPoint.get(0)[1], listPoint.get(1)[0], listPoint.get(1)[1]);
        float bottomY = getBottomScreenFocusY(listPoint.get(0)[0], listPoint.get(0)[1], listPoint.get(1)[0], listPoint.get(1)[1]);
        paint.setColor(colorX);
        Line lineY = new Line(topY, 0, bottomY, viewHeight);
        canvas.drawLine(lineY.startX, lineY.startY, lineY.endX, lineY.endY, paint);
        allYFocusPoint.add(lineY);
        //22222222
        float top = getTopScreenFocusY(listPoint.get(2)[0], listPoint.get(2)[1], pointD[0], pointD[1]);
        currentSpaceX = Math.abs(top - topY);
        for (int i = 1; i < 100; i++) {
            Line line1 = new Line(topY + currentSpaceX * i, 0, bottomY + currentSpaceX * i, viewHeight);
            Line line2 = new Line(topY - currentSpaceX * i, 0, bottomY - currentSpaceX * i, viewHeight);
            canvas.drawLine(line1.startX, line1.startY, line1.endX, line1.endY, paint);
            canvas.drawLine(line2.startX, line2.startY, line2.endX, line2.endY, paint);
            allYFocusPoint.add(line1);
            allYFocusPoint.add(line2);
        }
        //genera
        if (!isFileRect) {
            return;
        }
        paint.setColor(Color.RED);
//        getClickPostionRect(canvas, moveX, moveY, leftY, rightY);
        getClickPostionRect(canvas, moveX, moveY);
    }

    /**
     * 所有的线
     */
    private ArrayList<Line> allXFocusPoint, allYFocusPoint;

    float[] pointD = new float[2];

    private boolean isFileRect;

    public void setFillRect() {
        if (isFileRect) {
            isFileRect = false;
        } else {
            isFileRect = true;
        }
        invalidate();

    }

    /**
     * 横线偏移量和纵向偏移量
     */
    private float currentSpaceX, currentSpaceY;

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
    private Float[] getLineFocus(float x1, float y1, float x2, float y2, float x3,
                                 float y3, float x4, float y4) {
        Float[] resoult = new Float[2];
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

    private float getTopScreenFocusY(float x1, float y1, float x2, float y2) {
        float x3 = 0, y3 = 0, x4 = viewWidth, y4 = 0;
        float[] resoult = new float[2];
        resoult[0] = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4)
                * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        resoult[1] = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1)
                * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
        return resoult[0];

    }

    private float getBottomScreenFocusY(float x1, float y1, float x2, float y2) {
        float x3 = 0, y3 = viewHeight, x4 = viewWidth, y4 = viewHeight;
        float[] resoult = new float[2];
        resoult[0] = ((x1 - x2) * (x3 * y4 - x4 * y3) - (x3 - x4)
                * (x1 * y2 - x2 * y1))
                / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        resoult[1] = ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1)
                * (y3 - y4))
                / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
        return resoult[0];

    }

    private float getDistance(float x, float y, float x1, float y1) {
        float cX = x - x1;
        float cY = y - y1;

        return (float) Math.sqrt(cX * cX + cY * cY);
    }

    private double getRawAngle(float startX, float startY, float stopX,
                               float stopY) {
        double angle = 0;
        if (stopY > startY) {
            if (stopX > startX) {
                angle = Math.atan(Math.abs(stopY - startY)
                        / Math.abs(stopX - startX));

            } else {
                angle = 3.1415926 - Math.atan(Math.abs(stopY - startY)
                        / Math.abs(stopX - startX));
            }

        } else {
            if (stopX > startX) {
                angle = 3.1415926 - Math.atan(Math.abs(stopY - startY)
                        / Math.abs(stopX - startX));

            } else {
                angle = Math.atan(Math.abs(stopY - startY)
                        / Math.abs(stopX - startX));
            }

        }

        return angle;
    }


    /**
     * 根据xy，计算所在矩形
     */
    private void getClickPostionRect(Canvas canvas, float x, float y, float leftY, float rightY) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(20);

        double angleY = getRawAngle(listPoint.get(0)[0], listPoint.get(0)[1], listPoint.get(2)[0], listPoint.get(2)[1]);
        float endx = (float) (x + 50 * Math.cos(angleY));
        float endY = (float) (y + 50 * Math.sin(angleY));

        int numberY = (int) ((getLeftScreenFocusY(x, y, endx, endY) - leftY) / currentSpaceY);
        float rectTopLeftY = listPoint.get(0)[1] + currentSpaceY * numberY;
        float rectBottomLeftY = listPoint.get(1)[1] + currentSpaceY * numberY;
        float rectTopRightY = listPoint.get(2)[1] + currentSpaceY * numberY;
        float rectBottomRightY = pointD[1] + currentSpaceY * numberY;
        //
        double angleX = getRawAngle(listPoint.get(0)[0], listPoint.get(0)[1], listPoint.get(1)[0], listPoint.get(1)[1]);
        float otherX = (float) (x + 50 * Math.cos(angleX));
        float otherY = (float) (y + 50 * Math.sin(angleX));
        int numberX = (int) ((getTopScreenFocusY(x, y, otherX, otherY) - rightY) / currentSpaceX);
        float rectTopLeftX = listPoint.get(0)[0] + currentSpaceX * numberX;
        float rectBottomLeftX = listPoint.get(1)[0] + currentSpaceX * numberX;
        float rectTopRightX = listPoint.get(2)[0] + currentSpaceX * numberX;
        float rectBottomRightX = pointD[0] + currentSpaceX * numberX;
        //draw rect
        Path path = new Path();
        path.moveTo(rectTopLeftX, rectTopLeftY);
        path.lineTo(rectTopRightX, rectTopRightY);
        path.lineTo(rectBottomRightX, rectBottomRightY);
        path.lineTo(rectBottomLeftX, rectBottomLeftY);
        path.close();
//        canvas.drawPath(path, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawPoint(x, y, paint);
        paint.setColor(Color.RED);
        canvas.drawPoint(listPoint.get(0)[0] + currentSpaceX, listPoint.get(0)[1] + currentSpaceY, paint);
        canvas.drawPoint(listPoint.get(1)[0] + currentSpaceX, listPoint.get(1)[1] + currentSpaceY, paint);
        canvas.drawPoint(listPoint.get(2)[0] + currentSpaceX, listPoint.get(2)[1] + currentSpaceY, paint);
        System.out.println("numberX:" + numberX + ",numberY:" + numberY);
    }

    private void getClickPostionRect(Canvas canvas, final float x, final float y) {
        int size = allXFocusPoint.size();
        ArrayList<Float[]> allPoint = new ArrayList<>();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10);
        int[] colArray = new int[size];
        for (int i = 0; i < size; i++) {
            Line lineX = allXFocusPoint.get(i);
            for (int j = 0; j < size; j++) {
                Line lineY = allYFocusPoint.get(j);
                Float[] point = getLineFocus(lineX.startX, lineX.startY, lineX.endX, lineX.endY,
                        lineY.startX, lineY.startY, lineY.endX, lineY.endY);
                if (point[0] >= 0 && point[0] <= viewWidth && point[1] >= 0 && point[1] <= viewHeight) {
                    colArray[i]++;
                    allPoint.add(point);
                }
            }
        }

        Collections.sort(allPoint, new Comparator<Float[]>() {
            @Override
            public int compare(Float[] xy1, Float[] xy2) {
                if (getDistance(xy1, x, y) >= getDistance(xy2, x, y)) {
                    return 1;
                }
                return -1;
            }
        });
        if (allPoint.isEmpty()) {
            Log.i("log_test", "拷贝为空");
            return;
        }
        float[] tarPoint = getTarPoint(allPoint.get(0)[0],allPoint.get(0)[1]);

        Path path = new Path();
        path.moveTo(tarPoint[0], tarPoint[1]);
        path.lineTo(tarPoint[2], tarPoint[3]);
        path.lineTo(tarPoint[4], tarPoint[5]);
        path.lineTo(tarPoint[6], tarPoint[7]);
        path.close();
        canvas.drawPath(path, paint);
        paint.setColor(Color.BLUE);
        canvas.drawPoint(x, y, paint);
        canvas.drawPoint(allPoint.get(0)[0],allPoint.get(0)[1],paint);

    }

    /**
     * 根据终点坐标，计算偏移坐标
     * @param aFloat
     * @param aFloat1
     * @return
     */
    private float[] getTarPoint(Float aFloat, Float aFloat1) {
        float[] resoult=new float[8];
        resoult[0]=aFloat;
        resoult[1]=aFloat1;

        resoult[2]=aFloat+listPoint.get(2)[0]-listPoint.get(0)[0];
        resoult[3]=aFloat1+listPoint.get(2)[1]-listPoint.get(0)[1];

        resoult[4]=aFloat+pointD[0]-listPoint.get(0)[0];
        resoult[5]=aFloat1+pointD[1]-listPoint.get(0)[1];

        resoult[6]=aFloat+listPoint.get(1)[0]-listPoint.get(0)[0];
        resoult[7]=aFloat1+listPoint.get(1)[1]-listPoint.get(0)[1];

        return resoult;
    }

    private int indexFloatPoint(ArrayList<Float[]> tempPoint, Float[] f) {
        for (int i = 0; i < tempPoint.size(); i++) {
            if (tempPoint.get(i)[0] == f[0] && tempPoint.get(i)[1] == f[1]) {
                return i;
            }
        }
        return -1;
    }


    private float getDistance(Float[] xy, float x, float y) {
        return (float) Math.sqrt(Math.pow(xy[0] - x, 2) + Math.pow(xy[1] - y, 2));
    }


}

