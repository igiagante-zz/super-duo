package com.example.igiagante.football;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import adapters.MatchAdapter;
import model.Season;
import utils.IconsMap;
import utils.Utils;

/**
 * @author igiagante on 25/11/15.
 */
public class FixtureView extends LinearLayout {

    private Season mSeason;
    private ImageView mLogo;
    private TextView mTitle;
    private ListView mMatches;
    private MatchAdapter mAdapter;
    private Activity mActivity;
    private Context mContext;

    private IconsMap iconsMap;

    public FixtureView(Context context) {
        super(context);
        mContext = context;
    }

    public FixtureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixtureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        inflate(getContext(), R.layout.card_view, this);
        mTitle = (TextView) findViewById(R.id.league);
        mLogo = (ImageView) findViewById(R.id.country_flag);
        mMatches = (ListView) findViewById(R.id.match_list);

        if (Utils.getSmallWithDisplay(mActivity) > 550) {
            mTitle.setTextSize(22);
        }
        mTitle.setText(mSeason.getCaption());

        iconsMap = new IconsMap(mContext);
        iconsMap.initIconsMap();

        mLogo.setImageDrawable(iconsMap.getIcon(mSeason.getId()));

        //setup list view
        mAdapter = new MatchAdapter(mActivity, mContext, mSeason.getMatches());
        mMatches.setAdapter(mAdapter);
    }

    public void setSeason(Activity activity, Context context, Season season) {
        this.mSeason = season;
        mActivity = activity;
        mContext = context;
        init();
    }

    public int getTitleHeight() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_title);
        return layout.getHeight();
    }
}
