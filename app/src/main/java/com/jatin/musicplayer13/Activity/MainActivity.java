package com.jatin.musicplayer13.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.jatin.musicplayer13.Adapter.ViewPagerAdapter;
import com.jatin.musicplayer13.DB.FavoritesOperations;
import com.jatin.musicplayer13.Features.SleepTimer;
import com.jatin.musicplayer13.Fragments.AllSongFragment;
import com.jatin.musicplayer13.Fragments.CurrentSongFragment;
import com.jatin.musicplayer13.Fragments.FavSongFragment;
import com.jatin.musicplayer13.Model.SongsList;
import com.jatin.musicplayer13.R;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AllSongFragment.createDataParse,FavSongFragment.createDataParsed,CurrentSongFragment.createDataParsed {

    private Menu menu;

    private ImageButton imgBtnPlayPause, imgbtnReplay, imgBtnPrev, imgBtnNext, imgBtnSetting;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SeekBar seekbarController;
    private DrawerLayout mDrawerLayout;
    private TextView tvCurrentTime, tvTotalTime;
    private ArrayList<SongsList> songList;
    private int currentPosition;
    private String searchText = "";
    private SongsList currSong;
/**
    *checkflag boolean is used here for checking weather the user has selected any songs or not
    *favflag boolean is used here for checking weather the user can add a song fav or not
    *playContinueFlag boolean is used for weather the loop is active or not
    *playlistflag boolean is used for weather the playlist is fetched or not
    *Shuffle boolean is used for weather the suffle play is on or off
 */
    private boolean Shuffle=false,checkFlag = false, repeatFlag = false, playContinueFlag = false, favFlag = true, playlistFlag = false;
    private final int MY_PERMISSION_REQUEST = 100;
    private int allSongLength;


    MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();//view initialising
        grantedPermission();//a pop window to user for asking the permission

    }

    /**
     * Initialising the views
     */

    private void init() {
        //Because you must create the notification channel
        //before posting any notifications on Android 8.0 and higher,
        //you should execute this code as soon as your app starts
        createNotificationChannel();

        imgBtnPrev = findViewById(R.id.img_btn_previous);
        imgBtnNext = findViewById(R.id.img_btn_next);
        imgbtnReplay = findViewById(R.id.img_btn_replay);
        imgBtnSetting = findViewById(R.id.img_btn_Loop);

        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        FloatingActionButton refreshSongs = findViewById(R.id.btn_refresh);
        seekbarController = findViewById(R.id.seekbar_controller);
        viewPager = findViewById(R.id.songs_viewpager);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        imgBtnPlayPause = findViewById(R.id.img_btn_play);
        Toolbar toolbar = findViewById(R.id.toolbar);//this attribute will be important for creating new themes
        handler = new Handler();
        mediaPlayer = new MediaPlayer();

        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color));
        setSupportActionBar(toolbar);//this will wire the homescreen with navigationbar and populate with menu section

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;//An assert statement is used to declare an expected boolean condition in a program.
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);
        imgBtnNext.setOnClickListener(this);
        imgBtnPrev.setOnClickListener(this);
        imgbtnReplay.setOnClickListener(this);
        refreshSongs.setOnClickListener(this);
        imgBtnPlayPause.setOnClickListener(this);
        imgBtnSetting.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) //this is for the buttons created in navigation bar
                {
                    case R.id.nav_about://for about button in navigationBar
                        about();//shows the text in about button if clicked
                        break;
                    case R.id.nav_sleep_timer:
                        Sleeptimer();
                        break;
                    case R.id.nav_Shuffle_Play:
                        Shuffleplay();

                }
                return true;
            }
        });
    }


    /**
     * Function to ask user to grant the permission.
     */

    private void grantedPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    Snackbar snackbar = Snackbar.make(mDrawerLayout, "Provide the Storage Permission", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        } else {
            setPagerLayout();
        }
    }

    /**
     * Checking if the permission is granted or not
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        setPagerLayout();
                    } else {
                        Snackbar snackbar = Snackbar.make(mDrawerLayout, "Provide the Storage Permission", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        finish();//this will render all the activities if permission not granted
                        //finishAffinity();   // try this instead of above
                    }
                }
        }
    }

    /**
     * Setting up the tab layout with the viewpager in it.
     */

    private void setPagerLayout() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getContentResolver());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)//giving vetical scroll bar functionality in viewpager
            {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //now integrating tab section with pager layout
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) //that horizontal slidebar comes below the tabs which showcases the movements of user tab
            {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    /**
     * Function to show the dialog for about us.
     */
    private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.about))
                .setMessage(getString(R.string.about_text))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**Function to ShufflePlay the songs
     * @param rand for generating random outputs*/
    private Random rand;
    public void Shuffleplay(){
        rand=new Random();
        setShuffle();
        playNext();
        if (Shuffle==true) {
            Toast.makeText(MainActivity.this,"Shuffle song is -ON",Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(MainActivity.this,"Shuffle Song -OF" ,Toast.LENGTH_SHORT).show();
    }

    /**Function to to toggle the boolean Shuffle */
    public void setShuffle(){
        if(Shuffle) Shuffle=false;
        else Shuffle=true;
    }

    /**This function will play the shuffled songs  */
    public void playNext(){
        if (Shuffle){
            int newsong=currentPosition;
            while (newsong==currentPosition){
                newsong =rand.nextInt(songList.size());
            }
            currentPosition=newsong;
        }else{
            currentPosition++;
            if (currentPosition == songList.size()) currentPosition=0;
        }
    }



    /**
     * Function to show SLEEPTIMER FUNCTION*/
    public void Sleeptimer(){
        Intent sleeptimer = new Intent(MainActivity.this, SleepTimer.class);
        startActivity(sleeptimer);
            }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) //this function is called when the menu button of the device is pressed
     {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);//basically it reads the reesource file of action_bar_menu.xml and inflates it for user
        //the below search class and view are for the purpose of searching the song. searchview give a search bar for user  and searchmanager give access to system search service
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)//searched query is not available
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)//searched query is available
             {
                searchText = newText;
                queryText();
                setPagerLayout();//sets the viewpager for user in which the searched song shows
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);//to show the updated menu btw it should return true to show updated menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();//if someone long press the search icon it should show "search text"
                return true;
            case R.id.menu_favorites:
                if (checkFlag)
                    if (mediaPlayer != null) {
                        if (favFlag)//if true u can add the song to favList
                        {
                            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                            item.setIcon(R.drawable.ic_favorite_filled);
                            SongsList favList = new SongsList(songList.get(currentPosition).getTitle(),
                                    songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath());
                            FavoritesOperations favoritesOperations = new FavoritesOperations(this);
                            favoritesOperations.addSongFav(favList);
                            setPagerLayout();
                            favFlag = false;
                        } else//else unfill the favorite icon    #NOTE:this will not remove the song from favList that functionality is not added
                            {
                            item.setIcon(R.drawable.favorite_icon);
                            favFlag = true;
                            }
                    }
                return true;
        }

        return super.onOptionsItemSelected(item);

    }


    /**Before you can deliver the notification on Android 8.0 and higher,
    you must register your app's notification channel with the system
    by passing an instance of NotificationChannel to createNotificationChannel()*/

    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name= getString(R.string.channel_name);
            String description=getString(R.string.channel_description);
            int importance= NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("0",name,importance );
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Function for Notification bar //ITS PENDING-HELPER ACTIVITY,MY NOTIFICATION,NOTIFICATION LAYOUT
     * */
    private void addNotification(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this,"0")
                .setSmallIcon(R.drawable.icon_img)//icon in notification bar
                .setContentTitle(getTitle())//this is the heading title
                .setContentText("This is a demo notification")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        // Add as notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }



    /**
     * Function to handle the click events.
     *
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.img_btn_play:
                if (checkFlag)
                    //Here we have used functionality of built in mediaplayer class
                {
                    if (mediaPlayer.isPlaying()) //if media song is playing pause it
                    {
                        mediaPlayer.pause();
                        imgBtnPlayPause.setImageResource(R.drawable.play_icon);
                    } else if (!mediaPlayer.isPlaying())//otherwise start the media song
                         {
                        mediaPlayer.start();
                        imgBtnPlayPause.setImageResource(R.drawable.pause_icon);
                        playCycle();//will use a thread to play a song
                        }
                } else//else select the song since you haven't clicked in any song
                    {
                    Toast.makeText(this, "Select the Song ..", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_refresh://reset the layout using setpagerlayout
                Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
                setPagerLayout();
                break;
            case R.id.img_btn_replay:

                if (repeatFlag)//if flag is true means already replaying therefore replaying can be removed
                {
                    Toast.makeText(this, "Loop Song Deactivated", Toast.LENGTH_SHORT).show();
                    mediaPlayer.setLooping(false);
                    repeatFlag = false;
                } else //if flag is false means replaying can be added
                    {
                    Toast.makeText(this, "Loop Song Activated", Toast.LENGTH_SHORT).show();
                    mediaPlayer.setLooping(true);
                    repeatFlag = true;
                }
                break;
            case R.id.img_btn_previous:
                if (checkFlag) //if a song is selected
                {
                    if (mediaPlayer.getCurrentPosition() > 10)//logic behind this ten is we have created an empty arraylist in songadapter.java line 19
                    {
                        if (currentPosition - 1 > -1)//if this is not the last song than go to previous song
                        {
                            attachMusic(songList.get(currentPosition - 1).getTitle(), songList.get(currentPosition - 1).getPath());
                            currentPosition = currentPosition - 1;
                        } else//else play the same song
                            {
                            attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
                        }
                    } else //play the same song
                        {
                        attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
                    }
                } else //else song is not select pls select a song
                     {
                    Toast.makeText(this, "Select a Song . .", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_btn_next:
                if (checkFlag)//if a song is selected
                {
                    if (currentPosition + 1 < songList.size()) //if current position is less than total_number(size) of song than play next song
                    {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getPath());
                        currentPosition += 1;
                    } else //else current position is not less than total songs
                        {
                        Toast.makeText(this, "Playlist Ended", Toast.LENGTH_SHORT).show();
                    }
                } else//else pls select a song
                    {
                    Toast.makeText(this, "Select the Song ..", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_btn_Loop:
                if (!playContinueFlag) //if flag is false loop can be addded
                {
                    playContinueFlag = true;
                    Toast.makeText(this, "Loop List Activated", Toast.LENGTH_SHORT).show();
                } else //else remove the loop from songs
                    {
                    playContinueFlag = false;
                    Toast.makeText(this, "Loop List Deactivated", Toast.LENGTH_SHORT).show();
                }
                break;

            //case R.id.nav_sleep_timer:

           //     break;
        }
    }

    /**
     * Function to attach the song to the music player
     *
     * @param name
     * @param path
     */

    private void attachMusic(String name, String path)
    {
        imgBtnPlayPause.setImageResource(R.drawable.play_icon);
        setTitle(name);//this will make the title appear at toolbar section of layout
        menu.getItem(1).setIcon(R.drawable.favorite_icon);
        favFlag = true;

        try {
            mediaPlayer.reset();//reset the media_player to its uninitialized state
            //now we have to initialize it again
            mediaPlayer.setDataSource(path);//gets the path of stream
            mediaPlayer.prepare();//prepares the player for playback and synchronize it
            setControls();//calling the setControl() function from line 463
        } catch (Exception e) {
            e.printStackTrace();
        }

        //when the song is ended below class will execute
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                imgBtnPlayPause.setImageResource(R.drawable.play_icon);//reset the icon to play
                if (playContinueFlag)//if loop song is active
                {
                    if (currentPosition + 1 < songList.size()) //if currentposition is less than total_number of song than continue attaching and playing next song
                    {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getPath());
                        currentPosition += 1;
                    } else//else playlist has ended
                        {
                        Toast.makeText(MainActivity.this, "PlayList Ended", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Function to set the controls according to the song
     */

    private void setControls() {
        seekbarController.setMax(mediaPlayer.getDuration());//setting the max duration to total duration
        mediaPlayer.start();
        playCycle();
        checkFlag = true;//true->user has selected an song

        //this will trigger the Notification bar every time we play a song
        addNotification();

        if (mediaPlayer.isPlaying())
        {
            imgBtnPlayPause.setImageResource(R.drawable.pause_icon);//setting the image to pause icon since song is playing
            tvTotalTime.setText(getTimeFormatted(mediaPlayer.getDuration()));//setting the text by getting the duration of file
        }

        seekbarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) //true if the user is touched the seekbar i.e progress bar is being changed
                {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(getTimeFormatted(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Function to play the song using a thread
     */
    private void playCycle() //in a different thread tracking of time & progressbar
    {

        try {
            seekbarController.setProgress(mediaPlayer.getCurrentPosition());
            tvCurrentTime.setText(getTimeFormatted(mediaPlayer.getCurrentPosition()));
            if (mediaPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() //this is were the thread is used ..A Runnable is a Single Abstract Method (SAM) interface with a run() method that is executed in a thread when invoked.
                    {
                        playCycle();

                    }
                };
                handler.postDelayed(runnable, 100);//read the runnable discription given in this same line by hovering on "runnable"
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getTimeFormatted method will simply convert the milliseconds to hours:minutes:seconds*/
    private String getTimeFormatted(long milliSeconds) {
        String finalTimerString = "";
        String secondsString;

        //Converting total duration into time
        int hours = (int) (milliSeconds / 3600000);
        int minutes = (int) (milliSeconds % 3600000) / 60000;
        int seconds = (int) ((milliSeconds % 3600000) % 60000 / 1000);

        // Adding hours if any
        if (hours > 0)
            finalTimerString = hours + ":";

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;

        finalTimerString = finalTimerString + minutes + ":" + secondsString;//in this finaltimerstring will only show if hours are greater than zero

        // Return timer String;
        return finalTimerString;
    }


    /**
     * Function Overrided to receive the data from the fragment
     *
     * @param name
     * @param path
     */

    @Override
    public void onDataPass(String name, String path) {
        Toast.makeText(this, name, Toast.LENGTH_LONG).show();
        attachMusic(name, path);
    }

    @Override
    public void getLength(int length) {
        this.allSongLength = length;
    }

    @Override
    public void fullSongList(ArrayList<SongsList> songList, int position)//
    {
        this.songList = songList;
        this.currentPosition = position;
        this.playlistFlag = songList.size() == allSongLength;
        this.playContinueFlag = !playlistFlag;
    }

    @Override
    public String queryText() {
        return searchText.toLowerCase();
    }// have converted everything to lower case when searching for a song so that case sensitive never becomes an issue

    @Override
    public SongsList getSong() {
        currentPosition = -1;//everytime we get a song currentposition is reduced by 1
        return currSong;
    }

    @Override
    public boolean getPlaylistFlag() {
        return playlistFlag;
    }

    @Override
    public void currentSong(SongsList songsList) {
        this.currSong = songsList;
    }

    @Override
    public int getPosition() {
        return currentPosition;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
    }

    }

