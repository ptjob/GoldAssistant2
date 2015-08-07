package com.parttime.publish;

import android.content.Context;

import com.parttime.pojo.SalaryUnit;
import com.qingmu.jianzhidaren.R;

/**
 * Created by wyw on 2015/8/3.
 */
public class LabelUtils {

    /**
     * 获取薪酬标签，如XXX元/天，面议等
     */
    public static String getSalaryLabel(Context context, SalaryUnit unit, int salary) {
        if (unit == SalaryUnit.FACE_TO_FACE) {
            return context.getString(R.string.publish_job_salary_unit_face_to_face);
        }
        return salary + " " + getSalaryUnit(context, unit);
    }

    /**
     * 获取薪酬单位（面议返回单位是"")
     * @param context
     * @param unit
     * @return
     */
    public static String getSalaryUnit(Context context, SalaryUnit unit) {
        String salaryUnit = "";
        if (unit != null) {
            switch (unit) {
                case DAY:
                    salaryUnit = context.getString(R.string.publish_job_salary_unit_day);
                    break;
                case HOUR:
                    salaryUnit = context.getString(R.string.publish_job_salary_unit_hour);
                    break;
                case MONTH:
                    salaryUnit = context.getString(R.string.publish_job_salary_unit_month);
                    break;
                case TIMES:
                    salaryUnit = context.getString(R.string.publish_job_salary_unit_times);
                    break;
                case CASES:
                    salaryUnit = context.getString(R.string.publish_job_salary_unit_cases);
                    break;
            }
        }
        return salaryUnit;
    }
}
