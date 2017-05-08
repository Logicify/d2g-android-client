package app.logicify.com.imageprocessing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.FileInputStream;

/**
 * Created by Serhei on 04.05.2017.
 */

public class ProcessingPhotoActivity extends AppCompatActivity {


    private ImageView mImageView;
    private Bitmap mBitmap;
    String filename;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.processing_photo);

        mImageView = (ImageView) findViewById(R.id.ivPhoto);

        filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            mBitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mImageView.setImageBitmap(mBitmap);
    }
}
