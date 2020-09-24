package tech.info.sasurie.sociall;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MarkListActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef, UserRootRef, totalmarkdata,CurrentDateMarkDetail;
    private String currentUserID;
    private RecyclerView MarkList;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_list);


        mAuth = FirebaseAuth.getInstance();


        RootRef = FirebaseDatabase.getInstance().getReference().child("Mark_Detail");
        UserRootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        totalmarkdata = FirebaseDatabase.getInstance().getReference().child("Final_Mark_Detail");
        CurrentDateMarkDetail = FirebaseDatabase.getInstance().getReference().child("Current_Date_Mark_Detail");



        MarkList = (RecyclerView) findViewById(R.id.mark_recycler);
        MarkList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        MarkList.setLayoutManager(linearLayoutManager);


        DisplayAllUsersMark();


    }

    private void DisplayAllUsersMark() {


        final String saveCurrentDate, saveCurrentTime;

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Log.d("date=",saveCurrentDate);
        // Query query = totalmarkdata.orderBy("Final_Mark_Detail").limit(3);


        FirebaseRecyclerAdapter<DataSetFire, MarkViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<DataSetFire, MarkViewHolder>
                        (
                                DataSetFire.class,
                                R.layout.allusermarklist,
                                MarkViewHolder.class,
                                CurrentDateMarkDetail.orderByChild("totalmark")


                        ) {
                    @Override
                    protected void populateViewHolder(final MarkViewHolder viewHolder, final DataSetFire model, int position) {
                        final String userId = getRef(position).getKey();



                                String currentdate=model.getDate();

                        Log.d("currentdate=", model.getDate());
                       /* if(saveCurrentDate.equals(String.valueOf(model.getDate())))
                        {
                           // linearLayout.setVisibility(View.VISIBLE);
                            Log.d("totalmark=", String.valueOf(model.getTotalmark()));
                            viewHolder.setFullname1(model.getFullname());
                            viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                            viewHolder.setTotalmark1(model.getTotalmark());

                            TextView usermark=viewHolder.mView.findViewById(R.id.user_rank);
                            usermark.setText("RANK: 1");
                            TextView outofmark=viewHolder.mView.findViewById(R.id.all_of_user_out_of_mark_detail);
                            outofmark.setText("2");

                        }
                        else

                        {
                            viewHolder.mView.setVisibility(View.GONE);

                            viewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

                            //linearLayout.setVisibility(View.INVISIBLE);

                        }


                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent PersonMarkIntent = new Intent(MarkListActivity.this, PersonMarkdetailActivity.class);
                                PersonMarkIntent.putExtra("userid", userId);
                                startActivity(PersonMarkIntent);
                            }
                        });

                        */

                        if(saveCurrentDate.equals(String.valueOf(model.getDate())))
                        {
                            count = count +1;

                            viewHolder.setFullname1(model.getFullname());
                            viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                            viewHolder.setTotalmark1(model.getTotalmark());

                            TextView usermark=viewHolder.mView.findViewById(R.id.user_rank);
                            usermark.setText("RANK: "+count);
                            TextView outofmark=viewHolder.mView.findViewById(R.id.all_of_user_out_of_mark_detail);
                            outofmark.setText(model.getCount());

                           /* if( count ==1)
                            {

                                viewHolder.setFullname1(model.getFullname());
                                viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                                viewHolder.setTotalmark1(model.getTotalmark());

                                TextView usermark=viewHolder.mView.findViewById(R.id.user_rank);
                                usermark.setText("RANK: 1");
                                TextView outofmark=viewHolder.mView.findViewById(R.id.all_of_user_out_of_mark_detail);
                                outofmark.setText(model.getCount());
                            }else if(count ==2)
                            {
                                viewHolder.setFullname1(model.getFullname());
                                viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                                viewHolder.setTotalmark1(model.getTotalmark());
                                TextView usermark=viewHolder.mView.findViewById(R.id.user_rank);
                                usermark.setText("RANK: 2");
                                TextView outofmark=viewHolder.mView.findViewById(R.id.all_of_user_out_of_mark_detail);
                                outofmark.setText(model.getCount());
                            }
                            else
                            {
                                viewHolder.setFullname1(model.getFullname());
                                viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                                viewHolder.setTotalmark1(model.getTotalmark());
                                TextView usermark=viewHolder.mView.findViewById(R.id.user_rank);
                                usermark.setText("RANK: 3");
                                TextView outofmark=viewHolder.mView.findViewById(R.id.all_of_user_out_of_mark_detail);
                                outofmark.setText(model.getCount());

                            }
*/


                        }
                        else

                        {
                            viewHolder.mView.setVisibility(View.GONE);

                            viewHolder.mView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

                            //linearLayout.setVisibility(View.INVISIBLE);

                        }

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent PersonMarkIntent = new Intent(MarkListActivity.this, PersonMarkdetailActivity.class);
                                PersonMarkIntent.putExtra("userid", userId);
                                startActivity(PersonMarkIntent);
                            }
                        });




                    }
                };

        MarkList.setAdapter(firebaseRecyclerAdapter);


    }







        /* totalmarkdata.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot)
             {
                 FirebaseRecyclerAdapter<DataSetFire, MarkViewHolder> firebaseRecyclerAdapter =
                         new FirebaseRecyclerAdapter<DataSetFire, MarkViewHolder>
                                 (
                                         DataSetFire.class,
                                         R.layout.allusermarklist,
                                         MarkViewHolder.class,
                                         totalmarkdataref

                                 )
                         {
                             @Override
                             protected void populateViewHolder( MarkViewHolder viewHolder,  DataSetFire model,  int position) {
                                 final String userId = getRef(position).getKey();

                                 Log.d("Sum", String.valueOf(userId));
                                 count = count +1;

                                 if( count ==1)
                                 {

                                     viewHolder.setFullname1(model.getFullname());
                                     viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                                     viewHolder.setTotalmark1(model.getTotalmark());
                                 }else if(count ==2)
                                 {
                                     viewHolder.setFullname1(model.getFullname());
                                     viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                                     viewHolder.setTotalmark1(model.getTotalmark());
                                 }
                                 else
                                 {
                                     viewHolder.setFullname1(model.getFullname());
                                     viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                                     viewHolder.setTotalmark1(model.getTotalmark());

                                 }

                                 viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         Intent PersonMarkIntent = new Intent(MarkListActivity.this, PersonMarkdetailActivity.class);
                                         PersonMarkIntent.putExtra("userid", userId);
                                         startActivity(PersonMarkIntent);
                                     }
                                 });



                             }
                         };

                 MarkList.setAdapter(firebaseRecyclerAdapter);

             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });*/



    public static class MarkViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MarkViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setProfileimage(Context ctx1,String profileimage)
        {

            ImageView ProfileImage = (ImageView) mView.findViewById(R.id.all_users_profile_image);
            ProfileImage.setVisibility(View.VISIBLE);
            Picasso.with(ctx1).load(profileimage).into(ProfileImage);
        }

        public void setFullname1(String fullname)
        {
            TextView FullName=mView.findViewById(R.id.all_users_profile_of_full_name);
            FullName.setVisibility(View.VISIBLE);
            FullName.setText(fullname);
        }



        public void setTotalmark1(int totalmark)
        {

            TextView totalMark=mView.findViewById(R.id.all_of_user_mark);
            totalMark.setVisibility(View.VISIBLE);
            totalMark.setText(String.valueOf(totalmark));


        }


        public void setFullname2(String fullname)
        {
            TextView FullName=mView.findViewById(R.id.all_users_profile_of_full_name);
            FullName.setText(fullname);

        }



        public void setTotalmark2(String totalmark)
        {

            TextView totalMark=mView.findViewById(R.id.all_of_user_mark);
            totalMark.setText(totalmark);


        }



        public void setFullname3(String fullname)
        {
            TextView FullName=mView.findViewById(R.id.all_users_profile_of_full_name);
            FullName.setText(fullname);
           }



        public void setTotalmark3(String totalmark)
        {

            TextView totalMark=mView.findViewById(R.id.all_of_user_mark);
            totalMark.setText(totalmark);


        }





    }
}