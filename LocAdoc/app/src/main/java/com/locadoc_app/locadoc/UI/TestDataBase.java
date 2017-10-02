package com.locadoc_app.locadoc.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TestDataBase extends AppCompatActivity {
    private Map<Integer,String> fileMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_data_base);
        getApplicationContext().deleteDatabase("LocAdoc_database");
        DBHelper.init(getApplicationContext());
        //long l = PasswordSQLHelper.insert("testPassword","1231231");
        TextView t = (TextView) findViewById(R.id.test);

        //======================password========================
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
        Area ar = new Area();
        ar.setAreaId(1);
        ar.setName("office");
        ar.setDescription("office document");
        ar.setLatitude("1.3296");
        ar.setLongitude("103.7762");
        ar.setRadius("10");
        AreaSQLHelper.insert(ar,p2);
        Area ar1 = AreaSQLHelper.getRecord(1,p2);
        Log.d(">Area ID",String.valueOf(ar1.getAreaId()));
        Log.d(">Area Name",ar1.getName());
        Log.d(">Area Description",ar1.getDescription());
        Log.d(">Area Latitude",ar1.getLatitude());
        Log.d(">Area Longitude",ar1.getLongitude());
        Log.d(">Area Radius",ar1.getRadius());

        //===========================File======================================
        File file1 = new File();
        file1.setAreaId(ar1.getAreaId());
        file1.setFileId(1);
        file1.setCurrentfilename(usr.getUser()+"_"+file1.getFileId());
        file1.setOriginalfilename("something.pdf");
        file1.setPasswordId(p.getPasswordid());
        file1.setModified("0");
        FileSQLHelper.insert(file1,p2);
        File file3 = new File();
        file3.setAreaId(ar1.getAreaId());
        file3.setFileId(2);
        file3.setCurrentfilename(usr.getUser()+"_"+file1.getFileId());
        file3.setOriginalfilename("Hello.pdf");
        file3.setPasswordId(p2.getPasswordid());
        file3.setModified("0");
        FileSQLHelper.insert(file3,p2);
        File file4 = new File();
        file4.setAreaId(ar1.getAreaId());
        file4.setFileId(3);
        file4.setCurrentfilename(usr.getUser()+"_"+file1.getFileId());
        file4.setOriginalfilename("fyp.pdf");
        file4.setPasswordId(p2.getPasswordid());
        file4.setModified("0");
        FileSQLHelper.insert(file4,p);
        Log.d(">File ","========Search Area============");
        fileMap = FileSQLHelper.getFilesInArea(ar1.getAreaId(),p2);
        if(fileMap == null)
        {
            Log.d(">File","No file found in this area");
        }
        else
        {
            Print();
        }
        File file2 = FileSQLHelper.getFile(GetKey(file3.getOriginalfilename()),p2);
        Log.d(">File ","========Search By Name============");
        Log.d(">File ID",String.valueOf(file2.getFileId()));
        Log.d(">File CUR Name",file2.getCurrentfilename());
        Log.d(">File ORG Name",file2.getOriginalfilename());
        Log.d(">File Modified",file2.getModified());
        Log.d(">File PasswordID",String.valueOf(file2.getPasswordId()));
        Log.d(">File AreaID",String.valueOf(file2.getAreaId()));
        //====================================================
        //============================Area2==================================
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
            Print();
        }
        File file5 = FileSQLHelper.getFile(GetKey(file4.getOriginalfilename()),p2);
        Log.d(">File ","========Search By Name============");
        Log.d(">File ID",String.valueOf(file5.getFileId()));
        Log.d(">File CUR Name",file5.getCurrentfilename());
        Log.d(">File ORG Name",file5.getOriginalfilename());
        Log.d(">File Modified",file5.getModified());
        Log.d(">File PasswordID",String.valueOf(file5.getPasswordId()));
        Log.d(">File AreaID",String.valueOf(file5.getAreaId()));

        int n = PasswordSQLHelper.DeleteRecord(p2.getPasswordid());
        Log.d(">Delete PWD",String.valueOf(n));
        PasswordSQLHelper.DropTable();
        Log.d(">Delete Area",String.valueOf(AreaSQLHelper.deleteRecord(ar12.getAreaId())));








        //======================password2========================
        /*Password p2 = new Password();
        p.setPassword("NewPWD123");
        long l3 = PasswordSQLHelper.insert(p);
        Log.d("Password added ",Long.toString(l));
        Password pwd3 = PasswordSQLHelper.getRecord(2);
        Log.d("testing passwordID",Integer.toString(pwd3.getPasswordid()));
        Log.d("testing password",pwd3.getPassword());
        Log.d("testing password",pwd3.getSalt());
        User s3 = UserSQLHelper.getRecord("kabhijay@gmail.com");
        s3.setPasswordid(pwd3.getPasswordid());
        long l4 = UserSQLHelper.UpdateRecord(s3);
        Log.d(">Password changed",s2.getUser());
        Log.d(">Password changed",s2.getFirstname());
        Log.d(">Password changed",s2.getLastname());
        Log.d(">Password changed",s2.getLoggedin());
        Log.d(">Password changed",s2.getMacaddress());
        Log.d(">Password changed",Integer.toString(s2.getAdminareaid()));
        Log.d(">Password changed",Integer.toString(s2.getPasswordid()));
        Log.d(">Password changedC ",Long.toString(l));
        Log.d(">PasswordTable Count ",Long.toString(PasswordSQLHelper.getNumberofRecords()));
        Log.d(">UserTable Count ",Long.toString(UserSQLHelper.getNumberofRecords()));*/

    }
    public int GetKey(String filename)
    {
        Set<Integer> setCodes = fileMap.keySet();
        Iterator<Integer> iterator = setCodes.iterator();
        int key = -1;
        while (iterator.hasNext()) {
            key = iterator.next();
            String value = fileMap.get(key);
            if(value.equals(filename))
            {
                return key;
            }
        }
        return key;
    }
    public void Print()
    {
        Set<Integer> setCodes = fileMap.keySet();
        Iterator<Integer> iterator = setCodes.iterator();
        int key = -1;
        while (iterator.hasNext()) {
            key = iterator.next();
            String value = fileMap.get(key);
            Log.d(">File Name",value);
        }
    }
}
