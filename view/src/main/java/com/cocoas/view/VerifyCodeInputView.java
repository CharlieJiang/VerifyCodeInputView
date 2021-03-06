package com.cocoas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.method.BaseMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * 自定义验证码输入框
 *
 * @author 蒋庆意
 * @Date 2020/6/5
 * @Time 10:19
 */
public class VerifyCodeInputView extends AppCompatEditText {

    private static final String TAG = "VerifyCodeInputView";
    /** 输入框类型常量 */
    private interface BoxType{
        int LINE = 0;// 底部横线输入框
        int RECT = 1;// 矩形输入框
        int CIRCLE = 2;// 圆形输入框
    }

    /*自定义属性↓↓↓*/
    // 输入框宽度
    private int boxWidth = 128;
    // 输入框高度
    private int boxHeight = 128;
    // 输入框边框高度
    private int boxBorderHeight = 2;
    // 输入框间距
    private int boxSpacing = 30;
    // 输入框数量：默认6个
    private int boxCount = 6;
    // 输入框边框默认颜色
    private int boxBorderColorNormal = getResources().getColor(android.R.color.darker_gray);
    // 输入框边框高亮颜色
    private int boxBorderColorFocused = getResources().getColor(android.R.color.holo_blue_dark);
    // 输入框是否自动适应父布局宽度：如果不自动适应，则以设定的宽高为准
    // 该属性需要配合android:layout_width="match_parent"属性一起使用，否则可能无效
    private boolean autoFit;
    // 输入框类型
    private int boxType = BoxType.LINE;
    /*自定义属性↑↑↑*/
    //输入框Rect
    private Rect boxRect = new Rect();
    //输入完成监听
    private OnInputFinishedListener onInputFinishedListener;
    // 输入框中间坐标Y轴高度
    int centerY = 0;

