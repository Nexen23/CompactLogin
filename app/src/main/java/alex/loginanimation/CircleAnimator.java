package alex.loginanimation;

import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.view.animation.DecelerateInterpolator;


public class CircleAnimator extends ValueAnimator {
	final PointF pCurrent = new PointF(), pStart, pTarget;
	final float maxRadius;
	final float minAngle, maxAngle;

	public CircleAnimator(final PointF pStart, final PointF pTarget, float fromAngle) {
		this.pStart = new PointF(pStart.x, pStart.y);
		this.pTarget = new PointF(pTarget.x, pTarget.y);
		maxRadius = (float) Math.hypot(pStart.x - pTarget.x, pStart.y - pTarget.y);
		minAngle = fromAngle;
		maxAngle = (float) (Math.toDegrees(Math.atan((pTarget.y - pStart.y) / (pTarget.x - pStart.x)))) + 180f;
				//(float) Math.toDegrees(Math.acos((pTarget.x - pStart.x) / maxRadius)) + 180;  //225;
				//double t =
				//(float) (Math.toDegrees(Math.atan2(pStart.x - pTarget.x, pTarget.y - pStart.y))) % 360f;

		setFloatValues(0f, 1f);
		setInterpolator(new DecelerateInterpolator());
	}

	@Override
	public PointF getAnimatedValue() {
		float value = getAnimatedFraction();
		float radius = value * maxRadius;
		float radianAngle = (float) Math.toRadians(value * (maxAngle - minAngle) + minAngle);
		pCurrent.set(
				pStart.x + radius * ((float) Math.cos(radianAngle)),
				pStart.y + radius * ((float) Math.sin(radianAngle)));
		return new PointF(pCurrent.x, pCurrent.y);
	}
}
