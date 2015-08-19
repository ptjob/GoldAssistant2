/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.parttime.addresslist;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.carson.constant.ConstantForSaveList;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.activity.AlertDialog;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.db.MessageSetDao;
import com.easemob.chatuidemo.widget.ExpandGridView;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parttime.IM.ChatActivity;
import com.parttime.IM.activitysetting.GroupGagActivity;
import com.parttime.common.Image.ContactImageLoader;
import com.parttime.constants.ActionConstants;
import com.parttime.constants.ActivityExtraAndKeys;
import com.parttime.constants.SharedPreferenceConstants;
import com.parttime.net.DefaultCallback;
import com.parttime.net.HuanXinRequest;
import com.parttime.pojo.BaseUser;
import com.parttime.pojo.MessageSet;
import com.parttime.utils.IntentManager;
import com.parttime.utils.SharePreferenceUtil;
import com.parttime.widget.SetItem;
import com.qingmu.jianzhidaren.R;
import com.quark.common.Url;
import com.quark.http.image.LoadImage;
import com.quark.jianzhidaren.ApplicationControl;
import com.quark.model.HuanxinUser;
import com.quark.ui.widget.CustomDialog;
import com.quark.volley.VolleySington;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: GroupResumeSettingActivity
 * @Description: 群设置
 * @author howe
 * @date 2015-2-12 下午12:19:16
 * 
 */
