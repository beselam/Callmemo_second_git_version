package com.example.gbese.callmemo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import static android.nfc.NfcAdapter.EXTRA_ID;

public class Displayer extends AppCompatActivity {
    EditText displaytitle1, displaycontent1;
    SQLiteDatabase db;
    SQLiteOpenHelper data;
    Cursor ccursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayer);
        displaytitle1 = (EditText) findViewById(R.id.displaytitle);
        displaycontent1 = (EditText) findViewById(R.id.displaycontent);
        int ListId = (Integer) getIntent().getExtras().get(EXTRA_ID);
        data = new UserData(this);
        ccursor = ((UserData) data).getListContent(ListId);

        if (ccursor.moveToFirst()) {
            String ttitle = ccursor.getString(1);
            String ccontent = ccursor.getString(2);
            displaytitle1.setText(ttitle);
            displaycontent1.setText(ccontent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save2,menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.noteitem2){
            String newcontent = displaycontent1.getText().toString();
            String newtitle = displaytitle1.getText().toString();

            int ListId = (Integer) getIntent().getExtras().get(EXTRA_ID);
            SQLiteOpenHelper data = new UserData(this);

            ((UserData) data).UpdateData(newtitle,newcontent,ListId);

            boolean mm = ((UserData) data).UpdateData(newtitle,newcontent,ListId);
            if(mm){
                Toast.makeText(getApplicationContext(), "Memo is updated :)", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Data is not updated :(", Toast.LENGTH_LONG).show();
            }
        }
        else if(item.getItemId()==R.id.noteitem22){

            int ListId2 = (Integer) getIntent().getExtras().get(EXTRA_ID);
            SQLiteOpenHelper daata = new UserData(this);
            ((UserData) daata).DeleteData(ListId2);
            boolean deleteddata = ((UserData) daata).DeleteData(ListId2);
            if(deleteddata){
                Toast.makeText(getApplicationContext(), "Memo is Deleted :)", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Memo is not deleted  :)", Toast.LENGTH_LONG).show();

            }

            }
        else if(item.getItemId() == R.id.set_calander){

            Intent intent1 = new Intent(Intent.ACTION_INSERT);
            intent1.setData(CalendarContract.Events.CONTENT_URI);
            startActivity(intent1);
        }
        else if(item.getItemId() == R.id.share_content){

            EditText shrecontent1 = (EditText) findViewById(R.id.displaycontent);
            String sharecontent = shrecontent1.getText().toString();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, sharecontent);
            startActivity(Intent.createChooser(intent,"Share via"));
            return true;
        }

        finish();
        return true;

    }
}