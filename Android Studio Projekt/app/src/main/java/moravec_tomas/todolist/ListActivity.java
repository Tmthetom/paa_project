package moravec_tomas.todolist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Comparator;

public class ListActivity extends AppCompatActivity {

    // Data
    ArrayList<Item> items;
    ArrayAdapter<Item> adapter;
    Database database;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout
        setContentView(R.layout.list);

        // Elements from activity
        final ListView listView = (ListView) findViewById(R.id.listView);
        final EditText editText = (EditText) findViewById(R.id.editText);

        // Create instance of database
        database = new Database(this);  // New database

        // Load data from database
        items = database.GetItems();

        // Connect adapter and listView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);  // Create adapter
        listView.setAdapter(adapter);  // Set adapter


        // Handle button click on virtual keyboard
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {  // If key down
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {  // If enter
                        //HideKeyboard();  // Hide keyboard
                        editText.setFocusableInTouchMode(true);
                        editText.setFocusable(true);
                        editText.requestFocus();
                        String text = editText.getText().toString();  // Get text from textBox
                        AddItem(text);  // Add new item
                        editText.setText("");  // Clear old text

                        // Put focus back in the EditText after brief delay
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                editText.requestFocus();  // Request focus
                            }
                        }, 200);

                        return true;
                    }
                }
                return false;
            }
        });

        // ListView item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OpenItemDetail(items.get(position), position);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // Create option menu in right up corner
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // Menu item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_all:
                if (items.size() > 0){
                    items.clear();
                    Toast.makeText(this, "All deleted", Toast.LENGTH_LONG).show();
                    adapter.notifyDataSetChanged();  // Update view
                    return true;
                }
                else{
                    Toast.makeText(this, "List is empty", Toast.LENGTH_LONG).show();
                    return false;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Add item to list and refresh view
     *
     * @param name
     * @return
     */
    boolean AddItem(String name) {
        if (name.length() > 0) {  // When textBox not empty
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();  // Capitalise first char
            //database.InsertRow(name);  // Insert into database
            items.add(new Item(name));  // Add new text
            adapter.notifyDataSetChanged();  // Update view
            return true;
        }
        return false;
    }

    /**
     * Delete item from list and refresh view
     *
     * @param position
     * @return
     */
    boolean DeleteItem(int position) {
        //database.DeleteRow(items.get(position));  // Remove from database
        items.remove(position);  // Remove from list
        adapter.notifyDataSetChanged();  // Update view
        return true;
    }

    /**
     * Hide keyboard when called
     *
     * @return true when success
     */
    boolean HideKeyboard() {

        // Get current focus
        View view = getCurrentFocus();

        // If there is keyboard
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        }

        // If there is not
        else {
            return false;
        }
    }

    /**
     * Open detail of selected item
     *
     * @param item Instance of item to show
     */
    void OpenItemDetail(Item item, int position) {
        Intent itemDetailScreen = new Intent(this, ItemActivity.class);
        itemDetailScreen.putExtra("NAME", item.getName());
        itemDetailScreen.putExtra("DESCRIPTION", item.getDescription());
        itemDetailScreen.putExtra("IMAGEPATH", item.getImagePath());
        itemDetailScreen.putExtra("DATE_FROM", item.getDate_from());
        itemDetailScreen.putExtra("DATE_TO", item.getDate_to());
        itemDetailScreen.putExtra("POSITION", position);
        startActivityForResult(itemDetailScreen, 1);
    }

    /**
     * Catch return of closed detail and process any changes
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Delete item
        if (resultCode == 1) {

            // Get position and item from position
            int position = data.getExtras().getInt("POSITION");
            Item item = items.get(position);

            // Show notification
            Toast.makeText(this, item.getName() + " was deleted", Toast.LENGTH_LONG).show();

            // Delete item
            DeleteItem(position);
        }

        // Check any changes
        else if (resultCode == 2) {
            int position = data.getExtras().getInt("POSITION");
            Item item = items.get(position);
            String name = data.getExtras().getString("NAME");
            String description = data.getExtras().getString("DESCRIPTION");
            String imagePath = data.getExtras().getString("IMAGEPATH");
            long date_from = data.getExtras().getLong("DATE_FROM");
            long date_to = data.getExtras().getLong("DATE_TO");

            if (!item.getName().equals(name)) {  // Name changed
                item.setName(name);
            }
            if (!item.getDescription().equals(description)) {  // Description changed
                item.setDescription(description);
            }
            if (!item.getImagePath().equals(imagePath)) {  // Image changed
                item.setImagePath(imagePath);
            }
            if (item.getDate_from() != date_from) {  // Date-from changed
                item.setDate_from(date_from);
            }
            if (item.getDate_to() != date_to) {  // Date-to changed
                item.setDate_to(date_to);
            }

            adapter.notifyDataSetChanged();  // Update view
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("List Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    // Leaving application with back button
    @Override
    public void onBackPressed() {
        saveDatabase();
        finish();
        return;
    }

    // Clear database and save new values
    private void saveDatabase(){
        // Delete all rows
        database.ClearDatabase();

        // Insert all rows
        for (Item item : items) {
            database.InsertRow(item);
        }
    }
}