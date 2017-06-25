package alex.loginanimation;

import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


public class CircleAnimator extends ValueAnimator {
	View targetView;
	final PointF pCurrent = new PointF(), pStart, pTarget;
	final float maxRadius;
	float minAngle, maxAngle;
	Interpolator radiusInterpolator = new LinearInterpolator();
	private float currentAnimatedFraction = -1;
	private boolean isCounterClockwise = false;

	public CircleAnimator(final PointF pStart, final PointF pTarget, float fromAngle) {
		this.pStart = new PointF(pStart.x, pStart.y);
		this.pTarget = new PointF(pTarget.x, pTarget.y);
		maxRadius = (float) Math.hypot(pStart.x - pTarget.x, pStart.y - pTarget.y);
		minAngle = fromAngle;
		maxAngle = (float) Math.toDegrees(Math.atan2(pTarget.y - pStart.y, pTarget.x - pStart.x));

		maxAngle = (maxAngle + 360f) % 360f;
		if (maxAngle < minAngle) maxAngle += 360f;

		setFloatValues(0f, 1f);
		setInterpolator(new DecelerateInterpolator());

		addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				if (targetView != null) {
					updateCurrentPoint();
					targetView.setX(pCurrent.x);
					targetView.setY(pCurrent.y);
				}
			}
		});
	}

	@Override
	public PointF getAnimatedValue() {
		updateCurrentPoint();
		return new PointF(pCurrent.x, pCurrent.y);
	}

	@Override
	public CircleAnimator setDuration(long duration) {
		return (CircleAnimator) super.setDuration(duration);
	}

	public CircleAnimator onViewPosition(final View targetView) {
		this.targetView = targetView;
		return this;
	}

	public CircleAnimator setRadiusInterpolator(Interpolator interpolator) {
		radiusInterpolator = interpolator;
		return this;
	}

	public CircleAnimator counterClockwise() {
		if (isCounterClockwise) throw new IllegalStateException("already counterclockwise");
		isCounterClockwise = true;
		maxAngle -= 360f;
		return this;
	}

	void updateCurrentPoint() {
		if (currentAnimatedFraction == getAnimatedFraction()) return;

		float value = currentAnimatedFraction = getAnimatedFraction();
		float radius = maxRadius * radiusInterpolator.getInterpolation(value);
		float angleDistance = maxAngle - minAngle;
		float radianAngle = (float) Math.toRadians(value * angleDistance + minAngle);

		pCurrent.set(
				pStart.x + radius * ((float) Math.cos(radianAngle)),
				pStart.y + radius * ((float) Math.sin(radianAngle)));
	}
}
