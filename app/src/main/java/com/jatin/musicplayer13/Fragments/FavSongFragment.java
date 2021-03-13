package com.jatin.musicplayer13.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.jatin.musicplayer13.Adapter.SongAdapter;
import com.jatin.musicplayer13.DB.FavoritesOperations;
import com.jatin.musicplayer13.Model.SongsList;
import com.jatin.musicplayer13.R;

import java.util.ArrayList;

public class FavSongFragment extends ListFragment {

    private FavoritesOperations favoritesOperations;


    public ArrayList<SongsList> songsList;
    public ArrayList<SongsList> newList;

    private ListView listView;

    private createDataParsed createDataParsed;

    public static Fragment getInstance(int position) //setting the favouritesong fragment
    {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        FavSongFragment tabFragment = new FavSongFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) //fragment is attached to its context
    {
        super.onAttach(context);
        createDataParsed = (createDataParsed) context;
        favoritesOperations = new FavoritesOperations(context);
    }

    @Override//inflate the tab
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override//setting the playlist view
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        setContent();
    }

    /**
     * Setting the content in the listView and sending the data to the Activity
     */
    public void setContent() {
        boolean searchedList = false;
        songsList = new ArrayList<>();
        newList = new ArrayList<>();
        songsList = favoritesOperations.getAllFavorites();
        SongAdapter adapter = new SongAdapter(getContext(), songsList);
        if (!createDataParsed.queryText().equals("")) //if data is not empty
        {
            adapter = onQueryTextChange();
            adapter.notifyDataSetChanged();
            searchedList = true;
        } else //else return false
            {
            searchedList = false;
        }

        listView.setAdapter(adapter);

        final boolean finalSearchedList = searchedList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                if (!finalSearchedList)//if true than get the data and set also
                {
                    createDataParsed.onDataPass(songsList.get(position).getTitle(), songsList.get(position).getPath());
                    createDataParsed.fullSongList(songsList, position);
                } else //else do the same as if :)
                    {
                    createDataParsed.onDataPass(newList.get(position).getTitle(), newList.get(position).getPath());
                    createDataParsed.fullSongList(songsList, position);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override//if long pressed on a song on favourite song on its fragment the  song can be deleted
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteOption(position);
                return true;
            }
        });
    }

    private void deleteOption(int position)
    {
        if (position != createDataParsed.getPosition())//if  the song position which you want to delete is not equal to the current playing song than delete the song dailog box should appear
            showDialog(songsList.get(position).getPath(), position);
        else//the song  is currently playing so you cant delete it
            Toast.makeText(getContext(), "You Can't delete the Current Song", Toast.LENGTH_SHORT).show();
    }

    public interface createDataParsed {
        public void onDataPass(String name, String path);

        public void fullSongList(ArrayList<SongsList> songList, int position);

        public int getPosition();

        public String queryText();
    }

    public SongAdapter onQueryTextChange()
    {
        String text = createDataParsed.queryText();//convert the text to  lowercase
        for (SongsList songs : songsList) //add songs one by one
        {
            String title = songs.getTitle().toLowerCase();
            if (title.contains(text)) //if there is a title append the song
            {
                newList.add(songs);
            }
        }
        return new SongAdapter(getContext(), newList);

    }

    //creating the dialog box for removing or not removing the favorites song
    private void showDialog(final String index, final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.delete_text))
                .setCancelable(true)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        favoritesOperations.removeSong(index);
                        createDataParsed.fullSongList(songsList, position);
                        setContent();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
