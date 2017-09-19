package com.example.developerhaoz.ckwhiteboard.doodle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.developerhaoz.ckwhiteboard.common.util.SavePictureUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的用于涂鸦的 View
 *
 * Created by developerHaoz on 2017/7/11.
 */

public class Doodle extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder = null;
    private static final String TAG = "Doodle";

    //当前所选画笔的形状
    private Action curAction = null;
    //默认画笔为黑色
    private int currentColor = Color.BLACK;
    //画笔的粗细
    private int currentSize = 5;

    private Paint mPaint;
    //记录画笔的列表
    private List<Action> mActions;

    private Bitmap bmp;

    private ActionType type = ActionType.Path;

    public Doodle(Context context) {
        super(context);
        init();
    }

    public Doodle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Doodle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);

        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(currentSize);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
        mActions = new ArrayList<>();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed: ");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }

        float touchX = event.getX();
        float touchY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setCurAction(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                Canvas canvas = mSurfaceHolder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                for (Action a : mActions) {
                    a.draw(canvas);
                }
                curAction.move(touchX, touchY);
                curAction.draw(canvas);
                mSurfaceHolder.unlockCanvasAndPost(canvas);
                break;
            case MotionEvent.ACTION_UP:
                mActions.add(curAction);
                curAction = null;
                break;

            default:
                break;
        }
        return true;
    }

    // 得到当前画笔的类型，并进行实例
    public void setCurAction(float x, float y) {
        switch (type) {
            case Point:
                curAction = new MyPoint(x, y, currentColor);
                break;
            case Path:
                curAction = new MyPath(x, y, currentSize, currentColor);
                break;
            case Line:
                curAction = new MyLine(x, y, currentSize, currentColor);
                break;
            case Rect:
                curAction = new MyRect(x, y, currentSize, currentColor);
                break;
            case Circle:
                curAction = new MyCircle(x, y, currentSize, currentColor);
                break;
            case FillecRect:
                curAction = new MyFillRect(x, y, currentSize, currentColor);
                break;
            case FilledCircle:
                curAction = new MyFillCircle(x, y, currentSize, currentColor);
                break;
        }
    }

    /**
     * 设置画笔的颜色
     * @param color 颜色
     */
    public void setColor(String color) {
        currentColor = Color.parseColor(color);
    }

    /**
     * 设置画笔的粗细
     * @param size 画笔的粗细
     */
    public void setSize(int size) {
        currentSize = size;
    }

    /**
     * 设置当前画笔的形状
     * @param type 画笔的形状
     */
    public void setType(ActionType type) {
        this.type = type;
    }

    /**
     * 获取画布的截图
     *
     * @return Bitmap
     */
    public Bitmap getBitmap() {
        bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        doDraw(canvas);
        return bmp;
    }

    /**
     * 保存涂鸦后的图片
     *
     * @param doodle Doodle 的实例
     * @return
     */
    public String saveBitmap(Doodle doodle){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/doodle/" + System.currentTimeMillis() + ".png";
        if (!new File(path).exists()) {
            new File(path).getParentFile().mkdir();
        }
        SavePictureUtil.savePicByPNG(doodle.getBitmap(), path);
        return path;
    }

    public void doDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        for (Action a : mActions) {
            a.draw(canvas);
        }
        canvas.drawBitmap(bmp, 0, 0, mPaint);
    }

    /**
     * 回退
     * @return 是否回退成功
     */
    public boolean back() {
        if (mActions != null && mActions.size() > 0) {
            mActions.remove(mActions.size() - 1);
            Canvas canvas = mSurfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            for (Action a : mActions) {
                a.draw(canvas);
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
            return true;
        }
        return false;
    }

    /**
     * 重置签名
     */
    public void reset(){
        if(mActions != null && mActions.size() > 0){
            mActions.clear();
            Canvas canvas = mSurfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            for (Action action : mActions) {
                action.draw(canvas);
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

     enum ActionType {
        Point, Path, Line, Rect, Circle, FillecRect, FilledCircle, Eraser
    }

}












