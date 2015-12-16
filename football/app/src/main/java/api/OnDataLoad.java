package api;

import java.util.ArrayList;

import model.Season;

/**
 * @author igiagante on 25/11/15.
 */
public interface OnDataLoad {

    /**
     * The data was successfully retrieved.
     * @param mSeasons
     */
    void onDataLoadedSuccess(ArrayList<Season> mSeasons);

    /**
     * The data could not be retrieved.
     */
    void onDataLoadedFail();

    /**
     * Indicates the state of the data process search.
     * @param load
     */
    void onDataLoading(boolean load);
}
