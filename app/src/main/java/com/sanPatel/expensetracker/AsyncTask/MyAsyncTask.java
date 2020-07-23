package com.sanPatel.expensetracker.AsyncTask;

import android.os.AsyncTask;

public class MyAsyncTask extends AsyncTask {

    private AsyncTaskListener asyncTaskListener;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        asyncTaskListener.setBackgroundTask();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        asyncTaskListener.setPostExecuteTask();
    }

    public void setAsyncTaskListener(AsyncTaskListener mListener) {
        asyncTaskListener = mListener;
    }

    public interface AsyncTaskListener {
        void setBackgroundTask();
        void setPostExecuteTask();
    }
}
