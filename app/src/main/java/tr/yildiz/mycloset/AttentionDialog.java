package tr.yildiz.mycloset;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AttentionDialog extends AppCompatDialogFragment {
    private DialogListener Listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Attention !")
                .setMessage("Do you want to delete this item ?")
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .setPositiveButton("Delete", (dialog, which) -> Listener.onDeleteClicked());
        return builder.create();
    }

    public interface DialogListener{
        void onDeleteClicked();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            Listener = (DialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement DialogListener");
        }
    }
}