package us.handstand.kartwheel.model.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

abstract class CachedAsyncLoader<L extends List> extends AsyncTaskLoader<L> {
    private L cachedResults;

    CachedAsyncLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(L results) {
        if (isReset()) {
            return;
        }

        cachedResults = results;
        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(cachedResults);
        }
    }

    @Override
    protected void onStartLoading() {
        if (cachedResults != null && !cachedResults.isEmpty()) {
            // Deliver any previously loaded data immediately.
            deliverResult(cachedResults);
        }

        // Start listening for a force update
        if (takeContentChanged() || cachedResults == null || cachedResults.isEmpty()) {
            // When the observer detects a change, it should call notifyContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cachedResults = null;
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }
}
