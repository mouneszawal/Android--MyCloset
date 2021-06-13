package tr.yildiz.mycloset;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AddEvent extends AppCompatActivity {
    private static final String EVENTS_FILE_NAME = "events.txt";
    private static final String DELIMITER = "#";
    private TextView myDate,location,dis1,dis2,wear;
    private ImageButton addDate,addLocation;
    private Button updateEvent;
    private TextInputEditText type,name;
    private MaterialDatePicker.Builder builder;
    private MaterialDatePicker materialDatePicker;
    private FloatingActionButton addEventItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        //prevent keyboard from pupping up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        defineVariables();
        defineListeners();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null ){
            fillFields(bundle);
        }
    }

    private void fillFields(Bundle bundle) {
        addEventItem.setVisibility(View.GONE);
        updateEvent.setVisibility(View.VISIBLE);
        wear.setVisibility(View.GONE);
        dis2.setVisibility(View.GONE);
        location.setVisibility(View.VISIBLE);
        dis1.setVisibility(View.GONE);
        myDate.setVisibility(View.VISIBLE);
        name.setText(bundle.getString("eventName"));
        type.setText(bundle.getString("eventType"));
        myDate.setText(bundle.getString("eventDate"));
        location.setText(bundle.getString("eventLocation"));

    }


    private void defineVariables() {
        myDate = findViewById(R.id.myDate);
        dis1 = findViewById(R.id.dis1);
        dis2 = findViewById(R.id.dis2);
        location = findViewById(R.id.location);
        addDate = (ImageButton) findViewById(R.id.addDate);
        addLocation = (ImageButton) findViewById(R.id.addLocation);
        addEventItem = (FloatingActionButton) findViewById(R.id.addEventItem);
        name = (TextInputEditText) findViewById(R.id.eventName);
        type = (TextInputEditText) findViewById(R.id.eventType);
        updateEvent = findViewById(R.id.updateEvent);
        wear = findViewById(R.id.wear);
        //constraints
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointForward.now());
        //Date Picker
        builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("SELECT DATE");
        builder.setCalendarConstraints(constraintBuilder.build());
        materialDatePicker = builder.build();
    }

    private void defineListeners() {
        addDate.setOnClickListener(v -> {
            dis1.setVisibility(View.GONE);
            myDate.setVisibility(View.VISIBLE);
            materialDatePicker.show(getSupportFragmentManager(),"Date_Picker");
        });

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            myDate.setText(materialDatePicker.getHeaderText());
        });
        addLocation.setOnClickListener(v -> {
            dis2.setVisibility(View.GONE);
            location.setVisibility(View.VISIBLE);
            Intent intent = new Intent(v.getContext(),showMap.class);
            startActivityForResult(intent,20);
        });

        addEventItem.setOnClickListener(v -> {
            if (valid()){
                if (!eventNameExist()){
                    Event event = new Event(name.getText().toString().trim(),myDate.getText().toString().trim());
                    event.setLocation(location.getText().toString());
                    event.setType(type.getText().toString().trim());
                    event.setNotify(0);
                    saveEventItem(event);
                    Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }else{
                Toast.makeText(this, "Please Fill all Fields!", Toast.LENGTH_SHORT).show();
            }
        });

        updateEvent.setOnClickListener(v -> {
            if(valid()){
                Intent intentResult = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("eventName",name.getText().toString().trim());
                bundle.putString("eventType",type.getText().toString().trim());
                bundle.putString("eventDate",myDate.getText().toString());
                bundle.putString("eventLocation",location.getText().toString().trim());
                bundle.putInt("eventNotify",0);
                Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_SHORT).show();

                intentResult.putExtras(bundle);
                setResult(RESULT_OK,intentResult);
                finish();
            }else{
                Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean valid() {
        return !name.getText().toString().trim().equals("") &&
                !type.getText().toString().trim().equals("") &&
                !myDate.getText().toString().equals("") &&
                !location.getText().toString().equals("");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20){
            if (resultCode == RESULT_OK){
                if(data != null){
                    location.setText(data.getExtras().getString("add"));
                    Toast.makeText(this, data.getExtras().getString("add"), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * takes a cloth item and saves its data to internal storage
     * @param item
     * clothes item
     */
    private void saveEventItem(Event item){
        String fileContents = item.getName()+ DELIMITER +item.getType()+ DELIMITER +item.getDate()
                + DELIMITER +item.getLocation()+DELIMITER + item.getNotify();
        try {
            FileOutputStream fos = openFileOutput(EVENTS_FILE_NAME, Context.MODE_APPEND);
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
            writer.println(fileContents);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean eventNameExist() {
        String[] currentLine;
        try {
            FileInputStream fis = openFileInput(EVENTS_FILE_NAME);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                currentLine = line.split(DELIMITER);
                if (currentLine[0].equals(name.getText().toString().trim())){
                    Toast.makeText(this, "The Event name already exists!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}