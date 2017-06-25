package alex.loginanimation;

import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


public class CircleAnimator extends ValueAnimator {
	final float PI2 = (float) (Math.PI * 2f);
	final PointF pCurrent = new PointF(), pStart = new PointF(), pTarget = new PointF();
	final float maxRadius;
	float fromRadianAngle, toRadianAngle, radianAnglesDiff;
	boolean isCounterClockwise = false;
	View targetView;

	Interpolator radiusInterpolator;
	float currentAnimatedFraction = -1;

	public CircleAnimator(final PointF pStart, final PointF pTarget, float fromAngle) {
		this.pStart.set(pStart);
		this.pTarget.set(pTarget);
		maxRadius = (float) Math.hypot(pStart.x - pTarget.x, pStart.y - pTarget.y);
		float minRadianAngle = (float) Math.toRadians(fromAngle);
		float maxRadianAngle = (float) (Math.atan2(pTarget.y - pStart.y, pTarget.x - pStart.x));
		maxRadianAngle = (maxRadianAngle + PI2) % PI2;
		if (maxRadianAngle < minRadianAngle) maxRadianAngle += PI2;
		setMinMaxRadianAngles(minRadianAngle, maxRadianAngle);

		setFloatValues(0f, 1f);
		setInterpolator(new DecelerateInterpolator());
		setRadiusInterpolator(new LinearInterpolator());
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

		return this;
	}

	public CircleAnimator setRadiusInterpolator(Interpolator interpolator) {
		radiusInterpolator = interpolator;
		return this;
	}

	public CircleAnimator counterClockwise() {
		if (isCounterClockwise) throw new IllegalStateException("already counterclockwise");
		isCounterClockwise = true;
		setMinMaxRadianAngles(fromRadianAngle, toRadianAngle - PI2);
		return this;
	}

	void updateCurrentPoint() {
		if (currentAnimatedFraction == getAnimatedFraction()) return;

		float value = currentAnimatedFraction = getAnimatedFraction();
		float radius = maxRadius * radiusInterpolator.getInterpolation(value);
		float radianAngle = value * radianAnglesDiff + fromRadianAngle;

		pCurrent.set(
				pStart.x + radius * ((float) Math.cos(radianAngle)),
				pStart.y + radius * ((float) Math.sin(radianAngle)));
	}

	private void setMinMaxRadianAngles(float minRadianAngle, float maxRadianAngle) {
		this.fromRadianAngle = minRadianAngle;
		this.toRadianAngle = maxRadianAngle;
		radianAnglesDiff = maxRadianAngle - minRadianAngle;
	}
}
