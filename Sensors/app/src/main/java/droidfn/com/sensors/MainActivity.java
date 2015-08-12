package droidfn.com.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

    private SensorManager Smanger;
    private Sensor msensor;
    private BatteryManager bp;
    private static int flg = 0;
    private static int cont = 20;
    private String data;
    private static String finals="";
    TextView t,t2;

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    public BroadcastReceiver myreciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            //Log.d("Temp","Temp"+temperature);
            data += "{\"temp\":" + temperature / 10.0 + ", ";
        }
    };

    @Override
    public final void onSensorChanged(SensorEvent event) {

        final File fp;
        fp = getFilesDir();
        if (flg == 0) {
            this.registerReceiver(this.myreciever, new IntentFilter((Intent.ACTION_BATTERY_CHANGED)));
            flg = 1;
        }

        if (flg == 1) {
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            //Log.d("Sensor_Data","X="+axisX+"  Y= "+axisY+"  Z= "+axisZ);

            data += "\"x\":" + axisX + ", \"y\":" + axisY + ", \"z\":" + axisZ +", \"model\":\""+Build.MODEL+ "\"}\n";

            int op = 0, cp = 0;
            for (int i = 0; i < data.length(); i++) {
                if (data.charAt(i) == '{')
                    op++;
                if (data.charAt(i) == '}')
                    cp++;

            }
            if (op == 1&& cp == 1) {

                cont--;
                finals+=data;
                //Content in the try block is used to save data to a file
                try {
                    Log.d("JSON_Data",data);
                    Thread.sleep(1000);
                    //Code to save the file and upload the file is to be performed here
                    String filename = "json.txt";
                    FileOutputStream fout = openFileOutput(filename, Context.MODE_APPEND);

                    OutputStreamWriter ous = new OutputStreamWriter(fout);
                    ous.write(data);

                    ous.close();
                    //Log.d("Save File", "Writin" + fp.getAbsoluteFile());
                    fout.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            flg = 0;
            data = "";
            if (cont == -1) {

                try {
                    Thread.sleep(1000);
                    ConnectivityManager conmag = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo nf = conmag.getActiveNetworkInfo();
                    if (nf != null && nf.isConnected()) {
                        Log.d("acON", "DAS");
                        t.setText("Connected");
                        new DownloadWebpageTask().execute();
                        Thread.sleep(10000);
                        System.exit(0);
                    }
                    else{

                        Log.d("Value to be pushed","\n"+finals);
                        Calendar cel=Calendar.getInstance();
                        Log.d("TimeStamp","V"+cel.get(Calendar.SECOND)+cel.get(Calendar.MILLISECOND));
                        String Manu= Build.MODEL;


                        Log.d("SysIno",Manu);
                        Thread.sleep(10000);
                        System.exit(0);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

        }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String ret="";
            try {
                File fp= getFilesDir();


                String path = fp.getAbsolutePath() + "/json.txt";

                File fps = new File(path);
                Log.d("PATH", fps.getPath());


                InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(fps), -1);
                reqEntity.setContentType("text/plain");
                reqEntity.setChunked(true);

              /*
                HttpClient hcl = new DefaultHttpClient();

                HttpPut hp = new HttpPut("https://bi-hadoop-prod-2173.services.dal.bluemix.net:8443/gateway/default/webhdfs/v1/user/biblumix/examples/input-data2/text/ob" +6+ ".txt?op=CREATE");
                hp.addHeader("Authorization", "Basic YmlibHVtaXg6dzUyZDNAMXZ0dVhW");
                hp.addHeader("User-Agent", "Mozilla/5.0");*/
                Log.d("url","https://bi-hadoop-prod-2173.services.dal.bluemix.net:8443/gateway/default/webhdfs/v1/user/biblumix/examples/input-data2/text/fi" +1+ ".txt?op=APPEND");
                /*hp.setEntity(reqEntity);
                HttpResponse response = hcl.execute(hp);
                Log.d("Resoponse Code", "Rep=" + response.getStatusLine().getReasonPhrase());*/

                URL url = new URL("https://bi-hadoop-prod-2173.services.dal.bluemix.net:8443/gateway/default/webhdfs/v1/user/biblumix/examples/input-data2/text/fi" +1+ ".txt?op=APPEND");
                HttpURLConnection ucl=(HttpURLConnection)url.openConnection();
                ucl.setRequestMethod("POST");
                ucl.setRequestProperty("Authorization", "Basic YmlibHVtaXg6dzUyZDNAMXZ0dVhW");
                //DataOutputStream dops=new DataOutputStream(ucl.getOutputStream());
                //dops.writeChars("{\"temp\":31.1, \"x\":34.5, \"y\":51.2, \"z\":10.1}");
                Log.d("Res=","Code= "+ucl.getResponseCode());
                Log.d("Rep=", ucl.getResponseMessage());

                //Refer to webhdfs.html and try to obtain the redirected url and send the corresponding request

                BufferedReader reader = new BufferedReader((new InputStreamReader(ucl.getInputStream())));
                StringBuilder sb=new StringBuilder();

                String lin=null;
                while((lin=reader.readLine())!=null){
                    sb.append(lin+"\n");
                }
                Log.d("Content=", sb.toString());
                String tmpurl=ucl.getHeaderField("Location").toString();
                Log.d("URL=", tmpurl);
                URL tu = new URL(tmpurl);
                ucl.disconnect();

                HttpURLConnection ucl2=(HttpURLConnection)tu.openConnection();
                ucl2.setRequestMethod("POST");
                ucl2.setRequestProperty("Authorization", "Basic YmlibHVtaXg6dzUyZDNAMXZ0dVhW");
                ucl2.setRequestProperty("Content-Type", "text/plain");

                DataOutputStream dops=new DataOutputStream(ucl2.getOutputStream());
                dops.write(finals.getBytes());
                //dops.writeChars(finals);
                ucl2.connect();
                Log.d("Res=", "Code= " + ucl2.getResponseCode());
                //t2.setText("Code="+ucl2.getResponseCode());
                Log.d("Rep=",ucl2.getResponseMessage());
                Log.d("Type=",ucl2.getRequestMethod());
                finals="";
                ucl2.disconnect();

                url=new URL("https://bi-hadoop-prod-2173.services.dal.bluemix.net:8443/gateway/default/oozie/v1/jobs?action=start");
                ucl=(HttpURLConnection)url.openConnection();
                ucl.setRequestMethod("POST");
                ucl.setRequestProperty("Authorization","Basic YmlibHVtaXg6dzUyZDNAMXZ0dVhW");
                ucl.setRequestProperty("Content-Type","application/xml");

                String config="<configuration>\n" +
                        "<property>\n" +
                        "<name>user.name</name> \n" +
                        "<value>biblumix</value> \n" +
                        "</property>\n" +
                        "<property>\n" +
                        "<name>jobTracker</name> \n" +
                        "<value>mn01.services.dal.bluemix.net:8050</value> \n" +
                        "</property>\n" +
                        "<property>\n" +
                        "<name>examplesRoot</name> \n" +
                        "<value>examples</value> \n" +
                        "</property>\n" +
                        "<property>\n" +
                        "<name>oozie.wf.application.path</name> \n" +
                        "<value>/user/${user.name}/${examplesRoot}/apps/pig2</value> \n" +
                        "</property>\n" +
                        "<property>\n" +
                        "<name>queueName</name> \n" +
                        "<value>default</value> \n" +
                        "</property>\n" +
                        "<property>\n" +
                        "<name>oozie.use.system.libpath</name>\n" +
                        "<value>true</value>\n" +
                        "</property>\n" +
                        "<property>\n" +
                        "<name>nameNode</name> \n" +
                        "<value>hdfs://mn01.services.dal.bluemix.net:8020</value> \n" +
                        "</property>\n" +
                        "</configuration>";
                dops=new DataOutputStream(ucl.getOutputStream());
                dops.write(config.getBytes());
                Log.d("REp=","Code"+ucl.getResponseCode());
                ucl.disconnect();

            } catch (ClientProtocolException e) {
               return "NA";
            } catch (IOException e) {
                return "NA";
            }
            return "NA";
        }
        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t=new TextView(this);
        t=(TextView)findViewById(R.id.counter);
        t.setText("Application Capture Temp,X,Y,Z & System Info and sends it to a Hadoop Cluster");
        Smanger=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        msensor=Smanger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d("Sensors_Data", msensor.getVendor().toString());
        System.out.println(msensor.getVendor().toString());
        Smanger.registerListener(this, msensor, SensorManager.SENSOR_DELAY_NORMAL);


    }
    @Override
    protected void onPause() {
        super.onPause();
        Smanger.unregisterListener(this);
    }
    @Override
    protected void onResume(){
        super.onResume();
        Smanger.registerListener(this, msensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
