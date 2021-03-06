package com.github.yeriomin.yalpstore;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.yeriomin.yalpstore.model.App;

import java.util.Map;

public class SearchResultActivity extends EndlessScrollActivity {

    private String query;
    private String categoryId = CategoryManager.TOP;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String newQuery = getQuery(intent);
        if (null != newQuery && !newQuery.equals(this.query)) {
            this.categoryId = CategoryManager.TOP;
            this.query = newQuery;
            this.data.clear();
            setTitle(getString(R.string.activity_title_search, this.query));
            loadApps();
            ((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new CategoryManager(this).fill((Spinner) findViewById(R.id.filter));
    }

    public void setCategoryId(String categoryId) {
        if (!categoryId.equals(this.categoryId)) {
            this.categoryId = categoryId;
            data.clear();
            loadApps();
        }
    }

    public void loadApps() {
        SearchTask task = new SearchTask() {

            @Override
            protected void onPostExecute(Throwable e) {
                super.onPostExecute(e);
                addApps(apps);
            }
        };
        task.setCategoryManager(new CategoryManager(this));
        prepareTask(task).execute(query, categoryId);
    }

    private String getQuery(Intent intent) {
        if (intent.getScheme() != null
            && (intent.getScheme().equals("market")
            || intent.getScheme().equals("http")
            || intent.getScheme().equals("https"))
        ) {
            return intent.getData().getQueryParameter("q");
        }
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            return intent.getStringExtra(SearchManager.QUERY);
        } else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            return intent.getDataString();
        }
        return null;
    }
}
