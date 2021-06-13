package tr.yildiz.mycloset;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private List<Event> events = new ArrayList<>();
    private AppCompatActivity appCompatActivity;

    public AppCompatActivity getAppCompatActivity() {
        return appCompatActivity;
    }

    public void setAppCompatActivity(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }


    private OnItemClickListener mListener;
    private boolean selectionMode;
    private boolean selectOneMode;



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
                .inflate(R.layout.event_item, viewGroup, false);

        return new ViewHolder(itemView, mListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView name,type,date,location;
        Button clothbtn; ImageButton notify;
        CardView myCard;
        LinearLayout cardLay;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            defineVariables();
            AlarmManager[] alarmManager = new AlarmManager[getItemCount()];
            Intent[] intent = new Intent[getItemCount()];
            PendingIntent[] pendingIntent = new PendingIntent[getItemCount()];
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            clothbtn.setOnClickListener(v -> {
                Intent intent1 = new Intent(appCompatActivity.getApplicationContext(),DrawersContent.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("drawerName",getList().get(getAdapterPosition()).getName());
                myBundle.putBoolean("fromDrawer",false);
                intent1.putExtras(myBundle);
                appCompatActivity.startActivity(intent1, myBundle);
            });

            notify.setOnClickListener(v -> {
                /*
                  update the notify button status in app
                  and in db
                 */
                notify.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                notify.setEnabled(false);
                getList().get(getAdapterPosition()).setNotify(1);
                saveChanges(getList());

                createNotificationChannel();
                Toast.makeText(v.getContext(), "ReminderSet!", Toast.LENGTH_SHORT).show();
                intent[getAdapterPosition()] = new Intent(appCompatActivity.getApplicationContext(),Reminder.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("name",getList().get(getAdapterPosition()).getName());
                myBundle.putString("address",getList().get(getAdapterPosition()).getLocation());
                myBundle.putInt("id",getAdapterPosition());
                intent[getAdapterPosition()].putExtras(myBundle);

                pendingIntent[getAdapterPosition()] = PendingIntent.getBroadcast(appCompatActivity.getApplicationContext(),getAdapterPosition(),intent[getAdapterPosition()],PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager[getAdapterPosition()]=((AlarmManager) appCompatActivity.getSystemService(Context.ALARM_SERVICE));

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
                try {
                    System.out.println(sdf.parse(getList().get(getAdapterPosition()).getDate()));
                    cal.setTime(Objects.requireNonNull(sdf.parse(getList().get(getAdapterPosition()).getDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                alarmManager[getAdapterPosition()].set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent[getAdapterPosition()]);
            });
        }

        private void createNotificationChannel() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                CharSequence name = "Event Reminder";
                String description = "channel for reminder";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(getList().get(getAdapterPosition()).getName(),name,importance);
                channel.setDescription(description);


                NotificationManager notificationManager = appCompatActivity.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
        /**
         * Updates the file with the passed list
         * @param list
         * clothes list
         */
        public void saveChanges(List<Event> list) {
            deleteContent("events.txt");
            FileOutputStream fos = null;
            try {
                fos = appCompatActivity.openFileOutput("events.txt", Context.MODE_APPEND);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ));
            for(Event item: list){
                String fileContents = item.getName()+ "#" +item.getType()+ "#" +item.getDate()
                        + "#" +item.getLocation()+"#" +item.getNotify();
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
                fos = appCompatActivity.openFileOutput(fileName, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( fos ) );
            writer.print("");
            writer.close();
        }

        private void defineVariables() {
            type = itemView.findViewById(R.id.eventType);
            name = itemView.findViewById(R.id.eventName);
            location = itemView.findViewById(R.id.eventLocation);
            clothbtn = itemView.findViewById(R.id.extra);
            date = itemView.findViewById(R.id.eventDate);
            notify = itemView.findViewById(R.id.notify);
            myCard = (CardView) itemView.findViewById(R.id.myCard2);
            cardLay = (LinearLayout) itemView.findViewById(R.id.cardLay1);
            if (!selectionMode){
                myCard.setOnCreateContextMenuListener(this);
            }

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(),13,0,"Edit this item");
            menu.add(this.getAdapterPosition(),12,1,"Delete this item");
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Event currentObject = events.get(position);
        viewHolder.name.setText(currentObject.getName());
        viewHolder.type.setText(currentObject.getType());
        viewHolder.location.setText(currentObject.getLocation());
        viewHolder.date.setText(currentObject.getDate());
        if (currentObject.getNotify() == 1){
            viewHolder.notify.setImageResource(R.drawable.ic_baseline_notifications_active_24);
            viewHolder.notify.setEnabled(false);
        }else {
            viewHolder.notify.setImageResource(R.drawable.ic_notifications_24);
            viewHolder.notify.setEnabled(true);
        }
    }



    public void removeItem(int position){
        events.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setList(List<Event> clothes) {
        this.events = clothes;
        notifyDataSetChanged();
    }

    public List<Event> getList() {
        return events;
    }
}
