package com.example.gbese.callmemo;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

public class UserNote extends AppCompatActivity {
    EditText title1,content1;
    UserData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_note);
        title1 = (EditText) findViewById(R.id.displaytitle);
        content1 = (EditText) findViewById(R.id.displaycontent);
        data = new UserData(this);

    }
    public void addData() {
        String title;
        String content;
        title = title1.getText().toString();
        content = content1.getText().toString();



        boolean isinserted = data.addData(title, content);
        if (isinserted == true) {
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(getApplicationContext(), "Data not saved", Toast.LENGTH_LONG).show();
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.savemanu:
                String title;
                String content;
                title = title1.getText().toString();
                content = content1.getText().toString();
                if (title.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Please Enter a Title", Toast.LENGTH_LONG).show();
                } else if (content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter a content", Toast.LENGTH_LONG).show();
                } else {
                    addData();
                }
                return true;

            case R.id.share_content:
                EditText shrecontent1 = (EditText) findViewById(R.id.displaycontent);
                String sharecontent = shrecontent1.getText().toString();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, sharecontent);
                startActivity(Intent.createChooser(intent,"Share via"));
                return true;

            case R.id.set_calander:
                Intent intent1 = new Intent(Intent.ACTION_INSERT);
                intent1.setData(CalendarContract.Events.CONTENT_URI);
                startActivity(intent1);
        }
        return true;
    }


}