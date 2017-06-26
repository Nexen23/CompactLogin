package alex.loginanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import alex.loginanimation.anim.CircleAnimator;
import alex.loginanimation.misc.FabBackground;
import alex.loginanimation.misc.Point2d;
import alex.loginanimation.util.ViewUtil;
import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static alex.loginanimation.util.AnimUtil.sequentially;
import static alex.loginanimation.util.AnimUtil.together;

public class MainActivity extends AppCompatActivity implements View.OnLayoutChangeListener {
	@BindView(R.id.l_root)
	ViewGroup rootLayout;

	@BindView(R.id.b_register)
	FloatingActionButton registerButton;
	@BindView(R.id.b_close)
	ImageButton closeButton;

	@BindView(R.id.cv_back)
	CardView backScreen;
	@BindView(R.id.cv_login)
	CardView loginScreen;
	@BindView(R.id.cv_register)
	CardView registerScreen;

	@BindView(R.id.l_register)
	RelativeLayout registerContent;

	@BindDimen(R.dimen.card_elevation)
	@Dimension int frontScreenElevation;
	@BindDimen(R.dimen.fab_elevation)
	@Dimension int registerButtonElevation;

	@BindInt(R.integer.animation_duration_open_card)
	int duration;

	private Point2d pRevealCenter = new Point2d(), pUnrevealCenter = new Point2d(),
			pRegisterScreen = new Point2d(), pRegisterContent = new Point2d(),
			pRegisterButton = new Point2d(),
			pMoveRegisterScreenFrom = new Point2d();
	private float revealStartRadius, revealEndRadius;
	private float frontBackScreensYDiff;
	private FabBackground fabBackground;
	private AnimatorSet showRegisterScreenAnimatorSet, hideRegisterScreenAnimatorSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			registerScreen.setPreventCornerOverlap(false);
			loginScreen.setPreventCornerOverlap(false);
		}

		rootLayout.addOnLayoutChangeListener(this);
	}

	@Override
	public void onLayoutChange(View v,
	                           int left, int top, int right, int bottom,
	                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
		setupAnimatorsData();
		setupShowRegisterScreenAnimators();
		setupShowLoginScreenAnimators();

		rootLayout.removeOnLayoutChangeListener(this);
	}

	private void setupAnimatorsData() {
		fabBackground = new FabBackground(registerButton);

		frontBackScreensYDiff = loginScreen.getY() - backScreen.getY();

		pRegisterScreen.set(registerScreen);
		pRegisterContent.set(registerContent);
		pRegisterButton.set(registerButton);


		revealStartRadius = (int) Math.ceil(registerButton.getWidth() / 2f);
		revealEndRadius = (int) Math.ceil(Math.hypot(registerScreen.getWidth(), registerScreen.getHeight()));
		pRevealCenter.set(registerScreen.getWidth() * 0.8f, registerScreen.getHeight() * 0.4f);
		pUnrevealCenter.plusHalfSize(registerScreen);


		pMoveRegisterScreenFrom.set(pRegisterScreen)
				.plus(pRevealCenter)
				.minus(revealStartRadius);


		Point2d diff = new Point2d(pRegisterButton).minus(pMoveRegisterScreenFrom);
		pMoveRegisterScreenFrom.set(pRegisterScreen).plus(diff);
	}

	//region Show register screen
	void setupShowRegisterScreenAnimators() {
		showRegisterScreenAnimatorSet = together(
				getMoveRegisterScreenFromFabAnimator(),
				getMoveButtonFromRevealCenterAnimator(),
				getMorphPlusToCrossAnimator());
	}

	@OnClick(R.id.b_register)
	void onRegisterClick() {
		hideLoginScreen();
		showRegisterScreen();
	}

	private void hideLoginScreen() {
		int widthDifference = loginScreen.getWidth() - backScreen.getWidth();

		DecelerateInterpolator interpolator = new DecelerateInterpolator();
		long duration = (long) (this.duration * 0.5f);

		loginScreen.animate()
				.setDuration(duration)
				.setInterpolator(interpolator)
				.alpha(0.9f)
				.z(0)
				.y(pRegisterScreen.y - frontBackScreensYDiff)
				.scaleX(ViewUtil.GetScaleX(loginScreen, widthDifference));

		backScreen.animate()
				.setDuration(duration)
				.setInterpolator(interpolator)
				.y(pRegisterScreen.y - frontBackScreensYDiff * 2)
				.scaleX(ViewUtil.GetScaleX(backScreen, widthDifference));
	}

	private void showRegisterScreen() {
		setupRegisterScreenValues();
		showRegisterScreenAnimatorSet.start();
		getRevealRegisterScreenAnimator().start();
	}

	private void setupRegisterScreenValues() {
		registerScreen.setClipChildren(false);
		Point2d.of(loginScreen).place(registerScreen);
		registerScreen.setVisibility(View.VISIBLE);
		registerScreen.setZ(frontScreenElevation);
		registerContent.setAlpha(1f);

		pMoveRegisterScreenFrom.place(registerScreen);
		registerButton.setVisibility(View.INVISIBLE);
		closeButton.setVisibility(View.VISIBLE);
		closeButton.setClickable(false);
	}

	/**
	 * Reveal animator can't be restarted. Result must not be saved in instances
	 */
	private Animator getRevealRegisterScreenAnimator() {
		Animator revealAnimator = ViewAnimationUtils
				.createCircularReveal(registerScreen,
						((int) pRevealCenter.x), ((int) pRevealCenter.y),
						revealStartRadius, revealEndRadius)
				.setDuration(duration);

		revealAnimator.setInterpolator(new AccelerateInterpolator(1.9f));
		return revealAnimator;
	}

	private Animator getMoveRegisterScreenFromFabAnimator() {
		final View screen = registerScreen, content = registerContent, newButton = closeButton;
		final Point2d pFrom = pMoveRegisterScreenFrom, pTo = pRegisterScreen;

		ValueAnimator moveAnimator = new CircleAnimator(pFrom, pTo, 0)
				.onViewPosition(screen)
				.setDuration((long) (duration * 0.5f));

		ValueAnimator.AnimatorUpdateListener stabilizeContentListener = new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Point2d point = (Point2d) animation.getAnimatedValue();
				float xToTarget = pTo.x - point.x,
						yToTarget = pTo.y - point.y;
				content.setX(xToTarget);
				content.setY(yToTarget);
			}
		};

		moveAnimator.addUpdateListener(stabilizeContentListener);
		moveAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				newButton.setClickable(true);
			}
		});

		moveAnimator.setInterpolator(new AccelerateInterpolator());
		return moveAnimator;
	}

	private Animator getMoveButtonFromRevealCenterAnimator() {
		final Point2d
				pTo = Point2d.of(closeButton),
				pFrom = new Point2d(pRevealCenter).minusHalfSize(closeButton);

		ValueAnimator moveAnimator = new CircleAnimator(pFrom, pTo, -120)
				.onViewPosition(closeButton)
				.setRadiusInterpolator(new AccelerateInterpolator(2.2f))
				.setDuration(duration);
		moveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Point2d.of(closeButton)
						.plus(pRegisterContent).minus(registerContent)
						.place(closeButton);
			}
		});

		return moveAnimator;
	}

	private Animator getMorphPlusToCrossAnimator() {
		Animator morphAnimator = ObjectAnimator.ofFloat(closeButton, View.ROTATION, 0, 45);
		morphAnimator.setDuration((long) (duration * 0.9f))
				.setInterpolator(new AccelerateDecelerateInterpolator());
		return morphAnimator;
	}
	//endregion

	//region Show login screen
	void setupShowLoginScreenAnimators() {
		hideRegisterScreenAnimatorSet = together(
				getMorphCrossToPlusAnimator(),
				sequentially(
						together(
								getFadeRegisterContentAnimator(),
								getMoveButtonToCenterAnimator()),
						getMoveRegisterScreenToFabAnimator())
		);
	}

	@OnClick(R.id.b_close)
	void onCloseClick() {
		showLoginScreen();
		hideRegisterScreen();
	}

	private void hideRegisterScreen() {
		setupLoginScreenValues();
		hideRegisterScreenAnimatorSet.start();
		getUnrevealRegisterScreenAnimator().start();
	}

	@NonNull
	private Animator getMoveButtonToCenterAnimator() {
		final float unrevealEndRadius = revealStartRadius;

		final Point2d pFrom = Point2d.of(closeButton)
				.plusHalfSize(closeButton)
				.minusHalfSize(registerButton)
				.plus(registerScreen);

		final Point2d pTo = new Point2d(pUnrevealCenter)
				.minus(unrevealEndRadius)
				.plus(registerScreen);

		CircleAnimator moveCloseToCenterAnimator = new CircleAnimator(pFrom, pTo, 180)
				.onViewPosition(registerButton)
				.counterClockwise();
		moveCloseToCenterAnimator.setDuration((long) (duration * 0.4f));
		moveCloseToCenterAnimator.setInterpolator(new AccelerateInterpolator(0.4f));
		return moveCloseToCenterAnimator;
	}

	@NonNull
	private Animator getMorphCrossToPlusAnimator() {
		ObjectAnimator rotateCloseAnimator = ObjectAnimator.ofFloat(registerButton, View.ROTATION, 45, 0);
		rotateCloseAnimator.setDuration(duration);
		rotateCloseAnimator.setInterpolator(new DecelerateInterpolator());
		return rotateCloseAnimator;
	}

	@NonNull
	private CircleAnimator getMoveRegisterScreenToFabAnimator() {
		final float unrevealEndRadius = revealStartRadius;
		Point2d pStart = Point2d.of(registerScreen),
				pEnd = Point2d.of(registerButton).minus(pUnrevealCenter).plus(unrevealEndRadius);

		CircleAnimator moveToFabAnimator = new CircleAnimator(pStart, pEnd, 45)
				.onViewPosition(registerScreen)
				.counterClockwise()
				.setDuration((long) (duration * 0.6f));

		moveToFabAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Point2d point = (Point2d) animation.getAnimatedValue();
				point.plus(pUnrevealCenter).minus(unrevealEndRadius).place(registerButton);
			}
		});

		moveToFabAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				registerButton.setClickable(true);
			}
		});

		return moveToFabAnimator;
	}

	@NonNull
	private Animator getFadeRegisterContentAnimator() {
		ObjectAnimator fadeContentAnimator = ObjectAnimator.ofFloat(registerContent, View.ALPHA, 1f, 0f);
		fadeContentAnimator.setDuration((long) (duration * 0.4f));
		fadeContentAnimator.setInterpolator(new AccelerateInterpolator(0.2f));
		return fadeContentAnimator;
	}

	/**
	 * Reveal animator can't be restarted. Result must not be saved in instances
	 */
	private Animator getUnrevealRegisterScreenAnimator() {
		Animator unrevealAnimator = ViewAnimationUtils
				.createCircularReveal(registerScreen, ((int) pUnrevealCenter.x), ((int) pUnrevealCenter.y),
						revealEndRadius, revealStartRadius);
		unrevealAnimator
				.setDuration(duration)
				.setInterpolator(new DecelerateInterpolator(1.9f));

		unrevealAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				registerScreen.setVisibility(View.INVISIBLE);
				registerButton.animate().z(registerButtonElevation);
				fabBackground.restore();
			}
		});
		return unrevealAnimator;
	}

	void showLoginScreen() {
		long duration = (long) (this.duration * 0.25f);
		AccelerateInterpolator interpolator = new AccelerateInterpolator();

		backScreen.animate()
				.setDuration(duration)
				.setInterpolator(interpolator)
				.y(loginScreen.getY())
				.scaleX(1f);

		loginScreen.animate()
				.setDuration(duration)
				.setInterpolator(interpolator)
				.alpha(1f)
				.z(frontScreenElevation)
				.y(pRegisterScreen.y)
				.scaleX(1f);
	}

	private void setupLoginScreenValues() {
		registerScreen.setClipChildren(true); // HACK: clip children & circular reveal clip confront each other. See: https://stackoverflow.com/questions/44748155/circular-reveal-shows-black-background
		registerButton.setClickable(false);
		registerButton.setVisibility(View.VISIBLE);
		closeButton.setVisibility(View.INVISIBLE);
		fabBackground.clear();
	}
	//endregion
}
