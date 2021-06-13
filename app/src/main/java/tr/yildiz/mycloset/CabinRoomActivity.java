package tr.yildiz.mycloset;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CabinRoomActivity extends AppCompatActivity {
    private static final String COMB_FILE_NAME = "combinations.txt";
    private Button head,face,lower,upper,foot;
    private ImageView headImg,faceImg,lowerImg,upperImg,footImg;
    private Map geek,btns,bitmaps,imgnames;
    private FloatingActionButton share,add;
    private String fileName;
    private TextInputLayout txtLay;
    private TextInputEditText combname;
    private Bundle b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabin_room);
        setTitle("Cabin Room");
        //prevent keyboard from pupping up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        b = getIntent().getExtras();
        if (b!= null){
            fileName = b.getString("CombinationName")+".txt";
            System.out.println("FFFFFFFF: "+fileName);
        }

        defineVariables();
        defineListeners();
        defineDictionary();
        if (b != null && fileExist(fileName)){
            renderImages(getCombUris(fileName));
        }
    }



    private void renderImages(ArrayList<Uri> combUris) {
        ArrayList<ImageView> imgs = new ArrayList<>(geek.values());
        for (int i =0 ; i<combUris.size();i++){
            String selectedFilePath = FilePath.getPath(getApplicationContext(), combUris.get(i));
            final File f = new File(selectedFilePath);
            if(f.exists()){
                System.out.println(f.getName());
            }else{
                System.out.println("My path:" +combUris.get(0).getPath());
            }

            Picasso.get()
                    .load(f).resize(500, 400).centerCrop()
                    .into(imgs.get(i));
        }
    }



    private void defineDictionary() {
        geek = new Hashtable();
        geek.put("head",headImg);
        geek.put("face",faceImg);
        geek.put("upper",upperImg);
        geek.put("lower",lowerImg);
        geek.put("foot",footImg);
        btns = new Hashtable();
        btns.put("head",head);
        btns.put("face",face);
        btns.put("upper",upper);
        btns.put("lower",lower);
        btns.put("foot",foot);
        bitmaps = new Hashtable();
        imgnames = new Hashtable();
    }

    /**
     * Define the main_activity's buttons
     */
    private void defineVariables() {
        head  = findViewById(R.id.head);
        face  = findViewById(R.id.face);
        upper = findViewById(R.id.upper);
        lower = findViewById(R.id.lower);
        foot  = findViewById(R.id.foot);
        headImg  = findViewById(R.id.headImage);
        faceImg  = findViewById(R.id.faceImage);
        upperImg = findViewById(R.id.upperImage);
        lowerImg = findViewById(R.id.lowerImage);
        footImg  = findViewById(R.id.footImage);
        txtLay = (TextInputLayout) findViewById(R.id.textLay);
        share = (FloatingActionButton) findViewById(R.id.shareCom);
        add = (FloatingActionButton) findViewById(R.id.addCom);
        combname = (TextInputEditText) findViewById(R.id.combinationName);
        if (b != null){
            setCompVisibility();
        }
    }

    private void setCompVisibility() {
        head.setVisibility(View.GONE);
        face.setVisibility(View.GONE);
        lower.setVisibility(View.GONE);
        upper.setVisibility(View.GONE);
        foot.setVisibility(View.GONE);
        share.setVisibility(View.VISIBLE);
        add.setVisibility(View.GONE);
        headImg.setVisibility(View.VISIBLE);
        faceImg.setVisibility(View.VISIBLE);
        upperImg.setVisibility(View.VISIBLE);
        lowerImg.setVisibility(View.VISIBLE);
        footImg.setVisibility(View.VISIBLE);
        combname.setVisibility(View.GONE);
        txtLay.setVisibility(View.GONE);
    }


    /**
     * Define listeners for main_activity's buttons
     */
    private void defineListeners() {
        head.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),ShowClothes.class);
            Bundle bundle = new Bundle();
            bundle.putString("btn-name","head");
            bundle.putBoolean("select-one",true);
            intent.putExtras(bundle);
            startActivityForResult(intent,20);
        });

        face.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),ShowClothes.class);
            Bundle bundle = new Bundle();
            bundle.putString("btn-name","face");
            bundle.putBoolean("select-one",true);
            intent.putExtras(bundle);
            startActivityForResult(intent,20);
        });

        upper.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),ShowClothes.class);
            Bundle bundle = new Bundle();
            bundle.putString("btn-name","upper");
            bundle.putBoolean("select-one",true);
            intent.putExtras(bundle);
            startActivityForResult(intent,20);
        });

        lower.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),ShowClothes.class);
            Bundle bundle = new Bundle();
            bundle.putString("btn-name","lower");
            bundle.putBoolean("select-one",true);
            intent.putExtras(bundle);
            startActivityForResult(intent,20);
        });

        foot.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),ShowClothes.class);
            Bundle bundle = new Bundle();
            bundle.putString("btn-name","foot");
            bundle.putBoolean("select-one",true);
            intent.putExtras(bundle);
            startActivityForResult(intent,20);
        });

        share.setOnClickListener(v -> {
            shareCom();
            Toast.makeText(this, "Sharing in process!", Toast.LENGTH_SHORT).show();
        });


        add.setOnClickListener(v -> {
            if(!combNameExist() && bitmaps.size() == 5 && !combname.getText().toString().equals("")){
                SaveToCombFile();
                saveSelectedComb(new ArrayList<Uri>(bitmaps.values()),new ArrayList<String>(imgnames.values()));
                setResult(RESULT_OK);
                finish();
            }else{
                String message = "To add the combination:\n" +
                        "1- Combination Name should be unique!\n"
                        + "2- combination name shouldn't be empty!\n "
                        + "3- Select all parts!";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<Uri> getCombUris(String fileName) {

        ArrayList<Uri> list = new ArrayList<Uri>();
        try {
            FileInputStream fis = openFileInput(fileName);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                list.add(Uri.parse(line));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Uri path = FileProvider.getUriForFile(getApplicationContext(),"tr.yildiz.",
//                new File(getApplicationContext().getFilesDir().toString() +"/" +exams.get(getAdapterPosition())));
        return list;
    }

    private ArrayList<Uri> getCombUriFromImgNames(String fileName) {

        ArrayList<Uri> list = new ArrayList<Uri>();
        String[] current;
        try {
            FileInputStream fis = openFileInput(fileName);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                current = line.split("#");
                list.add(FileProvider.getUriForFile(getApplicationContext(),"tr.yildiz.mycloset",
                        new File(getApplicationContext().getFilesDir().toString() +"/" +current[1])));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void shareCom(){
        ArrayList<Uri> list = getCombUriFromImgNames(fileName);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("image/*"); /* This example is sharing jpeg images. */
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
        startActivity(Intent.createChooser(intent , "Share image"));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20){
            if(resultCode == RESULT_OK){
                System.out.println("SomeThing");
                if (data != null){
                    Bundle b = data.getExtras();
                    makeChanges(b.getString("organ"),b.getString("uri"),b.getString("fileName"));
                }
            }
        }
    }

    private void makeChanges(String organ, String uri,String name) {
        ImageView v =(ImageView) geek.get(organ);
        Button b = (Button) btns.get(organ);
        if(organ != null){
            if (!bitmaps.containsValue(Uri.parse(uri))){
                bitmaps.put(organ,Uri.parse(uri));
                imgnames.put(organ,name);
                b.setVisibility(View.GONE);
                v.setVisibility(View.VISIBLE);
                String selectedFilePath = FilePath.getPath(getApplicationContext(), Uri.parse(uri));
                final File f = new File(selectedFilePath);
                if(f.exists()){
                    System.out.println(f.getName());
                }else{
                    System.out.println("My path:" +Uri.parse(uri).getPath());
                }

                Picasso.get()
                        .load(f).resize(500, 400).centerCrop()
                        .into(v);
            }else{
                Toast.makeText(this, "This item is already selected\nPlease Select another Item", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveSelectedComb(List<Uri> list,List<String> names) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(combname.getText().toString()+".txt", Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ));
        int i =0;
        for(Uri item: list){
            String fileContents = item.toString()+"#"+names.get(i);
            writer.println(fileContents);
            i++;
        }
        writer.close();
    }

    private void SaveToCombFile() {
        try {
            FileOutputStream fos = openFileOutput(COMB_FILE_NAME, Context.MODE_APPEND);
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
            writer.println(combname.getText().toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private boolean combNameExist() {
        try {
            FileInputStream fis = openFileInput(COMB_FILE_NAME);
            BufferedReader reader = new BufferedReader( new InputStreamReader( fis ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                if (line.equals(combname.getText().toString())){
                    Toast.makeText(this, "The question title already exists!", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean fileExist(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }
}