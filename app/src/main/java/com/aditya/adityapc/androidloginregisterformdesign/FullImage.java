package com.aditya.adityapc.androidloginregisterformdesign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * Created by Aditya PC on 02-Jun-16.
 */
public class FullImage extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullimage);

        ImageView imageView = (ImageView) findViewById(R.id.imagetwo);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide
            .with(this)
            .load(R.raw.image)
            .into(imageViewTarget);
    }
}
