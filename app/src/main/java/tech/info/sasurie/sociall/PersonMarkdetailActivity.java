
package tech.info.sasurie.sociall;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PersonMarkdetailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef, UserRootRef,TotalTestTopicRef;
    private String currentUserID;
    private RecyclerView MarkList;
    private String Userkey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_markdetail);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        Userkey=getIntent().getExtras().get("userid").toString() ;
        RootRef = FirebaseDatabase.getInstance().getReference().child("Mark_Detail");
        TotalTestTopicRef=FirebaseDatabase.getInstance().getReference().child("Total_Test_Topic");


        MarkList = (RecyclerView) findViewById(R.id.mark_recycler1);
        MarkList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        MarkList.setLayoutManager(linearLayoutManager);





        DisplayAllMarks();

    }

    private void DisplayAllMarks()
    {

        String saveCurrentDate;

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        // final Query totalmarkdataref=TotalTestTopicRef.child(Userkey).orderByChild("mark");
        FirebaseRecyclerAdapter<DataSetFire,MarksViewHolder> fireFirebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<DataSetFire, MarksViewHolder>
                        (
                                DataSetFire.class,
                                R.layout.marklist,
                                MarksViewHolder.class,
                                RootRef.child(Userkey).orderByChild("date").equalTo(saveCurrentDate)

                        )
                {
                    @Override
                    protected void populateViewHolder(final MarksViewHolder viewHolder, final DataSetFire model, int position) {

                        final String testtopicid = getRef(position).getKey();

                        viewHolder.setTestname(model.getTestname());

                        viewHolder.setMark(model.getMark());


                        }



                };
        MarkList.setAdapter(fireFirebaseRecyclerAdapter);

    }

    public static class MarksViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MarksViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTestname(String testname) {
            TextView TestName = mView.findViewById(R.id.all_users_profile_full_name);
            TestName.setText(testname);
            TestName.setTextColor(Color.BLACK);
        }

        public void setMark(String mark) {
            TextView TestMark = mView.findViewById(R.id.all_user_mark);
            TestMark.setText(mark);
            TestMark.setTextColor(Color.BLACK);
        }
        public void setTestname1(String testname) {
            TextView TestName = mView.findViewById(R.id.all_users_profile_full_name);
            TestName.setText(testname);
            TestName.setTextColor(Color.RED);
        }

        public void setMark1(String mark) {
            TextView TestMark = mView.findViewById(R.id.all_user_mark);
            TestMark.setText(mark);
            TestMark.setTextColor(Color.RED);
        }

    }
}