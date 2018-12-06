package com.example.gbese.callmemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static android.nfc.NfcAdapter.EXTRA_ID;

public class MainActivity extends AppCompatActivity {
    ListView coontentList;
    SQLiteDatabase db;

    Cursor cursor;
    private TextView mTextMessage;
    BottomNavigationView bottonNavigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottonNavigation =(BottomNavigationView) findViewById(R.id.navigation);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        coontentList = (ListView) findViewById(R.id.contentlist);
        registerForContextMenu(coontentList);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},1);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        SQLiteOpenHelper data = new UserData(this);
        try {
            db = data.getReadableDatabase();
            cursor = db.query("CALLABLE",
                    new String[]{"_id", "CONTENT"},
                    null, null, null, null, null);
            SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"CONTENT"},
                    new int[]{android.R.id.text1},
                    0);
            coontentList.setAdapter(listAdapter);

        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        AdapterView.OnItemClickListener itemClickListener= new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,Displayer.class);
                intent.putExtra(EXTRA_ID, (int) id);
                startActivity(intent);
            }

        };
        coontentList.setOnItemClickListener(itemClickListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.writetext,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.noteitem) {

            Intent intent = new Intent(this, UserNote.class);
            startActivity(intent);
        }
        else if (item.getItemId()==R.id.callitem) {

            Intent iintent = new Intent(Intent.ACTION_DIAL);
            iintent.setData(Uri.parse("tel:"));
            startActivity(iintent);

        }
        return true;
    }

    public void nextacti(View view) {
        Intent intent= new Intent(this,UserNote.class);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.long_press,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo listViewInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        long selectedListId = listViewInfo.id;
        switch (item.getItemId()){
            case R.id.delete1:
                SQLiteOpenHelper daata = new UserData(this);
                ((UserData) daata).contextMenuDeleteData(selectedListId);
                boolean deletedata = ((UserData) daata).contextMenuDeleteData(selectedListId);
                if(deletedata){
                    Toast.makeText(this, "memo is deleted :)", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "memo is not deleted :(", Toast.LENGTH_SHORT).show();
                }
                onStart();
                return true;

            case R.id.share1:
                long selectedListId2 = listViewInfo.id;
                SQLiteOpenHelper daata2 = new UserData(this);
                Cursor cursor = ((UserData) daata2).getListContextMenuContent(selectedListId2);

                if (cursor.moveToFirst()) {
                    String contenet = cursor.getString(2);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, contenet);
                    startActivity(Intent.createChooser(intent,"Share via"));
                }
                default:
                    return super.onContextItemSelected(item);
        }

    }
}
