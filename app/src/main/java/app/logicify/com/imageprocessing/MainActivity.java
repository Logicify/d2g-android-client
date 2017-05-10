package app.logicify.com.imageprocessing;

/* @author Vadim Gladchenko
*/

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    static {
        OpenCVLoader.initDebug();
    }

    static Context context;

    private String KEY_BITMAP;

    private static final int REQUEST_GALLERY_PHOTO = 1;
    private static final int REQUEST_CAMERA_PHOTO = 2;

    private ImageButton btn_takeCameraPhoto;
    private ImageButton btn_takeGalleryPhoto;
    private MenuItem account;

    private File mPhotoFile;
    private Bitmap mBitmap;
    private String mCurrentPhotoPath;
    //private ImageView mImageView;
    private ProgressBar progressBar;
    ImgProcessing task;

    public EditText login;
    public EditText pass;
    private ProgressDialog dialog;
    private InputStream is;

    private ListView lvProduct;
    private ProductListAdapter mAdapter;
    private List<Product> mProductList;
    private FloatingActionButton floatingActionButton;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        context = this;

        KEY_BITMAP = getResources().getString(R.string.KEY_BITMAP);
        //mImageView = (ImageView) findViewById(R.id.ivPhoto);


        lvProduct = (ListView) findViewById(R.id.listviewProduct);
        mProductList = new ArrayList<>();

        mProductList.add(new Product("Мороженка со сгущенкой и стразами", 25, "Еда"));
        mProductList.add(new Product("Банан", 8.84, "Еда"));
        mProductList.add(new Product("Windows 10", 4500, "Компьютер"));
        mProductList.add(new Product("Тюленьчик", 100, "Игрушка"));
        mProductList.add(new Product("Йогурт Activia", 26.40, "Еда"));
        mProductList.add(new Product("Мыло", 5.00, "Бытовая химия"));
        mProductList.add(new Product("Жевательная резинка Orbit", 9.25, "Еда"));

        mAdapter = new ProductListAdapter(getApplicationContext(), mProductList);
        lvProduct.setAdapter(mAdapter);

        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Do something
                //Ex: display msg with product id get from view.getTag
                Toast.makeText(getApplicationContext(), "Clicked product id =" + view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
        progressBar.setVisibility(View.GONE);

        //btn_takeCameraPhoto = (ImageButton) findViewById(R.id.btn_takeCameraPhoto);
        //btn_takeGalleryPhoto = (ImageButton) findViewById(R.id.btn_takeGalleryPhoto);

        //btn_takeCameraPhoto.setOnClickListener(this);
       // btn_takeGalleryPhoto.setOnClickListener(this);


        if (savedInstanceState != null) {
            mBitmap = savedInstanceState.getParcelable(KEY_BITMAP);
            //mImageView.setImageBitmap(mBitmap);
        }
        /**
         * ATTENTION: This was auto-generated to implement the App Indexing API.
         * See https://g.co/AppIndexing/AndroidStudio for more information.
         */
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_account:
                Intent intentLoginScreen = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentLoginScreen);
                return true;

            case R.id.action_camera:
                takeCameraPhoto();
                return true;

            case R.id.action_gallery:
                takeGalleryPhoto();
                return true;

        }

        return super.onOptionsItemSelected(item);
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

            //mImageView.setImageDrawable(null); //

            task = new ImgProcessing();
            task.execute();
        }
    }

    protected void takeCameraPhoto() {
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

    protected void takeGalleryPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType(getResources().getString(R.string.IMAGE_TYPE));
        startActivityForResult(photoPickerIntent, REQUEST_GALLERY_PHOTO);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(getResources().getString(R.string.DATE_FORMAT)).format(new Date());
        String imageFileName = getResources().getString(R.string.IMG_FORMAT_NAME) + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,                                              /* prefix */
                getResources().getString(R.string.IMG_FORMAT_JPG),         /* suffix */
                storageDir                                                /* directory */
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:

                Toast.makeText(context, "Add", Toast.LENGTH_SHORT).show();
                //Получаем вид с файла prompt.xml, который применим для диалогового окна:
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.created_item_dialog, null);

                //Создаем AlertDialog
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);

                //Настраиваем prompt.xml для нашего AlertDialog:
                mDialogBuilder.setView(promptsView);

                //Настраиваем отображение поля для ввода текста в открытом диалоге:
                final EditText newItemName = (EditText) promptsView.findViewById(R.id.newItemName);
                final EditText newItemCategory = (EditText) promptsView.findViewById(R.id.newItemCategory);
                final EditText newItemPrice = (EditText) promptsView.findViewById(R.id.newItemPrice);

                //Настраиваем сообщение в диалоговом окне:
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //Вводим текст и отображаем в строке ввода на основном экране:
                                        //final_text.setText(userInput.getText());

                                        mProductList.add(new Product(newItemName.getText().toString(),
                                                                        Double.parseDouble(newItemPrice.getText().toString()),
                                                                        newItemCategory.getText().toString()));
                                        mAdapter.updateList(mProductList);
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                //Создаем AlertDialog:
                AlertDialog alertDialog = mDialogBuilder.create();

                //и отображаем его:
                alertDialog.show();
                newItemName.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


                break;
        }
    }


    public class ImgProcessing extends AsyncTask {
        String imageFileName;
        String filename;

        @Override
        protected Object doInBackground(Object[] params) {
            ImgProcessingFunction();

            try {
                String timeStamp = new SimpleDateFormat(getResources().getString(R.string.DATE_FORMAT)).format(new Date());
                imageFileName = getResources().getString(R.string.IMG_FORMAT_NAME) + timeStamp;

                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(
                        imageFileName,                                              /* prefix */
                        getResources().getString(R.string.IMG_FORMAT_JPG),                                                     /* suffix */
                        storageDir                                                  /* directory */
                );

                String s = image.getAbsolutePath();

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(image);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                } finally {
                    if (fos != null) fos.close();
                }

                filename = "bitmap.png";
                FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //Cleanup
                stream.close();
                //mBitmap.recycle();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressBar.setVisibility(View.GONE);

            //mImageView.setImageBitmap(mBitmap);

            //Intent intent = new Intent(MainActivity.this, ProcessingPhotoActivity.class);
            //intent.putExtra("BITMAP", mBitmap);
            //startActivity(intent);

            try {
                Intent intent = new Intent(MainActivity.this, ProcessingPhotoActivity.class);
                intent.putExtra("image", filename);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        private void ImgProcessingFunction() {
            try {
                Mat imageMat = new Mat();
                Utils.bitmapToMat(mBitmap, imageMat);

                Mat resultMat = PerspectiveCorrection(imageMat);
                imageMat = resultMat;
                Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGRA2GRAY);
                Imgproc.GaussianBlur(imageMat, imageMat, new Size(3, 3), 0);
                Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 33, 10);

                mBitmap = ImageUtils.matToBitmap(resultMat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public Mat PerspectiveCorrection(Mat srcMat) {

            // Find the largest rectangle.
            RectFinder rectFinder = new RectFinder(0.2, 0.98);
            MatOfPoint2f rectangle = rectFinder.findRectangle(srcMat);

            if (rectangle == null) {
                return srcMat;
            }

            // Transform the rectangle.
            PerspectiveTransformation perspective = new PerspectiveTransformation();
            Mat dstMat = perspective.transform(srcMat, rectangle);

            return dstMat;
        }
    }
}
