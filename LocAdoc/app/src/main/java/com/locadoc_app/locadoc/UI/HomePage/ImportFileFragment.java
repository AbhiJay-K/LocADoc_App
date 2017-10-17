package com.locadoc_app.locadoc.UI.HomePage;

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
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.R;

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

    private int radius;
    private Map<String, Integer> allAreaAround;
    ImportFileFragmentListener activity;

    public interface ImportFileFragmentListener {
        int createNewArea(String filename, Area area);
        void saveFile(String filename, int areaid);
        Location getLastKnownLoc();
        void performFileSearch();
        void hideImportFileFragment();
        void drawCircle(int radius);
    }

    //---empty constructor required
    public ImportFileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_import_file, container, false);

        radius = 5;
        activity = (ImportFileFragmentListener) getActivity();
        fileNameText = (TextView) view.findViewById(R.id.NewFileName);
        newAreaName = (EditText) view.findViewById(R.id.NewAreaName);
        newAreaDesc = (EditText) view.findViewById(R.id.NewAreaDesc);
        btnCreateNewArea = (Button) view.findViewById(R.id.CreateNewAreaBtn);
        btnSelectExistingArea = (Button) view.findViewById(R.id.ExistingAreaBtn);
        btnSelectFile = (Button) view.findViewById(R.id.SelectFileButton);

        FloatingActionButton closeFAB = (FloatingActionButton) view.findViewById(R.id.CloseImportFileFAB);
        closeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.hideImportFileFragment();
            }
        });

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
            }
        });

        radiusSeekBar = (SeekBar) view.findViewById(R.id.RadiusSeekBar);
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                radius = progressValue + 5;
                radiusText.setText(radius + "");
                activity.drawCircle(radius);
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
                activity.performFileSearch();
            }
        });

        btnCreateNewArea.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                String areaName = newAreaName.getText().toString();
                String filename = fileNameText.getText().toString();

                if(filename.isEmpty()){
                    Toast.makeText(getActivity(), "No file selected",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(areaName.trim().length() <= 0) {
                    Toast.makeText(getActivity(), "Area name is empty",
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
                Location loc = activity.getLastKnownLoc();
                area.setLatitude("" + loc.getLatitude());
                area.setLongitude("" + loc.getLongitude());

                if(AreaSQLHelper.checkLocExist(area, Credential.getPassword()) > 0){
                    Toast.makeText(getActivity(), "An area is already exist in the same position",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                int id = activity.createNewArea(filename, area);
                activity.saveFile(filename, id);

                resetForm();
                activity.hideImportFileFragment();
            }
        });

        btnSelectExistingArea.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                Object obj = existingArea.getSelectedItem();
                String filename = fileNameText.getText().toString();

                if(filename.isEmpty()){
                    Toast.makeText(getActivity(), "No file selected",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (obj == null) {
                    Toast.makeText(getActivity(), "No area selected",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //---gets the calling activity
                activity.saveFile(filename, allAreaAround.get(obj.toString()));

                resetForm();
                activity.hideImportFileFragment();
            }
        });

        return view;
    }

    public void getAllAreaAround()
    {
        Location loc = activity.getLastKnownLoc();
        allAreaAround = AreaSQLHelper.getAreaNameInLoc(loc, Credential.getPassword());
    }

    public void updateAreaAround(){
        getAllAreaAround();
        String[] strArr = allAreaAround.keySet().toArray(new String[allAreaAround.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, strArr);
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