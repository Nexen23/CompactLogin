package alex.loginanimation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import alex.loginanimation.R;


public class SizedCardView extends CardView {
	private final int maxWidth, maxHeight;

	public SizedCardView(Context context) {
		this(context, null);
	}

	public SizedCardView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SizedCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SizedCardView);
		maxWidth = a.getDimensionPixelSize(R.styleable.SizedCardView_maxWidth, 0);
		maxHeight = a.getDimensionPixelSize(R.styleable.SizedCardView_maxHeight, 0);
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Adjust width as necessary
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		if(maxWidth > 0 && maxWidth < measuredWidth) {
			int measureMode = MeasureSpec.getMode(widthMeasureSpec);
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, measureMode);
		}
		// Adjust height as necessary
		int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
		if(maxHeight > 0 && maxHeight < measuredHeight) {
			int measureMode = MeasureSpec.getMode(heightMeasureSpec);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, measureMode);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
