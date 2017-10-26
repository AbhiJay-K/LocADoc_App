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

public class NewAreaFragment extends Fragment {
    private EditText newAreaName;
    private EditText newAreaDesc;
    private Button btnCreateNewArea;
    private SeekBar radiusSeekBar;
    private EditText radiusText;

    private int radius;
    NewAreaFragmentListener activity;

    public interface NewAreaFragmentListener {
        int createNewArea(Area area);
        Location getLastKnownLoc();
        void hideAreaFragmentContainer();
        void drawCircle(int radius);
    }

    //---empty constructor required
    public NewAreaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_new_area, container, false);

        radius = 5;
        activity = (NewAreaFragmentListener) getActivity();
        newAreaName = (EditText) view.findViewById(R.id.NewAreaName);
        newAreaDesc = (EditText) view.findViewById(R.id.NewAreaDesc);
        btnCreateNewArea = (Button) view.findViewById(R.id.CreateNewAreaBtn);

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
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnCreateNewArea.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                if(radiusText.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "New radius is empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String areaName = newAreaName.getText().toString();
                radius = Integer.parseInt(radiusText.getText().toString());

                if(areaName.trim().length() <= 1) {
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
                Location loc = activity.getLastKnownLoc();
                area.setLatitude("" + loc.getLatitude());
                area.setLongitude("" + loc.getLongitude());

                if(AreaSQLHelper.checkLocExist(area, Credential.getPassword()) > 0){
                    Toast.makeText(getActivity(), "An area is already exist in the same position",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                activity.createNewArea(area);

                resetForm();
                activity.hideAreaFragmentContainer();
            }
        });

        return view;
    }

    public void resetForm(){
        newAreaName.setText("");
        newAreaDesc.setText("");
        radiusText.setText("");
    }
}