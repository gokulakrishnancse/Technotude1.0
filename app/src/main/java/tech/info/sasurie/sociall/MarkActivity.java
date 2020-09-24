package tech.info.sasurie.sociall;

import android.support.annotation.NonNull;
import android.util.Log;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.lang.String;

//import static aptitude.sasurie.leisurebreaker.LoginActivity.MyPREFERENCES;
//import static tech.info.sasurie.sociall.Les_LoginActivity.MyPREFERENCES;

public class MarkActivity extends AppCompatActivity {
    JSONObject jsonObject = null;
    String[] answer, answerdin;
    JSONArray result;
    private String testid, regno, mark, testname,count,coun1,coun2;
    private TextView textView, textView9;
    SharedPreferences sharedpreferences;
    private AdView mAdView;
    private DatabaseReference Markdata, totalmarkdata;
    Users users;
    private FirebaseUser userKey, user1;
    private FirebaseAuth firebaseAuth, mAuth;
    private DatabaseReference mDatabase, personmarkdataRef, TestTopicRef, TotalTestTopicRef, UserRef, CurrentDateMarkDetail, totaltesttopicref,MarkDetailEnter;
    private String JSON_STRING;
    private String fullname, reg_id, profileimage, currentUserID, CurrentuserId, getdate;
    private int marks;
    private static final String TAG = "MarkActivity";
    private long markupdatecount = 0, mark1 = 0, sum1 = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);
        Intent intent = getIntent();
        MobileAds.initialize(this,
                "ca-app-pub-2052757055681240~3637998856");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        firebaseAuth = FirebaseAuth.getInstance();

        testid = intent.getStringExtra(Config.TEST_ID);
        mark = intent.getStringExtra(Config.TEST_MARK);
        count = intent.getStringExtra("count");

        Log.d("countM=",count);
        testname = intent.getStringExtra(Config.TAG_TEST_NAME);
        reg_id = intent.getStringExtra(Config.KEY_REGNO);

        firebaseAuth = FirebaseAuth.getInstance();

        totaltesttopicref = FirebaseDatabase.getInstance().getReference().child("Total_Test_Topic");

        Markdata = FirebaseDatabase.getInstance().getReference().child("Mark_Detail");

        totalmarkdata = FirebaseDatabase.getInstance().getReference().child("Final_Mark_Detail");
        CurrentDateMarkDetail = FirebaseDatabase.getInstance().getReference().child("Current_Date_Mark_Detail");
        personmarkdataRef = FirebaseDatabase.getInstance().getReference().child("Final_Person_Mark_Detail");
        TestTopicRef = FirebaseDatabase.getInstance().getReference().child("Mark_Details");
        TotalTestTopicRef = FirebaseDatabase.getInstance().getReference().child("Total_Test_Topic");

        currentUserID = firebaseAuth.getCurrentUser().getUid();


        mAuth = FirebaseAuth.getInstance();
        CurrentuserId = mAuth.getCurrentUser().getUid();

        Log.d("CurrentuserId=", CurrentuserId);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentuserId);
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    fullname = dataSnapshot.child("fullname").getValue().toString().trim();
                    profileimage = dataSnapshot.child("profileimage").getValue().toString().trim();
                    //reg_id=dataSnapshot.child("regno").getValue().toString().trim();
                    // pass=dataSnapshot.child("mobile").getValue().toString().trim();

                    // Log.d("reg_id111=",reg_id);


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        HashMap map = new HashMap();
        //MarkUploadMap.put("uid",currentUserID);

        map.put("mark", mark);


        TotalTestTopicRef.child(currentUserID).child(testname).updateChildren(map);

        totaltesttopicref.child(currentUserID).child(testname).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    getdate = dataSnapshot.child("date").getValue().toString();
                    Log.d("datecurrent=",getdate);
                    UploadMarkDetails();
                    // FinalUploadMarkDetails();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        textView = findViewById(R.id.textmark);
        textView.setText(mark);
        textView9 = findViewById(R.id.textmarkclick);

        registerMark();
        //FinalMarkUpload();

       // UploadTotalMark();
        uploadcurrentdatemark();

        textView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Log.d("totalname=",String.valueOf(sum));
                Intent i = new Intent(MarkActivity.this, TestViewActivity.class);
                // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

                //Toast.makeText(MarkActivity.this,fullname,Toast.LENGTH_LONG).show();
                // Toast.makeText(MarkActivity.this, "Mark uploaded successfully", Toast.LENGTH_SHORT).show();


            }
        });


    }


    private void UploadMarkDetails() {
        final String saveCurrentDate, saveCurrentTime;

        final Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());


        MarkDetailEnter = FirebaseDatabase.getInstance().getReference().child("Mark_Detail").child(currentUserID).child(testname);
        if (getdate.equals(saveCurrentDate))
        {

            Log.d("con=","true");
            HashMap MarkUploadMap = new HashMap();
            //MarkUploadMap.put("uid",currentUserID);

            MarkUploadMap.put("testname", testname);
            MarkUploadMap.put("mark", mark);
            MarkUploadMap.put("count",count);
            MarkUploadMap.put("date",saveCurrentDate);
            //  MarkUploadMap.put("mark1", mark1);

            Markdata.child(currentUserID).child(testname).updateChildren(MarkUploadMap);


                     /*  HashMap map = new HashMap();
                        //MarkUploadMap.put("uid",currentUserID);
                        map.put("testname", testname);
                        map.put("mark", mark);
                        map.put("date",saveCurrentDate);


                        TotalTestTopicRef.child(currentUserID).child(testname).updateChildren(map);*/

        }else
        {


            Log.d("con=","false");
            HashMap MarkUploadMap = new HashMap();
            //MarkUploadMap.put("uid",currentUserID);

            MarkUploadMap.put("testname", testname);
            MarkUploadMap.put("mark", mark);
            MarkUploadMap.put("count",count);
            MarkUploadMap.put("date",saveCurrentDate);
            //  MarkUploadMap.put("mark1", mark1);

            Markdata.child(currentUserID).child(testname).updateChildren(MarkUploadMap);

                      /*  HashMap map = new HashMap();
                        //MarkUploadMap.put("uid",currentUserID);
                        map.put("testname", testname);
                        map.put("mark", mark);
                        MarkUploadMap.put("date",String.valueOf("4-april-2020"));


                        TotalTestTopicRef.child(currentUserID).child(testname).updateChildren(map);*/


        }



    }


    private void FinalUploadMarkDetails()
    {





           /* HashMap MarkUploadMap = new HashMap();
            //MarkUploadMap.put("uid",currentUserID);

            MarkUploadMap.put("testname", testname);
            MarkUploadMap.put("mark", mark);
            MarkUploadMap.put("count",count);
            MarkUploadMap.put("date",saveCurrentDate);
            //  MarkUploadMap.put("mark1", mark1);

            Markdata.child(currentUserID).child(testname).updateChildren(MarkUploadMap);*/


        HashMap map = new HashMap();
        //MarkUploadMap.put("uid",currentUserID);

        map.put("mark", mark);


        TotalTestTopicRef.child(currentUserID).child(testname).updateChildren(map);


    }




    private void UploadTotalMark()
    {

        Markdata.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists());
                {

                    String saveCurrentDate;

                    Calendar calFordDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    saveCurrentDate = currentDate.format(calFordDate.getTime());


                    int sum=0;
                    for(DataSnapshot ds:dataSnapshot.getChildren())
                    {

                        int pValue=Integer.parseInt(String.valueOf(ds.child("mark").getValue()));

                        sum+=pValue;
                        // Log.d("Sum",String.valueOf(sum));

                    }

                    sum1=sum;
                    HashMap MarkUploadMap = new HashMap();
                    //MarkUploadMap.put("uid",currentUserID);

                    MarkUploadMap.put("fullname", fullname);
                    MarkUploadMap.put("profileimage", profileimage);
                    //  MarkUploadMap.put("mark", String.valueOf(sum));
                    MarkUploadMap.put("totalmark",sum);

                    MarkUploadMap.put("date",saveCurrentDate);
                    //    MarkUploadMap.put("mark",sum1);







                    totalmarkdata.child(currentUserID).setValue(MarkUploadMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        //  Toast.makeText(MarkActivity.this, "Markuploaded successfully", Toast.LENGTH_SHORT).show();

                                        // Log.d(TAG, "Name: " + testname);
                                        //   Log.d(TAG, ": " + profileimage);
                                       /* Intent i = new Intent(MarkActivity.this, TestViewActivity.class);
                                        // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();*/
                                    } else {
                                        Toast.makeText(MarkActivity.this, "Mark not uploaded successfully", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });


                    //  mDatabase.child(currentUserID).child("totalmark").setValue(String.valueOf(sum));


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    private void FinalMarkUpload()
    {

        final String saveCurrentDate,saveCurrentTime;

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());



        if(getdate.equals(saveCurrentDate)) {

            HashMap MarkUploadMap = new HashMap();
            //MarkUploadMap.put("uid",currentUserID);
            MarkUploadMap.put("testname", testname);
            MarkUploadMap.put("mark", mark);
            MarkUploadMap.put("date",saveCurrentDate);


            TotalTestTopicRef.child(currentUserID).child(testname).updateChildren(MarkUploadMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(MarkActivity.this, "Markuploaded successfully", Toast.LENGTH_SHORT).show();

                                // Log.d(TAG, "Name: " + testname);
                                //   Log.d(TAG, ": " + profileimage);
                                       /* Intent i = new Intent(MarkActivity.this, TestViewActivity.class);
                                        // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();*/
                            } else {
                                Toast.makeText(MarkActivity.this, "Mark not uploaded successfully", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
        }else

        {
            HashMap MarkUploadMap = new HashMap();
            //MarkUploadMap.put("uid",currentUserID);
            MarkUploadMap.put("testname", testname);
            MarkUploadMap.put("mark", mark);
            MarkUploadMap.put("date", getdate);



            TotalTestTopicRef.child(currentUserID).child(testname).updateChildren(MarkUploadMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                // Toast.makeText(MarkActivity.this, "Markuploaded successfully", Toast.LENGTH_SHORT).show();

                                // Log.d(TAG, "Name: " + testname);
                                //   Log.d(TAG, ": " + profileimage);
                                       /* Intent i = new Intent(MarkActivity.this, TestViewActivity.class);
                                        // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();*/
                            } else {
                                Toast.makeText(MarkActivity.this, "Mark not uploaded successfully", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }
    }








        /*
        TestTopicRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    markupdatecount=dataSnapshot.getChildrenCount();
                }
                else
                {
                    markupdatecount=0;
                }


                Log.d("markuploadcount=",String.valueOf(markupdatecount));

                if(markupdatecount==0)
                {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(JSON_STRING);
                        JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);

                        for (int i = 0; i < result.length(); i++) {
                            JSONObject jo = result.getJSONObject(i);

                            String testname1 = jo.getString(Config.TAG_TEST_NAME);


                            if(testname==testname1)
                            {
                                HashMap markdetail = new HashMap();
                                markdetail.put("testname", testname);
                                markdetail.put("mark", mark);


                                TestTopicRef.child(currentUserID).child(testname).setValue(markdetail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(MarkActivity.this, "FinalMark Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                            }
                                        });


                            }
                            else
                            {
                                int mark1=-1;
                                HashMap markdetail1 = new HashMap();
                                markdetail1.put("testname", testname1);
                                markdetail1.put("mark", String.valueOf(mark1));


                                TestTopicRef.child(currentUserID).child(testname1).setValue(markdetail1)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(MarkActivity.this, "FinalMark Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                            }
                                        });

                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    TestTopicRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if ((dataSnapshot.child(testname).exists())) {


                                HashMap MarkUploadMap = new HashMap();
                                //MarkUploadMap.put("uid",currentUserID);
                                MarkUploadMap.put("testname", testname);
                                MarkUploadMap.put("mark", mark);


                                TestTopicRef.child(currentUserID).child(testname).setValue(MarkUploadMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MarkActivity.this, "Markuploaded successfully", Toast.LENGTH_SHORT).show();

                                                    // Log.d(TAG, "Name: " + testname);
                                                    //   Log.d(TAG, ": " + profileimage);
                                        Intent i = new Intent(MarkActivity.this, TestViewActivity.class);
                                        // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                                } else {
                                                    Toast.makeText(MarkActivity.this, "Mark not uploaded successfully", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */













    private void uploadcurrentdatemark()
    {


        Markdata.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists());
                {

                    String saveCurrentDate, saveCurrentTime;

                    Calendar calFordDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    saveCurrentDate = currentDate.format(calFordDate.getTime());


                    float sum = 0, count = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        String date = ds.child("date").getValue().toString();
                        Log.d("datecurrent=",date);

                        Log.d("date=", saveCurrentDate);
                        if (date.equalsIgnoreCase(saveCurrentDate)) {
                            Log.d("Condition=", "true");
                            int pValue = Integer.parseInt(String.valueOf(ds.child("mark").getValue()));
                            int qValue = Integer.parseInt(String.valueOf(ds.child("count").getValue()));

                            count += qValue;
                            sum += pValue;
                            Log.d("Sum", String.valueOf(sum));
                        }

                    }
                    float sixtypvalue = (count *60) / 100;
                    Log.d("Sum", String.valueOf(sum));
                    Log.d("count12345=", String.valueOf(count));
                    Log.d("Six==", String.valueOf(sixtypvalue));
                    int coun1= (int) count;

                    if (sum > sixtypvalue) {

                        HashMap MarkUploadMap = new HashMap();
                        //MarkUploadMap.put("uid",currentUserID);

                        MarkUploadMap.put("fullname", fullname);
                        MarkUploadMap.put("profileimage", profileimage);
                        //  MarkUploadMap.put("mark", String.valueOf(sum));
                        MarkUploadMap.put("totalmark", sum);
                        MarkUploadMap.put("date", saveCurrentDate);
                        MarkUploadMap.put("count",String.valueOf(coun1));
                        //    MarkUploadMap.put("mark",sum1);


                        CurrentDateMarkDetail.child(currentUserID).setValue(MarkUploadMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            //  Toast.makeText(MarkActivity.this, "Markuploaded successfully", Toast.LENGTH_SHORT).show();

                                            // Log.d(TAG, "Name: " + testname);
                                            //   Log.d(TAG, ": " + profileimage);
                                       /* Intent i = new Intent(MarkActivity.this, TestViewActivity.class);
                                        // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();*/
                                        } else {
                                            Toast.makeText(MarkActivity.this, "Mark not uploaded successfully", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });


                        //  mDatabase.child(currentUserID).child("totalmark").setValue(String.valueOf(sum));


                    }else
                    {
                        CurrentDateMarkDetail.child(currentUserID).removeValue();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }













    private void registerMark() {
        class AddUser extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MarkActivity.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(MarkActivity.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {





                HashMap<String,String> params = new HashMap<>();
                params.put(Config.KEY_REGNO,reg_id);
                params.put(Config.KEY_TESTID,testid);
                params.put(Config.TEST_MARK,mark);
                //params.put(Config.KEY_EMP_PHNO,phno);


                tech.info.sasurie.sociall.RequestHandler rh = new tech.info.sasurie.sociall.RequestHandler();
                final String res1 = rh.sendPostRequest(Config.URL_RESULT, params);

                return res1;




            }


        }

        AddUser ae = new AddUser();
        ae.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
