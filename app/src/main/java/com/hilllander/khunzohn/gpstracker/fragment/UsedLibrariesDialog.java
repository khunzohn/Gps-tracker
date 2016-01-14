package com.hilllander.khunzohn.gpstracker.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hilllander.khunzohn.gpstracker.R;

/**
 *Created by khunzohn on 1/14/16.
 */
public class UsedLibrariesDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Used libraries.");
        dialog.setContentView(R.layout.dialog_used_libraries);
        TextView mmtext = (TextView) dialog.findViewById(R.id.mmtext);
        TextView cirImage = (TextView) dialog.findViewById(R.id.circularImageView);
        TextView status = (TextView) dialog.findViewById(R.id.systemBarTint);
        mmtext.setMovementMethod(LinkMovementMethod.getInstance());
        cirImage.setMovementMethod(LinkMovementMethod.getInstance());
        status.setMovementMethod(LinkMovementMethod.getInstance());
        mmtext.setText(Html.fromHtml(getActivity().getString(R.string.mmText)));
        cirImage.setText(Html.fromHtml(getActivity().getString(R.string.circular_image_view)));
        status.setText(Html.fromHtml(getActivity().getString(R.string.system_bar_tint)));
        Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
