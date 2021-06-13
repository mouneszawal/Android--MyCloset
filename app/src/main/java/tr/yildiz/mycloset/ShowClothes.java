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
import java.util.Arrays;
import java.util.List;

public class ShowClothes extends AppCompatActivity implements AttentionDialog.DialogListener{
    private static final String CLOTHES_FILE_NAME = "clothes.txt";
    private static final String DELIMITER = "#";
    private int position;
    private String fileName;
    private ShowClothesAdapter adapter;
    private FloatingActionButton add;
    private boolean fromDrawer = false;
    private boolean selectOne = false;
    private boolean fromMain = false;
    private String btnName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_clothes);
        setTitle("My Clothes");
        Bundle b = getIntent().getExtras();
        if (b != null){
            if (b.getString("drawerName") != null){
                fromDrawer = true;
                fileName = b.getString("drawerName");
            }else{
                selectOne = b.getBoolean("select-one");
                btnName = b.getString("btn-name");
            }
            fromMain = b.getBoolean("fromMain");
        }
        defineVariables(getClothesList(CLOTHES_FILE_NAME));
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
        if (fromDrawer){
            adapter.setSelectionMode(true);
        }else if(selectOne){
            adapter.setSelectOneMode(true);
        }
        if (fromMain){
            adapter.setFromMainUpdate(true);
        }
    }

    private void defineListeners() {
        adapter.setOnItemClickListener(position -> Toast.makeText(getApplicationContext(), "Long press to delete or edit !", Toast.LENGTH_LONG).show());

        add.setOnClickListener(v -> {
            if(!fromDrawer){
                if (!selectOne){
                    Intent intent = new Intent(v.getContext(),AddClothItem.class);
                    startActivity(intent);
                }else{
                    if (adapter.getSelectedClothes().size() > 0){
                        Intent resultIntent = new Intent();
                        Bundle b = new Bundle();
                        b.putString("organ",btnName);
                        b.putString("uri",adapter.getSelectedClothes().get(0).getUri());
                        b.putString("fileName",adapter.getSelectedClothes().get(0).getFilename());
                        System.out.println(adapter.getSelectedClothes().get(0).getFilename());
                        resultIntent.putExtras(b);

                        setResult(RESULT_OK,resultIntent);
                        finish();
                    }else{
                        Toast.makeText(this, "Please Select an item!", Toast.LENGTH_SHORT).show();
                    }

                }
            }else{
                saveSelectedClothes(adapter.getSelectedClothes(),getFileUris(fileName));
                //go
                setResult(RESULT_OK);
                finish();
            }

        });
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
                item.setFilename(currentLine[5]);
                list.add(item);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<String> getFileUris(String fileName){
        String[] currentLine;
        List<String> list = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput(fileName);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                currentLine = line.split(DELIMITER);
                list.add(currentLine[4]);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveSelectedClothes(List<Clothes> list, List<String> uris) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ));
        for(Clothes item: list){
            if (!uris.contains(item.getUri())){
                String fileContents = item.getClothingType()+ DELIMITER +item.getClothingColor()+ DELIMITER +item.getPrice()
                        + DELIMITER +item.getDateOfPurchase()+ DELIMITER +item.getUri()+DELIMITER+item.getFilename();
                writer.println(fileContents);
            }
        }
        writer.close();
    }

    public boolean onContextItemSelected(@NonNull MenuItem item) {
        position = item.getGroupId();
        if (item.getItemId() == 12) {
            openDialog();
            return true;
        }else if(item.getItemId() == 13){
            Intent intent = new Intent(this,AddClothItem.class);
            Bundle bundle = new Bundle();
            bundle.putString("clothingType",adapter.getList().get(position).getClothingType());
            bundle.putString("clothingColor",adapter.getList().get(position).getClothingColor());
            bundle.putString("dateOfPurchase",adapter.getList().get(position).getDateOfPurchase());
            bundle.putString("clothingPrice",adapter.getList().get(position).getPrice());
            bundle.putString("clothingImage",adapter.getList().get(position).getUri());
            bundle.putString("clothingImageFileName",adapter.getList().get(position).getFilename());
            intent.putExtras(bundle);
            startActivityForResult(intent,20);
        }
        return super.onContextItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20){
            if(resultCode == RESULT_OK){
                //Do something
                if (data != null){
                    Bundle b = data.getExtras();
                    adapter.getList().get(position).setClothingType(b.getString("clothingType"));
                    adapter.getList().get(position).setClothingColor(b.getString("clothingColor"));
                    adapter.getList().get(position).setDateOfPurchase(b.getString("dateOfPurchase"));
                    adapter.getList().get(position).setPrice(b.getString("clothingPrice"));
                    adapter.getList().get(position).setUri(b.getString("clothingImage"));
                    adapter.getList().get(position).setFilename(b.getString("clothingImageFileName"));
                    saveChanges(adapter.getList());
                    adapter.setList(getClothesList(CLOTHES_FILE_NAME));
                    adapter.notifyDataSetChanged();
                }
            }
        }
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
        deleteContent(CLOTHES_FILE_NAME);
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(CLOTHES_FILE_NAME, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ));
        for(Clothes item: list){
            String fileContents = item.getClothingType()+ DELIMITER +item.getClothingColor()+ DELIMITER +item.getPrice()
                    + DELIMITER +item.getDateOfPurchase()+ DELIMITER +item.getUri()+DELIMITER+item.getFilename();
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