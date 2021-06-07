package com.jatin.musicplayer13.Fragments;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.jatin.musicplayer13.Adapter.SongAdapter;
import com.jatin.musicplayer13.Model.SongsList;
import com.jatin.musicplayer13.R;

import java.util.ArrayList;

public class AllSongFragment extends ListFragment {


    private static ContentResolver contentResolver1;

    public ArrayList<SongsList> songsList;
    public ArrayList<SongsList> newList;

    private ListView listView;

    private createDataParse createDataParse;
    private ContentResolver contentResolver;

    public static Fragment getInstance(int position, ContentResolver mcontentResolver) {
        Bundle bundle = new Bundle();//Android Bundle is used to pass data between activities. The values that are to be passed are mapped to String keys which are later used in the next activity to retrieve the values.
        bundle.putInt("pos", position);//inserting pos value into bundle  and replacing any existing value for the given key
        AllSongFragment tabFragment = new AllSongFragment();
        tabFragment.setArguments(bundle);//setting the allsong tab section
        contentResolver1 = mcontentResolver;
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//recreated fragment is recreated with the help of saved_instance_state

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);//Called when a fragment is first attached to its context
        createDataParse = (createDataParse) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);//inflate the tab bar
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        contentResolver = contentResolver1;
        setContent();//setting the content to the listview and sending the data to main activity
    }

    /**
     * Setting the content in the listView and sending the data to the Activity
     */

    public void setContent() {
        boolean searchedList = false;
        songsList = new ArrayList<>();//creating empty list to fill
        newList = new ArrayList<>();
        getMusic();
        SongAdapter adapter = new SongAdapter(getContext(), songsList);//returns thhe context to its present working fragment
        if (!createDataParse.queryText().equals("")) //if query is not empty
        {
            adapter = onQueryTextChange();
            adapter.notifyDataSetChanged();
            searchedList = true;//we can serach somethimg
        } else {
            searchedList = false;//elsewhere no
        }
        createDataParse.getLength(songsList.size());
        listView.setAdapter(adapter);

        final boolean finalSearchedList = searchedList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                if (!finalSearchedList) //if a song is found then fill the tab with song
                {
                    createDataParse.onDataPass(songsList.get(position).getTitle(), songsList.get(position).getPath());//gets the position ,title and song path
                    createDataParse.fullSongList(songsList, position);//positioning  the  songs inside the songviewpager
                } else //if not then also fill the tab with null element :)
                    {
                    createDataParse.onDataPass(newList.get(position).getTitle(), newList.get(position).getPath());
                    createDataParse.fullSongList(songsList, position);
                }
                //this sets the background color of the tile song that we clicked to grey
               // view.setBackgroundColor(Color.GRAY);

            }
        });
        //the below longclicklistener is used here with listview so when a user longpress the tile it will show a dialog window
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override//here this function will give user long press ability in listview and call another function to show a dialog box for the user and the opetations working
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                showDialog(position);
                return true;
            }
        });
    }


    public void getMusic() {
        //retrieving the song info
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//getting the file path for more info regarding Uri search "uri geeks for geeks"
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst())//if there is a song and move curser to the first row
        {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);//get the title
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);//get sub title
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);//get data of song

            //we are using an exit controlled so atleast one time the loop works if conditions are not satisfied , in this case the condition will always satisfy till there is a song
            do {
                songsList.add(new SongsList(songCursor.getString(songTitle), songCursor.getString(songArtist), songCursor.getString(songPath)));
            } while (songCursor.moveToNext());//move the cursor to next row if not possible than it returns the false
            songCursor.close();
        }
    }


    public SongAdapter onQueryTextChange()//for searching the song
    {
        String text = createDataParse.queryText();//for searching the song
        for (SongsList songs : songsList) //add songs to songlist one by one
        {
            String title = songs.getTitle().toLowerCase();//the search text will be converted to lower case always inside the search engine
            if (title.contains(text)) //return true if there is a text
            {
                newList.add(songs);//append the song
            }
        }
        return new SongAdapter(getContext(), newList);

    }

    private void showDialog(final int position) //for the the dialog box that shows when user long press a song
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.play_next))
                .setCancelable(true)//dialog box becomes cancellable
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override//if the negative button is pressed the dialog box will just disappear
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override//when yes button is pressed song gets added to current playlist
                    public void onClick(DialogInterface dialog, int which) {
                        createDataParse.currentSong(songsList.get(position));
                        setContent();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();//show the dialog box
    }

    public interface createDataParse {
        public void onDataPass(String name, String path);

        public void fullSongList(ArrayList<SongsList> songList, int position);

        public String queryText();

        public void currentSong(SongsList songsList);
        public void getLength(int length);
    }

}
