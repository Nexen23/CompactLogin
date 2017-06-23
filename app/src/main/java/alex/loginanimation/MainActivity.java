package alex.loginanimation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Property;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.ROTATION;
import static android.view.View.X;
import static android.view.View.Y;

public class MainActivity extends AppCompatActivity {
	@BindView(R.id.b_register)
	FloatingActionButton registerButton;
	@BindView(R.id.b_login)
	Button loginButton;
	@BindView(R.id.b_close)
	ImageButton closeButton;
	@BindView(R.id.et_username)
	EditText usernameEditText;
	@BindView(R.id.et_password)
	EditText passwordEditText;
	@BindView(R.id.cv_back)
	CardView backCardView;
	@BindView(R.id.cv_login)
	CardView loginCardView;
	@BindView(R.id.tv_title)
	TextView titleTextView;

	@BindView(R.id.cv_register)
	CardView registerCardView;

	@BindDimen(R.dimen.z_force_above)
	@Dimension int zForceAbove;
	final long duration = 5000,
			moveFabDuration = (long) (duration / 1.4f);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
	}

	@OnClick(R.id.b_register)
	void onRegisterClick() {

		//ValueAnimator animator = new

		moveLoginViewToBack();
		showRegisterView();
	}

	private void showRegisterView() {
		final View animatingView = registerCardView;

		int startRadius = (int) (registerButton.getWidth() / 2f);
		int endRadius = Math.max(animatingView.getWidth(), animatingView.getHeight());
		final PointF pCenter = new PointF(animatingView.getWidth() * 0.8f, animatingView.getHeight() * 0.4f);


		animatingView.setVisibility(View.VISIBLE);
		animatingView.setZ(zForceAbove);

		float x = animatingView.getX() + pCenter.x - startRadius,
				y = animatingView.getY() + pCenter.y - startRadius;
		float bx = registerButton.getX(),
				by = registerButton.getY();

		float dx = bx - x, dy = by - y;

		//registerButton.setVisibility(View.INVISIBLE);
		PointF pTarget = getXY(animatingView),
				pFab = getXY(animatingView);
		pFab.offset(dx, dy);
		setXY(animatingView, pFab);

		Animator xyAnimator = getCurveXYAnimator(animatingView, getXY(animatingView), pTarget, false);
		xyAnimator
				.setDuration(moveFabDuration)
				.start();



		Animator animator = ViewAnimationUtils
				.createCircularReveal(animatingView, ((int) pCenter.x), ((int) pCenter.y), startRadius, endRadius);
		animator
				.setDuration(duration)
				.setInterpolator(new AccelerateInterpolator(2f));
		animator.start();


		moveCloseButton(pCenter);
	}

	void moveCloseButton(final PointF pStartCenter) {
		final PointF
				pTarget = getXY(closeButton),
				pStart = centerToLeftTop(closeButton, pStartCenter);

		final PointF
				pStartBack = new PointF(pStart.x + 30, pStart.y - 60),
				pStartLeft = new PointF(pStart.x - 100, pStart.y);


		closeButton.setRotation(45);
		Animator rotateIconAnimator = ObjectAnimator.ofFloat(closeButton, ROTATION, 0, 45);
		Animator moveBackAnimator = getCurveXYAnimator(closeButton, pStart, pStartBack, false);
		Animator moveToLeftAnimator = getCurveXYAnimator(closeButton, pStartBack, pStartLeft, false);
		Animator moveToTargetAnimator = getCurveXYAnimator(closeButton, pStartLeft, pTarget, false);

		rotateIconAnimator.setDuration(moveFabDuration);
		moveBackAnimator.setDuration(moveFabDuration / 4);
		moveToLeftAnimator.setDuration((3 * moveFabDuration) / 4);
		moveToTargetAnimator.setDuration(duration - moveFabDuration);

		AnimatorSet fullAnimatorSet = new AnimatorSet();
		fullAnimatorSet.playSequentially(
				moveBackAnimator,
				moveToLeftAnimator,
				moveToTargetAnimator
		);

		AnimatorSet resultingAnimator = new AnimatorSet();
		resultingAnimator.playTogether(fullAnimatorSet, rotateIconAnimator);

		resultingAnimator.start();


		/*closeButton.animate()
				.setDuration(moveFabDuration)
				.setInterpolator(new DecelerateInterpolator())
				.setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					Interpolator interpolator = new OvershootInterpolator();
					Float prevDv;

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						float pureFraction = ((float) animation.getCurrentPlayTime()) / animation.getDuration();
						float value = interpolator.getInterpolation(pureFraction);
						Log.e("TEST", String.valueOf(value));

						float x = closeButton.getX(),
								y = closeButton.getY(),
								dv = value * 50;
						if (prevDv != null) {
							x -= prevDv;
							y += prevDv;
						}
						closeButton.setX(x + dv);
						closeButton.setY(y - dv);
						prevDv = dv;
					}
				})
				.rotationBy(45)
				.withEndAction(new Runnable() {
					@Override
					public void run() {
						//closeButton.animate().cancel();

						closeButton.animate()
								.setDuration(duration - moveFabDuration)
								.x(pTarget.x)
								.y(pTarget.y);
					}
				});*/
	}

	void setXY(View view, PointF p) {
		view.setX(p.x);
		view.setY(p.y);
	}

	PointF getXY(View view) {
		return new PointF(view.getX(), view.getY());
	}

	PointF centerToLeftTop(View view, PointF p) {
		PointF pLeftTop = new PointF(p.x, p.y);
		pLeftTop.offset(-view.getWidth() / 2, -view.getHeight() / 2);
		setXY(view, pLeftTop);
		return pLeftTop;
	}

	void moveLoginViewToBack() {
		int widthDifference = loginCardView.getWidth() - backCardView.getWidth();

		loginCardView.animate()
				.setDuration(duration / 3)
				.setInterpolator(new DecelerateInterpolator())
				.z(0)
				.y(backCardView.getTop())
				.scaleX(getNeededScaleX(loginCardView, widthDifference));

		backCardView.animate()
				.setDuration(duration / 3)
				.setInterpolator(new DecelerateInterpolator())
				.yBy(backCardView.getTop() - loginCardView.getTop())
				.scaleX(getNeededScaleX(backCardView, widthDifference));
	}

	float getNeededScaleX(View view, int deduct) {
		return (float) (view.getWidth() - deduct) / view.getWidth();
	}

	private Animator getTranslationAnimator(View view, Interpolator interpolator,
	                                        Property<View, Float> property, float from, float to) {
		Animator animator = ObjectAnimator.ofFloat(view, property, from, to);
		animator.setInterpolator(interpolator);
		return animator;
	}

	private Animator getCurveXYAnimator(View view, final PointF pFrom, final PointF pTo, boolean isCurveX) {
		AnimatorSet animatorSet = new AnimatorSet();

		Interpolator
				xInterpolator = new AccelerateInterpolator(1.5f),
				yInterpolator = new DecelerateInterpolator(0.7f);
		if (isCurveX) {
			Interpolator temp;
			temp = xInterpolator;
			//noinspection SuspiciousNameCombination
			xInterpolator = yInterpolator;
			yInterpolator = temp;
		}

		Animator xAnimator = getTranslationAnimator(view, xInterpolator, X, pFrom.x, pTo.x);
		Animator yAnimator = getTranslationAnimator(view, yInterpolator, Y, pFrom.y, pTo.y);
		animatorSet.playTogether(xAnimator, yAnimator);
		return animatorSet;
	}
}
