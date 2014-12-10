package org.jboss.aerogear.agreddit;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.http.HeaderAndBody;

public class LoginDialogFragment extends DialogFragment {

	private View root;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.login, container);
		final Handler closeHandler = new Handler();
		root.findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener() {

					public void onClick(View v) {
						String username = ((EditText) root
								.findViewById(R.id.username)).getText()
								.toString();
						String password = ((EditText) root
								.findViewById(R.id.password)).getText()
								.toString();
						((StoryListApplication) getActivity().getApplication())
								.login(username, password,
										new Callback<HeaderAndBody>() {

											public void onSuccess(
													HeaderAndBody data) {
												((StoryListFragment) getActivity()
														.getFragmentManager()
														.findFragmentById(
																R.id.story_list))
														.reload(true);
												closeHandler.post(new Runnable() {
													
													@Override
													public void run() {
														LoginDialogFragment.this.dismiss();
													}
												});
											}

											public void onFailure(Exception e) {
												Log.e("LoginDialog", e.getMessage(), e);
												Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
											}
										});
					}
				});
		return root;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

}
