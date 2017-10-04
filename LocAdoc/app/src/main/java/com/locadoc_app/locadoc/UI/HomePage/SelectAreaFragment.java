package com.locadoc_app.locadoc.UI.HomePage;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
    EditText newAreaName;
    EditText newAreaDesc;
    Button btnCreateNewArea;
    Button btnSelectExistingArea;
    Button btnCancel;
    Spinner radiusSpinner;
    Spinner existingArea;
    String filename;
    Map<String, Integer> allAreaAround;
    private final static String[] RADIUS_LIST = new String[]{"100m", "200m", "500m", "1km", "2km", "10km"};

    public interface SelectAreaDialogListener {
        void createNewArea(String filename, Area area);
        void saveFile(String filename, String areaName);
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
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, RADIUS_LIST);
        radiusSpinner.setAdapter(adapter2);

        existingArea = (Spinner) view.findViewById(R.id.ExistingArea);
        allAreaAround = new HashMap<String, Integer>();
        getAllAreaAround();
        String[] strArr = allAreaAround.keySet().toArray(new String[allAreaAround.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, strArr);
        existingArea.setAdapter(adapter);



        //---event handler for the button
        btnCreateNewArea.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                String areaName = newAreaName.getText().toString();

                if(areaName.isEmpty()) {
                    Toast.makeText(getActivity(), "Area name is empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (checkAreaExist(areaName)){
                    Toast.makeText(getActivity(), "Area name already exist",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String areaDesc = newAreaDesc.getText().toString();
                String radius = radiusSpinner.getSelectedItem().toString();
                //---gets the calling activity
                SelectAreaDialogListener activity = (SelectAreaDialogListener) getActivity();
                Area area = new Area();
                area.setName(areaName);
                area.setDescription(areaDesc);
                area.setRadius(radius);
                activity.createNewArea(filename, area);

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
                activity.saveFile(filename, obj.toString());
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

        //---set the title for the dialog
        //getDialog().setTitle("Select Area");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    public boolean checkAreaExist (String areaName)
    {
        boolean isExist = false;
        // check in database

        return isExist;
    }

    public void getAllAreaAround()
    {
        SelectAreaDialogListener activity = (SelectAreaDialogListener) getActivity();
        Location loc = activity.getLastKnownLoc();
        allAreaAround = AreaSQLHelper.getAreaNameInLoc(loc, Credential.getPassword());
    }
}