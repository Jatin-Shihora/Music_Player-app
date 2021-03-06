package com.jatin.musicplayer13.Fragments;


import android.content.Context;
import android.os.Bundle;

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

public class CurrentSongFragment extends ListFragment {

    public ArrayList<SongsList> songsList = new ArrayList<>();//creating an empty songlist

    private ListView listView;

    private createDataParsed createDataParsed;

    public static Fragment getInstance(int position) {
        //Android Bundle is used to pass data between activities. The values that are to be passed are mapped to String keys which are later used in the next activity to retrieve the values.
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        CurrentSongFragment tabFragment = new CurrentSongFragment();
        tabFragment.setArguments(bundle);//the currentsongfragment is constructed by passing the construction arguments into bunndle
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//acts like a checkpoint if a fragment is recreated this is the state

    }

    @Override
    public void onAttach(Context context) //fragment is being attached to its context
    {
        super.onAttach(context);
        createDataParsed = (createDataParsed) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);//inflate the fragment tab
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        //songsList = new ArrayList<>();
        setContent();
    }

    /**
     * Setting the content in the listView and sending the data to the Activity
     */
    public void setContent() {
        if (createDataParsed.getSong() != null)//if list is not null than append the songs
            songsList.add(createDataParsed.getSong());

        SongAdapter adapter = new SongAdapter(getContext(), songsList);

        if (songsList.size() > 1)
            if (createDataParsed.getPlaylistFlag()) //if there are more than 1 song than getplayistflag and clear all elements
            {
                songsList.clear();
            }

        listView.setAdapter(adapter);//the data of listview is setted
        adapter.notifyDataSetChanged();//if changes are made in some data than refresh the view itself
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                createDataParsed.onDataPass(songsList.get(position).getTitle(), songsList.get(position).getPath());
                createDataParsed.fullSongList(songsList, position);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override//nothing will happen if long pressed on currensongplaylist songs
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return true;
            }
        });
    }

    public interface createDataParsed {
        public void onDataPass(String name, String path);

        public void fullSongList(ArrayList<SongsList> songList, int position);

        public SongsList getSong();

        public boolean getPlaylistFlag();
    }


}
