package RFID.Client.namespace;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class DetailInfo extends Activity{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
        
        EditText txtServerip = (EditText)findViewById(R.id.txtServerip);
  	   //String serverip = txtServerip.getText().toString();
  	   EditText txtUsername = (EditText)findViewById(R.id.txtUsername);
  	   //String username = txtUsername.getText().toString();
  	   EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
  	  // String password = txtPassword.getText().toString();
  	 String name = this.getIntent().getStringExtra("String");
      
  	txtServerip.setText(name);

  	   //txtServerip.setText(this.getApplication().getExternalFilesDir(""));
    }

}
