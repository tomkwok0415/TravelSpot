package edu.cuhk.csci3310.travelspot.ui.tutorial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

public class CreateTutorial implements View.OnClickListener {

    private AlertDialog.Builder builder;
    private int titleResId;
    private int descriptionResId;

    public CreateTutorial(AlertDialog.Builder builder, int titleRes, int descriptionRes) {
        this.builder = builder;
        this.titleResId = titleRes;
        this.descriptionResId = descriptionRes;
    }

    @Override
    public void onClick(View view) {
        this.builder.setMessage(this.descriptionResId)
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setTitle(this.titleResId);
        dialog.show();
    }
}
