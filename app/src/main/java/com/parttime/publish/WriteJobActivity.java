package com.parttime.publish;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.parttime.common.activity.ChooseListActivity;
import com.parttime.common.head.ActivityHead;
import com.parttime.net.DefaultCallback;
import com.parttime.net.ErrorHandler;
import com.parttime.net.PublishRequest;
import com.parttime.pojo.PartJob;
import com.parttime.pojo.SalaryUnit;
import com.parttime.utils.ActionUtils;
import com.parttime.utils.ApplicationUtils;
import com.parttime.utils.CheckUtils;
import com.parttime.utils.FormatUtils;
import com.parttime.utils.IntentManager;
import com.parttime.utils.TimeUtils;
import com.qingmu.jianzhidaren.R;
import com.quark.db.CityService;
import com.quark.jianzhidaren.BaseActivity;
import com.quark.ui.widget.ActionSheet;
import com.quark.utils.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 发布兼职页
 */
public class WriteJobActivity extends BaseActivity implements
        View.OnClickListener, DialogInterface.OnCancelListener,
        ActionSheet.OnActionSheetSelected {

    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_PART_JOB = "part_job";
    private static final int REQUEST_TXT_SELECT = 0x1001;

    private String type;
    private PartJob partJob;

    private ActivityHead activityHead;
    private TextView mTxtMoreRequire;
    private TextView mTxtBeginTime, mTxtEndTime, mTxtPayType, mTxtWorkArea;
    private TextView mTxtSalaryUnit, mTxtLanguage, mTxtHeight, mTxtMeasurements;
    private TextView mTxtWorkRequireTip, mTxtSalaryUnitTip;
    private EditText mEditJobTitle, mEditWorkAddress, mEditSalary;
    private EditText mEditHeadSum, mEditMaleNum, mEditFemaleNum;
    private EditText mEditWorkRequire;
    private LinearLayout mLLHeadSumContainer, mLLSexContainer, mLLMoreRequireContainer;
    private RadioButton mRadioSexUnlimited, mRadioSexLimited;
    private RadioButton  mRadioShowTel;
    private RadioButton mRadioNeedHealthProve, mRadioUnNeedHealthProve;
    private Button mBtnPublish;

    private TextView mTxtSelectTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_job);
        initIntent();
        initControls();
        bindListener();
        bindData();
        refreshSalaryControls();
    }

    private void bindData() {
        if (partJob != null) {
            activityHead.setCenterTxt1(partJob.type);
            mEditJobTitle.setText(partJob.title);
            mTxtBeginTime.setText(partJob.beginTime);
            mTxtEndTime.setText(partJob.endTime);
            mTxtPayType.setText(partJob.payType);
            mTxtWorkArea.setText(partJob.area);
            mEditWorkAddress.setText(partJob.address);

            String[] salaryUnitArray = getResources().getStringArray(R.array.salary_unit);
            mTxtSalaryUnit.setText(salaryUnitArray[partJob.salaryUnit.ordinal()]);
            refreshSalaryControls();
            if (partJob.salaryUnit != SalaryUnit.FACE_TO_FACE) {
                mEditSalary.setText("" + partJob.salary);
            }

            if (partJob.apartSex) {
                mRadioSexLimited.setChecked(true);
                mEditMaleNum.setText("" + partJob.maleNum);
                mEditFemaleNum.setText("" + partJob.femaleNum);
            } else {
                mRadioSexUnlimited.setChecked(true);
                mEditHeadSum.setText("" + partJob.headSum);
            }

            mEditWorkRequire.setText(partJob.workRequire);

            if (partJob.isHasMoreRequire()) {
                // 展开更多
                toggleMoreRequire();
                if (partJob.healthProve != null) {
                    if (partJob.healthProve) {
                        mRadioNeedHealthProve.setChecked(true);
                    } else {
                        mRadioUnNeedHealthProve.setChecked(true);
                    }
                }

                mTxtLanguage.setText(partJob.language);

                if (partJob.height != null) {
                    mTxtHeight.setText("" + partJob.height);
                }

                if (partJob.isHasMeasurements()) {
                    mTxtMeasurements.setText(getString(
                                    R.string.publish_job_measurements_format,
                                    "" + partJob.bust, "" + partJob.beltline, "" + partJob.hipline)
                    );
                }
            }

            mBtnPublish.setText(R.string.modify);
        }
    }

    private void bindListener() {
        activityHead.setRightTxtOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyForm()) {
                    preview();
                }
            }
        });

        mTxtBeginTime.setOnClickListener(this);
        mTxtEndTime.setOnClickListener(this);
        mTxtPayType.setOnClickListener(this);
        mTxtWorkArea.setOnClickListener(this);
        mTxtSalaryUnit.setOnClickListener(this);
        mTxtLanguage.setOnClickListener(this);
        mTxtHeight.setOnClickListener(this);
        mTxtMeasurements.setOnClickListener(this);

        mTxtMoreRequire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMoreRequire();
            }
        });

        mEditWorkRequire.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 改变工作要求右下角的字数提示
                mTxtWorkRequireTip.setText(getString(R.string.publish_job_tip_work_require, editable.length()));
            }
        });

        mRadioSexUnlimited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                toggleSex();
            }
        });

        mRadioSexLimited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        mBtnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyForm()) {
                    if (partJob == null) {
                        publish();
                    } else {
                        modify();
                    }
                }
            }
        });
    }


    private void initIntent() {
        type = getIntent().getStringExtra(EXTRA_TYPE);
        if (getIntent().hasExtra(EXTRA_PART_JOB)) {
            partJob = (PartJob) getIntent().getSerializableExtra(EXTRA_PART_JOB);
        }
    }

    private void initControls() {
        activityHead = new ActivityHead(this);
        activityHead.setRightTxt(R.string.preview);
        activityHead.setCenterTxt1(type);

        mTxtMoreRequire = (TextView) findViewById(R.id.txt_more_require);

        mTxtBeginTime = (TextView) findViewById(R.id.txt_begin_time);
        mTxtEndTime = (TextView) findViewById(R.id.txt_end_time);
        mTxtPayType = (TextView) findViewById(R.id.txt_pay_type);
        mTxtWorkArea = (TextView) findViewById(R.id.txt_work_area);
        mTxtSalaryUnit = (TextView) findViewById(R.id.txt_salary_unit);
        mTxtLanguage = (TextView) findViewById(R.id.txt_language);
        mTxtHeight = (TextView) findViewById(R.id.txt_height);
        mTxtHeight.setFocusable(true);
        mTxtHeight.setFocusableInTouchMode(true);

        mTxtMeasurements = (TextView) findViewById(R.id.txt_measurements);
        mTxtWorkRequireTip = (TextView) findViewById(R.id.edittxt_work_require_tip);
        mTxtSalaryUnitTip = (TextView) findViewById(R.id.txt_salary_unit_tip);

        mEditJobTitle = (EditText) findViewById(R.id.edittxt_job_title);
        mEditWorkAddress = (EditText) findViewById(R.id.edittxt_work_address);
        mEditSalary = (EditText) findViewById(R.id.edittxt_salary);
        mEditHeadSum = (EditText) findViewById(R.id.edittxt_head_sum);
        mEditMaleNum = (EditText) findViewById(R.id.edittxt_male_num);
        mEditFemaleNum = (EditText) findViewById(R.id.edittxt_female_num);
        mEditWorkRequire = (EditText) findViewById(R.id.edittxt_work_require_content);

        mLLHeadSumContainer = (LinearLayout) findViewById(R.id.ll_head_sum_container);
        mLLSexContainer = (LinearLayout) findViewById(R.id.ll_sex_container);
        mLLMoreRequireContainer = (LinearLayout) findViewById(R.id.ll_more_require_container);

        mRadioSexUnlimited = (RadioButton) findViewById(R.id.radio_sex_unlimited);
        mRadioSexLimited = (RadioButton) findViewById(R.id.radio_sex_limited);
        mRadioShowTel = (RadioButton) findViewById(R.id.radio_show_tel);
        mRadioNeedHealthProve = (RadioButton) findViewById(R.id.radio_need_health_prove);
        mRadioUnNeedHealthProve = (RadioButton) findViewById(R.id.radio_unneed_health_prove);

        mBtnPublish = (Button) findViewById(R.id.btn_publish);
    }


    @Override
    public void onClick(View view) {
        View currentFocus = getCurrentFocus();
        if(currentFocus != null) {
            currentFocus.clearFocus();
        }

        switch (view.getId()) {
            case R.id.txt_begin_time:
                String beginTime = mTxtBeginTime.getText().toString();
                Calendar calendarBeginTime = Calendar.getInstance();
                if (!CheckUtils.isEmpty(beginTime)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.DATE_FORMAT_YMD);
                    try {
                        calendarBeginTime.setTime(simpleDateFormat.parse(beginTime));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                clearDay(calendarBeginTime);

                Calendar current = Calendar.getInstance();
                clearDay(current);

                if (CheckUtils.isEmpty(mTxtEndTime.getText().toString())) {
                    // 跟今天做比较
                    ActionUtils.selectDate(this, mTxtBeginTime, calendarBeginTime, getString(R.string.publish_job_label_begin_time), current, getString(R.string.publish_job_begin_time_warn));
                } else {
                    // 跟今天和 结束时间做比较
                    String endTime = mTxtEndTime.getText().toString();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.DATE_FORMAT_YMD);
                    try {
                        Date endTimeDate = simpleDateFormat.parse(endTime);
                        Calendar endTimeCalendar = Calendar.getInstance();
                        clearDay(endTimeCalendar);
                        Logger.i("calenrBeginTime=" + calendarBeginTime.toString() + "; endTimeCalendar=" + endTimeCalendar);
                        endTimeCalendar.setTime(endTimeDate);
                        ActionUtils.selectDate(this, mTxtBeginTime, calendarBeginTime, getString(R.string.publish_job_label_begin_time), current, getString(R.string.publish_job_begin_time_warn), endTimeCalendar, getString(R.string.publish_job_begin_time_warn));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.txt_end_time:
                String endTime = mTxtEndTime.getText().toString();
                Calendar calendarEndTime = Calendar.getInstance();
                calendarEndTime.add(Calendar.DATE, 1);

                if (!CheckUtils.isEmpty(endTime)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.DATE_FORMAT_YMD);
                    try {
                        calendarEndTime.setTime(simpleDateFormat.parse(endTime));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                clearDay(calendarEndTime);

                current = Calendar.getInstance();
                clearDay(current);

                if (CheckUtils.isEmpty(mTxtBeginTime.getText().toString())) {
                    // 跟今天做比较
                    ActionUtils.selectDate(this, mTxtEndTime, calendarEndTime, getString(R.string.publish_job_label_end_time), current, getString(R.string.publish_job_end_time_warn));
                } else {
                    // 跟今天结束时间做比较
                    beginTime = mTxtBeginTime.getText().toString();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeUtils.DATE_FORMAT_YMD);
                    try {
                        Date beginTimeDate = simpleDateFormat.parse(beginTime);
                        Calendar beginTimeCalendar = Calendar.getInstance();
                        clearDay(beginTimeCalendar);
                        beginTimeCalendar.setTime(beginTimeDate);
                        ActionUtils.selectDate(this, mTxtEndTime, calendarEndTime, getString(R.string.publish_job_label_end_time), beginTimeCalendar, getString(R.string.publish_job_end_time_warn));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.txt_pay_type:
                mTxtSelectTemp = mTxtPayType;
                String[] payTypeArray = getResources().getStringArray(R.array.pay_type);
                IntentManager.openChoooseListActivity(WriteJobActivity.this,
                        getString(R.string.publish_job_label_pay_type),
                        payTypeArray, REQUEST_TXT_SELECT);
                break;
            case R.id.txt_work_area:
                mTxtSelectTemp = mTxtWorkArea;

                ArrayList<String> citys = CityService.getSubCitys(this, ApplicationUtils.getCity());
                String[] workAreaArray = new String[citys.size()];
                for (int i = 0; i < citys.size(); i++) {
                    workAreaArray[i] = citys.get(i);
                }
                IntentManager.openChoooseListActivity(WriteJobActivity.this,
                        getString(R.string.publish_job_label_work_area),
                        workAreaArray, REQUEST_TXT_SELECT);
                break;
            case R.id.txt_salary_unit:
                mTxtSelectTemp = mTxtSalaryUnit;
                String[] salaryUnitArray = getResources().getStringArray(R.array.salary_unit);
                IntentManager.openChoooseListActivity(WriteJobActivity.this,
                        getString(R.string.publish_job_label_salary_unit),
                        salaryUnitArray, REQUEST_TXT_SELECT);
                break;
            case R.id.txt_language:
                ActionSheet.showSheetLanguage(this, this, this,
                        mTxtLanguage);
                break;
            case R.id.txt_height:
                mTxtSelectTemp = mTxtHeight;
                String[] heightArray = getResources().getStringArray(R.array.height);
                IntentManager.openChoooseListActivity(WriteJobActivity.this,
                        getString(R.string.publish_job_label_height),
                        heightArray, REQUEST_TXT_SELECT);
                break;
            case R.id.txt_measurements:
                ActionUtils.selectMeasurements(this, mTxtMeasurements);
                break;
            default:
                break;
        }
    }

    private void clearDay(Calendar current) {
        TimeUtils.clearDay(current);
    }

    @Override
    public void setBackButton() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TXT_SELECT:
                    if (data != null) {
                        String result = data.getStringExtra(ChooseListActivity.EXTRA_RESULT);
                        if (mTxtSelectTemp != null) {
                            mTxtSelectTemp.setText(result);
                            if (mTxtSelectTemp == mTxtSalaryUnit) {
                                refreshSalaryControls();
                            }

                            mTxtSelectTemp.requestFocusFromTouch();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 刷新薪酬单位关系
     */
    private void refreshSalaryControls() {
        SalaryUnit salaryUnit = SalaryUnit.parse(mTxtSalaryUnit.getText().toString());
        if (salaryUnit == SalaryUnit.FACE_TO_FACE) {
            mEditSalary.setText(R.string.publish_job_salary_unit_face_to_face);
            mEditSalary.setEnabled(false);
        } else {
            if (mEditSalary.getText().toString().equals(getString(R.string.publish_job_salary_unit_face_to_face))) {
                mEditSalary.setText("");
            }
            mEditSalary.setEnabled(true);
        }

        mTxtSalaryUnitTip.setText(LabelUtils.getSalaryUnit(this, salaryUnit));
    }

    /**
     * 切换是否指定性别
     */
    private void toggleSex() {
        if (mRadioSexUnlimited.isChecked()) {
            mLLHeadSumContainer.setVisibility(View.VISIBLE);
            mLLSexContainer.setVisibility(View.GONE);
        } else if (mRadioSexLimited.isChecked()) {
            mLLHeadSumContainer.setVisibility(View.GONE);
            mLLSexContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 切换更多要求
     */
    private void toggleMoreRequire() {
        if (mLLMoreRequireContainer.getVisibility() == View.GONE) {
            mLLMoreRequireContainer.setVisibility(View.VISIBLE);
            Drawable drawable = getResources().getDrawable(
                    R.drawable.other_btn_off);
            mTxtMoreRequire.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

        } else {
            mLLMoreRequireContainer.setVisibility(View.GONE);
            Drawable drawable = getResources().getDrawable(
                    R.drawable.other_btn_on);
            mTxtMoreRequire.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        }
    }


    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }

    @Override
    public void onClick(int whichButton) {

    }

    private boolean verifyForm() {
        String verifyStr;
        verifyStr = mEditJobTitle.getText().toString();
        if (CheckUtils.isEmpty(verifyStr)) {
            showToast(getString(R.string.publish_job_form_uninput_warn_format, getString(R.string.publish_job_label_job_title)));
            return false;
        }

        char[] titleChars = verifyStr.toCharArray();
        boolean isDigit = true;
        for (char titleChar : titleChars) {
            if (!Character.isDigit(titleChar)) {
                isDigit = false;
                break;
            }
        }

        if (isDigit) {
            showToast(R.string.publish_job_title_digit_warn);
            return false;
        }

        verifyStr = mTxtBeginTime.getText().toString();
        if (CheckUtils.isEmpty(verifyStr)) {
            showToast(getString(R.string.publish_job_form_uninput_warn_format, getString(R.string.publish_job_label_begin_time)));
            return false;
        }

        verifyStr = mTxtEndTime.getText().toString();
        if (CheckUtils.isEmpty(verifyStr)) {
            showToast(getString(R.string.publish_job_form_uninput_warn_format, getString(R.string.publish_job_label_end_time)));
            return false;
        }

        verifyStr = mTxtPayType.getText().toString();
        if (CheckUtils.isEmpty(verifyStr)) {
            showToast(getString(R.string.publish_job_form_uninput_warn_format, getString(R.string.publish_job_label_pay_type)));
            return false;
        }

        verifyStr = mTxtWorkArea.getText().toString();
        if (CheckUtils.isEmpty(verifyStr)) {
            showToast(getString(R.string.publish_job_form_uninput_warn_format, getString(R.string.publish_job_label_work_area)));
            return false;
        }

        verifyStr = mEditWorkAddress.getText().toString();
        if (CheckUtils.isEmpty(verifyStr) ||
                verifyStr.length() < getResources().getInteger(R.integer.work_address_min_length)) {
            showToast(R.string.publish_job_hint_work_address);
            return false;
        }

        verifyStr = mEditSalary.getText().toString();
        if (CheckUtils.isEmpty(verifyStr)) {
            showToast(getString(R.string.publish_job_form_uninput_warn_format, getString(R.string.publish_job_label_salary)));
            return false;
        } else {
            if (SalaryUnit.parse(mTxtSalaryUnit.getText().toString()) != SalaryUnit.FACE_TO_FACE) {
                try {
                    Integer.parseInt(verifyStr);
                } catch (NumberFormatException e) {
                    showToast(getString(R.string.publish_job_form_salary_format_error));
                    return false;
                }
            }
        }

        verifyStr = mTxtSalaryUnit.getText().toString();
        if (CheckUtils.isEmpty(verifyStr)) {
            showToast(getString(R.string.publish_job_form_uninput_warn_format, getString(R.string.publish_job_label_salary_unit)));
            return false;
        }

        int sum;
        if (mRadioSexLimited.isChecked()) {
            String maleNumStr = mEditMaleNum.getText().toString();
            sum = CheckUtils.isEmpty(maleNumStr) ? 0 : FormatUtils.parseToInt(maleNumStr);

            String femaleNumStr = mEditFemaleNum.getText().toString();
            sum += CheckUtils.isEmpty(femaleNumStr) ? 0 : FormatUtils.parseToInt(femaleNumStr);
        } else {
            String headSumStr = mEditHeadSum.getText().toString();
            sum = CheckUtils.isEmpty(headSumStr) ? 0 : FormatUtils.parseToInt(headSumStr);
        }

        // 招人人数为0
        if (sum == 0) {
            showToast(R.string.publish_job_hint_head_sum_zero);
            return false;
        }

        verifyStr = mEditWorkRequire.getText().toString();
        if (CheckUtils.isEmpty(verifyStr) ||
                verifyStr.length() < getResources().getInteger(R.integer.work_require_min_length)) {
            showToast(R.string.publish_job_hint_work_require);
            return false;
        }

        return true;
    }

    private void publish() {
        PartJob partJob = buildPartJob();

        showWait(true);
        new PublishRequest().publish(partJob, queue, new DefaultCallback() {
            @Override
            public void success(Object obj) {
                showWait(false);
                showToast(R.string.publish_job_success);
                IntentManager.goToJobManage(WriteJobActivity.this);
            }

            @Override
            public void failed(Object obj) {
                showWait(false);
                new ErrorHandler(WriteJobActivity.this, obj);
            }
        });
    }

    private void modify() {
        PartJob partJob = buildPartJob();

        showWait(true);
        new PublishRequest().modify(partJob, queue, new DefaultCallback() {
            @Override
            public void success(Object obj) {
                showWait(false);
                showToast(R.string.modify_job_success);
                setResult(RESULT_OK, getIntent());
                finish();
            }

            @Override
            public void failed(Object obj) {
                showWait(false);
                new ErrorHandler(WriteJobActivity.this, obj);
            }
        });
    }

    private void preview() {
        PartJob partJob = buildPartJob();

        IntentManager.openJobDetailActivity(this, partJob);
    }

    private PartJob buildPartJob() {
        PartJob partJob = new PartJob();
        if (this.partJob != null) {
            partJob.id = this.partJob.id;
        }
        partJob.companyId = ApplicationUtils.getLoginId();
        partJob.companyName = ApplicationUtils.getLoginName();
        partJob.type = activityHead.centerTxt1.getText().toString();
        partJob.title = FormatUtils.formatStr(mEditJobTitle.getText().toString());
        partJob.beginTime = FormatUtils.formatStr(mTxtBeginTime.getText().toString());
        partJob.endTime = FormatUtils.formatStr(mTxtEndTime.getText().toString());
        partJob.payType = FormatUtils.formatStr(mTxtPayType.getText().toString());
        partJob.city = ApplicationUtils.getCity();
        partJob.area = FormatUtils.formatStr(mTxtWorkArea.getText().toString());
        partJob.address = FormatUtils.formatStr(mEditWorkAddress.getText().toString());
        partJob.salaryUnit = SalaryUnit.parse(FormatUtils.formatStr(mTxtSalaryUnit.getText().toString()));
        if (partJob.salaryUnit != SalaryUnit.FACE_TO_FACE) {
            partJob.salary = FormatUtils.parseToInt(mEditSalary.getText().toString());
        }
        partJob.apartSex = mRadioSexLimited.isChecked();
        if (partJob.apartSex) {
            String maleNumStr = mEditMaleNum.getText().toString();
            partJob.maleNum = CheckUtils.isEmpty(maleNumStr) ? 0 : FormatUtils.parseToInt(maleNumStr);

            String femaleNumStr = mEditFemaleNum.getText().toString();
            partJob.femaleNum = CheckUtils.isEmpty(femaleNumStr) ? 0 : FormatUtils.parseToInt(femaleNumStr);
        } else {
            String headSumStr = mEditHeadSum.getText().toString();
            partJob.headSum = CheckUtils.isEmpty(headSumStr) ? 0 : FormatUtils.parseToInt(headSumStr);
        }

        partJob.workRequire = FormatUtils.formatStr(mEditWorkRequire.getText().toString());
        partJob.isShowTel = mRadioShowTel.isChecked();

        String heightTxt = mTxtHeight.getText().toString();
        if (!CheckUtils.isEmpty(heightTxt)) {
            // 设置了身高
            partJob.height = Integer.valueOf(heightTxt);
        }

        String measurementsTxt = mTxtMeasurements.getText().toString();
        if (!CheckUtils.isEmpty(measurementsTxt)) {
            // 设置了三围
            String[] measurementsArray = measurementsTxt.split("、");
            partJob.bust = Integer.valueOf(measurementsArray[0].substring(2));
            partJob.beltline = Integer.valueOf(measurementsArray[1].substring(2));
            partJob.hipline = Integer.valueOf(measurementsArray[2].substring(2));
        }

        if (mRadioNeedHealthProve.isChecked() || mRadioUnNeedHealthProve.isChecked()) {
            // 选择了是否要健康证
            partJob.healthProve = mRadioNeedHealthProve.isChecked();
        }

        String language = mTxtLanguage.getText().toString();
        if (!CheckUtils.isEmpty(language)) {
            // 设置了擅长的语言
            partJob.language = language;
        }
        return partJob;
    }
}