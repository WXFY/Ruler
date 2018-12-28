package com.zyf.ruler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.zyf.ruler.rulerlibrary.WeightChoiceView;

public class MainActivity extends AppCompatActivity {
    TextView weightValue;
    TextView heightValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WeightChoiceView weight = findViewById(R.id.weight);
        weightValue = findViewById(R.id.weight_value);
        weight.setListener(new WeightChoiceView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                weightValue.setText("体重："+value+"Kg");
            }
        });
        heightValue = findViewById(R.id.height_value);
        WeightChoiceView height = findViewById(R.id.height);
        height.setListener(new WeightChoiceView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                heightValue.setText("身高："+value+"cm");
            }
        });
    }
}
