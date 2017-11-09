package com.locadoc_app.locadoc.UI.HomePage;

import android.content.DialogInterface;
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

import com.locadoc_app.locadoc.DynamoDB.AreaDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.FileDynamoHelper;
import com.locadoc_app.locadoc.DynamoDB.UserDynamoHelper;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.Credential;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.S3.S3Helper;
import com.locadoc_app.locadoc.helper.Connectivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileOperationsFragment extends DialogFragment {
    private Button btnCopyFile;
    private Button btnMoveFile;
    private Button btnDelete;
    private Button btnBack;
    private Spinner allAreaSpinner;
    private File file;
    private Area fileArea;
    FileOperationDialogListener activity;

    public interface FileOperationDialogListener {
        void removeFile(String filename);
        boolean isInArea(Area area);
        boolean checkGPS();
    }

    //---empty constructor required
    public FileOperationsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view = inflater.inflate(
                R.layout.fragment_file_operations, container);

        Bundle extra = getArguments();
        int fileid = extra.getInt("fileid");
        file = FileSQLHelper.getFile(fileid, Credential.getPassword());
        fileArea = AreaSQLHelper.getRecord(file.getAreaId(), Credential.getPassword());

        TextView title = (TextView) view.findViewById(R.id.FileNameView);
        title.setText(file.getOriginalfilename());

        btnCopyFile = (Button) view.findViewById(R.id.buttonCopy);
        btnMoveFile = (Button) view.findViewById(R.id.buttonMove);
        btnDelete = (Button) view.findViewById(R.id.buttonDelete);
        btnBack = (Button) view.findViewById(R.id.buttonBack);

        Area area = AreaSQLHelper.getRecord(file.getAreaId(), Credential.getPassword());
        allAreaSpinner = (Spinner) view.findViewById(R.id.AreaSpinner);
        List<String> allArea = AreaSQLHelper.getAllOtherRecordName(area.getName(), Credential.getPassword());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, allArea);
        allAreaSpinner.setAdapter(adapter);
        activity = (FileOperationDialogListener) getActivity();

        //---event handler for the button
        btnCopyFile.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                if(!Connectivity.isNetworkAvailable()){
                    Toast.makeText(getActivity(), "Can not connect to internet. Please check your connection!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!activity.checkGPS()){
                    Toast.makeText(getActivity(), "GPS is off, please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!activity.isInArea(fileArea)){
                    Toast.makeText(getActivity(), "You are not within " + fileArea.getName() + "'s radius anymore",
                            Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }

                if(S3Helper.getIsUploading()){
                    Toast.makeText(getActivity(), "Another file is being uploaded, please try again later",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Object obj = allAreaSpinner.getSelectedItem();
                if (obj == null) {
                    Toast.makeText(getActivity(), "No area selected",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String areaname = obj.toString();
                Area area = AreaSQLHelper.getAreaName(areaname, Credential.getPassword());
                String originalFileName = getOriginalName(file.getOriginalfilename(), area.getAreaId());

                int newFileId = FileSQLHelper.maxID() + 1;
                String newFileName = Credential.getEmail() + newFileId;
                java.io.File src = new java.io.File(getActivity().getApplicationContext().getFilesDir().getAbsolutePath() +
                        "/vault/" + file.getCurrentfilename());
                java.io.File dst = new java.io.File(getActivity().getApplicationContext().getFilesDir().getAbsolutePath() +
                        "/vault/" + newFileName);

                User user = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
                long totalSizeUsed = Long.parseLong(user.getTotalsizeused());
                // check size over 1GB
                if(totalSizeUsed + dst.length() > 1000000000){
                    Toast.makeText(getActivity(), "You have exceeded 1GB of your data",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                InputStream in = null;
                OutputStream out = null;

                try{
                    in = new FileInputStream(src);
                    out = new FileOutputStream(dst);

                    byte [] buffer = new byte [8192];
                    int r;
                    while ((r = in.read(buffer)) > 0)
                    {
                        out.write(buffer, 0, r);
                    }

                    file.setFileId(newFileId);
                    file.setOriginalfilename(originalFileName);
                    file.setCurrentfilename(newFileName);
                    file.setAreaId(area.getAreaId());
                    file.setBackedup("false");

                    totalSizeUsed += dst.length();
                    user.setTotalsizeused("" + totalSizeUsed);
                    //activity.changeFileSizeUsed(user.getTotalsizeused());
                    UserSQLHelper.UpdateRecord(user, Credential.getPassword());
                    UserDynamoHelper.getInstance().updateTotalSizeUsed(totalSizeUsed + "");

                    Log.d("LocAdoc", "Total Size: " + totalSizeUsed);

                    Log.d("LocAdoc", "Name: " + file.getOriginalfilename() + ", current: " + file.getCurrentfilename());
                    FileSQLHelper.insert(file, Credential.getPassword());
                    FileDynamoHelper.getInstance().insert(file);
                } catch (Exception e){
                    Log.e("LocAdoc", e.toString());
                }
                finally {
                    try{
                        in.close();
                        out.close();
                    } catch (Exception e){
                        Log.e("LocAdoc", e.toString());
                    }
                }

                S3Helper.setIsUploading(true);
                S3Helper.setCurrFileId(newFileId);
                S3Helper.uploadFile(dst);
                Toast.makeText(getActivity(), originalFileName + " has been copied to " + areaname,
                        Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        btnMoveFile.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                if(!Connectivity.isNetworkAvailable()){
                    Toast.makeText(getActivity(), "Can not connect to internet. Please check your connection!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!activity.checkGPS()){
                    Toast.makeText(getActivity(), "GPS is off, please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!activity.isInArea(fileArea)){
                    Toast.makeText(getActivity(), "You are not within " + fileArea.getName() + "'s radius anymore",
                            Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Object obj = allAreaSpinner.getSelectedItem();
                                if (obj == null) {
                                    Toast.makeText(getActivity(), "No area selected",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String areaname = obj.toString();
                                Area area = AreaSQLHelper.getAreaName(areaname, Credential.getPassword());
                                String originalFileName = getOriginalName(file.getOriginalfilename(), area.getAreaId());
                                file.setOriginalfilename(originalFileName);
                                file.setAreaId(area.getAreaId());

                                FileSQLHelper.updateRecord(file, Credential.getPassword());
                                FileDynamoHelper.getInstance().insert(file);

                                activity.removeFile(file.getOriginalfilename());
                                Toast.makeText(getActivity(), originalFileName + " has been moved to " + areaname,
                                        Toast.LENGTH_SHORT).show();
                                dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure to move " + file.getOriginalfilename() + " (file will be removed in this area)?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                if(!Connectivity.isNetworkAvailable()){
                    Toast.makeText(getActivity(), "Can not connect to internet. Please check your connection!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!activity.checkGPS()){
                    Toast.makeText(getActivity(), "GPS is off, please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!activity.isInArea(fileArea)){
                    Toast.makeText(getActivity(), "You are not within " + fileArea.getName() + "'s radius anymore",
                            Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                java.io.File src = new java.io.File(getActivity().getApplicationContext().getFilesDir().getAbsolutePath() +
                                        "/vault/" + file.getCurrentfilename());

                                User user = UserSQLHelper.getRecord(Credential.getEmail(), Credential.getPassword());
                                long totalSizeUsed = Long.parseLong(user.getTotalsizeused());
                                totalSizeUsed -= Long.parseLong(file.getFilesize());

                                if(totalSizeUsed < 0){
                                    totalSizeUsed = 0;
                                }

                                Log.d("LocAdoc", "Total Size: " + totalSizeUsed);

                                user.setTotalsizeused("" + totalSizeUsed);
                                UserSQLHelper.UpdateRecord(user, Credential.getPassword());
                                //activity.changeFileSizeUsed(user.getTotalsizeused());
                                UserDynamoHelper.getInstance().updateTotalSizeUsed(totalSizeUsed + "");

                                if(src.exists()) {
                                    src.delete();
                                }

                                FileSQLHelper.deleteRecord(file.getFileId());
                                S3Helper.getHelper().removeFile(file.getCurrentfilename());
                                FileDynamoHelper.getInstance().delete(file);

                                activity.removeFile(file.getOriginalfilename());
                                Toast.makeText(getActivity(), file.getOriginalfilename() + " has been deleted",
                                        Toast.LENGTH_SHORT).show();
                                dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure to remove " + file.getOriginalfilename() + "?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                dismiss();
            }
        });

        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    public String getOriginalName (String tempName, int areaid){
        String originalFileName = tempName;
        int result;
        int count = 2;
        do{
            result = FileSQLHelper.checkFileNameInAnAreaExist(originalFileName, areaid,
                    Credential.getPassword());

            if(result > 0){
                originalFileName = tempName.substring(0, tempName.length() - 4) + "(" + count + ").pdf";
                count++;
            }
        } while (result > 0);

        return originalFileName;
    }
}