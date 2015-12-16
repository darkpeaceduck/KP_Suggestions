package ru.kpsug.app.film;

import ru.kpsug.app.R;
import ru.kpsug.app.search.SearchActivity;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class DepthDialog extends DialogFragment implements OnClickListener {
    private SeekBar seekBar;
    private Button seekBarValue;
    private int maxPart = 5;
    private int minPart = 2;
    private int numPart = 4;
    private Context context;
    private int currentValue = minPart;
    private final static int DEFAUT_SCALE = 10;

    public DepthDialog(int maxPart, int minPart) {
        super();
        this.minPart = minPart;
        this.maxPart = maxPart;
        this.numPart = maxPart - minPart + 1;
    }

    public DepthDialog(Context context) {
        super();
        this.context = context;
    }

    private void changeValue(int progress) {
        currentValue = progress;
        seekBarValue.setText(getString(R.string.depth_dialog_button_text)
                + " (" + String.valueOf(progress) + ")");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().setTitle(R.string.title_depth_dialog);
        View v = inflater.inflate(R.layout.depth_dialog, null);
        seekBar = (SeekBar) v.findViewById(R.id.seekBar1);
        seekBar.setProgress(0);
        seekBar.incrementProgressBy(DEFAUT_SCALE);
        seekBar.setMax(numPart * DEFAUT_SCALE - 1);
        seekBarValue = (Button) v.findViewById(R.id.btnYes);
        changeValue(minPart);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                progress /= DEFAUT_SCALE;
                progress *= DEFAUT_SCALE;
                changeValue(minPart + progress / DEFAUT_SCALE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarValue.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((Object) context) instanceof SuggestionsActivity) {
                    SuggestionsActivity controller = (SuggestionsActivity) context;
                    controller.onLoad(currentValue);
                    DepthDialog.this.dismiss();
                } else {
                    // TODO
                }

            }
        });

        return v;

    }

    public void onClick(View v) {
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
