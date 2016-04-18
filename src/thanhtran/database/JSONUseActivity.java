package thanhtran.database;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import thanhtran.database.R;
import thanhtran.database.android;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class JSONUseActivity extends Activity {
	private EditText rollnum, enrollNumber;
	Button submit;
	TextView tvResult;

	int semesterValue = -1;

	String returnString;
	String returnString2;
	private ProgressDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jsonuse);

		rollnum = (EditText) findViewById(R.id.et_roll_number);
		enrollNumber = (EditText) findViewById(R.id.et_enroll_number);
		submit = (Button) findViewById(R.id.submitbutton);
		tvResult = (TextView) findViewById(R.id.showresult);

		doSpinnerRelatedStuff();

		submit.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				if (rollnum.getText().toString().contentEquals("")
						|| enrollNumber.getText().toString().contentEquals("")
						|| semesterValue == -1) {
					Toast.makeText(JSONUseActivity.this,
							"Please Fill in the details", Toast.LENGTH_LONG)
							.show();
					return;
				}

				if (!isOnline(JSONUseActivity.this)) {
					Toast.makeText(JSONUseActivity.this,
							"No network connection", Toast.LENGTH_LONG).show();
					return;
				}

				Log.d("Jsonuse activity", "roll = "
						+ rollnum.getText().toString());
				Log.d("Jsonuse activity", "enroll Number = "
						+ enrollNumber.getText().toString());
				Log.d("Jsonuse activity",
						"semestser value " + String.valueOf(semesterValue));
				final ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

				postParameters.add(new BasicNameValuePair("roll", rollnum
						.getText().toString()));
				postParameters.add(new BasicNameValuePair("enroll_number",
						enrollNumber.getText().toString()));
				postParameters.add(new BasicNameValuePair("semester", String
						.valueOf(semesterValue)));
				String response = null;
				progressDialog = ProgressDialog.show(JSONUseActivity.this,
						"Results", "Fetching data...");
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						getResults(postParameters);
					}
				}).start();

			}
		});
	}

	private void getResults(ArrayList<NameValuePair> postParameters) {
		String response = null;
		try {
			response = CustomHttpClient.executeHttpPost(
					"http://faiz89.zapto.org/jsonscript_new.php", postParameters);

			String result = response.toString();
			returnString = "";
			JSONArray jArray = new JSONArray(result);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				Log.i("log_tag",
						"Enrollment: " + json_data.getString("Enrollment")
								+ ", Roll: " + json_data.getString("Roll")
								+ ", RNO: " + json_data.getString("RNO")
								+ ", RollID: " + json_data.getString("RollID")
								+ ", Part: " + json_data.getString("Part")
								+ ", CAT: " + json_data.getString("CAT")
								+ ", FirstName: "
								+ json_data.getString("FirstName")
								+ ", LastName: "
								+ json_data.getString("LastName")
								+ ", ClassSID: "
								+ json_data.getString("ClassSID")
								+ ", ClassName: "
								+ json_data.getString("ClassName")
								+ ", ExamID: " + json_data.getString("ExamID")
								+ ", ExamName: "
								+ json_data.getString("ExamName")
								+ ", ClassResult: "
								+ json_data.getString("ClassResult"));

				returnString += "\n" + json_data.getString("FirstName") + " "
						+ json_data.getString("LastName") + "\n"
						+ json_data.getString("ClassName") + "\n"
						+ json_data.getString("ExamName") + "\n"
						+ json_data.getString("ClassResult") + "\n"; 
			}
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					tvResult.setText(returnString);
				}
			});

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection!!" + e.toString());

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(JSONUseActivity.this,
							"Please fill fields correctly", Toast.LENGTH_LONG)
							.show();
					tvResult.setText("");
				}
			});

		}finally{
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					progressDialog.dismiss();
				}
			});
		}
	}

	private boolean isOnline(Context mContext) {
		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private void doSpinnerRelatedStuff() {
		Spinner semesterSpinner = (Spinner) findViewById(R.id.spinner_semester_selector);
		ArrayAdapter<CharSequence> semesterSpinnerAdapter = ArrayAdapter
				.createFromResource(this, R.array.semester_array,
						android.R.layout.simple_spinner_item);
		semesterSpinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		semesterSpinner.setAdapter(semesterSpinnerAdapter);
		semesterSpinner
				.setOnItemSelectedListener(new SemesterSelectedListener());
	}

	public class SemesterSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			if (pos == 0) {
				semesterValue = -1;
			} else {
				String semester = parent.getItemAtPosition(pos).toString();
				semesterValue = pos;
			}

		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}
}