package com.jatin.musicplayer13.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jatin.musicplayer13.Model.SongsList;
import com.jatin.musicplayer13.R;

import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<SongsList> implements Filterable{

    private Context mContext;
    private ArrayList<SongsList> songList = new ArrayList<>();//creating an emptylist of array with initial size as 10 bydefault

    public SongAdapter(Context mContext, ArrayList<SongsList> songList)
    {
        super(mContext, 0, songList);
        this.mContext = mContext;
        this.songList = songList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;//it will reuse the old view of listitem
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.playlist_items, parent, false);//1) When attachToRoot = false it means. Dont attach the child view to parent "Right Now", Add it later. 2) When attachToRoot = true it means. Attach the childView to parent "Right Now". In both cases the child view will be added to parentView eventually.
        }
        SongsList currentSong = songList.get(position);//get the position of song
        TextView tvTitle = listItem.findViewById(R.id.tv_music_name);//find the main title view
        TextView tvSubtitle = listItem.findViewById(R.id.tv_music_subtitle);//fin the subtitle view
        tvTitle.setText(currentSong.getTitle());//set the maintitle text of currrent song
        tvSubtitle.setText(currentSong.getSubTitle());//set the subtitle text of current song
        return listItem;//now return the updated
    }
}
