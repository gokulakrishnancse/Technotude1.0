package tech.info.sasurie.sociall;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//import static aptitude.sasurie.leisurebreaker.LoginActivity.MyPREFERENCES;
//import static tech.info.sasurie.sociall.Les_LoginActivity.MyPREFERENCES;

public class TestViewActivity extends AppCompatActivity implements ListView.OnItemClickListener{

    private ListView listView;
    private  Button button1;
    private String JSON_STRING,testname,testdate3;
    String questions;
    String regno;
    SharedPreferences sharedpreferences;
    private AdView mAdView;
    private DatabaseReference TestTopicRef,TotalTestTopicRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private TextView teststatus;

    private float time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view);
        button1 = findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(TestViewActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }
        });

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        TestTopicRef= FirebaseDatabase.getInstance().getReference().child("Mark_Details");
        TotalTestTopicRef=FirebaseDatabase.getInstance().getReference().child("Total_Test_Topic");




       /* sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        MobileAds.initialize(this,
                "ca-app-pub-2052757055681240~3637998856");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder() .addTestDevice("F304B865CE4B7E4100378ED91BA6FB17").build();
        mAdView.loadAd(adRequest);

        if(sharedpreferences.contains(Config.KEY_REGNO) && sharedpreferences.contains(Config.KEY_PASSWORD)){

        }else {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            if(sharedpreferences.contains(Config.KEY_REGNO) && sharedpreferences.contains(Config.KEY_PASSWORD)){

                Intent loginIntent = new Intent(Les_TestViewActivity.this,Les_LoginActivity.class);
                startActivity(loginIntent);

                finish();   //finish current activity
            }

        }*/
        Intent intent = getIntent();


        regno = intent.getStringExtra(Config.KEY_REGNO);
        listView = (ListView) findViewById(R.id.list_test);
        listView.setOnItemClickListener(this);
        getJSON();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_favorite).setVisible(false);
        return true;
    }

    private void showEmployee(){

        final String fsetteststatus = null;
        TotalTestTopicRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                JSONObject jsonObject = null;
                ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
                try {
                    jsonObject = new JSONObject(JSON_STRING);
                    JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

                    for(int i = 0; i<result.length(); i++) {
                        JSONObject jo = result.getJSONObject(i);
                        String testid = jo.getString(Config.TAG_TESTID);
                        testname = jo.getString(Config.TAG_TEST_NAME);
                        questions = jo.getString(Config.TAG_QUESTIONS);
                        String marks = jo.getString(Config.TAG_MARKS);
                        testdate3=jo.getString(Config.TEST_DATE);




                        String temptestmark = dataSnapshot.child(testname).child("mark").getValue().toString();
                        Log.d("testname=", testname);
                        Log.d("testmark=", temptestmark);


                        teststatus = findViewById(R.id.teststaus);
                        if (temptestmark.equals("-1"))
                        {

                            String setteststatus ="!";

                            HashMap<String,String> students = new HashMap<>();
                            students.put(Config.TAG_TESTID,testid);
                            students.put(Config.TAG_TEST_NAME,testname);
                            students.put(Config.TAG_QUESTIONS,"Ques : "+questions);
                            students.put(Config.TAG_MARKS,"Marks : "+marks);
                            students.put(fsetteststatus,setteststatus);
                            // teststatus.setVisibility(VISIBLE);

//                            teststatus.setEnabled(true);
                            list.add(students);

                        }
                        else
                        {
                            HashMap<String,String> students = new HashMap<>();
                            students.put(Config.TAG_TESTID,testid);
                            students.put(Config.TAG_TEST_NAME,testname);
                            students.put(Config.TAG_QUESTIONS,"Ques : "+questions);
                            students.put(Config.TAG_MARKS,"Marks : "+marks);
                            // students.put(fsetteststatus,setteststatus);
                            list.add(students);

                        }


              /*  TotalTestTopicRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)

                    {
                        if(dataSnapshot.exists())
                        {
                            String temptestmark=dataSnapshot.child(testname).child("mark").getValue().toString();
                            if(temptestmark.equals("-1"))
                            {
                                //String temptestmark=dataSnapshot.child("mark").getValue().toString();
                                Log.d("testmark=",temptestmark);

                            }else
                                {
                                Log.d("testmark=", temptestmark);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/

                /*
                String mark="null";
                HashMap markdetail=new HashMap();
                markdetail.put("testname",testname);
                markdetail.put("mark",mark);

                TestTopicRef.child(currentUserID).child(testname).setValue(markdetail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                Toast.makeText(TestViewActivity.this, "TestView Uploaded Successfully", Toast.LENGTH_SHORT).show();

                            }
                        });
                        */








                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ListAdapter adapter = new SimpleAdapter(
                        TestViewActivity.this, list, R.layout.listlayout,
                        new String[]{Config.TAG_TESTID,Config.TAG_TEST_NAME,Config.TAG_QUESTIONS,Config.TAG_MARKS,fsetteststatus},
                        new int[]{R.id.testid,R.id.testName, R.id.no_question,R.id.marks,R.id.teststaus});

                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TestViewActivity.this,"Fetching Data","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showEmployee();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Config.URL_GET_QUESTIONDETAIL);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id)
    {

        Intent intent = new Intent(TestViewActivity.this,TestActivity.class);


        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
        // testname= position.getString(Config.TAG_TEST_NAME);
        String testId = map.get(Config.TAG_TESTID).toString();
        testname=map.get(Config.TAG_TEST_NAME);
        intent.putExtra(Config.TEST_ID,testId);
        intent.putExtra(Config.KEY_REGNO,regno);
        intent.putExtra(Config.TAG_TEST_NAME,testname);
        intent.putExtra(Config.TEST_DATE,testdate3);
        startActivity(intent);



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_favorite:
                // search action
                return true;



            case R.id.action_settings:
                // location found
             /*   SharedPreferences sharedpreferences = getSharedPreferences(Les_LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                startActivity(new Intent(Les_TestViewActivity.this,Les_LoginActivity.class));
                this.finish();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            //moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1)
                    {

                        onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {


                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1)
                    {

                    }
                })
                .show();

    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent(TestViewActivity.this,MainActivity.class);
      // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       // Toast.makeText(this, "Please Click Home Button...", Toast.LENGTH_LONG).show();

        startActivity(intent);
        finish();


        // Do Here what ever you want do on back press;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

   /* private void StoreTotalMark()
    {
        MarkRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Log.i("currenrusername=",currentUserID);

                    int sum=0;
                    int pValue=0;
                    int mark=0;
                    for(DataSnapshot ds:dataSnapshot.getChildren())
                    {

                        pValue=Integer.parseInt(String.valueOf(ds.child("mark").getValue()));
zz
                        sum+=pValue;
                        Log.d("Sum",String.valueOf(sum));

                    }
                    HashMap<String, Integer> MarkUploadMap = new HashMap<>();
                    //MarkUploadMap.put("uid",currentUserID);
                    MarkUploadMap.put("TotalMark", sum);
                    MarkUploadMap.put("mark", mark);
                    MarkRef.setValue(MarkUploadMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(TestViewActivity.this, "Total MArk uploaded Succesfully", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }*/


}