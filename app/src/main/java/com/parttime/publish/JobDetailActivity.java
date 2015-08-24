package com.parttime.publish;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parttime.common.head.ActivityHead;
import com.parttime.net.DefaultCallback;
import com.parttime.net.ErrorHandler;
import com.parttime.net.PublishRequest;
import com.parttime.pojo.CertVo;
import com.parttime.pojo.JobAuthType;
import com.parttime.pojo.PartJob;
import com.parttime.pojo.PreCheckStatus;
import com.parttime.utils.ApplicationUtils;
import com.parttime.utils.CheckUtils;
import com.parttime.utils.IntentManager;
import com.qingmu.jianzhidaren.R;
import com.quark.common.ToastUtil;
import com.quark.common.Url;
import com.quark.jianzhidaren.ApplicationControl;
import com.quark.jianzhidaren.BaseActivity;
import com.quark.share.ShareModel;
import com.quark.share.SharePopupWindow;
import com.quark.ui.widget.CustomDialog;
import com.quark.ui.widget.CustomDialogThree;
import com.quark.utils.Logger;
import com.quark.utils.NetWorkCheck;
import com.thirdparty.alipay.RechargeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;

/**
 * 活动详情页
 * Created by wyw on 2015/7/20.
 */
public class JobDetailActivity extends BaseActivity {

    private static final int REQUEST_MODIFY = 0x123;

    enum Type {
        REVIEW,
        DETAIL
    }

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_GROUP_ID = "group_id";
    public static final String EXTRA_PART_JOB = "part_job";
    public static final String EXTRA_SHOULD_REFRESH = "should_refresh";


    private View mViewRoot;
    private TextView mTxtStatus, mTxtViewCount, mTxtHandCount;
    private TextView mTxtType, mTxtTitle, mTxtSalary, mTxtCompany, mTxtWorkArea, mTxtWorkTime;
    private TextView mTxtPayType, mTxtHeadSum, mTxtWorkAddress, mTxtWorkRequire;
    private TextView mTxtHeight, mTxtMeasurements, mTxtHealthProve, mTxtLanguage;
    private TextView mTxtRefreshOrExpedited, mTxtShelvesOrRepublish;

    private ImageView mImgViRefreshOrExpedited, mImgViShelvesOrRepublish;

    private LinearLayout mLLDeclareContainer, mLLCompanyContainer, mLLMoreRequireContainer, mLLActionContainer;
    private LinearLayout mLLHeightContainer, mLLMeasurementsContainer, mLLLanguageContainer, mLLHealthProveContainer;
    private LinearLayout mLLJobRefreshOrExpedited, mLLJobModify, mLLJobShelvesOrRepublish;

    private ActivityHead activityHead;
    private SharePopupWindow sharePopupWindow;

    private int id;
    private String groupId;
    private PartJob partJob;
    private Type type;