    public VerifyCodeInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeInputViewStyle);
        autoFit = typedArray.getBoolean(R.styleable.VerifyCodeInputViewStyle_autoFit, false);
        //获取自定义属性中有值的属性个数
        int indexCount = typedArray.getIndexCount();
        //遍历获取自定义属性的值
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.VerifyCodeInputViewStyle_boxWidth) {
                this.boxWidth = (int) typedArray.getDimension(attr, boxWidth);
            } else if (attr == R.styleable.VerifyCodeInputViewStyle_boxHeight) {
                this.boxHeight = (int) typedArray.getDimension(attr, boxHeight);
            } else if (attr == R.styleable.VerifyCodeInputViewStyle_boxBorderHeight) {
                this.boxBorderHeight = (int) typedArray.getDimension(attr, boxBorderHeight);
            } else if (attr == R.styleable.VerifyCodeInputViewStyle_boxSpacing) {
                this.boxSpacing = (int) typedArray.getDimension(attr, boxSpacing);
            } else if (attr == R.styleable.VerifyCodeInputViewStyle_boxCount) {
                this.boxCount = typedArray.getInt(attr, boxCount);
            } else if (attr == R.styleable.VerifyCodeInputViewStyle_boxBorderColorNormal) {
                this.boxBorderColorNormal = typedArray.getColor(attr,
                        getResources().getColor(android.R.color.darker_gray));
            } else if (attr == R.styleable.VerifyCodeInputViewStyle_boxBorderColorFocused) {
                this.boxBorderColorFocused = typedArray.getColor(attr,
                        getResources().getColor(android.R.color.holo_blue_dark));
            } else if (attr == R.styleable.VerifyCodeInputViewStyle_autoFit) {
                this.autoFit = typedArray.getBoolean(attr, false);
            } else if (attr == R.styleable.VerifyCodeInputViewStyle_boxType) {
                this.boxType = typedArray.getInt(attr, BoxType.LINE);
            }
        }
        //使用完TypedArray及时回收，以供其他模块使用
        typedArray.recycle();
        //设置输入框允许输入的最大数字数量
        setInputMaxLength(boxCount);
        //输入框不支持长按
        setLongClickable(false);
        //隐藏输入框原背景色
        setBackgroundColor(Color.TRANSPARENT);
        //不显示输入框中的光标
        setCursorVisible(false);
    }

    /**
     * 自动计算输入框宽高和间距
     */
    private void calculateAttrs(int width) {
//        // 计算输入框间间距
//        boxSpacing = (width - (boxBorderHeight + boxWidth) * boxCount) / ( boxCount - 1);
//		Logger.d("getMeasuredWidth()=" + width);
        // 计算输入框宽度
        boxWidth = (width - boxSpacing * (boxCount - 1) - boxBorderHeight * boxCount) / boxCount;
        boxHeight = boxWidth;
//		Logger.w("boxWidth=" + boxWidth + ",boxHeight=" + boxHeight + ",boxSpacing=" + boxSpacing);
    }

    /**
     * 设置最大输入长度
     *
     * @param maxLength
     */
    private void setInputMaxLength(int maxLength) {
        if (maxLength > 0) {
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        } else {
            setFilters(new InputFilter[0]);
        }
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        //不支持文本选中
        return new BaseMovementMethod();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //输入框当前的尺寸与通过自定义属性设置的尺寸进行比较
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthSpecModel = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecModel = MeasureSpec.getMode(heightMeasureSpec);
        // 比较输入框默认的高度（根据字体大小等自动计算的高度）是否符合设定的高度boxHeight
        if (height < boxHeight) {
            height = boxHeight;
        }
//        else{
//            // 重置输入框的高度为默认高度
//            boxHeight = height;
//        }
        // 设置单个输入框的宽高相同：取宽高中最小值
        if(boxWidth < boxHeight){
            boxHeight = boxWidth;
        }else{
            boxWidth = boxHeight;
        }
//        boxWidth = boxHeight;
        int actualWidth = (boxWidth + boxBorderHeight) * boxCount + boxSpacing * (boxCount - 1);
        if (widthSpecModel == MeasureSpec.AT_MOST) {
            width = actualWidth;
        }
        //重新计算输入框的测量尺寸
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, widthSpecModel);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, heightSpecModel);

        //设置新的测量尺寸
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//		}
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.w("onLayout","(" + getMeasuredWidth() + "," + getMeasuredHeight() + ")");
        //自动计算每个输入框的宽度、输入框间间距
        Log.d("onLayout","autoFit=" + autoFit);
        if (autoFit) {
            calculateAttrs(getMeasuredWidth());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 输入框中间坐标Y轴高度
        centerY = canvas.getHeight()/2;
        // 重新绘制输入框：根据设定的输入框类型属性进行绘制
        switch (boxType){
            case BoxType.RECT:// 矩形输入框
                drawRectBox(canvas);
                break;
            case BoxType.CIRCLE:// 圆形输入框
                drawCircleBox(canvas);
                break;
            case BoxType.LINE:// 底部横线输入框
            default:
                drawLineBox(canvas);
                break;
        }
        // 绘制数字
        drawNumber(canvas);
//        drawCenterLine(canvas);
    }

    /**
     * 绘制中轴线用于校正数字的位置
     * @param canvas
     */
    private void drawCenterLine(Canvas canvas){
        //获取当前画布保存的状态
        int saveCount = canvas.getSaveCount();
        //画布归位
        canvas.translate(0, 0);
        //画笔
        Paint linePaint = new Paint();
        linePaint.setColor(Color.RED);
        // 画线段
        canvas.drawLine(0,canvas.getHeight()/2,canvas.getWidth(),canvas.getHeight()/2,linePaint);
        //画布还原
        canvas.restoreToCount(saveCount);
    }

    /**
     * 绘制圆形输入框
     * @param canvas
     */
    private void drawCircleBox(Canvas canvas){
        // 计算圆形坐标坐标（需要考虑画笔的宽度）
        float cx = (boxBorderHeight + boxWidth)/2;// 圆心坐标x
        float cy = centerY;// 圆心坐标y
        float radius = (boxHeight - boxBorderHeight)/2;// 圆半径
        /*创建输入框画笔*/
        Paint boxPaint = new Paint();
        //普通输入框颜色
        boxPaint.setColor(boxBorderColorNormal);
        // 设置画笔样式：描边
        boxPaint.setStyle(Paint.Style.STROKE);
        // 设置画笔宽度
        boxPaint.setStrokeWidth(boxBorderHeight);
        //当前画布保存的状态
        int canvasSaveCount = canvas.getSaveCount();
        //保存画布
        canvas.save();
        /*遍历绘制所有数字输入框*/
        for (int i = 0; i < boxCount; i++) {
            canvas.drawCircle(cx,cy,radius, boxPaint);
            //计算下一个数字输入框的位置
            int nextBoxLeft = boxWidth + boxSpacing + boxBorderHeight;
            //保存画布
            canvas.save();
            //画布平移到下一个绘制输入框的位置
            canvas.translate(nextBoxLeft, 0);
        }
        // 还原画布状态
        canvas.restoreToCount(canvasSaveCount);
        // 画布归位
        canvas.translate(0, 0);
        /*绘制高亮显示数字输入框*/
        // 获取待输入数字输入框的序号
        int boxIndex = Math.max(0, getEditableText().length());
        // 输入完所有数字后不再绘制高亮的输入框
        if (boxIndex < boxCount) {
            // 计算此输入框的绘制位置
            cx = (boxBorderHeight + boxWidth)/2 + (boxWidth + boxBorderHeight + boxSpacing) * boxIndex;
            // 高亮显示输入框的颜色
            boxPaint.setColor(boxBorderColorFocused);
            // 绘制高亮输入框
            canvas.drawCircle(cx,cy,radius, boxPaint);
        }
    }

    /**
     * 绘制只有底部横线的输入框
     * @param canvas
     */
    private void drawLineBox(Canvas canvas){
        // 计算线段坐标（需要考虑画笔的宽度）
        float startX = boxBorderHeight/2;
        float startY = centerY + boxHeight/2 - boxBorderHeight/2;
        float stopX = startX + boxWidth;
        float stopY = startY;
        /*创建输入框画笔*/
        Paint boxPaint = new Paint();
        //普通输入框颜色
        boxPaint.setColor(boxBorderColorNormal);
        // 设置画笔样式：描边
        boxPaint.setStyle(Paint.Style.STROKE);
        // 设置画笔宽度
        boxPaint.setStrokeWidth(boxBorderHeight);
        //当前画布保存的状态
        int canvasSaveCount = canvas.getSaveCount();
        //保存画布
        canvas.save();
        /*遍历绘制所有数字输入框*/
        for (int i = 0; i < boxCount; i++) {
            canvas.drawLine(startX,startY,stopX,stopY, boxPaint);
            //计算下一个数字输入框的位置
            int nextBoxLeft = boxWidth + boxSpacing + boxBorderHeight;
            //保存画布
            canvas.save();
            //画布平移到下一个绘制输入框的位置
            canvas.translate(nextBoxLeft, 0);
        }
        // 还原画布状态
        canvas.restoreToCount(canvasSaveCount);
        // 画布归位
        canvas.translate(0, 0);
        /*绘制高亮显示数字输入框*/
        // 获取待输入数字输入框的序号
        int boxIndex = Math.max(0, getEditableText().length());
        // 输入完所有数字后不再绘制高亮的输入框
        if (boxIndex < boxCount) {
            // 计算此输入框的绘制位置
            startX = boxBorderHeight/2 + (boxWidth + boxBorderHeight + boxSpacing) * boxIndex;
            stopX = startX + boxWidth;
            // 高亮显示输入框的颜色
            boxPaint.setColor(boxBorderColorFocused);
            // 绘制高亮输入框
            canvas.drawLine(startX,startY,stopX,stopY, boxPaint);
        }
    }

    /**
     * 绘制矩形输入框
     *
     * @param canvas
     */
    private void drawRectBox(Canvas canvas) {
        // 计算单个数字输入框的坐标:输入框垂直居中
        boxRect.left = boxBorderHeight/2;
        boxRect.top = centerY - boxHeight/2 + boxBorderHeight/2;
        boxRect.right = boxWidth + boxRect.left;
        boxRect.bottom = centerY + boxHeight/2 - boxBorderHeight/2;
         /*创建输入框画笔*/
        Paint boxPaint = new Paint();
        //普通输入框颜色
        boxPaint.setColor(boxBorderColorNormal);
        // 设置画笔样式：描边
        boxPaint.setStyle(Paint.Style.STROKE);
        // 设置画笔宽度
        boxPaint.setStrokeWidth(boxBorderHeight);
        //当前画布保存的状态
        int canvasSaveCount = canvas.getSaveCount();
        //保存画布
        canvas.save();
        /*遍历绘制所有数字输入框*/
        for (int i = 0; i < boxCount; i++) {
            canvas.drawRect(boxRect, boxPaint);
            //计算下一个数字输入框的位置
            int nextBoxLeft = boxWidth + boxSpacing + boxBorderHeight;
            //保存画布
            canvas.save();
            //画布平移到下一个绘制输入框的位置
            canvas.translate(nextBoxLeft, 0);
        }
        //还原画布状态
        canvas.restoreToCount(canvasSaveCount);
        //画布归位
        canvas.translate(0, 0);
        /*绘制高亮显示数字输入框*/
        //获取待输入数字输入框的序号
        int boxIndex = Math.max(0, getEditableText().length());
        //输入完所有数字后不再绘制高亮的输入框
        if (boxIndex < boxCount) {
            //计算此输入框的绘制位置
            boxRect.left =  + boxBorderHeight/2 + (boxWidth + boxSpacing + boxBorderHeight) * boxIndex;
            boxRect.right = boxRect.left + boxWidth;
            //高亮显示输入框的颜色
            boxPaint.setColor(boxBorderColorFocused);
            //绘制输入框
            canvas.drawRect(boxRect, boxPaint);
        }
    }

    /**
     * 绘制输入的数字
     *
     * @param canvas
     */
    private void drawNumber(Canvas canvas) {
        //获取当前画布保存的状态
        int saveCount = canvas.getSaveCount();
        //画布归位
        canvas.translate(0, 0);
        //获取需要绘制的数字的个数
        int numberCount = getEditableText().length();
        //文本画笔
        TextPaint textPaint = getPaint();
//		textPaint.setTextSize(txtSize);
        textPaint.setColor(getCurrentTextColor());
        Rect numberBoundsRect = new Rect();
        //遍历绘制数字
        for (int i = 0; i < numberCount; i++) {
            String numberText = String.valueOf(getEditableText().charAt(i));
            //获取文本大小(重用输入框的Rect)
            textPaint.getTextBounds(numberText, 0, 1, numberBoundsRect);
            //计算数字的绘制位置
            int x = (boxWidth + boxBorderHeight) / 2 - Math.abs(numberBoundsRect.centerX()) + (boxWidth + boxSpacing + boxBorderHeight) * i;
            int y = centerY + numberBoundsRect.height()/2;
            //绘制数字
            canvas.drawText(numberText, x, y, textPaint);
        }
        //画布还原
        canvas.restoreToCount(saveCount);
    }

    /**
     * 设置输入完成监听
     *
     * @param listener
     */
    public void setOnInputFinishedListener(OnInputFinishedListener listener) {
        this.onInputFinishedListener = listener;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        int length = text.length();
        if (length == boxCount) {
            // 隐藏软键盘
//			hideSoftKeyboard();
            if (onInputFinishedListener != null) {
                onInputFinishedListener.onFinish(getEditableText().toString());
            }
        }
    }

    /**
     * 隐藏键盘
     */
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    /**
     * 输入完成监听器
     */
    public interface OnInputFinishedListener {
        /**
         * 输入完成
         *
         * @param text 用户输入的文本
         */
        void onFinish(String text);
    }
}
