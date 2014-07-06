package tjhs.meet.meetupversion10.meetupversion10.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import tjhs.meet.meetupversion10.meetupversion10.R;

/**
 * Created by Brandon on 7/5/14.
 */
public class whoDialogFragment extends DialogFragment {

    NoticeDialogListener mListener;

    public interface NoticeDialogListener {
        public void onWhoDialogPositiveClick(whoDialogFragment dialog);
        public void onWhoDialogNegativeClick(whoDialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mListener = (NoticeDialogListener)activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }


    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.who_dialog, null));
        builder.setTitle("Choose people to invite.");

        builder.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        return builder.create();
    }



}
