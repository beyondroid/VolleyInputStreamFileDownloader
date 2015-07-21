package example.inputstream.ht2.inputstreamdownloader;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


public class FileDownloadActivity extends ActionBarActivity implements Response.Listener<byte[]>, ErrorListener{
    Button btn_download;
    InputStreamVolleyRequest request;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        btn_download =(Button)findViewById(R.id.button);
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Change your url below
                String mUrl="http://yoururl.com";
                request = new InputStreamVolleyRequest(Request.Method.GET, mUrl, FileDownloadActivity.this, FileDownloadActivity.this, null);
                RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(),
                        new HurlStack());
                mRequestQueue.add(request);
            }
        });
    }

    @Override
    public void onResponse(byte[] response) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            if (response!=null) {

                //Read file name from headers
                String content =request.responseHeaders.get("Content-Disposition")
                        .toString();
				StringTokenizer st = new StringTokenizer(content, "=");
				String[] arrTag = st.toArray();

                String filename = arrTag[1];
                filename = filename.replace(":", ".");
                Log.d("DEBUG::RESUME FILE NAME", filename);

                try{
                    long lenghtOfFile = response.length;

                    //covert reponse to input stream
                    InputStream input = new ByteArrayInputStream(response);
                    File path = Environment.getExternalStorageDirectory();
                    File file = new File(path, filename);
                    map.put("resume_path", file.toString());
                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        output.write(data, 0, count);
                    }

                    output.flush();

                    output.close();
                    input.close();
                }catch(IOException e){
                    e.printStackTrace();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE. ERROR:: "+error.getMessage());
    }
}
