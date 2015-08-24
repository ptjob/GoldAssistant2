package com.parttime.mine.setting;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carson.constant.JiaoyanUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.parttime.base.LocalInitActivity;
import com.parttime.login.RegisterInfoActivity;
import com.parttime.net.BaseRequest;
import com.parttime.net.Callback;
import com.parttime.net.ErrorHandler;
import com.parttime.widget.EditItem;
import com.qingmu.jianzhidaren.R;
import com.quark.common.Url;
import com.quark.volley.VolleySington;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cjz on 2015/7/24.
 */
public class ModifyPwdActivity extends LocalInitActivity implements Callback{

    @ViewInject(R.id.ei_old_pwd)
    private EditItem eiOldPwd;
    @ViewInject(R.id.ei_new_pwd)
    private EditItem eiNewPwd;
    @ViewInject(R.id.btn_finish)
    private Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        COMPANY_EDITPASSWORD
        setContentView(R.layout.activity_modify_pwd);
        setContentView(R.layout.activity_modify_pwd);
        ViewUtils.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        super.initViews();
        center(R.string.modify_pwd);
        left(TextView.class, R.string.back);
        eiOldPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        eiNewPwd.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    @OnClick(R.id.btn_finish)
    public void modify(View v){
        if(!validate()){
            return;
        }
        showWait(true);
        Map<String, String> params = new HashMap<String, String>();
        params.put("old_password", JiaoyanUtil.MD5(eiOldPwd.getValue().trim()));
        params.put("new_password", JiaoyanUtil.MD5(eiNewPwd.getValue().trim()));
        params.put("user_id", getCompanyId());
        new BaseRequest().request(Url.MODIFY_PWD, params, VolleySington.getInstance().getRequestQueue(), this);
    }

    private boolean validate(){
        String pwd = eiNewPwd.getValue();
        if(pwd.length() <= 0){
            showToast(R.string.please_set_pwd);
            return false;
        }
        if(pwd .length() < RegisterInfoActivity.PWD_AT_LEAT || pwd.length() > RegisterInfoActivity.PWD_AT_MOST){
            showToast(R.string.pwd_should_range_from_6_to_10);
            return false;
        }
        return true;
    }

    @Override
    protected ViewGroup getLeftWrapper() {
        return null;
    }

    @Override
    protected ViewGroup getRightWrapper() {
        return null;
    }

    @Override
    protected TextView getCenter() {
        return null;
    }

    @Override
    public void success(Object obj) {
        showWait(false);
        showToast(R.string.modify_pwd_success);
        finish();
    }

    @Override
    public void failed(Object obj) {
        showWait(false);
        new ErrorHandler(this, obj).showToast();
    }
}
