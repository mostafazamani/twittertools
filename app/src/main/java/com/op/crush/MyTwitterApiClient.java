package com.op.crush;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

public class MyTwitterApiClient extends TwitterApiClient {

    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }

    public ServiceListener getCustomTwitterService(){
        return getService(ServiceListener.class);
    }
}
