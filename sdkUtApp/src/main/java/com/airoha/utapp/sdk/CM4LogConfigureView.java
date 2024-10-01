/* Copyright Statement:
 *
 * (C) 2020  Airoha Technology Corp. All rights reserved.
 *
 * This software/firmware and related documentation ("Airoha Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to Airoha Technology Corp. ("Airoha") and/or its licensors.
 * Without the prior written permission of Airoha and/or its licensors,
 * any reproduction, modification, use or disclosure of Airoha Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 * You may only use, reproduce, modify, or distribute (as applicable) Airoha Software
 * if you have agreed to and been bound by the applicable license agreement with
 * Airoha ("License Agreement") and been granted explicit permission to do so within
 * the License Agreement ("Permitted User").  If you are not a Permitted User,
 * please cease any access or use of Airoha Software immediately.
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT AIROHA SOFTWARE RECEIVED FROM AIROHA AND/OR ITS REPRESENTATIVES
 * ARE PROVIDED TO RECEIVER ON AN "AS-IS" BASIS ONLY. AIROHA EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES AIROHA PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH AIROHA SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN AIROHA SOFTWARE. AIROHA SHALL ALSO NOT BE RESPONSIBLE FOR ANY AIROHA
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND AIROHA'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO AIROHA SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT AIROHA'S OPTION, TO REVISE OR REPLACE AIROHA SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * AIROHA FOR SUCH AIROHA SOFTWARE AT ISSUE.
 */
/* Airoha restricted information */

package com.airoha.utapp.sdk;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.airoha.liblogdump.onlinedump.LoggerList;
import com.airoha.libutils.Converter;

public class CM4LogConfigureView extends LinearLayout {
    private final int CPU_CM4 = 0;
    private final int CPU_DSP0 = 1;

    private LinearLayout cm4LinearLayout;
    private MainActivity mCtx;
    //private LogDumpMgr mAirohaOnlineDumpMgr = null;

    public CM4LogConfigureView(Context context) {
        super(context);

        mCtx = (MainActivity)context;
        LayoutInflater.from(context).inflate(R.layout.fragment_cm4log_configure, this);

        initUIMember();
    }

    private void initUIMember() {
        cm4LinearLayout = (LinearLayout) findViewById(R.id.parent_cm4_layout);
        //LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //final View rowView = inflater.inflate(R.layout.activity_onlinelog_module_item, null);
    }

    public void initLogModuleMember(LoggerList<Byte> info) {
        // [cpu id:1b] [module id:1b] [module name leng:1b] [name....] [switch:1b] [level:1b]

        int index = 0;
        while(info.size() > 0) {
            if(info.size() < 3) {
                break;
            }
            byte cpuid = info.get(0);
            int moduleid = info.get(1) & 0xFF;
            byte module_name_length = info.get(2);

            if((info.size() - 3) < module_name_length) {
                break;
            }
            info.removeRange(0, 3);

            byte[] data = info.subArray(0, module_name_length, info);
            String module_name = Converter.hexToAsciiString(data);
            info.removeRange(0, module_name_length);

            if(info.size() < 1) {
                break;
            }

            byte isSwitch = (byte)((info.get(0) & (byte)0xF0) >> 4);
            byte level = (byte)(info.get(0) & (byte)0x0F);
            info.removeRange(0, 1);

            if(cpuid == CPU_CM4) {
                OnlinelogConfigItemView myView = new OnlinelogConfigItemView(this.getContext(), null, cpuid, module_name, moduleid, isSwitch, level);
                myView.setMgr(mCtx.getAirohaService().getAirohaLogDumpMgr());
                myView.setEnabled(false);
                cm4LinearLayout.addView(myView, cm4LinearLayout.getChildCount());
            }
        }

        //mTitle = (TextView) rowView.findViewById(R.id.episode_list_item_title);
    }

    public void notifyConnected() {
        for (int i = 0; i < cm4LinearLayout.getChildCount(); i++) {
            OnlinelogConfigItemView child = (OnlinelogConfigItemView)cm4LinearLayout.getChildAt(i);
            child.setEnabled(true);
        }
    }

    public void notifyInitialLogModule() {
        for (int i = 0; i < cm4LinearLayout.getChildCount(); i++) {
            OnlinelogConfigItemView child = (OnlinelogConfigItemView)cm4LinearLayout.getChildAt(i);
            child.initLogModule();
        }
    }

    public void notifyDisconnected() {
        for (int i = 0; i < cm4LinearLayout.getChildCount(); i++) {
            OnlinelogConfigItemView child = (OnlinelogConfigItemView)cm4LinearLayout.getChildAt(i);
            child.mSwitchLogOnOff.setChecked(false);
            child.mSpinLogLevel.setEnabled(false);
            child.setEnabled(false);
        }
    }

    public void clearLayout() {
        cm4LinearLayout.removeAllViews();
    }

}
