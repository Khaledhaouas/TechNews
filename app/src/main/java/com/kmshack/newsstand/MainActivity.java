package com.kmshack.newsstand;

import android.annotation.TargetApi;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.Parsing.ParsingRss;
import com.astuetz.PagerSlidingTabStrip;
import com.flavienlaurent.notboringactionbar.AlphaForegroundColorSpan;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nineoldandroids.view.ViewHelper;

import khaled.newsapp.R;

public class MainActivity extends ActionBarActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener, SwipeRefreshLayout.OnRefreshListener {

	private SwipeRefreshLayout swipeRefreshLayout;

	private Toolbar toolbar;
	private TextView title;
	private ImageView icon;


	private static AccelerateDecelerateInterpolator sSmoothInterpolator = new AccelerateDecelerateInterpolator();

	private KenBurnsView mHeaderPicture;
	private View mHeader;

	private PagerSlidingTabStrip mPagerSlidingTabStrip;
	private ViewPager mViewPager;
	private PagerAdapter mPagerAdapter;

	private int mActionBarHeight;
	private int mMinHeaderHeight;
	private int mHeaderHeight;
	private int mMinHeaderTranslation;
	private ImageView mHeaderLogo;

	private RectF mRect1 = new RectF();
	private RectF mRect2 = new RectF();

