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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

/**
 * this is the main activity to display the list
 */
public class MainActivity extends AppCompatActivity {
    ListView coontentList;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         coontentList = (ListView) findViewById(R.id.contentlist);
         // below , registering the listview for the context menu
        registerForContextMenu(coontentList);
//this if method is for OurPhoneStateReceiver class , incase the broadcast receiver didn't get
// a permission through the manifest then this method ask the user to give the access
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},1);
        }

    }


    /**
     * here is our cursor and simplecursoradapter
     * the curser get the data and the adapter display the list
     * we use try catch to run our app smoothly , so so if the data is not found the user get the toast
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        SQLiteOpenHelper data = new UserData(this);
        try {
            db = data.getReadableDatabase();
            cursor = db.query("CALLABLE",
                    new String[]{"_id", "TITLE"},
                    null, null, null, null, null);
            SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]{"TITLE"},
                    new int[]{android.R.id.text1},
                    0);
            // below , set the adapter inside the listview
            coontentList.setAdapter(listAdapter);

        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        /**
         * click listener for the listview
         * this will take the user to Displayer activity to edit or see his memo
         */
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

    /**
     * inflating menu icons in this activity
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.writetext,menu);

        return true;
    }

    /**
     * set action for the clicked menu icon
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // this icon take the user to the Userdata activity
        if (item.getItemId()==R.id.noteitem) {

            Intent intent = new Intent(this, UserNote.class);
            startActivity(intent);
        }
        // this icon use an intent to take the user to his call log or contact
        else if (item.getItemId()==R.id.callitem) {

            Intent iintent = new Intent(Intent.ACTION_DIAL);
            iintent.setData(Uri.parse("tel:"));
            startActivity(iintent);

        }

        return true;
    }

    /**
     * this is the method for the FAB button and it take the user to the UserNote activity to create memo
     * @param view
     */
    public void fabNextaActivity(View view) {
        Intent intent= new Intent(this,UserNote.class);
        startActivity(intent);
    }

    /**
     * inflating the contextmenu
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.long_press,menu);
    }

    /**
     * setting action fot the selected context menu options
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //below; using the adapterview we sore the data of the selected item in listViewInfo
        AdapterView.AdapterContextMenuInfo listViewInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        //we sore the id of the selected item in a new instance variable
        // we use this id to delete and share the data of the selected item
        long selectedListId = listViewInfo.id;
        switch (item.getItemId()){
            // this option get the data from the cursor and delete it
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
// this option get the content of the selected item and share it using implicit intent
            case R.id.share1:
                long selectedListId2 = listViewInfo.id;
                SQLiteOpenHelper daata2 = new UserData(this);
                Cursor cursor = ((UserData) daata2).getListContextMenuContent(selectedListId2);
// below; the curser is browsing the table of our database to get our data
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
