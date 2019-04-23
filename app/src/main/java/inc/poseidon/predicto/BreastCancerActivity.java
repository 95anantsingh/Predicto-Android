package inc.poseidon.predicto;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BreastCancerActivity extends AppCompatActivity {

    private String meanTexure;
    private String meanPerimeter;
    private String meanSmoothness;
    private String compactness;
    private String meanSymmetry;

    private boolean connected=false;
    private static final String IPaddress = "192.10.9.96";
    private InetAddress txAddress;
    private DatagramSocket dSocket;
    private int breastCancerPort = 2410;

    private EditText meanTex, meanPer, meanSmo, compact, meanSym;
    private Button evaluateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breast_cancer);

        meanTex = (EditText)findViewById(R.id.param1edit);
        meanPer = (EditText)findViewById(R.id.param2edit);
        meanSmo = (EditText)findViewById(R.id.param3edit);
        compact = (EditText)findViewById(R.id.param4edit);
        meanSym = (EditText)findViewById(R.id.param5edit);

        evaluateButton = (Button)findViewById(R.id.BreastCancerAC_predict_button);

        evaluateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty(meanTex)){
                    Snackbar.make(view, "Please enter Mean Texture", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(isEmpty(meanPer)){
                    Snackbar.make(view, "Please enter Mean Perimeter", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(isEmpty(meanSmo)){
                    Snackbar.make(view, "Please enter Mean Smoothness", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(isEmpty(compact)){
                    Snackbar.make(view, "Please enter Compactness", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if(isEmpty(meanSym)){
                    Snackbar.make(view, "Please enter Mean Symmetry", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else{
                    meanTexure = meanTex.getText().toString();
                    meanPerimeter = meanPer.getText().toString();
                    meanSmoothness = meanSmo.getText().toString();
                    compactness = compact.getText().toString();
                    meanSymmetry = meanSym.getText().toString();
                    startComm(breastCancerPort);
                }
            }
        });
    }


    private boolean isEmpty(EditText e){
        return e.getText().toString().trim().length() == 0 ;
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
                new commTask(this, txAddress, breastCancerPort, dSocket).execute(sendBit(breastCancerPort));
            }
        }
    }
    private class commTask extends AsyncTask<String, Void, String> {
        BreastCancerActivity FA;
        private DatagramSocket socket;
        private InetAddress txAddress;
        private int txPort;

        commTask(BreastCancerActivity Activity, InetAddress Addr, int aPort, DatagramSocket sock) {
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
        if(from==breastCancerPort){
            Log.d("DA","Receive: " + rxData);
            //tx2.setText(rxData);
            String prediction = rxData.substring(rxData.indexOf('[')+1,rxData.indexOf(']'));
            String accuracy = rxData.substring(rxData.indexOf(']')+1,rxData.indexOf('.')+2);
            Intent intent =new Intent(this,ResultActivity.class);
            Bundle data = new Bundle();
            Log.d("DA","Intent Started");
            data.putString("prediction",prediction);
            data.putString("accuracy",accuracy);
            intent.putExtras(data);
            evaluateButton.setEnabled(true);
            startActivity(intent);
        }
    }

    public String sendBit(int port){
        if(port == breastCancerPort) {
            String txData = ("[1]a" + meanTexure + "b" + meanPerimeter + "c" + meanSmoothness + "d" + compactness + "e" + meanSymmetry + "f");
            Log.d("DA", txData);
            return txData;
        }
        else return "";
    }

    private void commStateUpdate(String comState){
        //udpbutton.setText(comState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        startComm(0);
    }
}
