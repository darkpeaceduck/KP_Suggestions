package ru.kpsug.app.film;

import ru.kpsug.app.R;
import ru.kpsug.app.film.SuggestionsActivityFragmentAdapter.SortedMode;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

public class SortedDialog extends DialogFragment implements OnClickListener {
    private RadioGroup group;
    private Button button;
    private Context context;

    public SortedDialog(Context context) {
        super();
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().setTitle(R.string.title_sorted_dialog);
        View v = inflater.inflate(R.layout.sorted_dialog, null);
        group = (RadioGroup) v.findViewById(R.id.radioGroup1);
        button = (Button) v.findViewById(R.id.btnYes);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((Object) context) instanceof SuggestionsActivity) {
                    int Oid = group.getCheckedRadioButtonId();
                    View radioButton = group.findViewById(Oid);
                    int id = group.indexOfChild(radioButton);
                    SortedMode mode = null;
                    switch (id) {
                    case 0:
                        mode = SortedMode.RATING;
                        break;
                    case 1:
                        mode = SortedMode.YEAR_LESS;
                        break;
                    case 2:
                        mode = SortedMode.YEAR_MORE;
                        break;
                    }
                    SuggestionsActivity controller = (SuggestionsActivity) context;
                    controller.onSortedModeChange(mode);
                    SortedDialog.this.dismiss();
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
