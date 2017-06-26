package alex.loginanimation.misc;


import android.graphics.PointF;
import android.view.View;

public class Point2d extends PointF {
	public Point2d() {
	}

	public Point2d(float x, float y) {
		super(x, y);
	}

	public Point2d(Point2d p) {
		super(p.x, p.y);
	}

	public Point2d(View view) {
		super(view.getX(), view.getY());
	}

	public Point2d place(View view) {
		view.setX(x);
		view.setY(y);
		return this;
	}

	public Point2d set(View view) {
		set(view.getX(), view.getY());
		return this;
	}

	public Point2d set(Point2d point) {
		set(point.x, point.y);
		return this;
	}

	public Point2d plus(float value) {
		offset(value, value);
		return this;
	}

	public Point2d plus(Point2d point) {
		offset(point.x, point.y);
		return this;
	}

	public Point2d plus(View view) {
		offset(view.getX(), view.getY());
		return this;
	}

	public Point2d plusHalfSize(View view) {
		offset(view.getWidth() / 2f, view.getHeight() / 2f);
		return this;
	}

	public Point2d minus(float value) {
		offset(-value, -value);
		return this;
	}

	public Point2d minus(Point2d point) {
		offset(-point.x, -point.y);
		return this;
	}

	public Point2d minus(View view) {
		offset(-view.getX(), -view.getY());
		return this;
	}

	public Point2d minusHalfSize(View view) {
		offset(-view.getWidth() / 2f, -view.getHeight() / 2f);
		return this;
	}

	static public Point2d of(View view) {
		return new Point2d(view);
	}
}
