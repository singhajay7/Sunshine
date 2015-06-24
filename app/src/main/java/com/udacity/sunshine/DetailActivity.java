package com.udacity.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.sunshine.R;
import com.udacity.sunshine.data.WeatherContract;

public class DetailActivity extends ActionBarActivity {

    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private String mForecastStr;
        ShareActionProvider mActionProvider;

        public PlaceholderFragment() {
        }

        private static String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
        };

        // these constants correspond to the projection defined above, and must change if the
        // projection changes
        private static final int COL_WEATHER_ID = 0;
        private static final int COL_WEATHER_DATE = 1;
        private static final int COL_WEATHER_DESC = 2;
        private static final int COL_WEATHER_MAX_TEMP = 3;
        private static final int COL_WEATHER_MIN_TEMP = 4;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
           /* TextView dataView = (TextView) rootView.findViewById(R.id.forecastDataVw);
            String forecastData = getActivity().getIntent().getDataString();
            dataView.setText(forecastData);*/
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(0, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detail, menu);
            MenuItem item = menu.findItem(R.id.action_share);
            mActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            if (mForecastStr != null) {
                createShareForecastIntent();
            }
        }


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }
            CursorLoader c = new CursorLoader(getActivity(), intent.getData(), FORECAST_COLUMNS, null, null, null);
            return c;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (!data.moveToFirst()) {
                return;
            }
            String date = Utility.formatDate(data.getLong(COL_WEATHER_DATE));

            String desc = data.getString(COL_WEATHER_DESC);
            boolean isMetric = Utility.isMetric(getActivity());
            String maxTemp = Utility.formatTemperature(data.getDouble(COL_WEATHER_MAX_TEMP),
                    isMetric);
            String minTemp = Utility.formatTemperature(data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);


            mForecastStr = String.format("%s - %s - %s/%s", date, desc, maxTemp, minTemp);

            TextView detailTextView = (TextView)getView().findViewById(R.id.forecastDataVw);
            detailTextView.setText(mForecastStr);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mActionProvider != null) {
                mActionProvider.setShareIntent(createShareForecastIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    mForecastStr + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }
    }


}