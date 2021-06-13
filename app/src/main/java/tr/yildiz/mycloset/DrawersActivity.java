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
import android.view.View;
import android.widget.Button;
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

public class DrawersActivity extends AppCompatActivity implements AttentionDialog.DialogListener{
    private int position;
    private static final String DRAWERS_FILE_NAME = "drawers.txt";
    private DrawerAdapter adapter;
    private FloatingActionButton floatingBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawers);
        setTitle("Drawers");

        defineVariables(getDrawersList());
        defineListeners();
    }


    /**
     * define the recyclerView vars
     */
    private void defineVariables(List<Drawer> data) {
        adapter = new DrawerAdapter();
        adapter.setList(data);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getApplicationContext(),DrawersContent.class);
            Bundle myBundle = new Bundle();
            myBundle.putString("drawerName",adapter.getList().get(position).getDrawerName());
            myBundle.putBoolean("fromDrawer",true);
            intent.putExtras(myBundle);
            startActivity(intent, myBundle);
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);
        floatingBtn = findViewById(R.id.floating);
    }

    private void defineListeners() {
        floatingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),AddDrawer.class);
            startActivityForResult(intent,20);
        });


    }

    /**
     * loads the saved clothes items from internal Storage
     * @return
     * returns list of cloths
     */
    private List<Drawer> getDrawersList() {
        List<Drawer> list = new ArrayList<Drawer>();
        try {
            FileInputStream fis = openFileInput(DRAWERS_FILE_NAME);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                Drawer item = new Drawer(line.trim());
                list.add(item);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20){
            if(resultCode == RESULT_OK){
                adapter.setList(getDrawersList());
                adapter.notifyDataSetChanged();
            }
        }
    }

    public boolean onContextItemSelected(@NonNull MenuItem item) {
        position = item.getGroupId();
        if (item.getItemId() == 12) {
            openDialog();
            return true;
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
    public void onDeleteClicked() {
        deleteContent("drawers_"+adapter.getList().get(position).getDrawerName()+".txt");
        adapter.removeItem(position);
        saveChanges(adapter.getList());
        Toast.makeText(this, "Item deleted !", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the file with the passed list
     * @param list
     * clothes list
     */
    private void saveChanges(List<Drawer> list) {
        deleteContent(DRAWERS_FILE_NAME);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(DRAWERS_FILE_NAME, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ));
        for(Drawer item: list){
            String fileContents = item.getDrawerName();
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