package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @author igiagante on 19/11/15.
 *         It uses to make easy the transfer data of seasons using parcelable.
 */
public class SoccerSeason implements Parcelable {

    ArrayList<Season> seasons;

    public SoccerSeason() {
        seasons = new ArrayList<>();
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(ArrayList<Season> seasons) {
        this.seasons = seasons;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(seasons);
    }

    public static final Parcelable.Creator<SoccerSeason> CREATOR = new Parcelable.Creator<SoccerSeason>() {
        public SoccerSeason createFromParcel(Parcel in) {
            return new SoccerSeason(in);
        }

        public SoccerSeason[] newArray(int size) {
            return new SoccerSeason[size];
        }
    };

    private SoccerSeason(Parcel in) {
        in.readList(seasons, this.getClass().getClassLoader());
    }
}
