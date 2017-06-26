package alex.loginanimation.util;


import android.animation.Animator;
import android.animation.AnimatorSet;

public class AnimUtil {
	public static AnimatorSet together(Animator... animators) {
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(animators);
		return animatorSet;
	}

	public static AnimatorSet sequentially(Animator... animators) {
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playSequentially(animators);
		return animatorSet;
	}
}
