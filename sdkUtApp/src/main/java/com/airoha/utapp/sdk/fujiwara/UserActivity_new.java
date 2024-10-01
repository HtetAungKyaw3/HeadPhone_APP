// package com.airoha.utapp.sdk.fujiwara;
//
// import static android.content.ContentValues.TAG;
//
// import android.content.Context;
// import android.content.Intent;
// import android.content.IntentFilter;
// import android.content.SharedPreferences;
// import android.os.BatteryManager;
// import android.os.Bundle;
// import android.util.Log;
// import android.view.View;
// import android.widget.CompoundButton;
// import android.widget.ImageButton;
// import android.widget.SeekBar;
// import android.widget.SeekBar.OnSeekBarChangeListener;
// import android.widget.ToggleButton;
//
// import androidx.appcompat.app.AppCompatActivity;
//
// import com.airoha.utapp.sdk.R;
//
// import java.util.Locale;
//
// public class UserActivity extends AppCompatActivity {
//
//     @Override
//     protected void onCreate(Bundle savedInstanceState) {
//         super.onCreate(savedInstanceState);
//         setContentView(R.layout.activity_main);
//
//         // resetボタン Low
//         ImageButton loBt = (ImageButton) findViewById(R.id.imageButton_lo_reset);
//
//         loBt. setOnClickListener(
//                 new View.OnClickListener() {
//                     @Override
//                     public void onClick(View v){
//                         SeekBar reset_seekLo = findViewById(R.id.seekBar_lo);
//                         reset_seekLo.setProgress(60);
//                     }
//                 }
//         );
//
//         // resetボタン Lo-Mid
//         ImageButton lomidBt = (ImageButton) findViewById(R.id.imageButton_loMid_reset);
//
//         lomidBt. setOnClickListener(
//                 new View.OnClickListener() {
//                     @Override
//                     public void onClick(View v){
//                         SeekBar reset_seekLomid = findViewById(R.id.seekBar_loMid);
//                         reset_seekLomid.setProgress(60);
//                     }
//                 }
//         );
//
//         // resetボタン Hi-Mid
//         ImageButton himidBt = (ImageButton) findViewById(R.id.imageButton_hiMid_reset);
//
//         himidBt. setOnClickListener(
//                 new View.OnClickListener() {
//                     @Override
//                     public void onClick(View v){
//                         SeekBar reset_seekHimid = findViewById(R.id.seekBar_hiMid);
//                         reset_seekHimid.setProgress(60);
//                     }
//                 }
//         );
//
//         // resetボタン High
//         ImageButton hiBt = (ImageButton) findViewById(R.id.imageButton_hi_reset);
//
//         hiBt. setOnClickListener(
//                 new View.OnClickListener() {
//                     @Override
//                     public void onClick(View v){
//                         SeekBar reset_seekHi = findViewById(R.id.seekBar_hi);
//                         reset_seekHi.setProgress(60);
//                     }
//                 }
//         );
//
//         ImageButton allReset = (ImageButton) findViewById(R.id.imageButton_allReset);
//
//         allReset. setOnClickListener(
//                 new View.OnClickListener() {
//                     @Override
//                     public void onClick(View view) {
//                         SeekBar reset_seekLo = findViewById(R.id.seekBar_lo);
//                         reset_seekLo.setProgress(60);
//                         SeekBar reset_seekLomid = findViewById(R.id.seekBar_loMid);
//                         reset_seekLomid.setProgress(60);
//                         SeekBar reset_seekHimid = findViewById(R.id.seekBar_hiMid);
//                         reset_seekHimid.setProgress(60);
//                         SeekBar reset_seekHi = findViewById(R.id.seekBar_hi);
//                         reset_seekHi.setProgress(60);
//                     }
//                 }
//         );
//
//
//         // move Factory Mode
//         ImageButton sendButton = findViewById(R.id.imageButton_factory_off);
//         sendButton.setOnClickListener(v -> {
//             Intent intent = new Intent(getApplication(), FactoryActivity.class);
//             startActivity(intent);
//         });
//
//
//         // SeekBar Low
//         SeekBar seekBarLo = findViewById(R.id.seekBar_lo);
//         // 初期値
//         seekBarLo.setProgress(60);
//         // 最大値
//         seekBarLo.setMax(120);
//
//         seekBarLo.setOnSeekBarChangeListener(
//                 new OnSeekBarChangeListener() {
//
//                     //ツマミがドラッグされると呼ばれる
//                     @Override
//                     public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                         String str = String.format(Locale.US, "%d %%", progress);
//
//                     }
//
//                     @Override
//                     public void onStartTrackingTouch(SeekBar seekBar) {
//                         Log.i(TAG, "onStartTrackingTouch: ");
//                     }
//
//                     @Override
//                     public void onStopTrackingTouch(SeekBar seekBar) {
//                         Log.i(TAG, "onStartTrackingTouch: ");
//                     }
//
//                     //ツマミがタッチされた時に呼ばれる
// //                    @Override
// //                    public void onStartTrackingTouch(SeekBar seekBar) {
// //                    }
//
//                     //ツマミがリリースされた時に呼ばれる
// //                    @Override
// //                    public void onStopTrackingTouch(SeekBar seekBar) {
// //                    }
//                 }
//         );
//
//         // SeekBar LoMid
//         SeekBar seekBarLomid = findViewById(R.id.seekBar_loMid);
//         // 初期値
//         seekBarLomid.setProgress(60);
//         // 最大値
//         seekBarLomid.setMax(120);
//
//         seekBarLomid.setOnSeekBarChangeListener(
//                 new OnSeekBarChangeListener() {
//
//                     //ツマミがドラッグされると呼ばれる
//                     @Override
//                     public void onProgressChanged(
//                             SeekBar seekBar, int progress, boolean fromUser) {
//                         String str = String.format(Locale.US, "%d %%", progress);
//
//                     }
//
//                     @Override
//                     public void onStartTrackingTouch(SeekBar seekBar) {
//                         Log.i(TAG, "onStartTrackingTouch: ");
//                     }
//
//                     @Override
//                     public void onStopTrackingTouch(SeekBar seekBar) {
//                         Log.i(TAG, "onStartTrackingTouch: ");
//                     }
//                 }
//         );
//
//         // SeekBar HiMid
//         SeekBar seekBarHimid = findViewById(R.id.seekBar_hiMid);
//         // 初期値
//         seekBarHimid.setProgress(60);
//         // 最大値
//         seekBarHimid.setMax(120);
//
//         seekBarHimid.setOnSeekBarChangeListener(
//                 new OnSeekBarChangeListener() {
//
//                     //ツマミがドラッグされると呼ばれる
//                     @Override
//                     public void onProgressChanged(
//                             SeekBar seekBar, int progress, boolean fromUser) {
//                         String str = String.format(Locale.US, "%d %%", progress);
//
//                     }
//
//                     @Override
//                     public void onStartTrackingTouch(SeekBar seekBar) {
//                         Log.i(TAG, "onStartTrackingTouch: ");
//                     }
//
//                     @Override
//                     public void onStopTrackingTouch(SeekBar seekBar) {
//                         Log.i(TAG, "onStartTrackingTouch: ");
//                     }
//                 }
//         );
//
//         // SeekBar Hi
//         SeekBar seekBarHi = findViewById(R.id.seekBar_hi);
//         // 初期値
//         seekBarHi.setProgress(60);
//         // 最大値
//         seekBarHi.setMax(120);
//
//         seekBarHi.setOnSeekBarChangeListener(
//                 new OnSeekBarChangeListener() {
//
//                     //ツマミがドラッグされると呼ばれる
//                     @Override
//                     public void onProgressChanged(
//                         SeekBar seekBar, int progress, boolean fromUser) {
//                         String str = String.format(Locale.US, "%d %%", progress);
//
//                     }
//
//                     @Override
//                     public void onStartTrackingTouch(SeekBar seekBar) {
//                         Log.i(TAG, "onStartTrackingTouch: ");
//                     }
//
//                     @Override
//                     public void onStopTrackingTouch(SeekBar seekBar) {
//                         Log.i(TAG, "onStartTrackingTouch: ");
//                     }
//                 }
//         );
//
//         SeekBar db_lo = (SeekBar)findViewById(R.id.seekBar_lo);
//         SeekBar db_loMid = (SeekBar)findViewById(R.id.seekBar_loMid);
//         SeekBar db_hiMid = (SeekBar)findViewById(R.id.seekBar_hiMid);
//         SeekBar db_hi = (SeekBar)findViewById(R.id.seekBar_hi);
//
//         SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//         SharedPreferences.Editor editor = sharedPref.edit();
//
//         ImageButton btn_save = (ImageButton) findViewById(R.id.imageButton_save);
//         btn_save.setOnClickListener(new View.OnClickListener(){
//             @Override
//             public void onClick (View v){
//                 editor.putInt("db_lo",db_lo.getProgress());
//                 editor.putInt("db_loMid", db_loMid.getProgress());
//                 editor.putInt("db_hiMid", db_hiMid.getProgress());
//                 editor.putInt("db_hi", db_hi.getProgress());
//                 editor.apply();
//
//             }
//         });
//
//         ImageButton btn_load = (ImageButton) findViewById(R.id.imageButton_load);
//         btn_load.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View view) {
//
//                 int defaultValue = 60;
//
//                 int saved_db_lo = sharedPref.getInt("db_lo", defaultValue);
//                 int saved_db_loMid = sharedPref.getInt("db_loMid",defaultValue);
//                 int saved_db_hiMid = sharedPref.getInt("db_hiMid",defaultValue);
//                 int saved_db_hi = sharedPref.getInt("db_hi", defaultValue);
//
//                 db_lo.setProgress(saved_db_lo);
//                 db_loMid.setProgress(saved_db_loMid);
//                 db_hiMid.setProgress(saved_db_hiMid);
//                 db_hi.setProgress(saved_db_hi);
//
//             }
//         });
//
//         SharedPreferences sharedPref2 = getPreferences(Context.MODE_PRIVATE);
//         SharedPreferences.Editor editor2 = sharedPref2.edit();
//
// //        ImageButton btn_loBypass = (ImageButton) findViewById(R.id.imageButton_lo_bypass);
// //        btn_loBypass.setOnClickListener(new View.OnClickListener() {
// //            @Override
// //            public void onClick(View view) {
// //                editor2.putInt("db_lo_bypass",db_lo.getProgress());
// //
// //                int defaultValue = 60;
// //                int saved_db_lo = sharedPref2.getInt("db_lo", defaultValue);
// //                db_lo.setProgress(saved_db_lo);
// //
// //            }
// //        });
//
//         ToggleButton toggle_lo = (ToggleButton) findViewById(R.id.toggleButton_lo_bypass);
//         toggle_lo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                 toggle_lo.setOnClickListener(new View.OnClickListener() {
//                     @Override
//                     public void onClick(View v) {
//                         if (isChecked) {
//                             // The toggle is enabled
//                             db_lo.setEnabled(true);
// //                            Log.i(TAG, db_lo.toString());
//
//                             int defaultValue = 60;
//                             int saved_bypass_lo = sharedPref2.getInt("db_lo_bypass", defaultValue);
//                             db_lo.setProgress(saved_bypass_lo);
//
//                         } else {
//                             // The toggle is disabled
//                             editor2.putInt("db_lo_bypass", db_lo.getProgress());
//                             editor2.apply();
//                             Log.i(TAG, editor2.toString());
//                             db_lo.setProgress(60);
//
//                             db_lo.setEnabled(false);
// //                            Log.i(TAG, db_lo.toString());
//
//
//                         }
//
//                     }
//                 });
//             }
//         });
//
// //        ImageButton btn_loMidBypass = (ImageButton) findViewById(R.id.imageButton_loMid_bypass);
// //        btn_loMidBypass.setOnClickListener(new View.OnClickListener() {
// //            @Override
// //            public void onClick(View view) {
// //                editor2.putInt("db_lo_bypass",db_loMid.getProgress());
// //
// //                int defaultValue = 60;
// //                int saved_db_loMid = sharedPref2.getInt("db_loMid", defaultValue);
// //                db_loMid.setProgress(saved_db_loMid);
// //
// //            }
// //        });
//
//         ToggleButton toggle_loMid = (ToggleButton) findViewById(R.id.toggleButton_loMid_bypass);
//         toggle_loMid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                 toggle_loMid.setOnClickListener(new View.OnClickListener() {
//                     @Override
//                     public void onClick(View v) {
//                         if (isChecked) {
//                             // The toggle is enabled
//                             db_loMid.setEnabled(true);
//
//                             int defaultValue = 60;
//                             int saved_bypass_loMid = sharedPref2.getInt("db_loMid_bypass", defaultValue);
//                             db_loMid.setProgress(saved_bypass_loMid);
//
//                         } else {
//                             // The toggle is disabled
//                             editor2.putInt("db_loMid_bypass", db_loMid.getProgress());
//                             editor2.apply();
//                             Log.i(TAG, editor2.toString());
//                             db_loMid.setProgress(60);
//
//                             db_loMid.setEnabled(false);
//
//                         }
//
//                     }
//                 });
//             }
//         });
//
//
// //        ImageButton btn_hiMidBypass = (ImageButton) findViewById(R.id.imageButton_hiMid_bypass);
// //        btn_hiMidBypass.setOnClickListener(new View.OnClickListener() {
// //            @Override
// //            public void onClick(View view) {
// //                editor2.putInt("db_hiMid_bypass",db_hiMid.getProgress());
// //
// //                int defaultValue = 60;
// //                int saved_db_hiMid = sharedPref.getInt("db_hiMid", defaultValue);
// //                db_hiMid.setProgress(saved_db_hiMid);
// //
// //            }
// //        });
//
//         ToggleButton toggle_hiMid = (ToggleButton) findViewById(R.id.toggleButton_hiMid_bypass);
//         toggle_hiMid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                 toggle_hiMid.setOnClickListener(new View.OnClickListener() {
//                     @Override
//                     public void onClick(View v) {
//                         if (isChecked) {
//                             // The toggle is enabled
//                             db_hiMid.setEnabled(true);
//
//                             int defaultValue = 60;
//                             int saved_bypass_hiMid = sharedPref2.getInt("db_hiMid_bypass", defaultValue);
//                             db_hiMid.setProgress(saved_bypass_hiMid);
//
//                         } else {
//                             // The toggle is disabled
//                             editor2.putInt("db_hiMid_bypass", db_hiMid.getProgress());
//                             editor2.apply();
//                             Log.i(TAG, editor2.toString());
//                             db_hiMid.setProgress(60);
//
//                             db_hiMid.setEnabled(false);
//
//                         }
//
//                     }
//                 });
//             }
//         });
//
// //        ImageButton btn_hiBypass = (ImageButton) findViewById(R.id.imageButton_hi_bypass);
// //        btn_hiBypass.setOnClickListener(new View.OnClickListener() {
// //            @Override
// //            public void onClick(View view) {
// //                editor.putInt("db_hi",db_hi.getProgress());
// //
// //                int defaultValue = 60;
// //                int saved_db_hi = sharedPref.getInt("db_hi", defaultValue);
// //                db_hi.setProgress(saved_db_hi);
// //
// //            }
// //        });
//
//         ToggleButton toggle_hi = (ToggleButton) findViewById(R.id.toggleButton_hi_bypass);
//         toggle_hi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                 toggle_hi.setOnClickListener(new View.OnClickListener() {
//                     @Override
//                     public void onClick(View v) {
//                         if (isChecked) {
//                             // The toggle is enabled
//                             db_hi.setEnabled(true);
//
//                             int defaultValue = 60;
//                             int saved_bypass_hi = sharedPref2.getInt("db_hi_bypass", defaultValue);
//                             db_hi.setProgress(saved_bypass_hi);
//
//                         } else {
//                             // The toggle is disabled
//                             editor2.putInt("db_hi_bypass", db_hi.getProgress());
//                             editor2.apply();
//                             Log.i(TAG, editor2.toString());
//                             db_hi.setProgress(60);
//
//                             db_hi.setEnabled(false);
//
//                         }
//
//                     }
//                 });
//             }
//         });
//
// //        private StringBuilder sbuilder = new StringBuilder();
// //        private Intent batteryStatus;
// //        private BatteryManager bManager;
// //
// //        IntentFilter intentfilter;
// //        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
// //        Intent batteryStatus = this.registerReceiver(null, intentfilter );
// //
// //        int batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
// //        Log.d("Battery","Level: " + String.valueOf(batteryLevel));
//     }
// }
