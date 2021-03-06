package tech.info.sasurie.sociall;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersMarkDisplayActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private DatabaseReference UserData;
    private RecyclerView usersList;
    private ImageView SearchButton;
    private EditText SearchInputText;
    private  FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_mark_display);

        UserData= FirebaseDatabase.getInstance().getReference().child("Mark Details");

        usersList=findViewById(R.id.search_result_list);
        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(this));
        firebaseAuth = FirebaseAuth.getInstance();



    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseRecyclerAdapter<Users, AllUsersMarkDisplayActivity.UsersViewHolder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Users, AllUsersMarkDisplayActivity.UsersViewHolder>(

                Users.class,
                R.layout.users_mark_list,
                AllUsersMarkDisplayActivity.UsersViewHolder.class,
                UserData


        ) {
            @Override
            protected void populateViewHolder(AllUsersMarkDisplayActivity.UsersViewHolder viewHolder, Users model, int position) {

                viewHolder.setFullname(model.getFullname());
                viewHolder.setProfileimage(getApplicationContext(),model.getProfileimage());
                viewHolder.setMark(model.getMark());
                final String visit_user_id=getRef(position).getKey();

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent=new Intent(AllUsersMarkDisplayActivity.this,PersonProfileActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);

                    }
                });

            }
        };
        usersList.setAdapter(firebaseRecyclerAdapter);


    }




    public static class UsersViewHolder extends RecyclerView.ViewHolder
    {

        View view;


        public UsersViewHolder(View itemView)
        {
            super(itemView);
            view=itemView;
        }

        public void setFullname(String fullname){

            TextView userNameView=view.findViewById(R.id.all_users_profile_full_name);
            userNameView.setText(fullname);

        }
        public void setMark(int mark){

        TextView userMarkView=view.findViewById(R.id.user_mark);
        userMarkView.setText(Integer.toString(mark));

    }

        public void setProfileimage(final Context ctx, final String profileimage) {
            final CircleImageView userThumbImage=view.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(profileimage).placeholder(R.drawable.profile).into(userThumbImage);


        }
    }
}





