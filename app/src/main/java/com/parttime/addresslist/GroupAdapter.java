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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.carson.constant.ConstantForSaveList;
import com.easemob.chat.EMGroup;
import com.parttime.pojo.GroupDescription;
import com.qingmu.jianzhidaren.R;

import java.util.List;
import java.util.Map;

public class GroupAdapter extends ArrayAdapter<EMGroup> {

	private LayoutInflater inflater;

	public GroupAdapter(Context context, int res) {
		super(context, res);
		this.inflater = LayoutInflater.from(context);
	}

    public void updateData(List<EMGroup> gs){
        if(gs != null){
            clear();
            addAll(gs);
        }
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_group, null);
            holder = new ViewHolder();
            holder.name = ((TextView) convertView.findViewById(R.id.name));
            holder.tag = ((TextView) convertView.findViewById(R.id.avatar_tag));
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        EMGroup group = getItem(position);

        holder.name.setText(group.getGroupName());
        holder.tag.setVisibility(View.GONE);

        Map<String,GroupDescription> cache = ConstantForSaveList.groupDescriptionMapCache;
        if(cache != null && cache.get(group.getGroupId()) != null){
            GroupDescription groupDescription = cache.get(group.getGroupId());
            int type = groupDescription.type;
            if(type == GroupDescription.ACTIVITY_GROUP){
                holder.tag.setText(R.string.activity_group);
                holder.tag.setBackgroundResource(R.color.c_FF5C56);
                holder.tag.setVisibility(View.VISIBLE);
            }
        }

		return convertView;
	}

    class ViewHolder{
        TextView name;
        TextView tag;
    }

	@Override
	public int getCount() {
		 return super.getCount();
	}

}