package ru.kpsug.app.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import ru.kpsug.app.R;
import ru.kpsug.app.activity.SuggestionsActivity;
import ru.kpsug.app.etc.SuggestionsActivitySortedMode;

public class SortedDialog extends DialogFragment implements OnClickListener {
	public static final String TAG = "sorted_dialog";

	private RadioGroup sortedModeRadiogroup;
	private Button buttonYes;
	private Context savedDialogcontext;

	public SortedDialog(Context context) {
		super();
		this.savedDialogcontext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.title_sorted_dialog);
		View v = inflater.inflate(R.layout.sorted_dialog, null);
		sortedModeRadiogroup = (RadioGroup) v.findViewById(R.id.radioGroupSortedDialog);
		buttonYes = (Button) v.findViewById(R.id.btnYes);
		buttonYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((Object) savedDialogcontext) instanceof SuggestionsActivity) {
					int Oid = sortedModeRadiogroup.getCheckedRadioButtonId();
					View radioButton = sortedModeRadiogroup.findViewById(Oid);
					int id = sortedModeRadiogroup.indexOfChild(radioButton);
					SuggestionsActivitySortedMode mode = null;
					switch (id) {
					case 0:
						mode = SuggestionsActivitySortedMode.RATING;
						break;
					case 1:
						mode = SuggestionsActivitySortedMode.YEAR_LESS;
						break;
					case 2:
						mode = SuggestionsActivitySortedMode.YEAR_MORE;
						break;
					}
					SuggestionsActivity controller = (SuggestionsActivity) savedDialogcontext;
					controller.onSortedModeChange(mode);
					SortedDialog.this.dismiss();
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
