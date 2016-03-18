package com.ap.collegespacev2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by amaneureka on 31-Jan-16.
 */
public class PostDetails extends ActionBarActivity
{
    String aID;
    String aContent;
    String aUrl;
    String aDate;
    String aTitle;
    String aActionBar;
    boolean isPostFav = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        aActionBar = getIntent().getStringExtra("post_title_bar");
        aTitle = getIntent().getStringExtra("post_title");
        aID = getIntent().getStringExtra("post_id");
        aContent = getIntent().getStringExtra("post_content");
        aUrl = getIntent().getStringExtra("post_url");
        aDate = getIntent().getStringExtra("post_date");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(aActionBar);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Relative date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        try
        {
            Date ddate = formatter.parse(aDate);
            long millis = ddate.getTime();
            aDate = DateUtils.getRelativeTimeSpanString(millis, System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString();
        }
        catch(ParseException e) { }

        //Load Data
        ((TextView)findViewById(R.id.post_date)).setText(aDate);
        ((TextView)findViewById(R.id.post_title)).setText(aTitle);
        //((WebView)findViewById(R.id.post_content)).loadData(aContent, "text/html", "utf-8");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details, menu);
        SharedPreferences pref = getSharedPreferences("PREFERENCE_APP", 0);
        String favs = pref.getString("Favs" , "-1");
        if (favs.contains(aID))
        {
            isPostFav = true;
            MenuItem nofav = menu.findItem(R.id.menu_not_fav);
            nofav.setVisible(false);
        }
        else
        {
            isPostFav = false;
            MenuItem fav = menu.findItem(R.id.menu_fav);
            fav.setVisible(false);
        }
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_share:
                shareContent();
                return true;
            case R.id.menu_web:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(aUrl));
                startActivity(i);
                return true;
            case R.id.menu_fav:
                isPostFav = false;
                removeFav();
                supportInvalidateOptionsMenu();
                return true;
            case R.id.menu_not_fav:
                isPostFav = true;
                addFav();
                supportInvalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addFav()
    {
        SharedPreferences pref = getSharedPreferences("PREFERENCE_APP", 0);
        String favs = pref.getString("Favs" , null);
        if (favs != null)
        {
            String newfav = favs+aID+",";
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("Favs", newfav);
            editor.commit();
        }
        else
        {
            String newfav = aID+",";
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("Favs", newfav);
            editor.commit();
        }
        Toast.makeText(this, getString(R.string.msg_fav_added) ,Toast.LENGTH_SHORT).show();
    }

    private void removeFav()
    {
        SharedPreferences pref = getSharedPreferences("PREFERENCE_APP", 0);
        String favs = pref.getString("Favs" , null);
        String actfav =aID+ ",";
        String editfav = favs.replace(actfav, "");
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Favs", editfav);
        editor.commit();
        Toast.makeText(this, getString(R.string.msg_fav_removed) ,Toast.LENGTH_SHORT).show();
    }

    private void shareContent()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(aTitle) + "\n" + aUrl);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.action_share)));
    }
}