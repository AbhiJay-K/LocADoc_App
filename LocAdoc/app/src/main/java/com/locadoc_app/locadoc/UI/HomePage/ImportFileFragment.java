package com.locadoc_app.locadoc.UI.HomePage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.helper.Connectivity;

import java.util.Map;

public class ImportFileFragment extends Fragment {
    private EditText newAreaName;
    private EditText newAreaDesc;
    private Button btnCreateNewArea;
    private Button btnSelectExistingArea;
    private Button btnSelectFile;
    private TextView fileNameText;
    private SeekBar radiusSeekBar;
    private EditText radiusText;
    private Spinner existingArea;
    private boolean noArea;
    private int radius;
    private Map<String, Integer> allAreaAround;
    ImportFileFragmentListener listener;

    public interface ImportFileFragmentListener {
        int createNewArea(Area area);
        void saveFile(String filename, int areaid);
        Location getLastKnownLoc();
        void performFileSearch();
        void drawCircle(int radius);
        void hideAreaFragmentContainer();
        boolean isInArea(Area area);
        boolean checkGPS();
    }

    //---empty constructor required
    public ImportFileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_import_file, container, false);
        radius = 5;
        listener = (ImportFileFragmentListener) getActivity();
        fileNameText = (TextView) view.findViewById(R.id.NewFileName);
        newAreaName = (EditText) view.findViewById(R.id.NewAreaName);
        newAreaDesc = (EditText) view.findViewById(R.id.NewAreaDesc);
        btnCreateNewArea = (Button) view.findViewById(R.id.CreateNewAreaBtn);
        btnSelectExistingArea = (Button) view.findViewById(R.id.ExistingAreaBtn);
        btnSelectFile = (Button) view.findViewById(R.id.SelectFileButton);
        noArea = false;
        radiusText = (EditText) view.findViewById(R.id.RadiusEditText);
        radiusText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String s = radiusText.getText().toString();
                int rad = 0;

                if (!s.isEmpty()){
                    rad = Integer.parseInt(s);
                }

                if(rad < 5){
                    radiusText.setText("5");
                } else if(rad > 1000){
                    radiusText.setText("1000");
                }

                radius = Integer.parseInt(radiusText.getText().toString());
                listener.drawCircle(radius);
            }
        });

        radiusSeekBar = (SeekBar) view.findViewById(R.id.RadiusSeekBar);
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                radius = progressValue + 5;
                radiusText.setText(radius + "");
                listener.drawCircle(radius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //radiusText.setText(radius + "");
            }
        });

        existingArea = (Spinner) view.findViewById(R.id.ExistingArea);

        //---event handler for the button
        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                listener.performFileSearch();
            }
        });

        btnCreateNewArea.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                if(!Connectivity.isNetworkAvailable()){
                    Toast.makeText(getActivity(), "Can not connect to internet. Please check your connection!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!listener.checkGPS()){
                    Toast.makeText(getActivity(), "GPS is off, please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(radiusText.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "New radius is empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String areaName = newAreaName.getText().toString();
                String filename = fileNameText.getText().toString();
                radius = Integer.parseInt(radiusText.getText().toString());

                if(filename.isEmpty()){
                    Toast.makeText(getActivity(), "No file selected",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(areaName.trim().length() <= 1) {
                    Toast.makeText(getActivity(), "Area name must at least contain 2 characters",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (AreaSQLHelper.checkAreaNameExist(areaName, Credential.getPassword()) > 0){
                    Toast.makeText(getActivity(), "Area name already exist",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if(radius < 5 || radius > 1000){
                    Toast.makeText(getActivity(), "Radius has to be between 5 to 1000",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String areaDesc = newAreaDesc.getText().toString();
                //---gets the calling activity
                Area area = new Area();
                area.setName(areaName);
                area.setDescription(areaDesc);
                area.setRadius(radius + "");
                Location loc = listener.getLastKnownLoc();
                area.setLatitude("" + loc.getLatitude());
                area.setLongitude("" + loc.getLongitude());

                if(AreaSQLHelper.checkLocExist(area, Credential.getPassword()) > 0){
                    Toast.makeText(getActivity(), "An area is already exist in the same position",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                int id = listener.createNewArea(area);
                listener.saveFile(filename, id);

                resetForm();
                listener.hideAreaFragmentContainer();
            }
        });

        btnSelectExistingArea.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                if(!Connectivity.isNetworkAvailable()){
                    Toast.makeText(getActivity(), "Can not connect to internet. Please check your connection!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!listener.checkGPS()){
                    Toast.makeText(getActivity(), "GPS is off, please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                Object obj = existingArea.getSelectedItem();
                String filename = fileNameText.getText().toString();
                int areaid = allAreaAround.get(obj.toString());
                Area area = AreaSQLHelper.getRecord(areaid, Credential.getPassword());

                if(!listener.isInArea(area)) {
                    Toast.makeText(getActivity(), "You are not within " + area.getName() + "'s radius anymore",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if(filename.isEmpty()){
                    Toast.makeText(getActivity(), "No file selected",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (obj == null) {
                    Toast.makeText(getActivity(), "No area selected",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String tempName = filename;
                int result;
                int count = 2;
                do{
                    result = FileSQLHelper.checkFileNameInAnAreaExist(filename, areaid, Credential.getPassword());

                    if(result > 0){
                        filename = tempName.substring(0, tempName.length() - 4) + "(" + count + ").pdf";;
                        count++;
                    }
                } while (result > 0);

                //---gets the calling activity
                listener.saveFile(filename, areaid);

                resetForm();
                listener.hideAreaFragmentContainer();
            }
        });

        updateAreaAround();
        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
    }

    public void getAllAreaAround(Location loc)
    {
        allAreaAround = AreaSQLHelper.getAreaNameInLoc(loc, Credential.getPassword());
    }

    public void updateAreaAround(){
        Location loc = listener.getLastKnownLoc();
        if(loc == null || getActivity() == null){
            return ;
        }

        getAllAreaAround(loc);
        if(allAreaAround.size() == 0)
        {
            existingArea.setEnabled(false);
            btnSelectExistingArea.setEnabled(false);
            Toast.makeText(getActivity(), "Hmm.. there no area available currently",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            existingArea.setEnabled(true);
            btnSelectExistingArea.setEnabled(true);
        }
        String[] strArr = allAreaAround.keySet().toArray(new String[allAreaAround.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinnerlayout, strArr);
        existingArea.setAdapter(adapter);
    }

    public void setFileName(String fileName){
        fileNameText.setText(fileName);
    }

    public void resetForm(){
        newAreaName.setText("");
        newAreaDesc.setText("");
        fileNameText.setText("");
        radiusText.setText("");
    }
}