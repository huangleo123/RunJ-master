package com.rentalphang.runj.fragment;

import android.media.tv.TvContentRating;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rentalphang.runj.R;
import com.rentalphang.runj.model.bean.User;

import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/** 签到 人脸识别或二维码
 * Created by dd on 2018/5/29.
 */

public class SignFragment extends Fragment implements View.OnClickListener{
    //判断user中是否有存在facetoken,如果没有就进行拍照，
    private User user;
    private Button signButton;
    private TextView nameTextview;
    private TextView idtextView;
    private TextView statusTextView;
    private View rootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=  inflater.inflate(R.layout.fragment_sign,null);
        user = BmobUser.getCurrentUser(getActivity(),User.class);
        initComponent();
        whetherToShot();
        showUserData();

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
        if (user.getStudent_ID() == null){
            //TODO 跳转到人脸识别中
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
    }

}