public class NormalGroupSettingActivity extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "NormalGroupSettingActivity";
	private static final int REQUEST_CODE_ADD_USER = 0;
	private static final int REQUEST_CODE_EXIT = 1;
	private static final int REQUEST_CODE_EXIT_DELETE = 2;
	private static final int REQUEST_CODE_CLEAR_ALL_HISTORY = 3;
	private static final int REQUEST_CODE_ADD_TO_BALCKLIST = 4;
	private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;
	private static final int REQUEST_CODE_EXPORT_GROUPLIST = 6;

	private ExpandGridView userGridview;
	private ProgressBar loadingPB;
	private Button exitBtn;
	private Button deleteBtn;
	private GridAdapter adapter;
	private ProgressDialog progressDialog;
	public static NormalGroupSettingActivity instance;


	private SharePreferenceUtil sp;
	private RelativeLayout topLayout;
    private SetItem top, //置顶
            undisturb,   //免扰
            gag;         //禁言

    private int referenceWidth;
    private int referenceHeight;

    private boolean isDisturb;
    private String groupType = null;
    private MessageSet messageSet;
    private EMGroup group;
    private ArrayList<HuanxinUser> huanXinUsers;
    private ArrayList<String> userIds = new ArrayList<>();

    private String exportUrl;
    String longClickUsername = null;
    private String groupId;
    boolean isExcuteOnCreate = false;

    private MessageSetDao dao = new MessageSetDao(ApplicationControl.getInstance());
    protected RequestQueue queue = VolleySington.getInstance().getRequestQueue();
    private Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_details);
		sp = SharePreferenceUtil.getInstance(ApplicationControl.getInstance());
		exportUrl = Url.EXPORT_GROUP_LIST;
        
        initView();


        isExcuteOnCreate = true;
        bindData();

    }

    private void bindData() {

        adapter = new GridAdapter(this, R.layout.grid, userIds);
        userGridview.setAdapter(adapter);

        // 获取传过来的groupid
        groupId = getIntent().getStringExtra("groupId");
        group = EMGroupManager.getInstance().getGroup(groupId);

        //获取群组type
        Hashtable<String, EMConversation> conversations = EMChatManager
                .getInstance().getAllConversations();
        if(conversations != null){
            EMConversation conversation = conversations.get(groupId);
            if(conversation != null){
                groupType = conversation.getType().name();
            }
        }else{
            groupType = EMConversation.EMConversationType.GroupChat.name();
        }
        if( groupType != null) {
            //查询置顶
            messageSet = dao.getMessageSet(groupId, groupType);
        }

        if(messageSet != null){
            top.setRightImage(R.drawable.settings_btn_switch_on);
        }

        if(ConstantForSaveList.disturbCache == null || ConstantForSaveList.disturbCache.size() == 0){
            String disturbStr = sp.loadStringSharedPreference(SharedPreferenceConstants.DISTURB_CONFIGGURE);
            if(!TextUtils.isEmpty(disturbStr)){
                HashSet<String> data = gson.fromJson(disturbStr, new TypeToken<HashSet<String>>(){}.getType());
                if(data != null && data.size() > 0){
                    ConstantForSaveList.disturbCache.addAll(data);
                }
            }
        }
        if(ConstantForSaveList.disturbCache.contains(groupId)){
            undisturb.setRightImage(R.drawable.settings_btn_switch_on);
            isDisturb = true;
        }else{
            undisturb.setRightImage(R.drawable.settings_btn_switch_off);
        }

        top.setOnClickListener(this);
        undisturb.setOnClickListener(this);
        gag.setOnClickListener(this);

        if (group.getOwner() == null
                || "".equals(group.getOwner())
                || !group.getOwner().equals(
                        EMChatManager.getInstance().getCurrentUser())) {
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
        }
        // 如果自己是群主，显示解散按钮
        if (EMChatManager.getInstance().getCurrentUser()
                .equals(group.getOwner())) {
            // 判断是私有群并且自己是群主,则显示导出群员列表
            gag.setVisibility(View.VISIBLE);
            exitBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);

        }
        // 保证每次进详情看到的都是最新的group
        updateGroup();
        // 设置OnTouchListener
        userGridview.setOnTouchListener(new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (adapter.isInDeleteMode) {
                        adapter.isInDeleteMode = false;
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                break;
            default:
                break;
            }
            return false;
        }
        });
    }

    private void initView() {
        topLayout = (RelativeLayout) findViewById(R.id.title);
        topLayout.setBackgroundColor(getResources().getColor(
                R.color.guanli_common_color));
        instance = this;
        userGridview = (ExpandGridView) findViewById(R.id.gridview);
        loadingPB = (ProgressBar) findViewById(R.id.progressBar);
        exitBtn = (Button) findViewById(R.id.btn_exit_grp);
        deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);

        top = (SetItem)findViewById(R.id.top);
        undisturb = (SetItem)findViewById(R.id.undisturb);
        gag = (SetItem)findViewById(R.id.gag);

    }

    @Override
	protected void onResume() {
		super.onResume();
        if(! isExcuteOnCreate){
            updateGroup();
        }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(NormalGroupSettingActivity.this);
				progressDialog.setMessage("正在添加...");
				progressDialog.setCanceledOnTouchOutside(false);
			}
			switch (requestCode) {
			case REQUEST_CODE_ADD_USER:// 添加群成员
				final String[] newmembers = data
						.getStringArrayExtra(ActivityExtraAndKeys.Addressbook.MEMBER);
				progressDialog.show();
				addMembersToGroup(newmembers);
				break;
			case REQUEST_CODE_EXIT: // 退出群
				progressDialog.setMessage("正在退出群聊...");
				progressDialog.show();
				exitGrop();
				break;
			case REQUEST_CODE_EXIT_DELETE: // 解散群
				progressDialog.setMessage("正在解散群聊...");
				progressDialog.show();
				deleteGrop();
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 点击退出群组按钮
	 * 
	 * @param view View
	 */
	// 修改为 兼职自己的弹窗
	public void exitGroup(View view) {
		// startActivityForResult(new Intent(this, ExitGroupDialog.class),
		// REQUEST_CODE_EXIT);
		showAlertDialog2("您确定要退出该群吗？", "提示");
	}

	/**
	 * 点击解散群组按钮
	 * 
	 * @param view View
	 */
	// // 修改为 兼职自己的弹窗
	public void exitDeleteGroup(View view) {
		// startActivityForResult(new Intent(this,
		// ExitGroupDialog.class).putExtra("deleteToast",
		// getString(R.string.dissolution_group_hint)),
		// REQUEST_CODE_EXIT_DELETE);
		showAlertDialog("您确定要解散此群吗？", "提示");
	}

	/**
	 * 群主解散
	 * 
	 * @param str String
	 * @param str2 String
	 */
	public void showAlertDialog(String str, final String str2) {

		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage(str);
		builder.setTitle(str2);

		builder.setPositiveButton("解散", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(
							NormalGroupSettingActivity.this);
					progressDialog.setMessage("正在添加...");
					progressDialog.setCanceledOnTouchOutside(false);
				}
				deleteGrop();
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();
	}

	/**
	 * 退出
	 * 
	 * @param str String
	 * @param str2 String
	 */
	public void showAlertDialog2(String str, final String str2) {

		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage(str);
		builder.setTitle(str2);

		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(
							NormalGroupSettingActivity.this);
					progressDialog.setMessage("正在添加...");
					progressDialog.setCanceledOnTouchOutside(false);
				}
				exitGrop();
			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();
	}


	/**
	 * 退出群组
	 * 
	 */
	private void exitGrop() {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMGroupManager.getInstance().exitFromGroup(groupId);
					runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                            if (ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(),
									"退出群聊失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 解散群组
	 * 
	 */
	private void deleteGrop() {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							// 访问服务器给推送解散群聊
							String temp = "";
							if (group != null && group.getMembers().size() > 1) {
								for (String s : group.getMembers()) {
									temp += s;
									temp += ":";
								}
								carsonRemoveFromGroup(temp);
							}
							finish();
							if (ChatActivity.activityInstance != null)
								ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(),
									"解散群聊失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 增加群成员
	 * 
	 * @param newmembers String[]
	 */
	private void addMembersToGroup(final String[] newmembers) {
		new Thread(new Runnable() {

			public void run() {
				try {
					// 创建者调用add方法
					if (EMChatManager.getInstance().getCurrentUser()
							.equals(group.getOwner())) {
						EMGroupManager.getInstance().addUsersToGroup(groupId,
								newmembers);
					} else {
						// 一般成员调用invite方法
						EMGroupManager.getInstance().inviteUser(groupId,
								newmembers, null);
					}
					runOnUiThread(new Runnable() {
						public void run() {
                            for(String newUId : newmembers){
                                if(! userIds.contains(newUId)) {
                                    userIds.add(newUId);
                                }
                            }
							adapter.notifyDataSetChanged();
							((TextView) findViewById(R.id.group_name))
									.setText(group.getGroupName() + "("
											+ group.getAffiliationsCount()
											+ ")");
							progressDialog.dismiss();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(),
									"添加群成员失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

            case R.id.top:
                toTop();
                break;
            case R.id.undisturb:
                disturbSet();
                break;
            case R.id.gag:
                ConstantForSaveList.userIdUserCache.clear();

                if(huanXinUsers != null && huanXinUsers.size() > 0){
                    for(HuanxinUser userVO : huanXinUsers){
                        if(userVO != null){
                            BaseUser baseUser = new BaseUser();
                            baseUser.userId = userVO.getUid();
                            baseUser.name = userVO.getName();
                            baseUser.picture = userVO.getAvatar();
                            ConstantForSaveList.userIdUserCache.put(userVO.getUid(),baseUser);
                        }
                    }
                }
                Intent intent = new Intent(this, GroupGagActivity.class);
                intent.putExtra(ActivityExtraAndKeys.GroupSetting.GROUPID, groupId);
                startActivity(intent);
                break;
        }
    }

    private void toTop(){
        if(group == null){
            return;
        }

        if(messageSet == null){
            messageSet = new MessageSet();
            messageSet.name = group.getGroupId();
            messageSet.type = groupType;
            messageSet.isTop = true;
            messageSet.createTime = System.currentTimeMillis();
            dao.save(messageSet);
            top.setRightImage(R.drawable.settings_btn_switch_on);
        }else{
            dao.delete(groupId, groupType);
            messageSet = null;
            top.setRightImage(R.drawable.settings_btn_switch_off);
        }
    }

    public void disturbSet(){

        if(isDisturb){
            ConstantForSaveList.disturbCache.remove(groupId);
            isDisturb = false;
            undisturb.setRightImage(R.drawable.settings_btn_switch_off);
        }else{
            ConstantForSaveList.disturbCache.add(groupId);
            isDisturb = true;
            undisturb.setRightImage(R.drawable.settings_btn_switch_on);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isExcuteOnCreate = false;
        sp.saveSharedPreferences(SharedPreferenceConstants.DISTURB_CONFIGGURE,gson.toJson(ConstantForSaveList.disturbCache));
        new Thread(new Runnable() {
            @Override
            public void run() {
                EMChatManager.getInstance().getChatOptions().setReceiveNotNoifyGroup(new ArrayList<>(ConstantForSaveList.disturbCache));
            }
        }).start();
    }

    class ViewHold {
		ImageView avatar, delete;
        TextView name;
	}

	/**
	 * 群组成员gridadapter
	 * 
	 * @author admin_new
	 * 
	 */
	private class GridAdapter extends ArrayAdapter<String> {

		private int res;
		public boolean isInDeleteMode;

		public GridAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			res = textViewResourceId;
			isInDeleteMode = false;
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {

			ViewHold viewhold;
			if (convertView == null) {
				viewhold = new ViewHold();
				convertView = LayoutInflater.from(getContext()).inflate(res,
						null);
				viewhold.avatar = (ImageView) convertView
						.findViewById(R.id.avatar);
                viewhold.name = (TextView) convertView.findViewById(R.id.name);
                viewhold.delete = (ImageView) convertView.findViewById(R.id.badge_delete);
				convertView.setTag(viewhold);
			} else {
				viewhold = (ViewHold) convertView.getTag();
			}

            //Log.i(TAG,"count = " + getCount() + "position = " + position + ";avatar =" + viewhold.avatar);
			// 最后一个item，减人按钮
			if (position == getCount() - 1) {
				viewhold.name.setText("");
				// 设置成删除按钮
                viewhold.avatar.setTag("delete");
				viewhold.avatar.setBackgroundResource(R.drawable.smiley_minus_btn);
				// 如果不是创建者或者没有相应权限，不提供加减人按钮
				if (!group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
					// if current user is not group admin, hide add/remove avatar
					convertView.setVisibility(View.INVISIBLE);
				} else { // 显示删除按钮
					if (isInDeleteMode) {
						// 正处于删除模式下，隐藏删除按钮
						convertView.setVisibility(View.INVISIBLE);
					} else {
						// 正常模式
						convertView.setVisibility(View.VISIBLE);
                        viewhold.delete.setVisibility(View.INVISIBLE);
					}
					viewhold.avatar.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EMLog.d(TAG, "删除按钮被点击");
                            isInDeleteMode = true;
                            notifyDataSetChanged();
                        }
                    });
				}
			} else if (position == getCount() - 2) { // 添加群组成员按钮
                viewhold.name.setText("");
                // 设置成加入按钮
                viewhold.avatar.setTag("add");
                viewhold.avatar.setBackgroundResource(R.drawable.smiley_add_btn);
				// 如果不是创建者或者没有相应权限
				if (!group.isAllowInvites()
						&& !group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
					// if current user is not group admin, hide add/remove avatar
					convertView.setVisibility(View.INVISIBLE);
				} else {
					// 正处于删除模式下,隐藏添加按钮
					if (isInDeleteMode) {
						convertView.setVisibility(View.INVISIBLE);
					} else {
						convertView.setVisibility(View.VISIBLE);
                        viewhold.delete.setVisibility(View.INVISIBLE);
					}
					viewhold.avatar.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EMLog.d(TAG, "添加按钮被点击");
                            // 进入选人页面
                            Intent intent  = new Intent(NormalGroupSettingActivity.this,GroupPickContactsActivity.class);
                            intent.putExtra(ActivityExtraAndKeys.GroupSetting.GROUPID, groupId);
                            intent.putExtra(ActivityExtraAndKeys.FROM_WHERE, GroupPickContactsActivity.FROM_NORMAL_GROUP_SETTING);
                            startActivityForResult(intent, REQUEST_CODE_ADD_USER);
                        }
                    });
				}
			} else {
				if (userIds != null && position < userIds.size()) {
					// 普通item，显示群组成员
					// final String username = getItem(position);
					final String username = userIds.get(position);
					convertView.setVisibility(View.VISIBLE);
					viewhold.avatar.setVisibility(View.VISIBLE);

					// 加载缓存头像、名称
					if (viewhold.name != null) {
						viewhold.name.setText(sp.loadStringSharedPreference(
								username + "realname", ""));
					}

                    // 加载本地图片
                    Bitmap bitmap = ContactImageLoader.get(username);

                    if (bitmap != null) {
                        if (viewhold.name != null) {
                            viewhold.name.setText(sp.loadStringSharedPreference(username + "realname", ""));
                        }
                        viewhold.avatar.setBackgroundDrawable(LoadImage.bitmapToDrawable(bitmap));
                    } else {
                        getNick(username, viewhold.avatar,viewhold.name);
                    }

					// demo群组成员的头像都用默认头像，需由开发者自己去设置头像
					if (isInDeleteMode) {
						// 如果是删除模式下，显示减人图标
                        viewhold.delete.setVisibility(View.VISIBLE);
					} else {
                        viewhold.delete.setVisibility(View.INVISIBLE);
					}

                    viewhold.avatar.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!NetUtils
                                    .hasNetwork(getApplicationContext())) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        getString(R.string.network_unavailable),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                if (EMChatManager.getInstance().getCurrentUser().equals(username)) {
                                    Toast.makeText(getApplicationContext(),
                                            "您点击了自己", Toast.LENGTH_SHORT).show();
                                } else {

                                    ArrayList<String> uIds = new ArrayList<>(userIds);
                                    String uId = EMChatManager.getInstance().getCurrentUser();
                                    //去掉自己
                                    uIds.remove(uId);
                                    // 正常情况下点击user，可以进入用户详情
                                    IntentManager.intentToUseDetail(NormalGroupSettingActivity.this, username, groupId, uIds, group.getOwner());
                                }
                            }
                        }
                    });

                    viewhold.delete.setTag(username);
					viewhold.delete.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String userId = (String) v.getTag();
                            if (isInDeleteMode) {
                                // 如果是删除自己，return
                                if (EMChatManager.getInstance()
                                        .getCurrentUser().equals(userId)) {
                                    startActivity(new Intent(
                                            NormalGroupSettingActivity.this,
                                            AlertDialog.class).putExtra("msg",
                                            "不能删除自己"));
                                    return;
                                }
                                if (!NetUtils
                                        .hasNetwork(getApplicationContext())) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            getString(R.string.network_unavailable),
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                EMLog.d("group", "remove user from group:"
                                        + userId);
                                deleteMembersFromGroup(userId);
                            }
                        }

                        /**
                         * 删除群成员
                         *
                         * @param username String
                         */
                        protected void deleteMembersFromGroup(
                                final String username) {
                            final ProgressDialog deleteDialog = new ProgressDialog(
                                    NormalGroupSettingActivity.this);
                            deleteDialog.setMessage("正在移除...");
                            deleteDialog.setCanceledOnTouchOutside(false);
                            deleteDialog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // 删除被选中的成员
                                        EMGroupManager.getInstance()
                                                .removeUserFromGroup(groupId,
                                                        username);
                                        isInDeleteMode = false;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                deleteDialog.dismiss();
                                                userIds.remove(username);
                                                notifyDataSetChanged();
                                                carsonRemoveFromGroup(username);
                                                ((TextView) findViewById(R.id.group_name)).setText(group
                                                        .getGroupName()
                                                        + "("
                                                        + group.getAffiliationsCount()
                                                        + ")");
                                            }
                                        });
                                    } catch (final Exception e) {
                                        deleteDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "删除失败："
                                                                + e.getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                }
                            }).start();
                        }
                    });

					/*viewhold.avatar
							.setOnLongClickListener(new OnLongClickListener() {

								@Override
								public boolean onLongClick(View v) {
									if (group.getOwner().equals(
											EMChatManager.getInstance()
													.getCurrentUser())) {
										Intent intent = new Intent(
												NormalGroupSettingActivity.this,
												AlertDialog.class);
										intent.putExtra("msg",
												"确认将此成员加入至此群黑名单?");
										intent.putExtra("cancel", true);
										startActivityForResult(intent,
												REQUEST_CODE_ADD_TO_BALCKLIST);
										longClickUsername = username;
									}
									return false;
								}
							});*/
				}
			}
			return convertView;
		}


        @Override
		public int getCount() {
			return super.getCount() == 0 ? 0 : (super.getCount() + 2);
		}
	}

	protected void updateGroup() {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMGroup returnGroup = EMGroupManager.getInstance()
							.getGroupFromServer(groupId);
					// 更新本地数据
					EMGroupManager.getInstance().createOrUpdateLocalGroup(
							returnGroup);

					runOnUiThread(new Runnable() {
						public void run() {
							((TextView) findViewById(R.id.group_name))
									.setText(group.getGroupName() + "("
											+ group.getAffiliationsCount()
											+ ")");
							loadingPB.setVisibility(View.INVISIBLE);
							List<String> members = group.getMembers();
                            if(members != null && members.size() > 0){
                                Log.i(TAG, "after add member and size is = " + members.size());
                                for(String member : members) {
                                    if(! userIds.contains(member)){
                                        userIds.add(member);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
							if (EMChatManager.getInstance().getCurrentUser()
									.equals(group.getOwner())) {
								// 显示解散按钮
								exitBtn.setVisibility(View.GONE);
								deleteBtn.setVisibility(View.VISIBLE);
                                gag.setVisibility(View.VISIBLE);
							} else {
								// 显示退出按钮
								exitBtn.setVisibility(View.VISIBLE);
								deleteBtn.setVisibility(View.GONE);
                                gag.setVisibility(View.GONE);
							}

						}
					});

				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							loadingPB.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		}).start();
	}

	public void back(View view) {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
        sendBroadcast(new Intent(ActionConstants.ACTION_MESSAGE_TO_TOP));
	}


	public void getNick(final String id, final ImageView avatar, final TextView name) {
        avatar.setTag(R.id.avatar, id);
        new HuanXinRequest().getHuanxinUserList(id, queue, new DefaultCallback(){
            @Override
            public void success(Object obj) {
                super.success(obj);
                if(obj instanceof ArrayList){
                    @SuppressLint("unchecked")
                    ArrayList<HuanxinUser> list = (ArrayList<HuanxinUser>)obj;
                    huanXinUsers = list;
                    if(list.size() == 1) {
                        HuanxinUser us = list.get(0);
                        if (us.getName() != null
                                && !"".equals(us.getName())) {
                            if (avatar.getTag(R.id.avatar) != null
                                    && avatar.getTag(R.id.avatar).equals(id)) {
                                name.setText(us.getName());
                            }
                            sp.saveSharedPreferences(id + "realname", us.getName());
                        } else {
                            name.setText("");
                        }
                        if ((us.getAvatar() != null) && (!us.getAvatar().equals(""))) {
                            loadpersonPic(id, us.getAvatar(), avatar, 1);
                        } else {
                            avatar.setBackgroundResource(R.drawable.default_avatar);
                        }
                    }
                }
            }
        });
	}

	/**
	 * @Description: 加载图片
	 * @author howe
	 * @date 2014-7-30 下午5:57:52
	 *
	 */
	public void loadpersonPic(final String id, final String url,
			final ImageView avatar, final int isRound) {
        avatar.setTag(url);
        ContactImageLoader.loadpersonPic(queue,id, url, avatar,isRound);
	}

	// ================howe end==================
	/**
	 * 群主踢人或者解散群组给推送
	 *
	 */
	private void carsonRemoveFromGroup(final String userName) {
		StringRequest request = new StringRequest(Request.Method.POST,
				Url.REMOVE_FROM_GROUP, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject js = new JSONObject(response);

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {

					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<>();
				map.put("group_name", group.getGroupName());
				map.put("user_id", userName);
				return map;
			}
		};
		queue.add(request);
		request.setRetryPolicy(new DefaultRetryPolicy(
				ConstantForSaveList.DEFAULTRETRYTIME * 1000, 1, 1.0f));
	}
}
