package inc.poseidon.predicto;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DiabetesActivity extends AppCompatActivity {

    private boolean connected=false;
    private static final String IPaddress = "192.10.9.96";
    private InetAddress txAddress;
    private DatagramSocket dSocket;
    private int controlPort = 2410;

    private EditText pregnen, gluco, bloodPre, skinThic, insu, bmin, diaPeFu, ages;
    private Button evaluateButton;
    private TextView param2;

    private boolean sex = true;
    private String pregnencies;
    private String glucose;
    private String bloodPressure;
    private String skinThickness;
    private String insulin;
    private String bmi;
    private String dpf;
    private String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diabetes);
        final Spinner gender = (Spinner) findViewById(R.id.spinner);

        pregnen = (EditText)findViewById(R.id.param2edit);
        gluco = (EditText)findViewById(R.id.param3edit);
        bloodPre = (EditText)findViewById(R.id.param4edit);
        skinThic = (EditText)findViewById(R.id.param5edit);
        insu = (EditText)findViewById(R.id.param6edit);
        bmin = (EditText)findViewById(R.id.param7edit);
        diaPeFu = (EditText)findViewById(R.id.param8edit);
        ages = (EditText)findViewById(R.id.param9edit);
        param2 = (TextView)findViewById(R.id.param2);

        param2.setVisibility(View.INVISIBLE);
        pregnen.setVisibility(View.INVISIBLE);
        evaluateButton = (Button)findViewById(R.id.DiabetesAC_predict_button);
        evaluateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty(pregnen) && gender.getSelectedItem().toString().trim().equals("Female")){
                    Snackbar.make(view, "Please enter Number of Pregnencies", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(isEmpty(gluco)){
                    Snackbar.make(view, "Please enter Glucose Level", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(isEmpty(bloodPre)){
                    Snackbar.make(view, "Please enter Blood Pressure", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(isEmpty(skinThic)){
                    Snackbar.make(view, "Please enter Skin Thickness", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(isEmpty(insu)){
                    Snackbar.make(view, "Please enter Insulin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(isEmpty(bmin)){
                    Snackbar.make(view, "Please enter BMI", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(isEmpty(ages)){
                    Snackbar.make(view, "Please enter Age", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(isEmpty(diaPeFu)){
                    Snackbar.make(view, "Please enter Diabetes Pedigree Function", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    if(!sex) {
                        pregnencies = pregnen.getText().toString();
                    }
                    glucose = gluco.getText().toString();
                    bloodPressure = bloodPre.getText().toString();
                    skinThickness = skinThic.getText().toString();
                    insulin = insu.getText().toString();
                    bmi = bmin.getText().toString();
                    dpf = diaPeFu.getText().toString();
                    age = ages.getText().toString();
                    startComm(controlPort);
                }
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this , R.array.Gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(gender.getSelectedItem().toString().trim().equals("Male")){
                    if(!sex) {
                        param2.setVisibility(View.INVISIBLE);
                        pregnen.setVisibility(View.INVISIBLE);
                        sex = true;
                    }
                }
                else if(gender.getSelectedItem().toString().trim().equals("Female")) {
                    if (sex) {
                        param2.setVisibility(View.VISIBLE);
                        pregnen.setVisibility(View.VISIBLE);
                        sex = false;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void startComm(int port) {
        if (port == 0) {
            if (connected) {
                dSocket.close();
                connected = false;
                commStateUpdate("Connect");
            }
        }
        else {
            if (!connected) {
                try {
                    dSocket = new DatagramSocket();
                    txAddress = InetAddress.getByName(IPaddress);
                    commStateUpdate("Connecting...");
                } catch (IOException e) {
                    e.printStackTrace();
                    commStateUpdate("Error");
                    //Log.d("DA","Error starcomm");
                } finally {
                    if (dSocket != null) {
                        connected = true;
                        commStateUpdate("Disconnect");
                       // Log.d("DA","Socket formed starcomm");
                    }
                }
            }
            if(dSocket != null){
                new commTask(this, txAddress, controlPort, dSocket).execute(sendBit(controlPort));
            }
        }
    }
    private class commTask extends AsyncTask<String, Void, String> {
        DiabetesActivity FA;
        private DatagramSocket socket;
        private InetAddress txAddress;
        private int txPort;

        commTask(DiabetesActivity Activity, InetAddress Addr, int aPort, DatagramSocket sock) {
            this.FA = Activity;
            txAddress = Addr;
            txPort = aPort;
            socket = sock;
        }

        @Override
        protected String doInBackground(String... txData) {
            try {return connectToPi(txData[0], socket, txAddress, txPort);
            } catch (IOException e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String result) {//Log.d("FA","Execute Recieved: " + result);
            FA.receiveBit(result, txPort);
        }
    }
    private String connectToPi(String txData, DatagramSocket socket, InetAddress txAddress, int txPort) throws IOException {
        byte[] txDataBytes = txData.getBytes();
        int txDataLength = txData.length();
        byte[] rxDataBytes = new byte[1024];
        DatagramPacket packet = new DatagramPacket(txDataBytes, txDataLength, txAddress, txPort);
        socket.send(packet);
        packet = new DatagramPacket(rxDataBytes, rxDataBytes.length);
        socket.receive(packet);
        return new String(packet.getData(), 0, packet.getLength());
    }

    private void receiveBit(String rxData, int from) {
        if(from==controlPort) {
            Log.d("DA", "Receive: " + rxData);
            //tx2.setText(rxData);

            int prediction = Integer.parseInt(rxData.substring(rxData.indexOf('[') + 1, rxData.indexOf(']')));
            if (prediction == 0) {
                prediction = 2;
            }
            else if (prediction == 1) {
                prediction = 3;
            }
            String accuracy = rxData.substring(rxData.indexOf(']')+1,rxData.indexOf('.')+2);
            Intent intent =new Intent(this,ResultActivity.class);
            Bundle data = new Bundle();
            Log.d("DA","Intent Started");
            data.putString("prediction", String.valueOf(prediction));
            data.putString("accuracy",accuracy);
            intent.putExtras(data);
            evaluateButton.setEnabled(true);
            startActivity(intent);
        }
    }
    public String sendBit(int port){
        if(port == controlPort) {
            if(sex){
                String txData = ("[3]a" + glucose + "b" + bloodPressure + "c" + skinThickness + "d" + insulin + "e" + bmi + "f" + dpf + "g" + age + "h");
                Log.d("DA", txData);
                return txData;
            }
            else{
                    String txData = ("[2]a" + pregnencies + "b" + glucose + "c" + bloodPressure + "d" + skinThickness + "e" + insulin + "f" + bmi + "g" + dpf + "h" + age + "i");
                    Log.d("DA", txData);
                    return txData;
            }

        }
        else return "";
    }

    private void commStateUpdate(String comState){
        //udpbutton.setText(comState);
    }
    private boolean isEmpty(EditText e){
        return e.getText().toString().trim().length() == 0 ;
    }
}
