package tech.info.sasurie.sociall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AuthenticateActivity extends AppCompatActivity
{
    private Button button1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);



        button1=findViewById(R.id.button123);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthenticateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }



    @Override
    public void onBackPressed() {

        Intent intent=new Intent(AuthenticateActivity.this,MainActivity.class);
        startActivity(intent);



        // Do Here what ever you want do on back press;
    }

}

