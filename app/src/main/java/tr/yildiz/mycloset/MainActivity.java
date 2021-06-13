package tr.yildiz.mycloset;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MainActivity extends AppCompatActivity {
    private Button drawers,cabin,events,addClothItem,showClothes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defineVariables();
        defineListeners();
    }


    /**
     * Define the main_activity's buttons
     */
    private void defineVariables() {
        drawers = findViewById(R.id.drawers);
        cabin = findViewById(R.id.cabin);
        events = findViewById(R.id.events);
        addClothItem = findViewById(R.id.addClothItem);
        showClothes = findViewById(R.id.showClothes);
    }


    /**
     * Define listeners for main_activity's buttons
     */
    private void defineListeners() {
        showClothes.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),ShowClothes.class);
            Bundle b = new Bundle();
            b.putBoolean("fromMain",true);
            intent.putExtras(b);
            startActivity(intent);
        });

        drawers.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),DrawersActivity.class);
            startActivity(intent);
        });

        cabin.setOnClickListener(v -> {
//            Intent intent = new Intent(v.getContext(),CabinRoomActivity.class);
            Intent intent = new Intent(v.getContext(),ShowCombinationsActivity.class);
            startActivity(intent);
        });

        events.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),EventsActivity.class);
            startActivity(intent);
        });

        addClothItem.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),AddClothItem.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, data.getExtras().getString("add"), Toast.LENGTH_SHORT).show();
            }
        }
    }

}