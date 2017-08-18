package org.apds.croprecommender;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class GetRecommendation extends AppCompatActivity {

    private ImageView imgCropView;
    private TextView txtCropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_recommendation);

        imgCropView = (ImageView) findViewById(R.id.imgCrop);
        txtCropView = (TextView) findViewById(R.id.txtCrop);

        Random generator = new Random();
        int randNum = generator.nextInt(4);

        switch (randNum)
        {
            case 1:
                imgCropView.setImageDrawable(getDrawable(R.mipmap.rice));
                txtCropView.setText("Rice");
                break;
            case 2:
                imgCropView.setImageDrawable(getDrawable(R.mipmap.wheat));
                txtCropView.setText("Wheat");
                break;
            default:
                imgCropView.setImageDrawable(getDrawable(R.mipmap.sugarcane));
                txtCropView.setText("Sugarcane");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
