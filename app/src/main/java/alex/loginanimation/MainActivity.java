package alex.loginanimation;

import android.animation.Animator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
	@BindView(R.id.b_register)
	FloatingActionButton registerButton;
	@BindView(R.id.b_login)
	Button loginButton;
	@BindView(R.id.et_username)
	EditText usernameEditText;
	@BindView(R.id.et_password)
	EditText passwordEditText;
	@BindView(R.id.cv_login)
	CardView loginCardView;

	@BindView(R.id.cv_register)
	CardView registerCardView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		//loginCardView.setClipToOutline(false); // controls anyway have not onClick received
	}

	@OnClick(R.id.b_register)
	void onRegisterClick() {
		//final View animatingView = loginCardView;
		final View animatingView = registerCardView;

		int startRadius = registerButton.getWidth();
		int endRadius = Math.max(animatingView.getWidth(), animatingView.getHeight());
		int x = animatingView.getWidth() / 2;//(int) (loginCardView.getWidth() * 0.9f);
		int y = animatingView.getHeight() / 2;
		final int duration = 3000;

		/*Animator animator = ViewAnimationUtils.createCircularReveal(animatingView, x, y, startRadius, endRadius);
		animator
				.setDuration(duration)
				.setInterpolator(new AccelerateInterpolator());
		animator
				.start();*/

		loginCardView.setElevation(0);
		animatingView.setVisibility(View.VISIBLE);
		Animator animator = ViewAnimationUtils.createCircularReveal(animatingView, x, y, startRadius, endRadius);
		animator
				.setDuration(duration)
				.setInterpolator(new AccelerateInterpolator());
		animator
				.start();
	}

	void hideStatusBar() {
		// HACK: plays hiding animation. Better use theme
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
	}
}
