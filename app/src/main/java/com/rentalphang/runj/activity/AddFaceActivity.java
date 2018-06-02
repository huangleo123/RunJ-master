package com.rentalphang.runj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.platform.comapi.map.A;
import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.FaceSetOperate;
import com.megvii.cloud.http.Response;
import com.rentalphang.runj.R;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.utils.ConfigUtil;
import com.rentalphang.runj.utils.FileUtils;
import com.rentalphang.runj.utils.GeneralUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

public class AddFaceActivity extends Activity implements View.OnClickListener{

    private final static int REQUEST_CODE_CAMERA = 0X001; //拍照的requestcode
    private User user;
    private String facetoken;
    private String picPath;
    private ImageView photoImageView;
    private TextView jsonTextView;
    private Button sureButton;
    private Bitmap mImage;
    private StringBuffer sb = new StringBuffer();
    private String TAG ="Diolg";
    private TextView creatTokenTV;

    /**
     * 1.启动相机
     * 2.获得照片路径
     * 3.调用第三方工具校验token，
     * 3.1判断token是否已经拿到 然后上传token到数据库中
     * 4.完成
     * @param savedInstanceState
     */


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initComponent();
        user = BmobUser.getCurrentUser(getApplicationContext(),User.class);
        if (GeneralUtil.isSDCard()) {
            ///////////////////////拍照//////////////////////////
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                //判断系统是否有能处理cameraIntent的activity

                startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
            }
        } else {
            Toast.makeText(getBaseContext(), "没有检测到SD卡", Toast.LENGTH_SHORT).show();
        }

    }

    private void initComponent() {
        photoImageView = (ImageView) findViewById(R.id.img_sign_test);
        jsonTextView = (TextView) findViewById(R.id.tv_json_string);
        sureButton = (Button) findViewById(R.id.bt_sure_add_face);
        creatTokenTV= (TextView) findViewById(R.id.tv_text_cread);

        sureButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_sure_add_face:

                sureButton.setText("点击重新上传");

                Intent intent = new Intent(getApplicationContext(), AddFaceActivity.class);
                startActivity(intent);
                AddFaceActivity.this.finish();

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    Uri uri = data.getData();
                    if (uri != null) {
                        bitmap = BitmapFactory.decodeFile(uri.getPath());
                    }
                    if (bitmap == null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            bitmap = (Bitmap) bundle.get("data");//缩略图
                        } else {
                            Toast.makeText(getBaseContext(), "拍照失败！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    //获取拍照图片路径
                    this.picPath = FileUtils.saveBitmapToFile(bitmap, "headImg");
                    showPhoto2();
                    if (bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
                break;
        }
        upFaceImage();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void showPhoto2(){
        // 获取图片的宽和高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        mImage = BitmapFactory.decodeFile(picPath, options);

        // 压缩图片
        options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
                (double) options.outWidth / 1024f,
                (double) options.outHeight / 1024f)));
        options.inJustDecodeBounds = false;
        mImage = BitmapFactory.decodeFile(picPath, options);


        // 若mImageBitmap为空则图片信息不能正常获取
        if(null == mImage) {
            Toast.makeText(getBaseContext(),"图片信息无法正常获取！",Toast.LENGTH_LONG).show();
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //可根据流量及网络状况对图片进行压缩
        mImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        photoImageView.setImageBitmap(mImage);
        showFaceTokenResult(facetoken);

    }


    /**
     *根据相机的图片地址转换为bitmap
     * @param path
     * @return
     */
    private byte[] getBitmap(String path){
        //新增一个从相机过来的bimap
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    private void upFaceImage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                CommonOperate commonOperate = new CommonOperate(ConfigUtil.FACE_KEY,ConfigUtil.FACE_SECRET,false);
                FaceSetOperate FaceSet = new FaceSetOperate(ConfigUtil.FACE_KEY, ConfigUtil.FACE_SECRET, false);
                ArrayList<String> faces = new ArrayList<>();
                try{
                    Response response1 = commonOperate.detectByte(getBitmap(picPath), 0, null);
                    String faceToken1 =getFaceToken(response1);
                    //checkFaceToken(faceToken1);
                    creatFaceTokenKu(faceToken1);
                    facetoken =faceToken1;
                    faces.add(faceToken1);
                    sb.append("faceToken1: ");
                    sb.append(faceToken1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            jsonTextView.setText(sb.toString());
                            checkFaceToken(facetoken);
                            updataInfo();//上传数据

                        }
                    });



                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 传入人脸Token 集合创建以outer_id 为表示的人脸库
     * 尝试使用一个集合的
     * @param faceToken
     */
    private void creatFaceTokenKu(final String faceToken) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceSetOperate FaceSet = new FaceSetOperate(ConfigUtil.FACE_KEY,ConfigUtil.FACE_SECRET, false);
                ArrayList<String> faces = new ArrayList<>();
                faces.add(faceToken);
                String faceTokens = creatFaceTokens(faces);
                Response faceset = null;
                try {
                    faceset = FaceSet.createFaceSet(null,"test",null,faceTokens,null, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String faceSetResult = new String(faceset.getContent());
                Log.e("faceSetResult",faceSetResult);
                if(faceset.getStatus() == 200){
                    sb.append("\n");
                    sb.append("\n");
                    sb.append("faceSet creat success");
                    sb.append("\n");
                    sb.append("create result: ");
                    sb.append(faceSetResult);
                }else{
                    sb.append("\n");
                    sb.append("\n");
                    sb.append("faceSet creat faile");
                    sb.append("\n");
                    sb.append("create result: ");
                    sb.append(faceSetResult);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        creatTokenTV.setText(sb.toString());
                    }
                });
            }
        }).start();

    }

    private String creatFaceTokens(ArrayList<String> faceTokens){
        if(faceTokens == null || faceTokens.size() == 0){
            return "";
        }
        StringBuffer face = new StringBuffer();
        for (int i = 0; i < faceTokens.size(); i++){
            if(i == 0){
                face.append(faceTokens.get(i));
            }else{
                face.append(",");
                face.append(faceTokens.get(i));
            }
        }
        return face.toString();
    }

    /**
     * 根据返回的结果提出Tokrn
     * @param response
     * @return token
     * @throws JSONException
     */
    private String getFaceToken(Response response) throws JSONException {
        if(response.getStatus() != 200){
            return new String(response.getContent());
        }
        String res = new String(response.getContent());
        Log.e("response", res);
        JSONObject json = new JSONObject(res);
        String faceToken = json.optJSONArray("faces").optJSONObject(0).optString("face_token");
        return faceToken;
    }
    private void updataInfo(){
        User xUser = new User();
        xUser.setFacetoken(facetoken);
        xUser.update(getApplicationContext(),user.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                     Toast.makeText(getApplicationContext(),facetoken+"dd",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }
    private void checkFaceToken( String facetoken){
        if (null==facetoken){
            //字符串为空 重新拍照
            //弹出Diolg 完成

            AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
            builder.setTitle("提示");
            builder.setMessage("人脸信息不完全，请重新注册");
            Log.d(TAG, "checkFaceToken: 进入未完成的Diolg ");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),AddFaceActivity.class);
                    startActivity(intent);
                }
            });
            builder.show();
            Log.d(TAG, "checkFaceToken: 显示Diolg ");

        }else {
            //弹出Diolg 完成
           /* AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
            builder.setTitle("提示");
            builder.setMessage("注册人脸信息成功");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();*/
            new com.rentalphang.runj.ui.AlertDialog(this, "注意", "修改成功!", false, 0, new com.rentalphang.runj.ui.AlertDialog.OnDialogButtonClickListener() {
                @Override
                public void onDialogButtonClick(int requestCode, boolean isPositive) {
                    if (isPositive) {
                        AddFaceActivity.this.finish();
                    }

                }
            }).show();
        }
    }

    private void showFaceTokenResult(String facetoken){
        if (null == facetoken){
            //没有获取到Token
        }else{
            sureButton.isShown();
            sureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }
}
