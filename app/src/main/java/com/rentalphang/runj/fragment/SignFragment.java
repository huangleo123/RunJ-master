package com.rentalphang.runj.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rentalphang.runj.R;
import com.rentalphang.runj.activity.AddFaceActivity;
import com.rentalphang.runj.activity.FaceSignActivity;
import com.rentalphang.runj.model.bean.User;

import cn.bmob.v3.BmobUser;


/** 签到 人脸识别或二维码
 * Created by dd on 2018/5/29.
 */

public class SignFragment extends Fragment implements View.OnClickListener{
    //判断user中是否有存在facetoken,如果没有就进行拍照，
    private User user;
    private Button signButton;
    private TextView nameTextview;
    private String faceToken;
    private TextView idtextView;
    private TextView statusTextView;
    private View rootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=  inflater.inflate(R.layout.fragment_sign,null);
        user = BmobUser.getCurrentUser(getActivity(),User.class);
        faceToken=user.getFacetoken();
        initComponent();//初始化组件

        showUserData(); //显示用户数据

        //显示签到状态
        //如果点击了签到跳转到签到界面

        return rootView;

    }
    private void initComponent(){
        signButton= (Button) rootView.findViewById(R.id.bt_sign);
        nameTextview = (TextView) rootView.findViewById(R.id.tv_sign_name_value);
        idtextView = (TextView) rootView.findViewById(R.id.tv_sign_ID_value);
        statusTextView = (TextView) rootView.findViewById(R.id.tv_sign_status_value);
        signButton.setOnClickListener(this);
    }
    private void whetherToShot(){
        if (faceToken.equals("") ){
            //TODO 跳转到增加人脸中

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("提示");
            builder.setMessage("您未注册人脸信息");
            builder.setPositiveButton("现在就去注册", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent toAddFaceIntent = new Intent(getActivity(), AddFaceActivity.class);
                    startActivity(toAddFaceIntent);
                }
            });
            builder.show();


        }

    }
    private void showUserData(){
        nameTextview.setText(user.getTruename());
        idtextView.setText(user.getStudent_ID());
        if (!user.getIssign()){         //不等于真，没有签到
            statusTextView.setText("未签到");
        }else
            statusTextView.setText("已经签到");


    }
    @Override
    public void onClick(View v) {
        //TODO 跳转到摄像头界面，开始人脸比对
        switch (v.getId()){
            case R.id.bt_sign:
                whetherToShot();//
                if(user.getIssign()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("提示");
                    builder.setMessage("您已经签到");
                    builder.show();
                }
                if (!faceToken.equals("")){ //不为空
                    if (!user.getIssign()){ //没签到
                        Intent intent = new Intent(getContext(), FaceSignActivity.class);
                        startActivity(intent);
                    }
                }else{
                  /*  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("提示");
                    builder.setMessage("请先注册人脸信息");
                    builder.setPositiveButton("现在就去注册", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent toAddFaceIntent = new Intent(getActivity(), AddFaceActivity.class);
                            startActivity(toAddFaceIntent);
                        }
                    });
                    builder.show();*/
                }



        }

    }

}
