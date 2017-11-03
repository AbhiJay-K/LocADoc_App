package com.locadoc_app.locadoc.UI.HomePage;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.google.android.gms.maps.model.LatLng;
import com.locadoc_app.locadoc.DynamoDB.AreaDynamoHelper;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.helper.Connectivity;

import org.w3c.dom.Text;

import java.util.Map;

public class EditAreaFragment extends Fragment {
    private TextView title;
    private Button btnEditRadius;
    private Button btnDeleteArea;
    private SeekBar radiusSeekBar;
    private EditText radiusText;

    private Area area;
    private int radius;
    EditAreaFragmentListener activity;

    public interface EditAreaFragmentListener {
        void hideAreaFragmentContainer();
        void drawCircle(LatLng latLng, int radius);
        boolean isInArea(Area a);
        void removeLastClickedMarker();
        void removeAreaFromList(String areaName);
    }

    //---empty constructor required
    public EditAreaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.fragment_edit_area, container, false);

        activity = (EditAreaFragmentListener) getActivity();
        btnDeleteArea = (Button) view.findViewById(R.id.DeleteAreaBtn);
        btnEditRadius = (Button) view.findViewById(R.id.EditRadiusBtn);
        title = (TextView) view.findViewById(R.id.EditAreaTitle);

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
                LatLng latLng = new LatLng(Double.parseDouble(area.getLatitude()), Double.parseDouble(area.getLongitude()));
                activity.drawCircle(latLng, radius);
            }
        });

        Bundle arguments = getArguments();
        String areaName = arguments.getString("areaname");
        init(areaName);

        radiusSeekBar = (SeekBar) view.findViewById(R.id.RadiusSeekBar);
        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                radius = progressValue + 5;
                radiusText.setText(radius + "");
                LatLng latLng = new LatLng(Double.parseDouble(area.getLatitude()), Double.parseDouble(area.getLongitude()));
                activity.drawCircle(latLng, radius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnEditRadius.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                if(!Connectivity.isNetworkAvailable()){
                    Toast.makeText(getActivity(), "Can not connect to internet. Please check your connection!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(activity.isInArea(area)) {
                    if(radiusText.getText().toString().isEmpty()){
                        Toast.makeText(getActivity(), "New radius is empty",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    radius = Integer.parseInt(radiusText.getText().toString());
                    area.setRadius(radius + "");
                    LatLng latLng = new LatLng(Double.parseDouble(area.getLatitude()), Double.parseDouble(area.getLongitude()));
                    activity.drawCircle(latLng, Integer.parseInt(area.getRadius()));
                    AreaSQLHelper.updateRecord(area, Credential.getPassword());
                    AreaDynamoHelper.getInstance().insert(area);
                    Toast.makeText(getActivity(), area.getName() + "'s radius updated",
                            Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getActivity(), "You are not within " + area.getName() + "'s radius",
                            Toast.LENGTH_SHORT).show();
                }

                activity.hideAreaFragmentContainer();
            }
        });

        btnDeleteArea.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                if(!Connectivity.isNetworkAvailable()){
                    Toast.makeText(getActivity(), "Can not connect to internet. Please check your connection!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(activity.isInArea(area)){
                    if(!FileSQLHelper.checkFilesInAreaExist(area.getAreaId(), Credential.getPassword())) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        activity.removeAreaFromList(area.getName());
                                        AreaSQLHelper.deleteRecord(area.getAreaId());
                                        AreaDynamoHelper.getInstance().delete(area);
                                        Toast.makeText(getActivity(), area.getName() + " has been successfully deleted",
                                                Toast.LENGTH_SHORT).show();
                                        activity.removeLastClickedMarker();
                                        activity.hideAreaFragmentContainer();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure to remove " + area.getName() + "?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    } else{
                        Toast.makeText(getActivity(), area.getName() + " still contain file inside",
                                Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(getActivity(), "You are not within " + area.getName() + "'s radius",
                            Toast.LENGTH_SHORT).show();
                    activity.hideAreaFragmentContainer();
                }
            }
        });

        return view;
    }

    public void init(String areaName){
         if (title != null){
             area = AreaSQLHelper.getAreaName(areaName, Credential.getPassword());
             title.setText(areaName);
             radiusText.setText(area.getRadius());
         }
    }

    public Area getSelectedArea(){
        return area;
    }
}