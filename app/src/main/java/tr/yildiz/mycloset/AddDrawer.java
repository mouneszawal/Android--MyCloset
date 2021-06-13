package tr.yildiz.mycloset;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class AddDrawer extends AppCompatActivity {
    private static final String DRAWERS_FILE_NAME = "drawers.txt";
    private TextInputEditText drawername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drawer);
        setTitle("Add Drawer");
        //prevent keyboard from pupping up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        drawername = (TextInputEditText) findViewById(R.id.drawerName1);
        Button addDrawer = findViewById(R.id.addDrawer);
        addDrawer.setOnClickListener(v -> {
            if(!drawerNameExist()){
                SaveToDrawerFile();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void SaveToDrawerFile() {
        try {
            FileOutputStream fos = openFileOutput(DRAWERS_FILE_NAME, Context.MODE_APPEND);
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
            writer.println(drawername.getText().toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean drawerNameExist() {
        try {
            FileInputStream fis = openFileInput(DRAWERS_FILE_NAME);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                if (line.equals(drawername.getText().toString())){
                    Toast.makeText(this, "The Drawer name already exists!", Toast.LENGTH_SHORT).show();
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