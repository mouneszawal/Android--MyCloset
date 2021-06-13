package tr.yildiz.mycloset;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class AddClothItem extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int IMAGE_REQ = 2;
    private static final String CLOTHES_FILE_NAME = "clothes.txt";
    private static final String DELIMITER = "#";
    private ImageView img;
    private Button confirm,update; private ImageButton imgBtn;
    private TextInputEditText type,color,price;
    private TextView date; private Uri image;
    private MaterialDatePicker.Builder builder;
    private String imgFile;
    private MaterialDatePicker materialDatePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cloth_item);
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
        confirm.setVisibility(View.GONE);
        update.setVisibility(View.VISIBLE);
        type.setText(bundle.getString("clothingType"));
        color.setText(bundle.getString("clothingColor"));
        date.setText(bundle.getString("dateOfPurchase"));
        price.setText(bundle.getString("clothingPrice"));
        imgFile = bundle.getString("clothingImageFileName");
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image = Uri.parse(bundle.getString("clothingImage"));
        img.setImageURI(image);
    }


    private void defineVariables() {
        img = findViewById(R.id.item_image);
        color = (TextInputEditText) findViewById(R.id.clothingColor);
        type = (TextInputEditText) findViewById(R.id.clothingType);
        price = (TextInputEditText) findViewById(R.id.clothingPrice);
        date = (TextView) findViewById(R.id.date);
        imgBtn = findViewById(R.id.datePick);
        confirm = findViewById(R.id.confirm);
        update = findViewById(R.id.update);
        //Date picker
        builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("SELECT DATE");
        materialDatePicker = builder.build();
    }


    private void defineListeners() {
        img.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23){
                if(checkPermission()){
                    filePicker();
                }else{
                    requestPermission();
                }
            }else{
                filePicker();
            }
        });

        confirm.setOnClickListener(v -> {
            if(valid()){
                Clothes item = new Clothes(type.getText().toString().trim(),color.getText().toString().trim()
                                        ,date.getText().toString(),price.getText().toString().trim());
                item.setUri(image.toString());
                item.setFilename(imgFile);
                saveClothItem(item);
                goBack(v);
                Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            }
        });
        update.setOnClickListener(v -> {
            if(valid()){
                Intent intentResult = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("clothingType",type.getText().toString().trim());
                bundle.putString("clothingColor",color.getText().toString().trim());
                bundle.putString("dateOfPurchase",date.getText().toString());
                bundle.putString("clothingPrice",price.getText().toString().trim());
                bundle.putString("clothingImage",image.toString());
                bundle.putString("clothingImageFileName",imgFile);
                Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_SHORT).show();

                intentResult.putExtras(bundle);
                setResult(RESULT_OK,intentResult);
                finish();
            }else{
                Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            }
        });

        imgBtn.setOnClickListener(v -> {
            materialDatePicker.show(getSupportFragmentManager(),"Date_Picker");
        });

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            date.setText(materialDatePicker.getHeaderText());
        });
    }

    private void goBack(View v) {
        Intent intent = new Intent(v.getContext(),MainActivity.class);
        startActivity(intent);
    }

    private boolean valid() {
        return !color.getText().toString().equals("") &&
                !type.getText().toString().equals("") &&
                !date.getText().toString().equals("") &&
                image != null;
    }

    /**
     * takes a cloth item and saves its data to internal storage
     * @param item
     * clothes item
     */
    private void saveClothItem(Clothes item){
        String fileContents = item.getClothingType()+ DELIMITER +item.getClothingColor()+ DELIMITER +item.getPrice()
                + DELIMITER +item.getDateOfPurchase()+ DELIMITER +item.getUri()+DELIMITER+item.getFilename();
        try {
            FileOutputStream fos = openFileOutput(CLOTHES_FILE_NAME, Context.MODE_APPEND);
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
            writer.println(fileContents);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean UriExists(String uri){
        try {
            FileInputStream fis = openFileInput(CLOTHES_FILE_NAME);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            String[] currentline;
            while ( (line = reader.readLine()) != null ) {
                currentline = line.split(DELIMITER);
                if (currentline[4].equals(uri)){
                    return true;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void filePicker() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(intent, IMAGE_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == IMAGE_REQ) {
                if (data == null) {
                    return;
                }
                try {
                    image = Utils.copyFile(getApplicationContext(),data.getData(),data.getDataString().substring(data.getDataString().lastIndexOf("/")+1)+".jpg");
                    imgFile = data.getDataString().substring(data.getDataString().lastIndexOf("/")+1)+".jpg";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!UriExists(image.toString())){
                    img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    img.setImageURI(image);
                }else{
                    Toast.makeText(this, "Cloth item with this image already exists!", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(AddClothItem.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddClothItem.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(AddClothItem.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(AddClothItem.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                filePicker();
                Toast.makeText(AddClothItem.this, "Permission Successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddClothItem.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}