package com.rentalphang.runj.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.rentalphang.runj.R;
import com.rentalphang.runj.model.bean.FaceBean;
import com.rentalphang.runj.model.bean.User;
import com.rentalphang.runj.model.biz.ActivityManager;
import com.rentalphang.runj.utils.ConfigUtil;
import com.rentalphang.runj.utils.FileUtils;
import com.rentalphang.runj.utils.GeneralUtil;

import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.FaceSetOperate;
import com.megvii.cloud.http.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

public class FaceSignActivity extends Activity implements View.OnClickListener {

    private final static int REQUEST_CODE_CAMERA = 0X001; //拍照的requestcode
    private String picPath = null; //头像路径
    private ImageView photoImageView;
    private File mPictureFile;
    private Bitmap mImage;
    private TextView jsonTextView;
    private String key ="qro07AXmWi8KjcFuLI1IcMhzK5U-1iUm";
    private String secret ="a9Hw0-KoNR2xxqoZeLhpyS3-GJCf3Wgc";
    private StringBuffer sb = new StringBuffer();
    private User user;
    private String userFaceToken;
    private TextView resultTv;
    private String confidence;
    private FaceBean faceBean;

    /**
     * 1.直接启动摄像头
     * 2.获取拍照路径
     * 3.使用第三方库上传，获得Facetoken
     * 4.使用搜索数据库的方法获得结果JSOn
     * -------------------
     * 1.点击签到按钮后，判断用户是否签到 是否注册了人脸识别
     * 2.点击签到进行签到，成功就返回，不成功就显示界面重新签到
     * 3.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_sign);
        initComponent();
        user = BmobUser.getCurrentUser(getApplicationContext(),User.class);
        userFaceToken=user.getFacetoken();

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
        resultTv = (TextView) findViewById(R.id.tv_text_cread);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK){
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
        upForText();
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
    }

    private void upForText(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                CommonOperate commonOperate = new CommonOperate(key, secret, false);
                FaceSetOperate FaceSet = new FaceSetOperate(key, secret, false);
                ArrayList<String> faces = new ArrayList<>();
                try {
                    //检测第一个人脸，传的是本地图片文件
                    //detect first face by local file
                    Response response1 = commonOperate.detectByte(getBitmap(picPath), 0, null);
                    String faceToken1 = getFaceToken(response1);
                    seraTokenKu(faceToken1);//进行检测人脸
                    faces.add(faceToken1);
                    sb.append("faceToken1: ");
                    sb.append(faceToken1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            jsonTextView.setText(sb.toString());


                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
    /**
     *根据图片的地址转换为BItmap
     * @param res
     * @return
     */
    private byte[] getBitmap(int res){

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), res);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
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

    private void seraTokenKu(final String userFaceToken){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceSetOperate FaceSet = new FaceSetOperate(ConfigUtil.FACE_KEY,ConfigUtil.FACE_SECRET, false);
                CommonOperate commonOperate = new CommonOperate(key, secret, false);
                Response res = null;
                try {
                    res = commonOperate.searchByOuterId(userFaceToken, null, null, "test", 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String result = new String(res.getContent());
                try {
                    faceBean=new FaceBean();
                    confidence =pullFaceBeanJson(result);
                    faceBean.setConfidence(confidence);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("result", result);
                sb.append("\n");
                sb.append("\n");
                sb.append("search result: ");
                sb.append(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultTv.setText(confidence);
                        jsonTextView.setText(sb.toString());

                    }
                });
            }
        }).start();
    }
    private String pullFaceBeanJson(String string) throws JSONException {
        FaceBean faceBean = new FaceBean();

        JSONObject jsonObject = new JSONObject(string);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        JSONObject arrayObject = jsonArray.getJSONObject(0);
        String confidence = arrayObject.getString("confidence");

        faceBean.setConfidence(confidence);
        return confidence;
    }

    @Override
    public void onClick(View v) {

    }
}
