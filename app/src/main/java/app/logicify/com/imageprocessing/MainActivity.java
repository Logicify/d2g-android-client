package app.logicify.com.imageprocessing;

/* @author Vadim Gladchenko
*/

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.opencv.android.Utils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "myLogs";

    static {
        Log.d(TAG, "Hello");
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV loaded");
        } else {
            Log.d(TAG, "OpenCV not loaded");
        }
    }

    private static final String KEY_BITMAP = "bitmap";

    private static final int REQUEST_GALLERY_PHOTO = 1;
    private static final int REQUEST_CAMERA_PHOTO = 2;

    private File mPhotoFile;
    private File processedPhotoFile;
    private Bitmap mBitmap;
    private String mCurrentPhotoPath;
    private ImageView mImageView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.ivPhoto);

        if (savedInstanceState != null) {
            mBitmap = savedInstanceState.getParcelable(KEY_BITMAP);
            mImageView.setImageBitmap(mBitmap);
        }
        /**
         * ATTENTION: This was auto-generated to implement the App Indexing API.
         * See https://g.co/AppIndexing/AndroidStudio for more information.
         */
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_BITMAP, mBitmap);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_PHOTO) {
                if (mCurrentPhotoPath != null) {
                    bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                }
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
                mBitmap = bitmap;
                ImgProcessing();
                mImageView.setImageBitmap(mBitmap);


//            try {
//                processedPhotoFile = createImageFile();
//                galleryAddPic();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }



            // file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "savedBitmap.png");


            try {
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(
                        "savedBitmap",  /* prefix */
                        ".png",         /* suffix */
                        storageDir      /* directory */
                );

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(image);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Toast.makeText(this, storageDir.toString(), Toast.LENGTH_SHORT).show();
                } finally {
                    if (fos != null) fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void takeCameraPhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
             {
                try {
                    mPhotoFile = createImageFile();
                    Toast.makeText(this, mPhotoFile.getName().toString(), Toast.LENGTH_SHORT).show();
                    galleryAddPic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (mPhotoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takePictureIntent, REQUEST_CAMERA_PHOTO);
            }
        }
    }

    public void takeGalleryPhoto(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_GALLERY_PHOTO);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void ImgProcessing() {
        try {
            Mat imageMat = new Mat();
            Utils.bitmapToMat(mBitmap, imageMat);
            Log.d(TAG, String.format("**********************************************************************************************Default mat: %d - %d", imageMat.width(), imageMat.height()));

            Mat resultMat = PerspectiveCorrection(imageMat);
            imageMat = resultMat;
            Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGRA2GRAY);
            Imgproc.GaussianBlur(imageMat, imageMat, new Size(3, 3), 0);
            Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 33, 10);
            Log.d(TAG, String.format("**********************************************************************************************Result mat: %d - %d", resultMat.width(), resultMat.height()));

            //mBitmap = toGrayscale(mBitmap);

            mBitmap = ImageUtils.matToBitmap(resultMat);
            //Utils.matToBitmap(imageMat,mBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "**********************Error**********************");
        }
    }


    public Mat PerspectiveCorrection(Mat srcMat) {

        // Find the largest rectangle.
        // Find image views.
        RectFinder rectFinder = new RectFinder(0.2, 0.98);
        MatOfPoint2f rectangle = rectFinder.findRectangle(srcMat);

        if (rectangle == null) {
            Toast.makeText(this, "No rectangles were found", Toast.LENGTH_LONG).show();
            return srcMat;
        }

        // Transform the rectangle.
        PerspectiveTransformation perspective = new PerspectiveTransformation();
        Mat dstMat = perspective.transform(srcMat, rectangle);

        return dstMat;
    }


    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}
