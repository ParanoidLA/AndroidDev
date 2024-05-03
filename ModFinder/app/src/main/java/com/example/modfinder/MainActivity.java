package com.example.modfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button buttonAdd,buttonSub,buttonMul,buttonDiv,buttonMod;
    EditText ed1,ed2;
    TextView tv;
    int num1,num2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd=findViewById(R.id.add);
        buttonSub=findViewById(R.id.sub);
        buttonMul=findViewById(R.id.mul);
        buttonDiv=findViewById(R.id.div);
        buttonMod=findViewById(R.id.mod);
        ed1=findViewById(R.id.num1);
        ed2=findViewById(R.id.num2);
        tv=findViewById(R.id.result);

        buttonAdd.setOnClickListener(this);
        buttonSub.setOnClickListener(this);
        buttonMul.setOnClickListener(this);
        buttonDiv.setOnClickListener(this);
        buttonMod.setOnClickListener(this);






    }
    public int getIntFromEdittext(EditText editText)
    {
        if(editText.getText().toString().equals(""))
        {
            Toast.makeText(this, "Enter Number", Toast.LENGTH_SHORT).show();
            return 0;
        }
        else {
            int number = Integer.parseInt(editText.getText().toString());
            return number;
        }
    }
    @Override
    public void onClick(View view)
    {
       num1=getIntFromEdittext(ed1);
       num2=getIntFromEdittext(ed2);

       switch (view.getId()){
           case R.id.add:
               tv.setText("Answer = "+ (num1+num2));
               break;
           case R.id.sub:
               tv.setText("Answer = "+ (num1-num2));
               break;
           case R.id.mul:
               tv.setText("Answer = "+ (num1*num2));
               break;
           case R.id.div:
               tv.setText("Answer = "+ ((float)num1/(float)num2));
               break;
           case R.id.mod:
               tv.setText("Answer = "+(num1%num2));
               break;
       }

    }
}