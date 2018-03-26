package com.a3kings.falldetect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactFragment extends Fragment implements View.OnClickListener {

    private static final String tablename = "EmergencyDetails";  // tablename
    private DBManager dbManager;
    private Context context;
    private SQLiteDatabase db;
    private ContentValues cv;

    private EditText etName1, etEmail1, etMob1, etName2, etEmail2, etMob2;
    private Button save;

    public ContactFragment() {

    }

    View rootView;
    private boolean contact1=false, contact2=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        context = getActivity().getApplicationContext();

        initAll();
        getDataFromDbIfPresent();


        return rootView;
    }

    private void initAll() {

        //Making Database ready for further operations
        dbManager = new DBManager(context);
        db = dbManager.getWritableDatabase();
        cv = new ContentValues();

        //For contact 1
        etName1 = (EditText) rootView.findViewById(R.id.etName1);
        etEmail1 = (EditText) rootView.findViewById(R.id.etEmail1);
        etMob1 = (EditText) rootView.findViewById(R.id.etMob1);
        //For contact 2
        etName2 = (EditText) rootView.findViewById(R.id.etName2);
        etEmail2 = (EditText) rootView.findViewById(R.id.etEmail2);
        etMob2 = (EditText) rootView.findViewById(R.id.etMob2);
        //Save button
        save = (Button) rootView.findViewById(R.id.save);
        save.setOnClickListener(this);
    }

    private String field1="", field2="";
    private void getDataFromDbIfPresent() {
        //If Data is present in database then put it into front end edittext
        try {
            List<HashMap<String, String>> data = dbManager.getAllData();
            //Toast.makeText(context, ""+data, Toast.LENGTH_SHORT).show();
            if (data.size() != 0) {
                try{
                    etName1.setText(data.get(0).get("name"));
                    etEmail1.setText(data.get(0).get("email"));
                    etMob1.setText(data.get(0).get("mobile"));
                    contact1 = true;
                }catch (Exception e){}

                try{
                    etName2.setText(data.get(1).get("name"));
                    etEmail2.setText(data.get(1).get("email"));
                    etMob2.setText(data.get(1).get("mobile"));
                    contact2 = true;
                }catch (Exception e){}

            } else {
                Toast.makeText(context, "No data in database", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            Toast.makeText(context, "Exce:"+ex, Toast.LENGTH_SHORT).show();
        }

    }

    private void insertIntoTable(String name, String email, String mobile){
        cv.put("name", name);
        cv.put("email", email);
        cv.put("mobile", mobile);
        db.insert(tablename, null, cv);
        //db.close();
    }

    private void deleteAllDataForId(String id){
        db.delete(tablename, "id=" + id, null);
    }

    private void updateAllDataForId(String name, String email, String mobile){
        cv.put("name", name);
        cv.put("email", email);
        db.update(tablename, cv, "mobile = '"+mobile+"'" , null);
    }

    @Override
    public void onClick(View view) {
        if(view == save){
            //checkIfData
            if( ( !etName1.getText().toString().equals("") && !etEmail1.getText().toString().equals("") && !etMob1.getText().toString().equals("")) ||
                ( !etName2.getText().toString().equals("") && !etEmail2.getText().toString().equals("") && !etMob2.getText().toString().equals("")) ) {

                if( ( !etName1.getText().toString().equals("") && !etEmail1.getText().toString().equals("") && !etMob1.getText().toString().equals("")) ){

                    //Contact 1 data ok
                    if ( checkMob(etMob1.getText().toString()) != 1 ){
                        Toast.makeText(context, "Invalid Mob No", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ( checkEmail(etEmail1.getText().toString()) != 1 ){
                        Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //check if contact 1 is already present in database
                    if(contact1)  {
                        //If already present then update
                        //Toast.makeText(context, "Updating 1", Toast.LENGTH_SHORT).show();
                        updateAllDataForId(etName1.getText().toString(), etEmail1.getText().toString(), etMob1.getText().toString() );

                    }else{
                        //If not present then insert

                        insertIntoTable(etName1.getText().toString(), etEmail1.getText().toString(), etMob1.getText().toString() );
                        contact1=true;
                    }

                }

                if( ( !etName2.getText().toString().equals("") && !etEmail2.getText().toString().equals("") && !etMob2.getText().toString().equals("")) ){
                    //Contact 2 data ok
                    if ( checkMob(etMob2.getText().toString()) != 1 ){
                        Toast.makeText(context, "Invalid Mob No", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ( checkEmail(etEmail2.getText().toString()) != 1 ){
                        Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //check if contact 2 is already present in database
                    if(contact2)  {
                        //If already present then update
                        //Toast.makeText(context, "Updating 2", Toast.LENGTH_SHORT).show();
                        updateAllDataForId(etName2.getText().toString(), etEmail2.getText().toString(), etMob2.getText().toString() );

                    }else{
                        //If not present then insert

                        insertIntoTable(etName2.getText().toString(), etEmail2.getText().toString(), etMob2.getText().toString() );
                        contact2=true;
                    }

                }

                //All saving done.
                Toast.makeText(context, "saved successfully", Toast.LENGTH_SHORT).show();
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            }else{
                Toast.makeText(context, "all feilds are compulsory", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private static final String MOB_PATTERN = "(0/91)?[7-9][0-9]{9}";
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private int checkMob(String mobNo) {
        Pattern p = Pattern.compile( MOB_PATTERN );
        Matcher m = p.matcher(mobNo);
        if (m.find() && m.group().equals(mobNo)){
            return 1;
        }
        return 0;
    }




    private int checkEmail(String email) {
        Pattern p = Pattern.compile( EMAIL_PATTERN );
        Matcher m = p.matcher(email);
        if (m.find() && m.group().equals(email)){
            return 1;
        }
        return 0;
    }

}