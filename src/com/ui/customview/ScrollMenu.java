package com.ui.customview;

import com.example.scrollviewtest.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ScrollMenu extends ViewGroup {

	private int menu_space = 64;
	private ViewGroup menuContainer;
	private LinearLayout contentContainer = null;
	
	private ScrollController scrollController = null;
	private GestureDetector gestureDetector = null;
	
	private boolean isScrollEnabled = true;
	private ScrollMenuListener listener = null;

	public ScrollMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.init(context, attrs);
	}

	public ScrollMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs)
	{
		setClipChildren(false);
	    setClipToPadding(false);
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollMenuAttrs);
		this.menu_space = a.getDimensionPixelSize(R.styleable.ScrollMenuAttrs_menu_spacing, menu_space);
		final int menuLayout = a.getResourceId(R.styleable.ScrollMenuAttrs_menu_layout, 0);
	    final int contentLayout = a.getResourceId(R.styleable.ScrollMenuAttrs_content_layout, 0);
	    a.recycle();
	    
	    if(menuLayout == 0 || contentLayout == 0)
	    	return;
	    
	    LayoutInflater inflater = LayoutInflater.from(context);
	    this.menuContainer = new FrameLayout(context);
	    inflater.inflate(menuLayout, this.menuContainer,true);
	    
	    addView(this.menuContainer, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    
	    this.contentContainer = new LinearLayout(context){
	        @Override
	        public boolean onTouchEvent(MotionEvent event) {
	          // prevent ray cast of touch events to actions container
	        	Rect mContentHitRect = new Rect();
	        	getHitRect(mContentHitRect);
	        	mContentHitRect.offset(-this.getScrollX(), this.getScrollY());
	        	
	        	if (mContentHitRect.contains((int)event.getX(), (int)event.getY())) {
	        		return true;	        	
	        	}

	        	return super.onTouchEvent(event);
	        }
	    };
	    
	    inflater.inflate(contentLayout, contentContainer);
	    addView(this.contentContainer,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    
	    this.scrollController = new ScrollController(new Scroller(context));
	    this.gestureDetector = new GestureDetector(context, scrollController);
	    this.gestureDetector.setIsLongpressEnabled(false);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		
		for(int idx = 0;idx < this.getChildCount();idx++)
		{
			View v= this.getChildAt(idx);
			
			if(this.menuContainer.equals(v))
				v.measure(MeasureSpec.makeMeasureSpec(width - this.menu_space, MeasureSpec.EXACTLY), heightMeasureSpec);
			else
				v.measure(widthMeasureSpec, heightMeasureSpec);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		// putting every child view to top-left corner
	    final int childrenCount = getChildCount();
	    
	    for (int i=0; i<childrenCount; ++i) {
	      final View v = getChildAt(i);
	      v.layout(l, t, l + v.getMeasuredWidth(), t + v.getMeasuredHeight());
	    }
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub	
		
		if(this.isScrollEnabled)
		{
			if(this.gestureDetector.onTouchEvent(ev) && listener !=  null)
				listener.onMenuStatusChange(isShowMenu());
		}
		
		if(ev.getAction() == MotionEvent.ACTION_UP)
		{			
			this.scrollController.autoCompleted();
		}
		
		return super.dispatchTouchEvent(ev);
	}
	
	public void toggleMenu()
	{
		if(this.isShowMenu() || !this.isScrollEnabled)			
			this.showContent();
		else
			this.showMenu();
	}	
	
	public void showMenu()
	{
		if(listener !=  null)listener.onMenuStatusChange(true);
		this.scrollController.autoOpen();
	}
	
	public void showContent()
	{
		if(listener !=  null)listener.onMenuStatusChange(false);
		this.scrollController.autoClose();
	}
	
	public boolean isScrollEnabled() {
		return isScrollEnabled;
	}

	public void setScrollEnabled(boolean isScrollEnabled) {
		this.isScrollEnabled = isScrollEnabled;
	}

	public boolean isShowMenu()
	{
		return (Math.abs(this.contentContainer.getScrollX()) > getWidth() / 2);
	}	

	public ScrollMenuListener getListener() {
		return listener;
	}

	public void setScrollMenuListener(ScrollMenuListener listener) {
		this.listener = listener;
	}

	private class ScrollController implements GestureDetector.OnGestureListener,Runnable
	{
		private Scroller scroller = null;
		private boolean isScroll = false;

		public ScrollController(Scroller scroller)
		{
			this.scroller = scroller;
		}
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			this.isScroll = (e.getX(0) >= -contentContainer.getScrollX());
			return false;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}

		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub			
			if(!this.isScroll)
				return false;
			else if (Math.abs(distanceX) < Math.abs(distanceY)) 
				return false;
			else 
				this.scrollTo(contentContainer.getScrollX() + (int)distanceX);				

			return true;
		}

		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		private void scrollTo(int x) {
			
	    	int bound = menu_space - getWidth();
		    int nx = x;
		    nx = (nx < bound)?bound:nx;
		    nx = (nx > 0)?0:nx;
		    contentContainer.scrollTo(nx, 0);
		    if(Build.VERSION.SDK_INT >= 11)
		    	menuContainer.setAlpha((float)((float)nx/(float)bound));
	    }
		
		private void autoOpen()
		{
			int bound = menu_space - getWidth();
			int dx = bound - contentContainer.getScrollX();
			this.scroller.startScroll(contentContainer.getScrollX(), 0, dx, 0);			
			contentContainer.post(this);			
		}
		
		private void autoClose()
		{
			this.scroller.startScroll(contentContainer.getScrollX(), 0, -contentContainer.getScrollX(), 0);		
			contentContainer.post(this);
		}
		private void autoCompleted()
		{
			if(isShowMenu())
				this.autoOpen();
			else
				this.autoClose();
		}

		public void run() {
			// TODO Auto-generated method stub			
			if(this.scroller.computeScrollOffset())
				contentContainer.post(this);
			else
				this.scroller.forceFinished(true);
			
			this.scrollTo(this.scroller.getCurrX());
		}
	}

	public interface ScrollMenuListener
	{
		public void onMenuStatusChange(boolean isOpen);		
	}
}
