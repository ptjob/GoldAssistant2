package com.parttime.addresslist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.carson.constant.ConstantForSaveList;
import com.google.gson.Gson;
import com.parttime.common.head.ActivityHead;
import com.parttime.constants.ActivityExtraAndKeys;
import com.parttime.constants.ApplicationConstants;
import com.parttime.constants.SharedPreferenceConstants;
import com.parttime.net.DefaultCallback;
import com.parttime.net.UserDetailRequest;
import com.parttime.utils.SharePreferenceUtil;
import com.qingmu.jianzhidaren.R;
import com.quark.jianzhidaren.ApplicationControl;
import com.quark.jianzhidaren.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class GroupUpdateRemarkActivity extends BaseActivity {

    private String groupId , userId;
    private String oldName;


    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_update_remark);
        ActivityHead headView = new ActivityHead(this);
        headView.setCenterTxt1(R.string.update_group_remark);

        name = (EditText)findViewById(R.id.name);
        findViewById(R.id.done).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String newName = name.getText().toString();
                if(! newName.equals(oldName)){
                    if(! (userId.contains(ApplicationConstants.NORMALI_USER_PREFIX_CHAR) || userId.contains(ApplicationConstants.SPECIAL_USER_PREFIX_CHAR))){
                        userId = ApplicationConstants.NORMALI_USER_PREFIX_CHAR + userId;
                    }
                    new UserDetailRequest().setUserAlias(groupId,userId,newName,queue,new DefaultCallback(){
                        @Override
                        public void success(Object obj) {
                            Map<String, String> cache = ConstantForSaveList.aliasCache.get(groupId);
                            if(cache == null){
                                cache = new HashMap<>();
                            }
                            cache.put(userId,newName);
                            ConstantForSaveList.aliasCache.put(groupId,cache);
                            Toast.makeText(GroupUpdateRemarkActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra(ActivityExtraAndKeys.name, newName);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    });
                }


            }
        });

        bindData();

    }

    private void bindData() {
        groupId = getIntent().getStringExtra(ActivityExtraAndKeys.GroupSetting.GROUPID);
        userId = getIntent().getStringExtra(ActivityExtraAndKeys.USER_ID);
        oldName = getIntent().getStringExtra(ActivityExtraAndKeys.GroupUpdateRemark.USER_NAME);

        if(! (userId.contains(ApplicationConstants.NORMALI_USER_PREFIX_CHAR) || userId.contains(ApplicationConstants.SPECIAL_USER_PREFIX_CHAR))){
            userId = ApplicationConstants.NORMALI_USER_PREFIX_CHAR + userId;
        }
        Map<String, String> cache = ConstantForSaveList.aliasCache.get(groupId);
        if(cache != null && !TextUtils.isEmpty(cache.get(userId))){
            oldName = cache.get(userId);
        }

        name.setText(oldName);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharePreferenceUtil.getInstance(ApplicationControl.getInstance())
                .saveSharedPreferences(
                        SharedPreferenceConstants.GROUP_REMARK_CONFIGGURE,
                        new Gson().toJson(ConstantForSaveList.aliasCache)
                );
    }
}
