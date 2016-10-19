package me.example.sauce.anychattest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IntSigActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.textView)
    TextView textView;
    private IntSignApi intSignApi;
    public static final int PHOTO_ZOOM = 0;
    public static final int PHOTO_TAKE = 1;
    public static final int IMAGE_COMPLETE = 2;
    private boolean isIdcard;
    private String photoSavePath;
    private String photoSaveName;
    private String photoSaveName2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_sig);
        ButterKnife.bind(this);
        Stetho.initializeWithDefaults(this);
        OkHttpClient okhttp = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        intSignApi = new Retrofit.Builder()
                .baseUrl("https://imgs-sandbox.intsig.net")
                .client(okhttp)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(IntSignApi.class);
        initData();
    }

    protected void initData() {
        File file = new File(Environment.getExternalStorageDirectory(),
                "/test/");
        if (!file.exists())
            file.mkdirs();
        photoSavePath = Environment.getExternalStorageDirectory()
                + "/test/";
        photoSaveName = System.currentTimeMillis() + ".png";
        photoSaveName2 = "idcard" + ".jpeg";

    }

    @OnClick({R.id.button_idCard, R.id.button_bankCard})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_idCard:


                new AlertDialog.Builder(this).setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageFromPhoto();
                        isIdcard=true;
                    }
                }).setPositiveButton("相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageFromCamera();
                        isIdcard=true;
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
            case R.id.button_bankCard:
                new AlertDialog.Builder(this).setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageFromPhoto();
                        isIdcard=false;
                    }
                }).setPositiveButton("相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageFromCamera();
                        isIdcard=false;
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
        }
    }

    public void imageFromCamera() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(new File(photoSavePath, photoSaveName));
        openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, PHOTO_TAKE);
    }

    public void imageFromPhoto() {
        Intent openAlbumIntent = new Intent();
        openAlbumIntent.setAction(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(openAlbumIntent, PHOTO_ZOOM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String path = "";
        switch (requestCode) {
            case PHOTO_ZOOM:
                if (data == null) {
                    return;
                }
                path = selectImage(this, data);
                break;
            case PHOTO_TAKE:
                path = photoSavePath + photoSaveName;
                break;
        }
        if (TextUtils.isEmpty(path)) {
            return;
        }
        transImage(path);
        File file = new File(photoSavePath + photoSaveName2);

        Picasso.with(this).load(file).into(imageView);

        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/jpej"), file);
        MultipartBody.Part photo = MultipartBody.Part.create(photoRequestBody);
        if (isIdcard){
            intSignApi.postIdCard("IntSig_test", "trial", photo).enqueue(new Callback<IdCardBean>() {
                @Override
                public void onResponse(Call<IdCardBean> call, Response<IdCardBean> response) {
                    try {
                        textView.setText(response.body().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<IdCardBean> call, Throwable t) {
                    textView.setText(t.getMessage());
                }
            });
        }else {
            intSignApi.postBankCard("IntSig_test", "trial", photo).enqueue(new Callback<BankCardBean>() {
                @Override
                public void onResponse(Call<BankCardBean> call, Response<BankCardBean> response) {
                    try {
                        textView.setText(response.body().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BankCardBean> call, Throwable t) {
                    textView.setText(t.getMessage());
                }
            });
        }
    }

    @Nullable
    public static String selectImage(Context context, Intent data) {
        Uri selectedImage = data.getData();
        if (selectedImage != null) {
            String uriStr = selectedImage.toString();
            String path = uriStr.substring(10, uriStr.length());
            if (path.startsWith("com.sec.android.gallery3d")) {
                return null;
            }
        }
        switch (data.getData().getScheme()) {
            case "file":
                return data.getData().getPath();
            case "content":
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                if (selectedImage != null) {
                    cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                }
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    return picturePath;
                }
                break;
        }
        return null;
    }

    public void transImage(String fromFile) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 缩放图片的尺寸
            float scaleWidth = (float) 1024 / bitmapWidth;
            float scaleHeight = (float) 768 / bitmapHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 产生缩放后的Bitmap对象
            Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            // save file
            File myCaptureFile = new File(photoSavePath + photoSaveName2);
            FileOutputStream out = new FileOutputStream(myCaptureFile);
            if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)) {
                out.flush();
                out.close();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();//记得释放资源，否则会内存溢出
            }
            if (!resizeBitmap.isRecycled()) {
                resizeBitmap.recycle();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
