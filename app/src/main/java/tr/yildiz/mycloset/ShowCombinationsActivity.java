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

public class ShowCombinationsActivity extends AppCompatActivity implements AttentionDialog.DialogListener{
    private int position;
    private static final String COMB_FILE_NAME = "combinations.txt";
    private ShowCombinationAdapter adapter;
    private FloatingActionButton floatingBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_combinations);
        setTitle("Cabin Room");
        defineVariables(getCombinationsList());
        defineListeners();
    }

    /**
     * define the recyclerView vars
     */
    private void defineVariables(List<String> data) {
        adapter = new ShowCombinationAdapter();
        adapter.setList(data);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(getApplicationContext(),CabinRoomActivity.class);
            Bundle myBundle = new Bundle();
            myBundle.putString("CombinationName",adapter.getList().get(position));
            intent.putExtras(myBundle);
            startActivity(intent, myBundle);
        });

        RecyclerView recyclerView = findViewById(R.id.combinationsRecyclerView);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);
        floatingBtn = findViewById(R.id.floatingCombination);
    }

    private void defineListeners() {
        floatingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),CabinRoomActivity.class);
            startActivityForResult(intent,20);
        });


    }

    /**
     * loads the saved clothes items from internal Storage
     * @return
     * returns list of cloths
     */
    private List<String> getCombinationsList() {
        List<String> list = new ArrayList<String>();
        try {
            FileInputStream fis = openFileInput(COMB_FILE_NAME);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                list.add(line.trim());
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
                adapter.setList(getCombinationsList());
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
        deleteContent(adapter.getList().get(position)+".txt");
        adapter.removeItem(position);
        saveChanges(adapter.getList());
        Toast.makeText(this, "Item deleted !", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the file with the passed list
     * @param list
     * clothes list
     */
    private void saveChanges(List<String> list) {
        deleteContent(COMB_FILE_NAME);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(COMB_FILE_NAME, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ));
        for(String item: list){
            writer.println(item);
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