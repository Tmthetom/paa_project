package moravec_tomas.todolist;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ItemActivity extends FragmentActivity {

    String imagePath = "";
    long date_from = 0;
    long date_to = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        // Elements
        EditText editText_Name = (EditText) findViewById(R.id.editText_Name);
        EditText editText_Description = (EditText) findViewById(R.id.editText_Description);

        // Get data from calling activity
        Intent callingActivity = getIntent();
        String name = callingActivity.getExtras().getString("NAME");
        String description = callingActivity.getExtras().getString("DESCRIPTION");
        imagePath = callingActivity.getExtras().getString("IMAGEPATH");
        date_from = callingActivity.getExtras().getLong("DATE_FROM");
        date_to = callingActivity.getExtras().getLong("DATE_TO");

        // Show texts
        editText_Name.setText(name);
        editText_Description.setText(description);

        // Set image
        if (!ShowImage(imagePath)){  // If not set
            Button button_setImage = (Button) findViewById(R.id.button_setImage);
            button_setImage.setText("Add image");
        }

        // Show dates
        UpdateDateTime();
    }

    private void UpdateDateTime(){
        EditText editText_Created = (EditText) findViewById(R.id.editText_Created);
        EditText editText_Created_Time = (EditText) findViewById(R.id.editText_Created_Time);
        EditText editText_Ending = (EditText) findViewById(R.id.editText_Ending);
        EditText editText_Ending_Time = (EditText) findViewById(R.id.editText_Ending_Time);

        editText_Created.setText(DateFormat(date_from));
        editText_Created_Time.setText(TimeFormat(date_from));
        editText_Ending.setText(DateFormat(date_to));
        editText_Ending_Time.setText(TimeFormat(date_to));
    }

    // Convert date format
    private String DateFormat(Long ms){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date resultDate = new Date(ms);
        return sdf.format(resultDate);
    }

    // Convert time format
    private String TimeFormat(Long ms){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date resultDate = new Date(ms);
        return sdf.format(resultDate);
    }

    // Show image
    private boolean ShowImage(String path){
        try{
            File imgFile = new File(path);
            if(imgFile.exists()){

                // Show image
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(BitmapFactory.decodeFile(path));

                // Change button text
                Button button_setImage = (Button) findViewById(R.id.button_setImage);
                button_setImage.setText("Change image");

                return true;
            }
        }catch (Exception e){
            ;
        }

        return false;
    }

    // Change image
    public void changeImage(View view){
        if (checkPermission()){
            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 3);
        }
    }

    // Control external storage permission
    public boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            // Permission is granted
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;

            // Permission is revoked
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }

        // Permission is automatically granted on sdk<23 upon installation
        else {
            return true;
        }
    }

    // Change date to
    public void changeDateClick(View w){
        Calendar calendar_old = Calendar.getInstance();
        calendar_old.setTimeInMillis(date_to);
        DatePickerDialog datePick = new DatePickerDialog(ItemActivity.this, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Old date
                Calendar calendar_old = Calendar.getInstance();
                calendar_old.setTimeInMillis(date_to);
                int hours = calendar_old.get(Calendar.HOUR);  // Get hours
                int minutes = calendar_old.get(Calendar.MINUTE);  // Get minutes

                // New date with old time
                Calendar calendar_new = new GregorianCalendar(year, month, dayOfMonth, hours, minutes);
                date_to = calendar_new.getTimeInMillis();  // Set new time
                UpdateDateTime();  // Update showed date and time

                Toast.makeText(ItemActivity.this, "Ending date changed", Toast.LENGTH_LONG).show();
            }
        },calendar_old.get(Calendar.YEAR), calendar_old.get(Calendar.MONTH), calendar_old.get(Calendar.DAY_OF_MONTH));
        datePick.setTitle("Select date");
        datePick.show();
    }

    // Change time to
    public void changeTimeClick(View w){
        Calendar calendar_old = Calendar.getInstance();
        calendar_old.setTimeInMillis(date_to);
        TimePickerDialog timePick = new TimePickerDialog(ItemActivity.this, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Old date
                Calendar calendar_old = Calendar.getInstance();
                calendar_old.setTimeInMillis(date_to);
                int year = calendar_old.get(Calendar.YEAR);  // Get hours
                int month = calendar_old.get(Calendar.MONTH);  // Get minutes
                int dayOfMonth = calendar_old.get(Calendar.DAY_OF_MONTH);

                // New date with old time
                Calendar calendar_new = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute);
                date_to = calendar_new.getTimeInMillis();  // Set new time
                UpdateDateTime();  // Update showed date and time

                Toast.makeText(ItemActivity.this, "Ending time changed", Toast.LENGTH_LONG).show();
            }
        },calendar_old.get(Calendar.HOUR), calendar_old.get(Calendar.MINUTE), true);
        timePick.setTitle("Select date");
        timePick.show();
    }

    // Delete button
    public void buttonClick(View view) {
        PutExtras(1);  // Intent with calling back activity
        finish();  // Close activity
    }

    // Back button
    @Override
    public void onBackPressed() {
        PutExtras(2);  // Intent with calling back activity
        finish();  // Close activity
    }

    // Put return values
    private void PutExtras(int result){

        // Elements
        EditText editText_Name = (EditText) findViewById(R.id.editText_Name);
        EditText editText_Description = (EditText) findViewById(R.id.editText_Description);

        // Put extras and return intent
        Intent intent = new Intent();  // Returning intent
        Intent callingIntent = getIntent();  // Intent with old activity
        intent.putExtra("NAME", editText_Name.getText().toString());  // Return calling name
        intent.putExtra("DESCRIPTION", editText_Description.getText().toString());  // Return calling name
        intent.putExtra("IMAGEPATH", imagePath);  // Return calling name
        intent.putExtra("DATE_FROM", date_from);  // Return calling name
        intent.putExtra("DATE_TO", date_to);  // Return calling name
        intent.putExtra("POSITION", callingIntent.getExtras().getInt("POSITION"));  // Get position
        setResult(result, intent);  // Set result code and intent
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Image selected
        if (requestCode == 3 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // If image is set, save path
            if (ShowImage(picturePath)){
                imagePath = picturePath;

                // Show notification
                Toast.makeText(this, "Image saved", Toast.LENGTH_LONG).show();
            }
        }
    }
}
