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

public class DrawersContent extends AppCompatActivity implements AttentionDialog.DialogListener{
    private static final String DELIMITER = "#";
    private int position;
    private String fileName;
    private ShowClothesAdapter adapter;
    private FloatingActionButton add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawers_content);

        Bundle b = getIntent().getExtras();
        String prefix;
        if(b.getBoolean("fromDrawer")){
            prefix = "drawers_";
            setTitle("Drawer Content");
        }else{
            prefix = "events_";
            setTitle("Event Content");
        }
        fileName = prefix+b.getString("drawerName")+".txt";
        defineVariables(getClothesList(fileName));
        defineListeners();
    }

    /**
     * define the recyclerView vars
     */
    private void defineVariables(List<Clothes> data) {
        adapter = new ShowClothesAdapter();
        adapter.setList(data);
        adapter.setAppCompatActivity(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);

        // addition button
        add = (FloatingActionButton) findViewById(R.id.addClothItem2);

    }

    private void defineListeners() {
        adapter.setOnItemClickListener(position -> Toast.makeText(getApplicationContext(), "Long press to delete or edit !", Toast.LENGTH_LONG).show());

        add.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ShowClothes.class);
            Bundle myBundle = new Bundle();
            myBundle.putString("drawerName",fileName);
            intent.putExtras(myBundle);
            startActivityForResult(intent,20, myBundle);
        });
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

    /**
     * loads the saved clothes items from internal Storage
     * @return
     * returns list of cloths
     */
    private List<Clothes> getClothesList(String fileName) {
        List<Clothes> list = new ArrayList<Clothes>();
        String[] currentLine;
        try {
            FileInputStream fis = openFileInput(fileName);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                currentLine = line.split(DELIMITER);
                Clothes item = new Clothes(currentLine[0],currentLine[1],currentLine[3],currentLine[2]);
                item.setUri(currentLine[4]);
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
                adapter.setList(getClothesList(fileName));
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDeleteClicked() {

        adapter.removeItem(position);
        saveChanges(adapter.getList());
        Toast.makeText(this, "Item deleted !", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the file with the passed list
     * @param list
     * clothes list
     */
    private void saveChanges(List<Clothes> list) {
        deleteContent(fileName);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ));
        for(Clothes item: list){
            String fileContents = item.getClothingType()+ DELIMITER +item.getClothingColor()+ DELIMITER +item.getPrice()
                    + DELIMITER +item.getDateOfPurchase()+ DELIMITER +item.getUri();
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