package com.locadoc_app.locadoc;

import android.location.Location;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.locadoc_app.locadoc.LocalDB.ApplicationInstance;
import com.locadoc_app.locadoc.LocalDB.AreaSQLHelper;
import com.locadoc_app.locadoc.LocalDB.DBHelper;
import com.locadoc_app.locadoc.LocalDB.FileSQLHelper;
import com.locadoc_app.locadoc.LocalDB.PasswordSQLHelper;
import com.locadoc_app.locadoc.LocalDB.UserSQLHelper;
import com.locadoc_app.locadoc.Model.Area;
import com.locadoc_app.locadoc.Model.File;
import com.locadoc_app.locadoc.Model.Password;
import com.locadoc_app.locadoc.Model.User;
import com.locadoc_app.locadoc.R;
import com.locadoc_app.locadoc.helper.Hash;

import org.junit.*;
import org.junit.runner.RunWith;

import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
@RunWith(AndroidJUnit4.class)
public class TestDataBase extends AppCompatActivity{
    private Map<String,Integer> fileMap;
    private Map<String,Integer> AreaMap;
    @org.junit.Test
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_data_base);
        getApplicationContext().deleteDatabase("LocAdoc_database");
        DBHelper.init(getApplicationContext());
        //long l = PasswordSQLHelper.insert("testPassword","1231231");
        TextView t = (TextView) findViewById(R.id.test);


        //Instance
        Log.d(">Instance","==================Instance===================");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String TimeStamp = simpleDateFormat.format(new Date());
        String random =  UUID.randomUUID().toString();
        Log.d(">Instance random",random);
        Log.d(">Instance date",TimeStamp);
        String Instance = Hash.Hash(TimeStamp,random);
        Log.d(">Instance Instance",Instance);
        ApplicationInstance.insert(Instance);
        String Instance2 = ApplicationInstance.getRecord();
        Log.d(">Instance2 Instance",Instance2);
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String TimeStamp2 = simpleDateFormat2.format(new Date());
        String random2 =  UUID.randomUUID().toString();
        Log.d(">Instance random",random2);
        Log.d(">Instance date",TimeStamp2);
        String Instance3 = Hash.Hash(TimeStamp2,random2);
        ApplicationInstance.updateRecord(Instance3);
        String Instance4 = ApplicationInstance.getRecord();
        Log.d(">Instance4 Instance",Instance4);
        Log.d(">Instance No. Record",String.valueOf(ApplicationInstance.getNumberofRecords()));
        //======================password========================
        Log.d(">password","==================Password===================");
        Password p = new Password();
        p.setPasswordid(1);
        p.setSalt(Hash.SecureRandomGen());
        p.setPassword(Hash.Hash("TestPWD",p.getSalt()));
        PasswordSQLHelper.insert(p);
        Password p2 = PasswordSQLHelper.getRecord(1);
        Log.d(">password ID",Integer.toString(p2.getPasswordid()));
        Log.d(">password pwd",p2.getPassword());
        Log.d(">password salt",p2.getSalt());
        //===========================user=====================
        Log.d(">user","==================User===================");
        User usr = new User();
        usr.setUser("kabhijay@gmail.com");
        usr.setFirstname("Abhi Jay");
        usr.setLastname("Krishnan");
        usr.setPasswordid(p.getPasswordid());
        usr.setAdminareaid(1);
        UserSQLHelper.insert(usr,p);
        User usr2 = UserSQLHelper.getRecord(usr.getUser(),p2);
        Log.d(">User ID",usr2.getUser());
        Log.d(">User First Name",usr2.getFirstname());
        Log.d(">User Last Name",usr2.getLastname());
        Log.d(">User pwd ID",Integer.toString(usr2.getPasswordid()));
        Log.d(">User Area ID",Integer.toString(usr2.getAdminareaid()));

        //============================Area==================================
        Log.d(">Area","==================Area===================");
        Area ar = new Area();
        ar.setAreaId(1);
        ar.setName("SIM");
        ar.setDescription("SIM document");
        ar.setLatitude("1.3296");
        ar.setLongitude("103.7762");
        ar.setRadius("1000");
        AreaSQLHelper.insert(ar,p2);
        Log.d("Area count",String.valueOf(AreaSQLHelper.checkAreaNameExist("SIM",p2)));
        Log.d("Area MAXID",String.valueOf(AreaSQLHelper.maxID()));
        Area ar1 = AreaSQLHelper.getRecord(1,p2);
        Log.d(">Area ID",String.valueOf(ar1.getAreaId()));
        Log.d(">Area Name",ar1.getName());
        Log.d(">Area Description",ar1.getDescription());
        Log.d(">Area Latitude",ar1.getLatitude());
        Log.d(">Area Longitude",ar1.getLongitude());
        Log.d(">Area Radius",ar1.getRadius());
        Area ar5 = new Area();
        ar5.setAreaId(2);
        ar5.setName("NYP");
        ar5.setDescription("NYP Document");
        ar5.setLatitude("1.3329");
        ar5.setLongitude("103.7775");
        ar5.setRadius("1000");
        if(AreaSQLHelper.checkLocExist(ar5,p2) == 0)
        {
            AreaSQLHelper.insert(ar5,p2);
            Area ar4 = AreaSQLHelper.getRecord(ar5.getAreaId(),p2);
            Log.d(">Area ID",String.valueOf(ar4.getAreaId()));
            Log.d(">Area Name",ar4.getName());
            Log.d(">Area Description",ar4.getDescription());
            Log.d(">Area Latitude",ar4.getLatitude());
            Log.d(">Area Longitude",ar4.getLongitude());
            Log.d(">Area Radius",ar4.getRadius());
            Log.d("Area MAXID",String.valueOf(AreaSQLHelper.maxID()));
        }
        else
        {
            Log.d(">Area Error","Area already exist");
        }
        Area ar6 = new Area();
        ar6.setAreaId(3);
        ar6.setName("");
        ar6.setDescription("");
        ar6.setLatitude("1.333498666");
        ar6.setLongitude("103.772830242");
        ar6.setRadius("1000");
        Location l2 = new Location("");
        l2.setLatitude(Double.parseDouble(ar6.getLatitude()));
        l2.setLongitude(Double.parseDouble(ar6.getLongitude()));
        AreaMap = AreaSQLHelper.getAreaNameInLoc(l2,p2);
        if(AreaMap == null)
        {
            Log.d(">Area","No Area found in current location");
        }
        else
        {
            PrintArea();
        }
        Log.d(">File ","========Area List============");
        for(Area a: AreaSQLHelper.getAllRecord(p2))
        {
            Log.d(">Area ID",String.valueOf(a.getAreaId()));
            Log.d(">Area Name",a.getName());
            Log.d(">Area Description",a.getDescription());
            Log.d(">Area Latitude",a.getLatitude());
            Log.d(">Area Longitude",a.getLongitude());
            Log.d(">Area Radius",a.getRadius());
        }
        //===========================File======================================
        Log.d(">File","==================File===================");
        File file1 = new File();
        file1.setAreaId(ar1.getAreaId());
        file1.setFileId(1);
        file1.setCurrentfilename(usr.getUser()+"_"+file1.getFileId());
        file1.setOriginalfilename("something.pdf");
        file1.setPasswordId(p.getPasswordid());
        //file1.setModified("0");
        FileSQLHelper.insert(file1,p2);
        Log.d("File MAXID",String.valueOf(FileSQLHelper.maxID()));
        File file3 = new File();
        file3.setAreaId(ar1.getAreaId());
        file3.setFileId(2);
        file3.setCurrentfilename(usr.getUser()+"_"+file1.getFileId());
        file3.setOriginalfilename("Hello.pdf");
        file3.setPasswordId(p2.getPasswordid());
        //file3.setModified("0");
        FileSQLHelper.insert(file3,p2);
        Log.d("File MAXID",String.valueOf(FileSQLHelper.maxID()));
        File file4 = new File();
        file4.setAreaId(ar1.getAreaId());
        file4.setFileId(3);
        file4.setCurrentfilename(usr.getUser()+"_"+file1.getFileId());
        file4.setOriginalfilename("fyp.pdf");
        file4.setPasswordId(p2.getPasswordid());
        //file4.setModified("0");
        FileSQLHelper.insert(file4,p);
        Log.d("File MAXID",String.valueOf(FileSQLHelper.maxID()));
        Log.d("File count",String.valueOf(FileSQLHelper.checkFileNameExist("fyp.pdf",p2)));
        Log.d(">File ","========Search Area============");
        fileMap = FileSQLHelper.getFilesInArea(ar1.getAreaId(),p2);
        if(fileMap == null)
        {
            Log.d(">File","No file found in this area");
        }
        else
        {
            PrintFile();
        }

        File file2 = FileSQLHelper.getFile(GetKey(file3.getOriginalfilename()),p2);
        Log.d(">File ","========Search By Name============");
        Log.d(">File ID",String.valueOf(file2.getFileId()));
        Log.d(">File CUR Name",file2.getCurrentfilename());
        Log.d(">File ORG Name",file2.getOriginalfilename());
        //Log.d(">File Modified",file2.getModified());
        Log.d(">File PasswordID",String.valueOf(file2.getPasswordId()));
        Log.d(">File AreaID",String.valueOf(file2.getAreaId()));
        //====================================================
        //============================Area2==================================
        Log.d(">Area2","==================Area2===================");
        Area ar2 = new Area();
        ar.setAreaId(2);
        ar.setLatitude("1.3521");
        ar.setLongitude("103.8198");
        ar.setRadius("20");
        AreaSQLHelper.insert(ar2,p2);
        Area ar12 = AreaSQLHelper.getRecord(2,p2);
        Log.d(">Area 2 ID",String.valueOf(ar12.getAreaId()));
        Log.d(">Area 2 Latitude",ar12.getLatitude());
        Log.d(">Area 2 Longitude",ar12.getLongitude());
        Log.d(">Area 2 Radius",ar12.getRadius());

        //Transfer file 4 to area 2
        file4.setAreaId(2);
        FileSQLHelper.updateRecord(file4,p2);
        fileMap = FileSQLHelper.getFilesInArea(ar12.getAreaId(),p2);
        if(fileMap == null)
        {
            Log.d(">File","No file found in this area");
        }
        else
        {
            PrintFile();
        }
        File file5 = FileSQLHelper.getFile(GetKey(file4.getOriginalfilename()),p2);
        Log.d(">File ","========Search By Name============");
        Log.d(">File ID",String.valueOf(file5.getFileId()));
        Log.d(">File CUR Name",file5.getCurrentfilename());
        Log.d(">File ORG Name",file5.getOriginalfilename());
        //Log.d(">File Modified",file5.getModified());
        Log.d(">File PasswordID",String.valueOf(file5.getPasswordId()));
        Log.d(">File AreaID",String.valueOf(file5.getAreaId()));

        int n = PasswordSQLHelper.DeleteRecord(p2.getPasswordid());
        Log.d(">Delete PWD",String.valueOf(n));
        PasswordSQLHelper.DropTable();
        Log.d(">Delete Area",String.valueOf(AreaSQLHelper.deleteRecord(ar12.getAreaId())));

    }
    public int GetKey(String filename)
    {
        int key = fileMap.get(filename);
        return key;
    }
    public void PrintFile()
    {
        for (Map.Entry<String, Integer> entry : fileMap.entrySet())
        {
            Log.d("Print File ",entry.getKey() + "/" + entry.getValue());
        }
    }
    public void PrintArea()
    {
        for (Map.Entry<String, Integer> entry : AreaMap.entrySet())
        {
            Log.d("Print Area ",entry.getKey() + "/" + entry.getValue());
        }
    }
}
