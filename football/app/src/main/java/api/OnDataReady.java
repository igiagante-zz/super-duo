package api;

import model.SoccerSeason;

/**
 * @author igiagante on 25/11/15.
 */
public interface OnDataReady {

    /**
     * When data is ready, the listener will implement this interface to process the data.
     * @param soccerSeason
     */
    void onDataReady(SoccerSeason soccerSeason);
}
