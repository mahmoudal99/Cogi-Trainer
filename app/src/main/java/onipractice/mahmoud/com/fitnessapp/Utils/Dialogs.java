package onipractice.mahmoud.com.fitnessapp.Utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import onipractice.mahmoud.com.fitnessapp.R;

public class Dialogs {

    private static final String TAG = "Dialogs";

    private Context context;
    private TextView confirmDialogTv, cancelDialogTv;
    private Button maleBtn, femaleBtn;
    private EditText passwordEt, emailEt;

    private String passwordDialog, emailDialog, newGender;
    private int newWeight, newHeight;
    private FirebaseMethods firebaseMethods;

    public Dialogs(Context context) {
        this.context = context;
    }

    //---------- Change Height Dialog ----------//

    public void heightDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.dialog_change_height, null);

        final NumberPicker numberPicker = (NumberPicker) mView.findViewById(R.id.heightPicker);

        numberPicker.setMaxValue(300);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.d(TAG, "onValueChange: ");
                newHeight = i;
            }
        });

        confirmDialogTv = (TextView) mView.findViewById(R.id.dialogConfirm);
        cancelDialogTv = (TextView) mView.findViewById(R.id.dialogCancel);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        confirmDialogTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseMethods = new FirebaseMethods(context);
                firebaseMethods.updateHeight(String.valueOf(newHeight) + "cm");
                dialog.dismiss();
            }
        });

        cancelDialogTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //---------- Change Weight Dialog ----------//

    public void weightDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.dialog_change_weight, null);

        final NumberPicker numberPicker = (NumberPicker) mView.findViewById(R.id.heightPicker);

        numberPicker.setMaxValue(300);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.d(TAG, "onValueChange: ");
                newWeight = i;
            }
        });

        confirmDialogTv = (TextView) mView.findViewById(R.id.dialogConfirm);
        cancelDialogTv = (TextView) mView.findViewById(R.id.dialogCancel);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        confirmDialogTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseMethods = new FirebaseMethods(context);
                firebaseMethods.updateWeight(String.valueOf(newWeight) + "kg");
                dialog.dismiss();
            }
        });
        cancelDialogTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //---------- Change Gender Dialog ----------//

    public void genderDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.dialog_change_gender, null);

        maleBtn = (Button) mView.findViewById(R.id.maleBtn);
        femaleBtn = (Button) mView.findViewById(R.id.femaleBtn);
        confirmDialogTv = (TextView) mView.findViewById(R.id.dialogConfirm);
        cancelDialogTv = (TextView) mView.findViewById(R.id.dialogCancel);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        maleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleBtn.setBackgroundColor(context.getResources().getColor(R.color.outline));
                maleBtn.setTextColor(context.getResources().getColor(R.color.white));
                femaleBtn.setBackgroundColor(context.getResources().getColor(R.color.white));
                femaleBtn.setTextColor(context.getResources().getColor(R.color.outline));

                newGender = "male";
            }
        });

        femaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maleBtn.setBackgroundColor(context.getResources().getColor(R.color.white));
                maleBtn.setTextColor(context.getResources().getColor(R.color.outline));

                femaleBtn.setBackgroundColor(context.getResources().getColor(R.color.outline));
                femaleBtn.setTextColor(context.getResources().getColor(R.color.white));

                newGender = "female";
            }
        });

        confirmDialogTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseMethods = new FirebaseMethods(context);
                firebaseMethods.updateGender(newGender);
                dialog.dismiss();
            }
        });

        cancelDialogTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //---------- Email Dialog ----------//

    public void emailDialog() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.dialog_change_email, null);

        passwordEt = (EditText) mView.findViewById(R.id.confirm_password);
        emailEt = (EditText) mView.findViewById(R.id.confirm_email);

        confirmDialogTv = (TextView) mView.findViewById(R.id.dialogConfirm);
        cancelDialogTv = (TextView) mView.findViewById(R.id.dialogCancel);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        confirmDialogTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordDialog = passwordEt.getText().toString();
                emailDialog = emailEt.getText().toString();
                dialog.dismiss();
                firebaseMethods = new FirebaseMethods(context);
                firebaseMethods.changeEmail(passwordDialog, emailDialog);

            }
        });

        cancelDialogTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
