package ru.kpsug.app.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import ru.kpsug.app.R;
import ru.kpsug.app.activity.SuggestionsActivity;

public class LimitDialog extends DialogFragment implements OnClickListener {
	public static final String TAG = "limit_dialog";
	
    private static final int MAXPART_DEFAULT = 50;
    private static final int MINPART_DEFAULT = 1;
    private static final int DEFAUT_SCALE = 10;

    private SeekBar seekBar;
    private Button seekBarValue;
    private Context context;
    
    private int maxPart = MAXPART_DEFAULT;
    private int minPart = MINPART_DEFAULT;
    private int numPart = maxPart - minPart + 1;
    private int currentValue = minPart;
    

    public LimitDialog(int maxPart, int minPart) {
        super();
        this.minPart = minPart;
        this.maxPart = maxPart;
        this.numPart = maxPart - minPart + 1;
    }

    public LimitDialog(Context context) {
        super();
        this.context = context;
    }

    private void changeValue(int progress) {
        currentValue = progress;
        seekBarValue.setText(getString(R.string.depth_dialog_button_text)
                + " (" + String.valueOf(progress) + ")");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().setTitle(R.string.title_limit_dialog);
        View v = inflater.inflate(R.layout.depth_dialog, null);
        seekBar = (SeekBar) v.findViewById(R.id.seekBarDepthDialog);
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
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

        });
        seekBarValue.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((Object) context) instanceof SuggestionsActivity) {
                    SuggestionsActivity controller = (SuggestionsActivity) context;
                    controller.onLimitChange(currentValue);
                    LimitDialog.this.dismiss();
                } 
            }
        });

        return v;

    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
