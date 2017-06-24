package alex.loginanimation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;


public class DrawingView extends CardView {
	Path path = new Path();
	ValueAnimator animator;
	Paint paint = new Paint();

	PointF pTarget;
	Paint targetPaint = new Paint();

	public DrawingView(Context context) {
		super(context);
	}

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
		if (pTarget != null) {
			canvas.drawCircle(pTarget.x, pTarget.y, 10f, targetPaint);
		}
		canvas.drawPath(path, paint);
	}

	public void animateCircle(long duration) {
		targetPaint.setColor(Color.RED);
		targetPaint.setAntiAlias(true);
		targetPaint.setStyle(Paint.Style.FILL);

		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4f);

		final PointF pStart = new PointF(getWidth() / 2f, getHeight() / 2f);
		pTarget = new PointF(pStart.x - 120, pStart.y - 400);
		if (animator != null) animator.cancel();
		path.reset();
		path.moveTo(pStart.x, pStart.y);

		animator = new CircleAnimator(pStart, pTarget, -45)
				.setDuration(duration);

		/*animator = ValueAnimator.ofFloat(0f, 1f).setDuration(duration);
		animator.setInterpolator(new DecelerateInterpolator());*/
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			/*PointF pCurrent = new PointF();
			float maxRadius = (float) Math.hypot(pStart.x - pTarget.x, pStart.y - pTarget.y);
			float minAngle = -45,
					maxAngle = //(float) Math.toDegrees(Math.acos((pTarget.x - pStart.x) / maxRadius)) + 180;  //225;
			//double t =
							//(float) (Math.toDegrees(Math.atan2(pStart.x - pTarget.x, pTarget.y - pStart.y))) % 360f;
							(float) (Math.toDegrees(Math.atan((pTarget.y - pStart.y) / (pTarget.x - pStart.x)))) + 180f;*/

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				/*float value = (float) animation.getAnimatedValue();
				float radius = value * maxRadius;
				float radianAngle = (float) Math.toRadians(value * (maxAngle - minAngle) + minAngle);
				pCurrent.set(
						pStart.x + radius * ((float) Math.cos(radianAngle)),
						pStart.y + radius * ((float) Math.sin(radianAngle)));*/
				PointF pCurrent = (PointF) animation.getAnimatedValue();
				path.lineTo(pCurrent.x, pCurrent.y);
				invalidate();
			}
		});
		animator.start();


	}
}
