package tech.info.sasurie.sociall;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
{

    JSONObject jsonObject = null;
    private String JSON_STRING;
    public static String APP_ID="ca-app-pub-2052757055681240~4926377798";//ca-app-pub-3940256099942544~3347511713";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference UsersRef, PostsRef, LikesRef,TotalTestTopicRef,countRef,Markdata,TotalTestTopicCountRef;
    private String CurrentUserId,pass,reg_id,currentid;
    private Toolbar mToolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ImageView AddNewPostButton,RequestButton;
    private RecyclerView postList;
    private Boolean LikeChecker=false;
    private AdView mAdView;
    private long markupdatecount=0;
    private String count="2";
    private String scount;
    private TextView storetext;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        MobileAds.initialize(this, MainActivity.APP_ID);

        countRef= FirebaseDatabase.getInstance().getReference().child("counts");


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        TotalTestTopicRef=FirebaseDatabase.getInstance().getReference().child("Total_Test_Topic");
        Markdata = FirebaseDatabase.getInstance().getReference().child("Mark_Detail");
        TotalTestTopicCountRef=FirebaseDatabase.getInstance().getReference().child("Total_Test_Topic_Count");


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);





        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        storetext=findViewById(R.id.storecount);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);



        AddNewPostButton=(ImageView)findViewById(R.id.add_new_post_button);
        RequestButton=(ImageView)findViewById(R.id.friend_request_btn);

        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);


        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPostActivity();
            }
        });
        RequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToFriendsRequestActivity();
            }
        });





        if (currentUser !=null)
        {
            CurrentUserId=currentUser.getUid();
            UsersRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        if(dataSnapshot.hasChild("fullname"))
                        {
                            String fullname = dataSnapshot.child("fullname").getValue().toString();
                            NavProfileUserName.setText(fullname);
                        }
                        if(dataSnapshot.hasChild("profileimage"))
                        {
                            String image = dataSnapshot.child("profileimage").getValue().toString();
                            Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Please Fill Your Details", Toast.LENGTH_SHORT).show();

                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            currentid = mAuth.getCurrentUser().getUid();



        }



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                UserMenuSelector(item);
                return false;
            }
        });

        try
        {
            DisplayAllUsersPosts();
        }
        catch (Exception e)
        {
            System.out.print(e);
        }

        getJSON();
    }

    public void updateUserStatus(String state)
    {
        String saveCurrentDate,saveCurrentTime;

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss a");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        Map currentUserStatus=new HashMap();
        currentUserStatus.put("time",saveCurrentTime);
        currentUserStatus.put("date",saveCurrentDate);
        currentUserStatus.put("type",state);

        UsersRef.child(CurrentUserId).child("userState").updateChildren(currentUserStatus);




    }

    private void SendUserToFriendsRequestActivity()
    {
        startActivity(new Intent(MainActivity.this,FriendsRequestActivity.class));
    }

    private void DisplayAllUsersPosts()
    {
        final Query newPosts=PostsRef.orderByChild("counter");


        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                                (
                                        Posts.class,
                                        R.layout.all_posts_layout,
                                        PostsViewHolder.class,
                                        newPosts
                                )
                        {
                            @Override
                            protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position)
                            {

                                final String PostKey=getRef(position).getKey();

                                viewHolder.setFullname(model.getFullname());
                                viewHolder.setTime(model.getTime());
                                viewHolder.setDate(model.getDate());
                                viewHolder.setDescription(model.getDescription());
                                viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                                viewHolder.setPostimage(getApplicationContext(), model.getPostimage());
                                viewHolder.setLikeButtonStatus(PostKey);

                               /*viewHolder.username.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(MainActivity.this,UsersPostActivity.class).putExtra("PostKey",PostKey));
                                    }
                                });*/



                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(MainActivity.this,ClickPostActivity.class).putExtra("PostKey",PostKey));
                                    }
                                });

                                viewHolder.CommentButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        startActivity(new Intent(MainActivity.this,CommentsActivity.class).putExtra("PostKey",PostKey));

                                    }
                                });


                                viewHolder.LikeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        LikeChecker=true;

                                        LikesRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (LikeChecker.equals(true))
                                                {

                                                    if (dataSnapshot.child(PostKey).hasChild(CurrentUserId))
                                                    {

                                                        LikesRef.child(PostKey).child(CurrentUserId).removeValue();
                                                        LikeChecker=false;


                                                    }
                                                    else
                                                    {

                                                        LikesRef.child(PostKey).child(CurrentUserId).setValue(true);
                                                        LikeChecker=false;



                                                    }

                                                }


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });


                            }
                        };
                postList.setAdapter(firebaseRecyclerAdapter);
                updateUserStatus("online");


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        ImageView LikeButton,CommentButton;
        TextView LikesNo;
        int countLikes;
        String currentUserId;
        DatabaseReference LikesRef;
        TextView username;

        public PostsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;

            LikeButton=(ImageView) mView.findViewById(R.id.like_button);
            CommentButton= (ImageView) mView.findViewById(R.id.comment_button);
            LikesNo=(TextView) mView.findViewById(R.id.display_no_of_likes);

            username = (TextView) mView.findViewById(R.id.post_user_name);

            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

            if (user!=null)
            {
                currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
            }

        }

        public void setFullname(String fullname)
        {

            username.setText(fullname);
        }

        public void setProfileimage(Context ctx, String profileimage)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileimage).into(image);
        }

        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("    " + time);
        }

        public void setDate(String date)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("    " + date);
        }

        public void setDescription(String description)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setPostimage(Context ctx1,  String postimage)
        {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.click_post_image);
            Picasso.with(ctx1).load(postimage).into(PostImage);
        }
        public void setLikeButtonStatus(final String PostKey)
        {


            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(PostKey).hasChild(currentUserId))
                    {

                        countLikes=(int)  dataSnapshot.child(PostKey).getChildrenCount();

                        LikeButton.setImageResource(R.drawable.afterlike);

                        LikesNo.setText(Integer.toString(countLikes)+(" "));

                    }
                    else
                    {

                        countLikes=(int)  dataSnapshot.child(PostKey).getChildrenCount();

                        LikeButton.setImageResource(R.drawable.beforelike);

                        LikesNo.setText(Integer.toString(countLikes)+(" "));


                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    private void SendUserToPostActivity()
    {
        startActivity(new Intent(MainActivity.this,PostActivity.class));
    }


    @Override
    protected void onStart()
    {
        super.onStart();



        if (currentUser==null)
        {
            SendUserToLoginActivity();

        }
        else
        {

            //   Log.d("count=",String.valueOf(count));
            CheckUserDetails();
            updateUserStatus("online");
        }

    }

    private void CheckUserDetails()
    {
        UsersRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.hasChild("fullname"))
                {



                    SendUserToSetupActivity();

                }
                else
                {



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.nav_Lesurebreaker:
                getcount();

                break;
            case R.id.Marks:
                SendUserToPersonMarkActivity();
                break;

            case R.id.Leaderboard:
                SendUserTOMarkListActivity();
                break;

            case R.id.nav_post:
                SendUserToPostActivity();
                break;

            case R.id.nav_profile:
                SendUserToProfileActivity();
                break;

            case R.id.nav_home:
                SendUserToHomeActivity();
                break;

            case R.id.nav_friends:
                SendUserToFriendsActivity();
                break;

            case R.id.nav_find_friends:
                SendUserToFindFriendsActivity();
                break;

            case R.id.nav_messages:
                SendUserToMessageActivity();
                break;

            case R.id.nav_settings:
                SendUserToSettingsActivity();
                break;

            case R.id.nav_Logout:
                updateUserStatus("offline");
                mAuth.signOut();
                SendUserToLoginActivity();
                break;

            case R.id.nav_about:
                SendUserToAboutActivity();
                break;
        }

    }

    private void getcount()
    {

        currentid = mAuth.getCurrentUser().getUid();
        UsersRef.child(currentid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {

                    reg_id = dataSnapshot.child("regno").getValue().toString().trim();
                    pass = dataSnapshot.child("mobile").getValue().toString().trim();

                    validate();

                    // Log.d("count===",dataSnapshot.child("count").getValue().toString());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToPersonMarkActivity()
    {
        startActivity(new Intent(MainActivity.this,PersonMarkActivity.class));
    }

    private void SendUserTOMarkListActivity()
    {
        startActivity(new Intent(MainActivity.this,MarkListActivity.class));

    }

    private void SendUserToAllUsersMarkDisplayActivity() {
        startActivity(new Intent(MainActivity.this,AllUsersMarkDisplayActivity.class));
    }


    private void SendUserToTestViewActivity() {




        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(Config.KEY_REGNO,reg_id);
        editor.putString(Config.KEY_PASSWORD,pass);
        editor.commit();

        Intent intent = new Intent(MainActivity.this,TestViewActivity.class);
        intent.putExtra(Config.KEY_REGNO,reg_id);
        startActivity(intent);

      /*  Intent intent=new Intent(MainActivity.this,AuthenticateActivity.class);
        intent.putExtra("regno",reg_id);
        intent.putExtra("pass",pass);
        Log.d("reg_id=", reg_id);
        Log.d("pass=", pass);

        startActivity(intent);*/

        //startActivity(new Intent(MainActivity.this,AuthenticateActivity.class));


    }

    private void SendUserToAboutActivity()
    {
        if (CurrentUserId.equals("kUomz6kgXzSLJmxSzMF3V5gjgPM2"))
        {
            startActivity(new Intent(MainActivity.this, AdminSociallActivity.class));
        }
        else
        {
            startActivity(new Intent(MainActivity.this, AboutSociallActivity.class));
        }
    }

    private void SendUserToMessageActivity()
    {
        startActivity(new Intent(MainActivity.this,MessageActivity.class));
    }

    private void SendUserToHomeActivity()
    {
        startActivity(new Intent(MainActivity.this,MainActivity.class));
    }

    private void SendUserToFriendsActivity()
    {
        startActivity(new Intent(MainActivity.this,FriendsActivity.class));
    }

    private void SendUserToFindFriendsActivity()
    {
        startActivity(new Intent(MainActivity.this,FindFriendsActivity.class));
    }

    private void SendUserToSettingsActivity()
    {
        startActivity(new Intent(MainActivity.this,SettingsActivity.class));
    }

    private void SendUserToProfileActivity()
    {
        startActivity(new Intent(MainActivity.this,ProfileActivity.class));
    }

    private void SendUserToSetupActivity()
    {

        startActivity(new Intent(MainActivity.this,SetupActivity.class));

        finish();
        //
    }

    private void SendUserToLoginActivity()
    {
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        updateUserStatus("offline");

    }






    private void FinalMarkUpload()
    {

        if (currentUser!=null) {


            TotalTestTopicRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        markupdatecount = dataSnapshot.getChildrenCount();


                 /*   TotalTestTopicCountRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                int count= Integer.parseInt(dataSnapshot.child("count").getValue().toString());

                                    JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(JSON_STRING);
                                    JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);


                                        if(count!= result.length())
                                        {


                                            int count1 = result.length();
                                            TotalTestTopicCountRef.child(CurrentUserId).child("count").setValue(String.valueOf(count1));


                                            Log.d("markuploadcounr=", "not equal");
                                            for (int i = 0; i < result.length(); i++) {
                                                JSONObject jo = result.getJSONObject(i);

                                                String testname1 = jo.getString(Config.TAG_TEST_NAME);
                                                String testdate = jo.getString(Config.TEST_DATE);

                                                int mark1 = -1;
                                                HashMap markdetail1 = new HashMap();
                                                markdetail1.put("testname", testname1);
                                                markdetail1.put("mark", String.valueOf(mark1));
                                                markdetail1.put("date", testdate);


                                                TotalTestTopicRef.child(CurrentUserId).child(testname1).updateChildren(markdetail1);


                                            }


                                            Markdata.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) ;
                                                    {
                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                            String testname2 = ds.child("testname").getValue().toString();
                                                            String mark = ds.child("mark").getValue().toString();
                                                            HashMap markdetail11 = new HashMap();

                                                            markdetail11.put("mark", mark);

                                                            TotalTestTopicRef.child(CurrentUserId).child(testname2).updateChildren(markdetail11);


                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/
                    } else {
                        markupdatecount = 0;
                    }


                    //  Log.d("markuploadcount=",String.valueOf(markupdatecount));
                    if (markupdatecount == 0 || markupdatecount > 0) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(JSON_STRING);
                            JSONArray result = jsonObject.getJSONArray(Config.TAG_JSON_ARRAY);


                            if (markupdatecount != result.length()) {
                                int count = result.length();
                                TotalTestTopicCountRef.child(CurrentUserId).child("count").setValue(String.valueOf(count));


                                Log.d("markuploadcounr=", "not equal");
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject jo = result.getJSONObject(i);

                                    String testname1 = jo.getString(Config.TAG_TEST_NAME);
                                    String testdate = jo.getString(Config.TEST_DATE);

                                    int mark1 = -1;
                                    HashMap markdetail1 = new HashMap();
                                    markdetail1.put("testname", testname1);
                                    markdetail1.put("mark", String.valueOf(mark1));
                                    markdetail1.put("date", testdate);


                                    TotalTestTopicRef.child(CurrentUserId).child(testname1).updateChildren(markdetail1);


                                }


                                Markdata.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) ;
                                        {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                String testname2 = ds.child("testname").getValue().toString();
                                                String mark = ds.child("mark").getValue().toString();
                                                HashMap markdetail11 = new HashMap();

                                                markdetail11.put("mark", mark);

                                                TotalTestTopicRef.child(CurrentUserId).child(testname2).updateChildren(markdetail11);


                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                Log.d("markuploadcounr=", "equal");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }








    }


    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                //']
                FinalMarkUpload();
            }

            @Override
            protected String doInBackground(Void... params) {
                tech.info.sasurie.sociall.RequestHandler rh = new tech.info.sasurie.sociall.RequestHandler();
                String s = rh.sendGetRequest(Config.URL_GET_QUESTIONDETAIL);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }



    private void auth() {
        class Validate extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Checking...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();


                if("Data".equals(s.toString().trim())){
                    //   Toast.makeText(AuthenticateActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();

                    SendUserToTestViewActivity();


                }else{


                    SendUserToAuthenticateActivity();
                    //startActivity(new Intent(MainActivity.this,AuthenticateActivity.class));
                    //  Toast.makeText(AuthenticateActivity.this,s,Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Config.KEY_REGNO, reg_id);
                params.put(Config.KEY_PASSWORD, pass);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_LOGIN, params);
                return res;
            }
        }

        Validate ae = new Validate();
        ae.execute();
    }

    private void SendUserToAuthenticateActivity()
    {

        Intent intent = new Intent(MainActivity.this,AuthenticateActivity.class);
        startActivity(intent);
    }

    public  void validate() {

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        auth();
    }


}