	private TypedValue mTypedValue = new TypedValue();
	private SpannableString mSpannableString;
	private AlphaForegroundColorSpan mAlphaForegroundColorSpan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.min_header_height);
		mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
		mMinHeaderTranslation = -mMinHeaderHeight + getActionBarHeight();
		setContentView(R.layout.activity_main);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		icon = (ImageView) findViewById(R.id.icon);
		title = (TextView) findViewById(R.id.title);

		mHeaderPicture = (KenBurnsView) findViewById(R.id.header_picture);
		mHeaderLogo = (ImageView) findViewById(R.id.header_logo);
		mHeader = findViewById(R.id.header);

		mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(4);

		mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
		mPagerAdapter.setTabHolderScrollingContent(this);

		mViewPager.setAdapter(mPagerAdapter);

		mPagerSlidingTabStrip.setViewPager(mViewPager);
		mPagerSlidingTabStrip.setOnPageChangeListener(this);
		mSpannableString = new SpannableString(getString(R.string.actionbar_title));
		mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(0xffffffff);
		
		ViewHelper.setAlpha(getActionBarIconView(), 0f);
		
		getSupportActionBar().setBackgroundDrawable(null);

		mHeaderPicture.setImageResource(R.drawable.google_background);
		mHeaderLogo.setImageResource(R.drawable.google);

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.post(new Runnable() {
									@Override
									public void run() {
										swipeRefreshLayout.setRefreshing(true);

										new ParsingRss().execute();
										swipeRefreshLayout.setRefreshing(false);

									}

								}
		);
		// Recherchez AdView comme ressource et chargez une demande.
		AdView adView = (AdView)this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().addTestDevice("F2A71918D03135A22715F40EB7933EEB").build();
		adView.loadAd(adRequest);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// nothing
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// nothing
	}

	@Override
	public void onPageSelected(int position) {
		SparseArrayCompat<ScrollTabHolder> scrollTabHolders = mPagerAdapter.getScrollTabHolders();
		ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);
		if(position==0) {
			mHeaderPicture.setImageResource(R.drawable.google_background);
			mHeaderLogo.setImageResource(R.drawable.google);
			getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor1));
		} else if(position==1) {
			mHeaderPicture.setImageResource(R.drawable.yahoo_background);
			mHeaderLogo.setImageResource(R.drawable.yahoo);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor2));
		}
		else if(position==2) {
				mHeaderPicture.setImageResource(R.drawable.facebook_background);
				mHeaderLogo.setImageResource(R.drawable.facebook);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor3));
		}else if(position==3) {
			mHeaderPicture.setImageResource(R.drawable.twitter_background);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor4));
			mHeaderLogo.setImageResource(R.drawable.twitter);
		}else if(position==4) {
			mHeaderPicture.setImageResource(R.drawable.android_background);
			mHeaderLogo.setImageResource(R.drawable.android);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor5));
		}else if(position==5) {
			mHeaderPicture.setImageResource(R.drawable.samsung_background);
			mHeaderLogo.setImageResource(R.drawable.samsung);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor6));
		}
		else if(position==6) {
			mHeaderPicture.setImageResource(R.drawable.apple_background);
			mHeaderLogo.setImageResource(R.drawable.apple);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor7));
		}else if(position==7) {
			mHeaderPicture.setImageResource(R.drawable.microsoft_background);
			mHeaderLogo.setImageResource(R.drawable.microsoft);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor8));
		}else if(position==8) {
			mHeaderPicture.setImageResource(R.drawable.amazon_background);
			mHeaderLogo.setImageResource(R.drawable.amazon);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor9));
		}
		currentHolder.adjustScroll((int) (mHeader.getHeight() + ViewHelper.getTranslationY(mHeader))+40);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
		if (mViewPager.getCurrentItem() == pagePosition) {
			int scrollY = getScrollY(view);
			ViewHelper.setTranslationY(mHeader, Math.max(-scrollY, mMinHeaderTranslation));
			float ratio = clamp(ViewHelper.getTranslationY(mHeader) / mMinHeaderTranslation, 0.0f, 1.0f);
			interpolate(mHeaderLogo, getActionBarIconView(), sSmoothInterpolator.getInterpolation(ratio));
			setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
		}
		swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && view.getCheckedItemPosition()==0);

	}

	@Override
	public void adjustScroll(int scrollHeight) {
		// nothing
	}

	public int getScrollY(AbsListView view) {
		View c = view.getChildAt(0);
		if (c == null) {
			return 0;
		}

		int firstVisiblePosition = view.getFirstVisiblePosition();
		int top = c.getTop();

		int headerHeight = 0;
		if (firstVisiblePosition >= 1) {
			headerHeight = mHeaderHeight;
		}

		return -top + firstVisiblePosition * c.getHeight() + headerHeight;
	}

	public static float clamp(float value, float max, float min) {
		return Math.max(Math.min(value, min), max);
	}

	private void interpolate(View view1, View view2, float interpolation) {
		getOnScreenRect(mRect1, view1);
		getOnScreenRect(mRect2, view2);

		float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
		float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
		float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
		float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

		ViewHelper.setTranslationX(view1, translationX);
		ViewHelper.setTranslationY(view1, translationY - ViewHelper.getTranslationY(mHeader));
		ViewHelper.setScaleX(view1, scaleX);
		ViewHelper.setScaleY(view1, scaleY);
	}

	private RectF getOnScreenRect(RectF rect, View view) {
		rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
		return rect;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public int getActionBarHeight() {
		if (mActionBarHeight != 0) {
			return mActionBarHeight;
		}
		
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
			getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
		}else{
			getTheme().resolveAttribute(R.attr.actionBarSize, mTypedValue, true);
		}
		
		mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
		
		return mActionBarHeight;
	}

	private void setTitleAlpha(float alpha) {
		mAlphaForegroundColorSpan.setAlpha(alpha);
		mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		title.setText(mSpannableString);
	}

	private ImageView getActionBarIconView() {
		return icon;
	}

	public class PagerAdapter extends FragmentPagerAdapter {

		private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
		private final String[] TITLES = { "Google","Yahoo","Facebook","Twitter", "Android", "Samsung", "Apple","Microsoft","Amazon"};
		private ScrollTabHolder mListener;

		public PagerAdapter(FragmentManager fm) {
			super(fm);
			mScrollTabHolders = new SparseArrayCompat<ScrollTabHolder>();
		}

		public void setTabHolderScrollingContent(ScrollTabHolder listener) {
			mListener = listener;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			ScrollTabHolderFragment fragment = (ScrollTabHolderFragment) SampleListFragment.newInstance(position);

			mScrollTabHolders.put(position, fragment);
			if (mListener != null) {
				fragment.setScrollTabHolder(mListener);
			}

			return fragment;
		}

		public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders() {
			return mScrollTabHolders;
		}

	}
	@Override
	public void onRefresh() {
		new ParsingRss().execute();
		swipeRefreshLayout.setRefreshing(false);

	}
}
