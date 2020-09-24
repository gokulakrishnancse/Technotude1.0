package tech.info.sasurie.sociall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class SetupActivity extends AppCompatActivity
{

    private CircleImageView UserProfileImage;
    private EditText UserName, UserFullName, UserCountry,UserMobile;
    private Button ButtonSave;

    private Session session;
    private ImageView ChooseImage;
    private final static int Gallery_Pick = 1;
    private ProgressDialog loadingBar;
    private DatabaseReference UsersRef,countRef;
    private StorageReference UserProfileImageRef;
    private String CurrentUserId,currentuserid;
    private FirebaseAuth mAuth;
    private Uri ImageUri,ResultUri;
    private String CurrentUserName,CurrentUserFullName,CurrentUserCountry,CurrentUserMobile,CurrentUserDept,CurrentUserRegno,CurrentUserYear;
    private SpotsDialog spotsDialog;
    private EditText userregno,userdept,useryear;
    private String email,password,userEmail;
    private int count=1;

    private String downloadUrl="https://firebasestorage.googleapis.com/v0/b/myapp-4eadd.appspot.com/o/chatterplace.png?alt=media&token=e51fa887-bfc6-48ff-87c6-e2c61976534e";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();




        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        countRef= FirebaseDatabase.getInstance().getReference().child("counts");

        userEmail= user.getEmail();

        userregno=findViewById(R.id.register_setup_number);
        userdept=findViewById(R.id.dept);
        useryear=findViewById(R.id.year);
        UserProfileImage = findViewById(R.id.userImage);
        UserFullName = findViewById(R.id.user_full_name);
        UserName = findViewById(R.id.user_name);
        UserCountry = findViewById(R.id.user_country);
        UserMobile=findViewById(R.id.user_mobile);
        ChooseImage=findViewById(R.id.choose_image);
        ButtonSave=findViewById(R.id.buttonSave);



        loadingBar = new ProgressDialog(this);

        spotsDialog=new SpotsDialog(this,"Saving Information");

        CurrentUserId = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        ChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        ButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUserFullName=UserFullName.getText().toString().trim();
                CurrentUserName=UserName.getText().toString().trim();
                CurrentUserCountry=UserCountry.getText().toString().trim();
                CurrentUserMobile=UserMobile.getText().toString().trim();

                CurrentUserRegno=userregno.getText().toString().trim();
                CurrentUserDept=userdept.getText().toString().trim();
                CurrentUserYear=useryear.getText().toString().trim();

                if (TextUtils.isEmpty(CurrentUserFullName))
                {
                    Toast.makeText(SetupActivity.this, "Enter Your Name!!!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(CurrentUserName))
                {
                    Toast.makeText(SetupActivity.this, "Enter Your UserName!!!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(CurrentUserCountry))
                {
                    Toast.makeText(SetupActivity.this, "Enter Your Country!!!", Toast.LENGTH_SHORT).show();
                }
                else if (CurrentUserMobile.length() !=10 || TextUtils.isEmpty(CurrentUserMobile))
                {
                    Toast.makeText(SetupActivity.this, "Please Given Valid Mobile Number", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    registerUser();



                    SavingUserInformation();
                }
            }
        });



    }

    private void SavingUserInformation()
    {
        /*loadingBar.setTitle("Saving Information");
        loadingBar.setMessage("Please wait");
        loadingBar.show();
        loadingBar.setCancelable(false);*/

        spotsDialog.show();

        StorageReference filePath = UserProfileImageRef.child(CurrentUserId + ".jpg");

        if (ResultUri !=null)
        {
            filePath.putFile(ResultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful())
                    {

                        downloadUrl = task.getResult().getDownloadUrl().toString();
                        SavingInfoUser();

                    }
                }
            });

        }
        else
        {
            SavingInfoUser();
        }
    }

    private void SavingInfoUser()
    {
        Intent intent=getIntent();
        Log.d("pass1=", String.valueOf(intent.getStringExtra(Config.KEY_PASSWORD)));


        HashMap userMap = new HashMap();
        userMap.put("username", CurrentUserName);
        userMap.put("regno",CurrentUserRegno);
        userMap.put("fullname", CurrentUserFullName);
        userMap.put("country", CurrentUserCountry);
        userMap.put("profileimage",downloadUrl);
        userMap.put("status", "Hey there, i am using Technotude , developed by Students.");
        userMap.put("gender", "none");
        userMap.put("dob", "none");
        userMap.put("relationshipstatus", "none");
        userMap.put("mobile",CurrentUserMobile);
        userMap.put("password",String.valueOf(intent.getStringExtra(Config.KEY_PASSWORD)));

        UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if(task.isSuccessful())
                {
                    storecount();
                    sendMail();
                    SendUserToMainActivity();
                    spotsDialog.dismiss();
                    //loadingBar.dismiss();
                }
                else
                {
                    String message =  task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                    spotsDialog.dismiss();
                    //loadingBar.dismiss();
                }
            }
        });


    }

    private void sendMail()
    {


      //  String subject = "Your App Account Details";


        //Send Mail

        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("chandrushakthi8@gmail.com", "121419999");
            }
        });

        ReceiveFeedTask tssk=new ReceiveFeedTask();
        tssk.execute();

        //Send Mail
        // JavaMailAPI javaMailAPI = new JavaMailAPI(this,userEmail,subject,message);
        // javaMailAPI.execute();
    }

    class ReceiveFeedTask extends AsyncTask<String,Void,String>

    {
        @Override
        protected String doInBackground(String... strings)
        {

            try
            {

                String subject = "Your TECHNOTUDE App Account Details";
                String mmessage= "Username: "+CurrentUserFullName+"\n"+"Regno: "+CurrentUserRegno+"\n"+ "E-mail: "+userEmail+"\n" +"Password: "+CurrentUserMobile+
                        "\n"+"Department: "+CurrentUserDept+"\n"+"Mobileno: "+CurrentUserMobile+"\n"+"Year: "+CurrentUserYear+"\n";
                Log.d("email",userEmail);

                Message message =new MimeMessage(session);
                message.setFrom(new InternetAddress("chandrushakthi8@gmail.com"));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
                //Adding subject
                message.setSubject(subject);
                //Adding message
                // message.setContent(mmessage,"text/html; charset=utf-8");
                //Sending email
                message.setText(mmessage);
                Transport.send(message);
            }catch (MessagingException e)
            {
                e.printStackTrace();

            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {

           // Toast.makeText(SetupActivity.this, "Message Send", Toast.LENGTH_LONG).show();
        }
    }

    private void storecount()
    {
        int count=2;
        currentuserid=mAuth.getCurrentUser().getUid();
        countRef.child(currentuserid).child("count").setValue(String.valueOf(count));
    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.putExtra("count",count);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null)
        {
            ImageUri = data.getData();

            CropImage.activity(ImageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {

                ResultUri = result.getUri();
                UserProfileImage.setImageURI(ResultUri);
            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void registerUser() {
        class AddUser extends AsyncTask<Void,Void,String> {

            //ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // loadingBar = ProgressDialog.show(SetupActivity.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                ////  loadingBar.dismiss();
                Toast.makeText(SetupActivity.this,s,Toast.LENGTH_SHORT).show();
                //  Toast.makeText(RegisterActivity.this,"Your Details are Submited Plrase Wait for authenticatikn Then you get Loged in",Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v)
            {




                HashMap<String,String> params = new HashMap<>();
                params.put(Config.KEY_REGNO,CurrentUserRegno);
                params.put(Config.KEY_NAME,CurrentUserFullName);
                params.put(Config.KEY_PHNO,CurrentUserMobile);
                params.put(Config.KEY_EMAIL,userEmail);
                params.put(Config.KEY_PASSWORD,CurrentUserMobile);
                params.put(Config.KEY_DEPT,CurrentUserDept);
                params.put(Config.KEY_YEAR,CurrentUserYear);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_REGISTER, params);
                return res;


            }
        }

        AddUser ae = new AddUser();
        ae.execute();
    }



}
