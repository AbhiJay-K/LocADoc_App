package com.locadoc_app.locadoc.UI.HomePage;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectAreaFragment extends DialogFragment {
    private EditText newAreaName;
    private EditText newAreaDesc;
    private Button btnCreateNewArea;
    private Button btnSelectExistingArea;
    private Button btnCancel;
    private Spinner radiusSpinner;
    private Spinner existingArea;

    private String filename;
    private Map<String, Integer> allAreaAround;
    private final static String[] RADIUS_LIST = new String[]{"20m", "50m", "100m", "200m", "500m", "1000m"};

    public interface SelectAreaDialogListener {
        int createNewArea(String filename, Area area);
        void saveFile(String filename, int areaid);
        Location getLastKnownLoc();
    }

    //---empty constructor required
    public SelectAreaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(
                R.layout.fragment_select_area, container);

        Bundle extra = getArguments();
        filename = extra.getString("filename");
        TextView title = (TextView) view.findViewById(R.id.SelectAreaTitle);
        title.setText(filename);

        newAreaName = (EditText) view.findViewById(R.id.NewAreaName);
        newAreaDesc = (EditText) view.findViewById(R.id.NewAreaDesc);
        btnCreateNewArea = (Button) view.findViewById(R.id.CreateNewAreaBtn);
        btnSelectExistingArea = (Button) view.findViewById(R.id.ExistingAreaBtn);
        btnCancel = (Button) view.findViewById(R.id.CancelBtn);

        radiusSpinner = (Spinner) view.findViewById(R.id.Radius);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, RADIUS_LIST);
        radiusSpinner.setAdapter(adapter2);

        existingArea = (Spinner) view.findViewById(R.id.ExistingArea);
        getAllAreaAround();
        String[] strArr = allAreaAround.keySet().toArray(new String[allAreaAround.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, strArr);
        existingArea.setAdapter(adapter);

        //---event handler for the button
        btnCreateNewArea.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                String areaName = newAreaName.getText().toString();

                if(areaName.trim().length() <= 0) {
                    Toast.makeText(getActivity(), "Area name is empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (AreaSQLHelper.checkAreaNameExist(areaName, Credential.getPassword()) > 0){
                    Toast.makeText(getActivity(), "Area name already exist",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String areaDesc = newAreaDesc.getText().toString();
                String radius = radiusSpinner.getSelectedItem().toString();
                radius = radius.substring(0, radius.length() - 1);
                //---gets the calling activity
                SelectAreaDialogListener activity = (SelectAreaDialogListener) getActivity();
                Area area = new Area();
                area.setName(areaName);
                area.setDescription(areaDesc);
                area.setRadius(radius);
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

                //---dismiss the alert
                dismiss();
            }
        });

        btnSelectExistingArea.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                Object obj = existingArea.getSelectedItem();
                if (obj == null) {
                    Toast.makeText(getActivity(), "No area selected",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //---gets the calling activity
                SelectAreaDialogListener activity = (SelectAreaDialogListener) getActivity();
                activity.saveFile(filename, allAreaAround.get(obj.toString()));
                //---dismiss the alert
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                //---dismiss the alert
                dismiss();
            }
        });

        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    public void getAllAreaAround()
    {
        SelectAreaDialogListener activity = (SelectAreaDialogListener) getActivity();
        Location loc = activity.getLastKnownLoc();
        allAreaAround = AreaSQLHelper.getAreaNameInLoc(loc, Credential.getPassword());
    }
}