package com.parttime.IM.activitysetting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.carson.constant.ConstantForSaveList;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.exceptions.EaseMobException;
import com.parttime.common.Image.ContactImageLoader;
import com.parttime.common.head.ActivityHead;
import com.parttime.constants.ActivityExtraAndKeys;
import com.parttime.net.DefaultCallback;
import com.parttime.net.HuanXinRequest;
import com.parttime.net.UserDetailRequest;
import com.parttime.pojo.BaseUser;
import com.qingmu.jianzhidaren.R;
import com.quark.model.HuanxinUser;
import com.quark.utils.NetWorkCheck;
import com.quark.volley.VolleySington;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupGagActivity extends BaseActivity {

    private ActivityHead headView;
    private ListView listView;

    private UngagAdapter adapter = new UngagAdapter();

    private ArrayList<AdapterVO> adapterVOs = new ArrayList<>();

    private String groupId;

    protected RequestQueue queue = VolleySington.getInstance().getRequestQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_gag);
        
        initView(); 
        
        bindData();
        
    }

    private void initView() {
        headView = new ActivityHead(this);
        headView.setCenterTxt1(R.string.group_not_allow_send_msg);
        listView = (ListView)findViewById(R.id.listView);

        listView.setAdapter(adapter);

        Intent intent = getIntent();
        groupId = intent.getStringExtra(ActivityExtraAndKeys.GroupSetting.GROUPID);

        new Thread(new Runnable() {

            public void run() {
                //获取禁言列表
                new UserDetailRequest().getGagList(groupId,queue,new DefaultCallback(){
                    @Override
                    public void success(Object obj) {
                        @SuppressWarnings("unchecked")
                        final ArrayList<UserDetailRequest.GagUser> gagUsers = (ArrayList<UserDetailRequest.GagUser>) obj;
                        if (gagUsers != null) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Map<String, BaseUser> userIdPictureMap = ConstantForSaveList.userIdUserCache;
                                    StringBuilder requestData = new StringBuilder();
                                    final HashMap<String , AdapterVO> temp = new HashMap<>();
                                    for(UserDetailRequest.GagUser gagUser : gagUsers) {
                                        if(gagUser == null){
                                            continue;
                                        }
                                        String userId = gagUser.userId;
                                        AdapterVO vo = new AdapterVO();
                                        vo.userId = userId;
                                        if(userIdPictureMap != null){
                                            BaseUser baseUser = userIdPictureMap.get(userId);
                                            if(baseUser != null) {
                                                vo.picture = baseUser.picture;
                                                vo.name = gagUser.alias;
                                            }else{
                                                requestData.append(userId).append(",");
                                            }
                                        }else{
                                            requestData.append(userId).append(",");
                                        }
                                        if(TextUtils.isEmpty(vo.name)){
                                            temp.put(userId, vo);
                                        }else {
                                            adapterVOs.add(vo);
                                        }
                                    }
                                    //从普通群组过来的数据可能存在没有用户信息的情况
                                    if(requestData.length() > 0){ //本地不存在用户信息，直接请求网络数据
                                        String requestD = requestData.subSequence(0, requestData.length() - 1).toString();
                                        //获取环信用户信息
                                        new HuanXinRequest().getHuanxinUserList(requestD, queue , new DefaultCallback(){
                                            @Override
                                            public void success(Object obj) {
                                                @SuppressWarnings("unchecked")
                                                ArrayList<HuanxinUser> huanxinUsers = (ArrayList<HuanxinUser>)obj;
                                                if(huanxinUsers != null && huanxinUsers.size() > 0){
                                                    for(HuanxinUser huanxinUser : huanxinUsers){
                                                        String userId = huanxinUser.getUid();
                                                        AdapterVO vo = temp.get(userId);
                                                        if(vo != null){
                                                            vo.name = huanxinUser.getName();
                                                            vo.picture = huanxinUser.getAvatar();
                                                            adapterVOs.add(vo);
                                                        }
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }else {
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                });

            }
        }).start();
    }

    private void bindData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除保存的UserId和picture
        ConstantForSaveList.userIdUserCache.clear();
    }

    class UngagAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return adapterVOs.size();
        }

        @Override
        public AdapterVO getItem(int position) {
            return adapterVOs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view ;
            ViewHolder holder ;
            if(convertView == null){
                view = getLayoutInflater().inflate(R.layout.activity_group_gag_item,parent, false);

                holder = new ViewHolder();
                holder.picture = (ImageView)view.findViewById(R.id.picture);
                holder.name = (TextView)view.findViewById(R.id.name);
                holder.unGag = (Button)view.findViewById(R.id.ungag);

                view.setTag(holder);

            }else{
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            bindItemData(holder, position);

            return view;
        }

        private void bindItemData(ViewHolder holder , int position) {
            AdapterVO itemVo = getItem(position);
            //设置头像
            String head = itemVo.picture;
            if (! TextUtils.isEmpty(head)) {
                holder.picture.setTag(itemVo.picture);
                // 默认加载本地图片
                ContactImageLoader.loadNativePhoto(String.valueOf(itemVo.userId),
                        head, holder.picture, queue);
            } else {
                holder.picture.setBackgroundResource(R.drawable.default_avatar);
            }

            holder.name.setText(itemVo.name);
            holder.unGag.setTag(position);
            holder.unGag.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                if (! NetWorkCheck.isOpenNetwork(GroupGagActivity.this)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_net_tip), Toast.LENGTH_SHORT).show();
                    return;
                }

                int position = (int)v.getTag();
                final AdapterVO itemVo = getItem(position);
                final String tobeRemoveUser = itemVo.userId;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new UserDetailRequest().setUserGag(groupId,tobeRemoveUser, UserDetailRequest.GagStatus.OFF, queue, new DefaultCallback(){
                            @Override
                            public void success(Object obj) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapterVOs.remove(itemVo);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }

                            @Override
                            public void failed(Object obj) {
                                Toast.makeText(GroupGagActivity.this, getString(R.string.failed),Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }).start();


                }
            });
        }
    }

    class AdapterVO{
        String userId;
        String name;
        String picture;
    }

    class ViewHolder{
        ImageView picture;
        TextView name;
        Button unGag;
    }

}
