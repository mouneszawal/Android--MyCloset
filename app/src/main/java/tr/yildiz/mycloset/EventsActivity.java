package tr.yildiz.mycloset;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity implements AttentionDialog.DialogListener{
    private static final String EVENTS_FILE_NAME = "events.txt";
    private static final String DELIMITER = "#";
    private FloatingActionButton btn;
    private EventsAdapter adapter;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        setTitle("Events");
        defineVariables();
        defineListeners();
    }




    private void defineVariables() {
        adapter = new EventsAdapter();
        adapter.setList(getEventsList());
        adapter.setAppCompatActivity(this);
        RecyclerView recyclerView = findViewById(R.id.eventsRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);
        btn = (FloatingActionButton) findViewById(R.id.addEvent);
    }

    private List<Event> getEventsList() {
        List<Event> list = new ArrayList<Event>();
        String[] currentLine;
        try {
            FileInputStream fis = openFileInput(EVENTS_FILE_NAME);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                currentLine = line.split(DELIMITER);
                Event item = new Event(currentLine[0],currentLine[2]);
                item.setType(currentLine[1]);
                item.setLocation(currentLine[3]);
                item.setNotify(Integer.parseInt(currentLine[4]));
                list.add(item);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void defineListeners() {
        adapter.setOnItemClickListener(position -> {
            Toast.makeText(getApplicationContext(), "Long press to delete or edit !", Toast.LENGTH_LONG).show();
        });

        btn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),AddEvent.class);
            startActivityForResult(intent,20);
        });
    }
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        position = item.getGroupId();
        if (item.getItemId() == 12) {
            openDialog();
            return true;
        }else if (item.getItemId() == 13){
            Intent intent = new Intent(this,AddEvent.class);
            Bundle bundle = new Bundle();
            bundle.putString("eventName",adapter.getList().get(position).getName());
            bundle.putString("eventType",adapter.getList().get(position).getType());
            bundle.putString("eventDate",adapter.getList().get(position).getDate());
            bundle.putString("eventLocation",adapter.getList().get(position).getLocation());
            intent.putExtras(bundle);
            startActivityForResult(intent,21);
        }
        return super.onContextItemSelected(item);
    }

    /**
     * opens an attention dialog so the user can confirm
     * the operation
     */
    private void openDialog() {
        AttentionDialog dialog = new AttentionDialog();
        dialog.show(getSupportFragmentManager(),"Confirm");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20){
            if(resultCode == RESULT_OK){
                adapter.setList(getEventsList());
                adapter.notifyDataSetChanged();
            }
        }else if (requestCode == 21){
            //Do something
            if (data != null){
                Bundle b = data.getExtras();
                adapter.getList().get(position).setName(b.getString("eventName"));
                adapter.getList().get(position).setType(b.getString("eventType"));
                adapter.getList().get(position).setDate(b.getString("eventDate"));
                adapter.getList().get(position).setLocation(b.getString("eventLocation"));
                adapter.getList().get(position).setNotify(b.getInt("eventNotify"));
                saveChanges(adapter.getList());
                adapter.setList(getEventsList());
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDeleteClicked() {
        deleteContent("events_"+adapter.getList().get(position).getName()+".txt");
        adapter.removeItem(position);
        saveChanges(adapter.getList());
        Toast.makeText(this, "Item deleted !", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the file with the passed list
     * @param list
     * clothes list
     */

    public void saveChanges(List<Event> list) {
        deleteContent(EVENTS_FILE_NAME);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(EVENTS_FILE_NAME, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ));
        for(Event item: list){
            String fileContents = item.getName()+ DELIMITER +item.getType()+ DELIMITER +item.getDate()
                    + DELIMITER +item.getLocation()+DELIMITER +item.getNotify();
            writer.println(fileContents);
        }
        writer.close();
    }

    /**
     * deletes a file's content
     * @param fileName
     * the file name
     */
    private void deleteContent(String fileName) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
        writer.print("");
        writer.close();
    }
}