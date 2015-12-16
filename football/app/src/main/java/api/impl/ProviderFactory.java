package api.impl;

import android.content.Context;

import api.OnDataReady;
import api.Provider;
import networking.Connection;

/**
 * @author igiagante on 25/11/15.
 */
public class ProviderFactory {

    private Context mContext;
    private OnDataReady mOnDataReady;

    public ProviderFactory(Context context, OnDataReady onDataReady ){
        mContext = context;
        mOnDataReady = onDataReady;
    }

    /**
     * Gets the correct provider. It depends on the internet connection.
     * @return Provider.
     */
    public Provider getProvider() {
        if(Connection.checkInternet(mContext)) {
            return new InternetProvider(mContext, mOnDataReady);
        } else {
            return new DatabaseProvider(mContext, mOnDataReady);
        }
    }
}
