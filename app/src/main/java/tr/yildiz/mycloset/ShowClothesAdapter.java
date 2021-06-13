package tr.yildiz.mycloset;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class ShowClothesAdapter extends RecyclerView.Adapter<ShowClothesAdapter.ViewHolder> {

    private List<Clothes> clothes = new ArrayList<>();
    private List<Clothes> selectedClothes = new ArrayList<>();
    private AppCompatActivity appCompatActivity;

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    public void setAppCompatActivity(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public List<Clothes> getSelectedClothes() {
        return selectedClothes;
    }

    public void setSelectedClothes(List<Clothes> selectedClothes) {
        this.selectedClothes = selectedClothes;
    }

    private OnItemClickListener mListener;
    private boolean selectionMode;
    private boolean selectOneMode;
    private boolean fromMainUpdate = false;

    public boolean isFromMainUpdate() {
        return fromMainUpdate;
    }

    public void setFromMainUpdate(boolean fromMainUpdate) {
        this.fromMainUpdate = fromMainUpdate;
    }

    public boolean isSelectOneMode() {
        return selectOneMode;
    }

    public void setSelectOneMode(boolean selectOneMode) {
        this.selectOneMode = selectOneMode;
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(boolean mode) {
        selectionMode = mode;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.clothes_list_item, viewGroup, false);

        return new ViewHolder(itemView, mListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView type,color,date,price;
        ImageView clothImage;
        CardView myCard;
        LinearLayout cardLay;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);


            defineVariables();
            itemView.setOnClickListener(v -> {
                if(!selectionMode){
                    if(!selectOneMode){
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onItemClick(position);
                            }
                        }
                    }else{//select for Cabin Room
                        if (selectedClothes.contains(clothes.get(getAdapterPosition()))){
                            cardLay.setBackgroundColor(Color.TRANSPARENT);
                            selectedClothes.remove(clothes.get(getAdapterPosition()));

                        }else{
                            cardLay.setBackgroundColor(ResourcesCompat.getColor(v.getResources(), R.color.selected, null)); //without theme
                            selectedClothes.removeAll(getSelectedClothes());
                            selectedClothes.add(clothes.get(getAdapterPosition()));
                        }
                    }

                }else{
                    if (selectedClothes.contains(clothes.get(getAdapterPosition()))){
                        cardLay.setBackgroundColor(Color.TRANSPARENT);
                        selectedClothes.remove(clothes.get(getAdapterPosition()));

                    }else{
                        cardLay.setBackgroundColor(ResourcesCompat.getColor(v.getResources(), R.color.selected, null)); //without theme
                        selectedClothes.add(clothes.get(getAdapterPosition()));
                    }
                }

            });

        }

        private void defineVariables() {
            type = itemView.findViewById(R.id.type);
            color = itemView.findViewById(R.id.color);
            price = itemView.findViewById(R.id.price);
            clothImage = itemView.findViewById(R.id.clothingImage);
            date = itemView.findViewById(R.id.dateOfPurchase);
            myCard = (CardView) itemView.findViewById(R.id.myCard1);
            cardLay = (LinearLayout) itemView.findViewById(R.id.cardLay);
            if (!selectionMode){
                myCard.setOnCreateContextMenuListener(this);
            }

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            if (fromMainUpdate){
                menu.add(this.getAdapterPosition(),13,0,"Edit this item");
                menu.add(this.getAdapterPosition(),12,1,"Delete this item");
            }else{
                menu.add(this.getAdapterPosition(),12,0,"Delete this item");
            }
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Clothes currentObject = clothes.get(position);

        viewHolder.price.setText(currentObject.getPrice());
        viewHolder.type.setText(currentObject.getClothingType());
        viewHolder.color.setText(currentObject.getClothingColor());
        viewHolder.date.setText(currentObject.getDateOfPurchase());

        //viewHolder.cardLay.setBackgroundColor(ResourcesCompat.getColor(appCompatActivity.getResources(), R.color.selected, null)); //without theme

//        try {
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(appCompatActivity.getContentResolver(), Uri.parse(currentObject.getUri()));
//        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
//        viewHolder.clothImage.setImageBitmap(ShrinkBitmap(bmpFactoryOptions,currentObject.getUri(),bmpFactoryOptions.outWidth,200));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        viewHolder.clothImage.setImageURI(Uri.parse(currentObject.getUri()));
//        String selectedFilePath = FilePath.getPath(appCompatActivity.getApplicationContext(), Uri.parse(currentObject.getUri()));
//        assert selectedFilePath != null;
//        final File f = new File(selectedFilePath);
        Picasso.get()
                .load(Uri.parse(currentObject.getUri())).resize(500, 400).centerCrop()
                .into(viewHolder.clothImage);

    }



    public void removeItem(int position){
        clothes.remove(position);
        notifyDataSetChanged();
    }
    public int getSelectedClothesCount() {
        return selectedClothes.size();
    }

    @Override
    public int getItemCount() {
        return clothes.size();
    }

    public void setList(List<Clothes> clothes) {
        this.clothes = clothes;
        notifyDataSetChanged();
    }

    public List<Clothes> getList() {
        return clothes;
    }
}