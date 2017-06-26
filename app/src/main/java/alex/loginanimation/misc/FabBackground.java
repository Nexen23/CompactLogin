package alex.loginanimation.misc;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.ViewOutlineProvider;

public class FabBackground {
	private final FloatingActionButton fab;
	private ViewOutlineProvider savedOutlineProvider;
	private ColorStateList savedBackgroundTintList;
	private boolean isCleared = false;

	public FabBackground(FloatingActionButton fab) {
		this.fab = fab;
	}

	public void clear() {
		if (isCleared) throw new IllegalStateException("already cleared");
		isCleared = true;
		savedOutlineProvider = fab.getOutlineProvider();
		fab.setOutlineProvider(null);

		savedBackgroundTintList = fab.getBackgroundTintList();
		fab.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
	}

	public void restore() {
		if (!isCleared) throw new IllegalStateException("not cleared");
		isCleared = false;
		fab.setBackgroundTintList(savedBackgroundTintList);
		fab.setOutlineProvider(savedOutlineProvider);
	}
}
