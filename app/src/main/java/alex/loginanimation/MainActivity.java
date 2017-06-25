package alex.loginanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
	@BindView(R.id.l_register)
	RelativeLayout registerLayout;

	@BindDimen(R.dimen.card_elevation)
	@Dimension int topCardElevation;
	@BindDimen(R.dimen.fab_elevation)
	@Dimension int fabElevation;

	long duration = 800;
	final float alphaBack = 0.9f;
	boolean disableRegisterAnim = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		registerCardView.setPreventCornerOverlap(false);
		loginCardView.setPreventCornerOverlap(false);
	}

	@OnClick(R.id.b_close)
	void onCloseRegistrationClick() {
		moveLoginViewToFront();


		final ViewGroup animatingView = registerCardView;

		final int endRadius = (int) (registerButton.getWidth() / 2f);
		int startRadius = (int) Math.ceil(Math.hypot(animatingView.getWidth(), animatingView.getHeight()));
		final PointF pRevealCenter = new PointF(animatingView.getWidth() / 2f, animatingView.getHeight() / 2f);


		Animator unrevealAnimator = ViewAnimationUtils
				.createCircularReveal(animatingView, ((int) pRevealCenter.x), ((int) pRevealCenter.y), startRadius, endRadius);
		unrevealAnimator
				.setDuration(duration)
				.setInterpolator(new DecelerateInterpolator(1.9f));
		unrevealAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				animatingView.setVisibility(View.INVISIBLE);
			}
		});



		ObjectAnimator fadeContentAnimator = ObjectAnimator.ofFloat(registerLayout, View.ALPHA, 1f, 0f);
		fadeContentAnimator.setDuration((long) (duration * 0.4f));
		fadeContentAnimator.setInterpolator(new AccelerateInterpolator(0.2f));


		PointF pEnd = getXY(registerButton), pStart = getXY(registerCardView);
		pEnd.offset(-pRevealCenter.x + endRadius, -pRevealCenter.y + endRadius);

		CircleAnimator moveToFabAnimator = new CircleAnimator(pStart, pEnd, 45)
				.onViewPosition(registerCardView)
				.counterClockwise()
				.setDuration((long) (duration * 0.6f));







		final PointF pFrom = getXY(closeButton), pTo = new PointF();
		pFrom.offset(closeButton.getWidth() / 2, closeButton.getHeight() / 2); // center
		pFrom.offset(-registerButton.getWidth() / 2, -registerButton.getHeight() / 2);
		pFrom.offset(registerCardView.getX(), registerCardView.getY());
		pTo.offset(
				pRevealCenter.x - endRadius + registerCardView.getX(),
				pRevealCenter.y - endRadius + registerCardView.getY());
		registerButton.setVisibility(View.VISIBLE);
		closeButton.setVisibility(View.INVISIBLE);




		final ViewOutlineProvider savedOutlineProvider = registerButton.getOutlineProvider();
		final ColorStateList savedBackgroundTintList = registerButton.getBackgroundTintList();
		registerButton.setOutlineProvider(null);
		registerButton.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));



		CircleAnimator moveCloseToCenterAnimator = new CircleAnimator(pFrom, pTo, 180)
				.onViewPosition(registerButton)
				.counterClockwise();
		moveCloseToCenterAnimator.setDuration((long) (duration * 0.4f));
		moveCloseToCenterAnimator.setInterpolator(new AccelerateInterpolator(0.4f));

		ObjectAnimator rotateCloseAnimator = ObjectAnimator.ofFloat(registerButton, View.ROTATION, 45, 0);
		rotateCloseAnimator.setDuration(duration);
		rotateCloseAnimator.setInterpolator(new DecelerateInterpolator());



		moveToFabAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				PointF point = (PointF) animation.getAnimatedValue();
				point.offset(pRevealCenter.x - endRadius, pRevealCenter.y - endRadius);
				setXY(registerButton, point);
			}
		});




		AnimatorSet fadeMoveAS = sequentially(
				together(fadeContentAnimator, moveCloseToCenterAnimator),
				moveToFabAnimator);

		AnimatorSet unrevealAnimatorSet = together(unrevealAnimator, rotateCloseAnimator, fadeMoveAS);
		unrevealAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				registerButton.setBackgroundTintList(savedBackgroundTintList);
				registerButton.setOutlineProvider(savedOutlineProvider);
				registerButton.animate().z(fabElevation);
			}
		});
		unrevealAnimatorSet.start();
	}

	@OnClick(R.id.b_register)
	void onRegisterClick() {
		long prevDuration = duration;
		if (disableRegisterAnim) duration = 0;
		registerLayout.setAlpha(1f);
		moveLoginViewToBack();
		showRegisterView();

		duration = prevDuration;
	}

	private void showRegisterView() {
		setXY(registerCardView, getXY(loginCardView));
		closeButton.setVisibility(View.VISIBLE);

		final ViewGroup animatingView = registerCardView;

		int startRadius = (int) (registerButton.getWidth() / 2f);
		int endRadius = (int) Math.ceil(Math.hypot(animatingView.getWidth(), animatingView.getHeight()));
		final PointF pRevealCenter = new PointF(animatingView.getWidth() * 0.8f, animatingView.getHeight() * 0.4f);

		animatingView.setVisibility(View.VISIBLE);
		animatingView.setZ(topCardElevation);

		float x = animatingView.getX() + pRevealCenter.x - startRadius,
				y = animatingView.getY() + pRevealCenter.y - startRadius;
		float bx = registerButton.getX(),
				by = registerButton.getY();

		final float dx = bx - x, dy = by - y;

		registerButton.setVisibility(View.INVISIBLE);

		final PointF pTarget = getXY(animatingView),
				pFab = getXY(animatingView);
		pFab.offset(dx, dy);
		setXY(animatingView, pFab);


		ValueAnimator xyAnimator = new CircleAnimator(pFab, pTarget, 90)
				.onViewPosition(animatingView)
				.setDuration((long) (duration * 0.5f));
		
		xyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				PointF point = (PointF) animation.getAnimatedValue();
				float xToTarget = pTarget.x - point.x,
						yToTarget = pTarget.y - point.y;
				registerLayout.setX(xToTarget);
				registerLayout.setY(yToTarget);
			}
		});

		final PointF pLayoutIdle = getXY(registerLayout);

		xyAnimator.setInterpolator(new AccelerateInterpolator());
		xyAnimator.start();






		Animator animator = ViewAnimationUtils
				.createCircularReveal(animatingView, ((int) pRevealCenter.x), ((int) pRevealCenter.y), startRadius, endRadius);
		animator
				.setDuration(duration)
				.setInterpolator(new AccelerateInterpolator(1.9f));
		animator.start();


		moveCloseButton(pRevealCenter, pLayoutIdle);
	}

	void moveCloseButton(final PointF pRevealCenter, final PointF pLayoutIdle) {
		final PointF
				pTarget = getXY(closeButton),
				pStart = centerToLeftTop(closeButton, pRevealCenter);

		Animator rotateIconAnimator = ObjectAnimator.ofFloat(closeButton, View.ROTATION, 0, 45);
		rotateIconAnimator
				.setDuration((long) (duration * 0.9f))
				.setInterpolator(new AccelerateDecelerateInterpolator());

		ValueAnimator circleAnimator = new CircleAnimator(pStart, pTarget, -120)
				.onViewPosition(closeButton)
				.setRadiusInterpolator(new AccelerateInterpolator(2.2f))
				.setDuration(duration);
		circleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				PointF xy = getXY(closeButton);
				xy.offset(
						pLayoutIdle.x - registerLayout.getX(),
						pLayoutIdle.y - registerLayout.getY());
				setXY(closeButton, xy);
			}
		});

		AnimatorSet resultingAnimator = new AnimatorSet();
		resultingAnimator.playTogether(circleAnimator, rotateIconAnimator);

		resultingAnimator.start();
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

		long duration = (long) (this.duration * 0.5f);
		loginCardView.animate()
				.setDuration(duration)
				.setInterpolator(new DecelerateInterpolator())
				.alpha(alphaBack)
				.z(0)
				.y(backCardView.getTop())
				.scaleX(getNeededScaleX(loginCardView, widthDifference));

		backCardView.animate()
				.setDuration(duration)
				.setInterpolator(new DecelerateInterpolator())
				.yBy(backCardView.getTop() - loginCardView.getTop())
				.scaleX(getNeededScaleX(backCardView, widthDifference));
	}

	void moveLoginViewToFront() {
		int widthDifference = registerCardView.getWidth() - loginCardView.getWidth();

		long duration = (long) (this.duration * 0.25f);
		backCardView.animate()
				.setDuration(duration)
				.setInterpolator(new AccelerateInterpolator())
				.y(loginCardView.getY())
				.scaleX(getNeededScaleX(backCardView, widthDifference));

		loginCardView.animate()
				.setDuration(duration)
				.setInterpolator(new AccelerateInterpolator())
				.alpha(1f)
				.z(topCardElevation)
				.y(registerCardView.getY())
				.scaleX(getNeededScaleX(loginCardView, widthDifference));
	}

	float getNeededScaleX(View view, int deduct) {
		return (float) (view.getWidth() - deduct) / view.getWidth();
	}

	/*private Animator getTranslationAnimator(View view, Interpolator interpolator,
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

		Animator xAnimator = getTranslationAnimator(view, xInterpolator, View.X, pFrom.x, pTo.x);
		Animator yAnimator = getTranslationAnimator(view, yInterpolator, View.Y, pFrom.y, pTo.y);
		animatorSet.playTogether(xAnimator, yAnimator);
		return animatorSet;
	}*/

	AnimatorSet together(Animator... animators) {
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(animators);
		return animatorSet;
	}

	AnimatorSet sequentially(Animator... animators) {
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playSequentially(animators);
		return animatorSet;
	}
}
