package tr.yildiz.mycloset;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShowCombinationAdapter extends RecyclerView.Adapter<ShowCombinationAdapter.ViewHolder> {

    private List<String> combinations = new ArrayList<>();

    private OnItemClickListener mListener;

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
                .inflate(R.layout.drawer_item, viewGroup, false);

        return new ViewHolder(itemView, mListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView drawerName;
        CardView cardLay;
        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);


            defineVariables();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "onClick: before");
                    if (listener != null) {
                        Log.d("TAG", "onClick: after");
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                    // Toast.makeText(v.getContext(), "Hello", Toast.LENGTH_SHORT).show();
                }
            });




        }

        private void defineVariables() {
            drawerName = itemView.findViewById(R.id.drawerName);
            cardLay = (CardView) itemView.findViewById(R.id.myCard);
            cardLay.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(),12,0,"Delete this item");
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String currentObject = combinations.get(position);


        viewHolder.drawerName.setText(currentObject);

    }

    public void removeItem(int position){
        combinations.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return combinations.size();
    }

    public void setList(List<String> drawers) {
        this.combinations = drawers;
        notifyDataSetChanged();
    }

    public List<String> getList() {
        return combinations;
    }
}