    private int bindCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindCount = 0;
        setContentView(R.layout.activity_job_detail);
        ShareSDK.initSDK(this);
        initIntent();
        initControls();
        bindListener();
        bindData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharePopupWindow != null && sharePopupWindow.isShowing()) {
            sharePopupWindow.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_MODIFY) {
            // 编辑成功后，刷新页面
            bindData();
        }
    }

    @Override
    public void finish() {
        Logger.i("bindCount=" + bindCount);
        if (bindCount > 1) {
            Intent intent = getIntent();
            intent.putExtra(EXTRA_SHOULD_REFRESH, true);
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }

    private void bindData() {
        ++bindCount;
        if (type == Type.REVIEW) {
            bindWithPartJob();
        } else if (type == Type.DETAIL) {
            showWait(true);
            new PublishRequest().publishActivityDetail(id, groupId, queue, new DefaultCallback() {
                @Override
                public void success(Object obj) {
                    showWait(false);
                    partJob = (PartJob) obj;
                    bindWithPartJob();
                }

                @Override
                public void failed(Object obj) {
                    showWait(false);
                    new ErrorHandler(JobDetailActivity.this, obj);
                }
            });
        }
    }


    private void bindListener() {
        activityHead.setRightTxtOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
        mLLJobRefreshOrExpedited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTxtRefreshOrExpedited.getText().equals(getString(R.string.refresh))) {
                    jobRefresh();
                } else {
                    jobExpedited();
                }
            }
        });

        mLLJobModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jobModify();
            }
        });

        mLLJobShelvesOrRepublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTxtShelvesOrRepublish.getText().equals(getString(R.string.shelves))) {
                    jobShelves();
                } else {
                    jobRepublish();
                }
            }
        });
    }


    private void initControls() {

        mViewRoot = findViewById(R.id.root);
        mTxtStatus = (TextView) findViewById(R.id.txt_job_status);
        mTxtViewCount = (TextView) findViewById(R.id.txt_view_count);
        mTxtHandCount = (TextView) findViewById(R.id.txt_hand_count);

        mTxtType = (TextView) findViewById(R.id.txt_type);
        mTxtTitle = (TextView) findViewById(R.id.txt_title);
        mTxtSalary = (TextView) findViewById(R.id.txt_salary);
        mTxtCompany = (TextView) findViewById(R.id.txt_company);
        mTxtWorkArea = (TextView) findViewById(R.id.txt_work_area);
        mTxtWorkTime = (TextView) findViewById(R.id.txt_work_time);
        mTxtPayType = (TextView) findViewById(R.id.txt_pay_type);
        mTxtHeadSum = (TextView) findViewById(R.id.txt_head_sum);
        mTxtWorkAddress = (TextView) findViewById(R.id.txt_work_address);
        mTxtWorkRequire = (TextView) findViewById(R.id.txt_work_require);
        mTxtHeight = (TextView) findViewById(R.id.txt_height);
        mTxtMeasurements = (TextView) findViewById(R.id.txt_measurements);
        mTxtHealthProve = (TextView) findViewById(R.id.txt_health_prove);
        mTxtLanguage = (TextView) findViewById(R.id.txt_language);
        mTxtRefreshOrExpedited = (TextView) findViewById(R.id.txt_refresh_or_expedited);
        mTxtShelvesOrRepublish = (TextView) findViewById(R.id.txt_shelves_or_republish);
        mImgViRefreshOrExpedited = (ImageView) findViewById(R.id.imgvi_refresh_or_expedited);
        mImgViShelvesOrRepublish = (ImageView) findViewById(R.id.imgvi_shelves_or_republish);
        mLLDeclareContainer = (LinearLayout) findViewById(R.id.ll_job_declare_container);
        mLLCompanyContainer = (LinearLayout) findViewById(R.id.ll_company_container);
        mLLMoreRequireContainer = (LinearLayout) findViewById(R.id.ll_more_require_container);
        mLLActionContainer = (LinearLayout) findViewById(R.id.ll_job_action_container);
        mLLHeightContainer = (LinearLayout) findViewById(R.id.ll_height_container);
        mLLMeasurementsContainer = (LinearLayout) findViewById(R.id.ll_measurements_container);
        mLLLanguageContainer = (LinearLayout) findViewById(R.id.ll_language_container);
        mLLHealthProveContainer = (LinearLayout) findViewById(R.id.ll_health_prove_container);
        mLLJobRefreshOrExpedited = (LinearLayout) findViewById(R.id.ll_job_refresh_or_expedited);
        mLLJobModify = (LinearLayout) findViewById(R.id.ll_job_mofify);
        mLLJobShelvesOrRepublish = (LinearLayout) findViewById(R.id.ll_job_shelves_or_republish);

        activityHead = new ActivityHead(this);
        activityHead.initHead(this);
        if (type == Type.REVIEW) {
            activityHead.setCenterTxt1(R.string.publish_job_preview_title);
        } else if (type == Type.DETAIL) {
            activityHead.setCenterTxt1(R.string.publish_job_detail_title);
            activityHead.setRightTxt(R.string.share);
        }

        // 初始化隐藏，并且不可点击状态
        mLLActionContainer.setVisibility(View.GONE);
        switchRefreshOrExpedited(true, false);
        switchModify(false);
        switchShelveOrRepublish(false, true);
    }

    private void initIntent() {
        id = getIntent().getIntExtra(EXTRA_ID, -1);
        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        if (id <= 0 && CheckUtils.isEmpty(groupId)) {
            partJob = (PartJob) getIntent().getSerializableExtra(EXTRA_PART_JOB);
            type = Type.REVIEW;
        } else {
            type = Type.DETAIL;
        }
    }

    @Override
    public void setBackButton() {

    }

    private void bindWithPartJob() {
        Logger.i(partJob.toString());

        mTxtType.setText(CheckUtils.isNull(partJob.type) ? "" : partJob.type);
        mTxtTitle.setText(partJob.title);
        mTxtSalary.setText(LabelUtils.getSalaryLabel(ApplicationControl.getInstance(), partJob.salaryUnit, partJob.salary));

        mTxtCompany.setText(partJob.companyName);
        mTxtWorkArea.setText(partJob.area);
        mTxtWorkTime.setText(getString(R.string.job_detail_work_time_format, partJob.beginTime, partJob.endTime));
        mTxtPayType.setText(partJob.payType);
        if (partJob.apartSex) {
            mTxtHeadSum.setText(getString(R.string.job_detail_apart_sex_format, partJob.maleNum, partJob.femaleNum));
        } else {
            mTxtHeadSum.setText(getString(R.string.job_detail_head_sum_format, partJob.headSum));
        }
        mTxtWorkAddress.setText(partJob.address);
        if (type == Type.DETAIL && partJob.companyId == ApplicationUtils.getLoginId()) {
            // 查看详情并且发布人是自己时
            // 显示 活动信息框 和 下面操作框
            mLLDeclareContainer.setVisibility(View.VISIBLE);
            mLLActionContainer.setVisibility(View.VISIBLE);
            // 初始化下方动作框显示逻辑
            bindActionLogic();
        } else {
            // 同时隐藏活动信息和下面操作框
            mLLDeclareContainer.setVisibility(View.GONE);
            mLLActionContainer.setVisibility(View.GONE);
        }
        if (partJob.isHasMoreRequire()) {
            mLLMoreRequireContainer.setVisibility(View.VISIBLE);
            if (partJob.height != null) {
                mTxtHeight.setText(getString(R.string.job_detail_height_format, partJob.height));
                mLLHeightContainer.setVisibility(View.VISIBLE);
            } else {
                mLLHeightContainer.setVisibility(View.GONE);
            }

            if (partJob.isHasMeasurements()) {
                mTxtMeasurements.setText(getString(R.string.job_detail_measurements_format, partJob.bust, partJob.beltline, partJob.hipline));
                mLLMeasurementsContainer.setVisibility(View.VISIBLE);
            } else {
                mLLMeasurementsContainer.setVisibility(View.GONE);
            }

            if (partJob.healthProve != null) {
                mTxtHealthProve.setText(partJob.healthProve ? getString(R.string.need) : getString(R.string.unneed));
                mLLHealthProveContainer.setVisibility(View.VISIBLE);
            } else {
                mLLHealthProveContainer.setVisibility(View.GONE);
            }
            if (!CheckUtils.isEmpty(partJob.language)) {
                mTxtLanguage.setText(partJob.language);
                mLLLanguageContainer.setVisibility(View.VISIBLE);
            } else {
                mLLLanguageContainer.setVisibility(View.GONE);
            }

        } else {
            mLLMoreRequireContainer.setVisibility(View.GONE);
        }

        mTxtWorkRequire.setText(partJob.workRequire);
    }

    // 下方动作框显示逻辑
    private void bindActionLogic() {
        String status = "";

        if (partJob.jobAuthType == JobAuthType.READY) {
            // 未审核
            status = getString(R.string.job_detail_status_ready);
            if (partJob.isStart) {
                // 如果活动已经开始，刷新按钮则为加急按钮，不可点击。
                switchRefreshOrExpedited(false, false);
            } else {
                // 如果活动未开始，则为刷新按钮，不可点击。
                switchRefreshOrExpedited(true, false);
            }
            switchModify(true);
            switchShelveOrRepublish(false, true);
        } else {
            if (partJob.isEnd) {
                // 活动已结束
                status = getString(R.string.job_detail_status_fail);
                switchRefreshOrExpedited(false, false);
                switchModify(false);
                switchShelveOrRepublish(false, true);
            } else {
                // 活动未结束
                switch (partJob.jobAuthType) {
                    case DELETE:
                    case FAIL_TO_PASS:
                    case STELVE:
                        status = getString(R.string.job_detail_status_fail);
                        // 如果活动结束，则按钮显示为加急，且为不可按状态
                        switchRefreshOrExpedited(false, false);
                        switchModify(true);
                        switchShelveOrRepublish(true, false);
                        break;
                    case FROZEN:
                        status = getString(R.string.job_detail_status_fail);
                        switchRefreshOrExpedited(true, false);
                        switchModify(false);
                        switchShelveOrRepublish(false, true);
                        break;
                    case PASS:
                        status = getString(R.string.job_detail_status_pass);
                        if (partJob.isStart) {
                            // 如果活动已经开始，刷新按钮则为加急按钮。
                            switchRefreshOrExpedited(false, true);
                        } else {
                            // 如果活动未开始，则为刷新按钮。
                            switchRefreshOrExpedited(true, true);
                        }
                        switchModify(true);
                        switchShelveOrRepublish(true, true);
                        break;
                }
            }
        }

        mTxtStatus.setText(status);
        mTxtViewCount.setText(getString(R.string.job_detail_view_count_format, partJob.viewCount));
        mTxtHandCount.setText(getString(R.string.job_detail_hand_count_format, partJob.handCount));
    }

    private void switchRefreshOrExpedited(boolean isRefresh, boolean isEnable) {
        if (isRefresh) {
            mTxtRefreshOrExpedited.setText(R.string.refresh);
            mImgViRefreshOrExpedited.setImageDrawable(getResources().getDrawable(R.drawable.refresh));
        } else {
            mTxtRefreshOrExpedited.setText(R.string.expedited);
            mImgViRefreshOrExpedited.setImageDrawable(getResources().getDrawable(R.drawable.expired));
        }

        mLLJobRefreshOrExpedited.setEnabled(isEnable);
    }

    private void switchShelveOrRepublish(boolean isEnable, boolean isShelve) {
        if (isShelve) {
            mTxtShelvesOrRepublish.setText(R.string.shelves);
            mImgViShelvesOrRepublish.setImageDrawable(getResources().getDrawable(R.drawable.expired));
        } else {
            mTxtShelvesOrRepublish.setText(R.string.republish);
            mImgViShelvesOrRepublish.setImageDrawable(getResources().getDrawable(R.drawable.republish));
        }

        mLLJobShelvesOrRepublish.setEnabled(isEnable);
    }

    private void switchModify(boolean isEnable) {
        mLLJobModify.setEnabled(isEnable);
    }

    // 点击分享按钮触发
    private void share() {
        if (NetWorkCheck.isOpenNetwork(this)) {
            // status =2 ，4时可以分享 其它不能分享
            sharePopupWindow = new SharePopupWindow(this, true, partJob);
            sharePopupWindow.setPlatformActionListener(new ShareDialogActionListener());
            ShareModel model = new ShareModel();

            String shareText = "工作地点:" + partJob.area + ";工作时间:" + partJob.beginTime;
            model.setText(shareText);
            String shareTitle = partJob.title + ";" + LabelUtils.getSalaryLabel(this, partJob.salaryUnit, partJob.salary);
            model.setTitle(shareTitle);

            String share_url = "http://weixin.jobdiy.cn/info1.php?user_id=1&activity_id="
                    + partJob.id + "&type=" + partJob.type;
            model.setUrl(share_url);

            String imageurl = Url.GETPIC + "pop_share_btn_jz.png";
            model.setImageUrl(imageurl);
            sharePopupWindow.initShareParams(model, 0);
            // 添加分享的额外属性
            sharePopupWindow.shareDataFromActivity(String.valueOf(partJob.id),
                    partJob.title, partJob.salary,
                    partJob.salaryUnit.ordinal(), partJob.area,
                    partJob.beginTime, partJob.headSum
                            - partJob.handCount);// 传递activity
            // 详细信息
            sharePopupWindow.showShareWindow();
            // 显示窗口 (设置layout在PopupWindow中显示的位置)
            sharePopupWindow.showAtLocation(mViewRoot, Gravity.BOTTOM
                    | Gravity.CENTER_HORIZONTAL, 0, 0);

        } else {
            ToastUtil.showShortToast("网络不好,请检查网络设置。");
        }

    }

    private void jobRefresh() {
        showWait(true);
        new PublishRequest().preRefresh(partJob.id, queue, new DefaultCallback() {
            @Override
            public void success(Object obj) {
                showWait(false);
                try {
                    if (obj instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) obj;
                        PreCheckStatus status = PreCheckStatus.parse(jsonObject.getInt("status"));

                        if (status == PreCheckStatus.FREE) {
                            // 可以免费刷新一次
                            CustomDialog.Builder builder = new CustomDialog.Builder(JobDetailActivity.this);
                            builder.setMessage(R.string.job_refresh_free_msg);
                            builder.setTitle(R.string.prompt);

                            builder.setPositiveButton(R.string.confirm,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            refreshNow();
                                            dialog.dismiss();
                                        }
                                    });
                            builder.setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();

                        } else if (status == PreCheckStatus.LACK_OF_MONEY) {

                            // 当前免费刷新次数用完,余额不足
                            CustomDialog.Builder builder = new CustomDialog.Builder(JobDetailActivity.this);
                            builder.setMessage(R.string.job_refresh_lack_of_money_msg);
                            builder.setTitle(R.string.prompt);

                            builder.setPositiveButton(R.string.confirm,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            IntentManager.intentToRecharge(JobDetailActivity.this);
                                            dialog.dismiss();
                                        }
                                    });
                            builder.setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();

                        } else if (status == PreCheckStatus.SHOULD_PAY) {
                            // 付费刷新
                            int money = jsonObject.getInt("money");
                            CustomDialog.Builder builder = new CustomDialog.Builder(JobDetailActivity.this);
                            builder.setMessage(getString(R.string.job_refresh_pay_msg, money));
                            builder.setTitle(R.string.prompt);

                            builder.setPositiveButton(R.string.confirm,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            refreshNow();
                                            dialog.dismiss();
                                        }
                                    });
                            builder.setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();
                        } else if (status == PreCheckStatus.SHOULD_VERIFY) {
                            // 需认证
                            CustomDialog.Builder builder = new CustomDialog.Builder(JobDetailActivity.this);
                            builder.setMessage(R.string.job_refresh_should_verify_msg);
                            builder.setTitle(R.string.prompt);

                            builder.setPositiveButton(R.string.to_verify,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            CertVo vo = new CertVo();
                                            com.parttime.base.IntentManager.intentToBeforeCertedActivity(JobDetailActivity.this, vo);
                                            dialog.dismiss();
                                        }
                                    });
                            builder.setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();

                        } else if (status == PreCheckStatus.VERIFYING) {
                            showToast(R.string.verifying);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failed(Object obj) {
                showWait(false);
                new ErrorHandler(JobDetailActivity.this, obj).showToast();
            }
        });
    }

    private void jobExpedited() {
        showWait(true);
        new PublishRequest().preUrgent(partJob.id, queue, new DefaultCallback() {
            @Override
            public void success(Object obj) {
                showWait(false);
                try {
                    if (obj instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) obj;
                        PreCheckStatus status = PreCheckStatus.parse(jsonObject.getInt("status"));

                        if (status == PreCheckStatus.FREE) {
                            // 可以免费加急一次
                            CustomDialog.Builder builder = new CustomDialog.Builder(JobDetailActivity.this);
                            builder.setMessage(R.string.job_expedited_free_msg);
                            builder.setTitle(R.string.prompt);

                            builder.setPositiveButton(R.string.confirm,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            expeditedNow();
                                            dialog.dismiss();
                                        }
                                    });
                            builder.setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();

                        } else if (status == PreCheckStatus.LACK_OF_MONEY) {

                            // 当前免费加急次数用完,余额不足
                            CustomDialog.Builder builder = new CustomDialog.Builder(JobDetailActivity.this);
                            builder.setMessage(R.string.job_expedited_lack_of_money_msg);
                            builder.setTitle(R.string.prompt);

                            builder.setPositiveButton(R.string.confirm,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            IntentManager.intentToRecharge(JobDetailActivity.this);
                                            dialog.dismiss();
                                        }
                                    });
                            builder.setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();

                        } else if (status == PreCheckStatus.SHOULD_PAY) {
                            // 付费急次
                            int money = jsonObject.getInt("money");
                            CustomDialog.Builder builder = new CustomDialog.Builder(JobDetailActivity.this);
                            builder.setMessage(getString(R.string.job_expedited_pay_msg, money));
                            builder.setTitle(R.string.prompt);

                            builder.setPositiveButton(R.string.confirm,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            expeditedNow();
                                            dialog.dismiss();
                                        }
                                    });
                            builder.setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();
                        } else if (status == PreCheckStatus.SHOULD_VERIFY) {
                            // 需认证
                            CustomDialog.Builder builder = new CustomDialog.Builder(JobDetailActivity.this);
                            builder.setMessage(R.string.job_expedited_should_verify_msg);
                            builder.setTitle(R.string.prompt);

                            builder.setPositiveButton(R.string.to_verify,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            CertVo vo = new CertVo();
                                            com.parttime.base.IntentManager.intentToBeforeCertedActivity(JobDetailActivity.this, vo);
                                            dialog.dismiss();
                                        }
                                    });
                            builder.setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();

                        } else if (status == PreCheckStatus.VERIFYING) {
                            showToast(R.string.verifying);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failed(Object obj) {
                showWait(false);
                new ErrorHandler(JobDetailActivity.this, obj).showToast();
            }
        });

    }

    private void jobModify() {
        IntentManager.intentToEditJob(this, REQUEST_MODIFY, partJob);
    }

    private void jobShelves() {
        CustomDialog.Builder builder = new CustomDialog.Builder(JobDetailActivity.this);
        builder.setMessage(R.string.job_shelve_msg);
        builder.setTitle(R.string.prompt);

        builder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showWait(true);
                        new PublishRequest().shelve(partJob.id, queue, new DefaultCallback() {
                            @Override
                            public void success(Object obj) {
                                showWait(false);
                                showToast(R.string.job_shelve_success);
                                bindData();
                            }

                            @Override
                            public void failed(Object obj) {
                                showWait(false);
                                new ErrorHandler(JobDetailActivity.this, obj);
                            }
                        });

                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    /**
     * 重新上架
     */
    public void jobRepublish() {
        CustomDialog.Builder builder = new CustomDialog.Builder(JobDetailActivity.this);
        builder.setMessage(R.string.job_republish_msg);
        builder.setTitle(R.string.prompt);

        builder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showWait(true);
                        new PublishRequest().republish(partJob.id, queue, new DefaultCallback() {
                            @Override
                            public void success(Object obj) {
                                showWait(false);
                                showToast(R.string.job_republish_success);
                                bindData();
                            }

                            @Override
                            public void failed(Object obj) {
                                showWait(false);
                                new ErrorHandler(JobDetailActivity.this, obj);
                            }
                        });

                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    /**
     * 加急付费活动时弹框
     */

    public void showFeeExpeditedAlertDialog(String message, final String title,
                                            final String positive, final String negative, String money,
                                            final String flag) {

        CustomDialogThree.Builder builder = new CustomDialogThree.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setMoney("(帐号余额:" + money + "元)");
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 2是立即充值,其它是加急
                if ("2".equals(flag)) {
                    Intent intent = new Intent();
                    intent.setClass(JobDetailActivity.this,
                            RechargeActivity.class);
                    startActivity(intent);
                } else {
                    expeditedNow();
                }
            }
        });

        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    /**
     * 刷新兼职信息
     */
    private void refreshNow() {
        showWait(true);
        new PublishRequest().refresh(partJob.id, queue, new DefaultCallback() {
            @Override
            public void success(Object obj) {
                showWait(false);
                showToast(R.string.job_refresh_success);
                bindData();
            }

            @Override
            public void failed(Object obj) {
                showWait(false);
                new ErrorHandler(JobDetailActivity.this, obj);
            }
        });
    }

    /**
     * 进入加急流程
     */
    private void expeditedNow() {
        Intent intent = new Intent(this, JobExpeditedActivity.class);
        intent.putExtra(JobExpeditedActivity.EXTRA_JOB_ID, partJob.id);
        startActivity(intent);
    }


    class ShareDialogActionListener implements PlatformActionListener, Handler.Callback {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
            Message msg = new Message();
            msg.arg1 = 1;
            msg.arg2 = i;
            msg.obj = platform;
            UIHandler.sendMessage(msg, this);
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Message msg = new Message();
            msg.what = 1;
            UIHandler.sendMessage(msg, this);
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Message msg = new Message();
            msg.what = 0;
            UIHandler.sendMessage(msg, this);
        }

        @Override
        public boolean handleMessage(Message message) {
            switch (message.arg1) {
                case 1: {
                    // 成功
                    System.out.println("分享回调成功------------");
                }
                break;
                case 2: {
                    // 失败
                }
                break;
                case 3: {
                    // 取消
                }
                break;
            }
            if (sharePopupWindow != null && sharePopupWindow.isShowing()) {
                sharePopupWindow.dismiss();
            }
            return false;
        }
    }
}
