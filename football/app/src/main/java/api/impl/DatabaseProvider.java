package api.impl;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

import api.OnDataReady;
import api.Provider;
import data.DaoHelper;
import model.Season;
import model.SoccerSeason;

/**
 * @author igiagante on 25/11/15.
 */
public class DatabaseProvider implements Provider, Serializable {

    private Context mContext;
    private OnDataReady mOnDataReady;

    public DatabaseProvider(Context context, OnDataReady onDataReady) {
        mContext = context;
        mOnDataReady = onDataReady;
    }

    @Override
    public void getFixtures() {

        DaoHelper daoHelper = new DaoHelper(mContext);
        SoccerSeason soccerSeason = new SoccerSeason();
        ArrayList<Season> seasons = daoHelper.getAllSeasons();

        for (Season season : seasons) {
            season.setMatches(daoHelper.getAllMatchForOneSeason(season.getId()));
        }
        soccerSeason.setSeasons(seasons);

        mOnDataReady.onDataReady(soccerSeason);
    }
}
