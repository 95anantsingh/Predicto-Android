package inc.poseidon.predicto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Objects;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        String prediction = null;
        if (data != null) {
            prediction = data.getString("prediction");
        }
        String accuracy = data.getString("accuracy");
        //Log.d("RA","Prediction: " + prediction);
        //Log.d("RA","Accuracy: " + accuracy);
        TextView para1 = (TextView)findViewById(R.id.resultAc_para1);
        TextView label2 = (TextView)findViewById(R.id.resultAc_lable2);

        if(Objects.equals(prediction, "0")){
            para1.setText(getResources().getString(R.string.resultAc_breast_cancer_negative));
            label2.setText(String.format("%s %s", getResources().getString(R.string.resultAc_label2_text), accuracy));
        }
        else if(Objects.equals(prediction, "1")){
            para1.setText(getResources().getString(R.string.resultAc_breast_cancer_positive));
            label2.setText(String.format("%s %s", getResources().getString(R.string.resultAc_label2_text), accuracy));
        }
        else if(Objects.equals(prediction, "2")){
            para1.setText(getResources().getString(R.string.resultAc_diabetes_negative));
            label2.setText(String.format("%s %s", getResources().getString(R.string.resultAc_label2_text), accuracy));
        }
        else if(Objects.equals(prediction, "3")) {
            para1.setText(getResources().getString(R.string.resultAc_diabetes_positive));
            label2.setText(String.format("%s %s", getResources().getString(R.string.resultAc_label2_text), accuracy));
        }
    }

}
