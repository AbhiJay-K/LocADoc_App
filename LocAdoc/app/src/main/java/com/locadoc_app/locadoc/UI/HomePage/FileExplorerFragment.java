package com.locadoc_app.locadoc.UI.HomePage;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.itextpdf.text.List;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.R;

import java.util.Map;

public class FileExplorerFragment extends Fragment
        implements AdapterView.OnItemClickListener {
    public interface FileExplorerFragmentListener{
        void openGoogleMap();
        Location getLastKnownLoc();
        void openFile(int fileid);
    }

    private ListView listView;
    private boolean exploreArea;
    private Map<String, Integer> allAreaAround;
    private Map<String,Integer> allFileInArea;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_file_explorer, container, false);
        exploreArea = true;

        getAllAreaAround();
        String[] strArr = allAreaAround.keySet().toArray(new String[allAreaAround.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, strArr);
        listView = (ListView) rootView.findViewById(R.id.ListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        FloatingActionButton fileExplorerfab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        fileExplorerfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(exploreArea){
                    FileExplorerFragmentListener listener = (FileExplorerFragmentListener) getActivity();
                    listener.openGoogleMap();
                } else{
                    getAllAreaAround();
                    String[] strArr = allAreaAround.keySet().toArray(new String[allAreaAround.size()]);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_list_item_1, strArr);
                    listView.setAdapter(adapter);
                    exploreArea = true;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> args, View v, int position, long id) {
        if(exploreArea) {
            String data = (String) args.getItemAtPosition(position);
            Log.d("LocAdoc", "Item: " + data);
            int areaid = allAreaAround.get(data);
            allFileInArea = FileSQLHelper.getFilesInArea(areaid, Credential.getPassword());
            String[] strArr = allFileInArea.keySet().toArray(new String[allFileInArea.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, strArr);
            listView.setAdapter(adapter);
            exploreArea = false;
        } else{
            String data = (String) args.getItemAtPosition(position);
            int fileid = allFileInArea.get(data);
            FileExplorerFragmentListener listener = (FileExplorerFragmentListener) getActivity();
            listener.openFile(fileid);
        }
    }

    public void getAllAreaAround()
    {
        FileExplorerFragmentListener activity = (FileExplorerFragmentListener) getActivity();
        Location loc = activity.getLastKnownLoc();
        allAreaAround = AreaSQLHelper.getAreaNameInLoc(loc, Credential.getPassword());
    }
}
