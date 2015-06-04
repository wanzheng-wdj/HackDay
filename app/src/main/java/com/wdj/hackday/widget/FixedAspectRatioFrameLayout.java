package com.wdj.hackday.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author wanzheng@wandoujia.com (Zheng Wan)
 */
public class FixedAspectRatioFrameLayout extends FrameLayout
{
  private int mAspectRatioWidth;
  private int mAspectRatioHeight;

  public FixedAspectRatioFrameLayout(Context context)
  {
    super(context);
  }

  public FixedAspectRatioFrameLayout(Context context, AttributeSet attrs)
  {
    super(context, attrs);

    Init(context, attrs);
  }

  public FixedAspectRatioFrameLayout(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);

    Init(context, attrs);
  }

  private void Init(Context context, AttributeSet attrs)
  {
//    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedAspectRatioFrameLayout);
//
//    mAspectRatioWidth = a.getInt(R.styleable.FixedAspectRatioFrameLayout_aspectRatioWidth, 4);
//    mAspectRatioHeight = a.getInt(R.styleable.FixedAspectRatioFrameLayout_aspectRatioHeight, 3);
//
//    a.recycle();
    mAspectRatioWidth = 3;
    mAspectRatioHeight = 3;
  }
  // **overrides**

  @Override protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
  {
    int originalWidth = MeasureSpec.getSize(widthMeasureSpec);

    int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

    int calculatedHeight = originalWidth * mAspectRatioHeight / mAspectRatioWidth;

    int finalWidth, finalHeight;

    if (calculatedHeight > originalHeight)
    {
      finalWidth = originalHeight * mAspectRatioWidth / mAspectRatioHeight;
      finalHeight = originalHeight;
    }
    else
    {
      finalWidth = originalWidth;
      finalHeight = calculatedHeight;
    }

    super.onMeasure(
        MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
  }
}
