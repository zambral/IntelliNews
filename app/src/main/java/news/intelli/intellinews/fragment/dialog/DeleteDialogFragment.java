package news.intelli.intellinews.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by llefoulon on 22/11/2016.
 */
//https://github.com/codepath/android_guides/wiki/Using-DialogFragment
public class DeleteDialogFragment extends DialogFragment implements Dialog.OnClickListener{
    private static final String TITLE = "title";

    public static DeleteDialogFragment newInstance(String s) {
        DeleteDialogFragment dialogFragment = new DeleteDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE,s);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //super.onCreateDialog(savedInstanceState);
        Bundle bundle = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false)
                .setTitle(bundle.getString(TITLE,null))
                .setMessage("Do you really want to delete this tab")
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, this);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }


}
