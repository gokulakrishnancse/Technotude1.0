package tech.info.sasurie.sociall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadActivity extends AppCompatActivity
{

    ProgressBar progressBar;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar=findViewById(R.id.progress_bar);
        textView=findViewById(R.id.textview);


        progressBar.setMax(100);
        progressBar.setScaleY(5f);



        progressAnimation();
    }

    public void progressAnimation()
    {
        ProgressbarAnimation animation=new ProgressbarAnimation(this,progressBar,textView,0.f,100.f);
        animation.setDuration(8000);
        progressBar.setAnimation(animation);
    }
}
