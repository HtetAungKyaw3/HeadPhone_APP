// package com.airoha.utapp.sdk.fujiwara;
//
// import androidx.appcompat.app.AppCompatActivity;
//
// import android.bluetooth.BluetoothGatt;
// import android.bluetooth.BluetoothGattCharacteristic;
// import android.os.Bundle;
// import android.view.View;
// import android.widget.ImageButton;
//
// import com.airoha.utapp.sdk.R;
//
// import java.util.UUID;
//
// public class FactoryActivity extends AppCompatActivity {
//     static BluetoothGatt gattAno = null;
//
//     public static void setGatt(BluetoothGatt gatt) {
//         FactoryActivity.gattAno = gatt;
//     }
//
//     @Override
//     protected void onCreate(Bundle savedInstanceState){
//         super.onCreate(savedInstanceState);
//         // setContentView(R.layout.activity_factory);
//
//         // ImageButton returnButton = findViewById(R.id.imageButton_user_off);
//         // returnButton.setOnClickListener(v -> finish());
//
//
// //      1565　PEQ01にアクセス
//         ImageButton donShariBt = (ImageButton) findViewById(R.id.imageButton_donShari);
//         donShariBt. setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                               BluetoothGattCharacteristic eqPre1=
//                                                       gattAno.getService(UUID.fromString("feed0001-c497-4476-a7ed-727de7648ab1")).getCharacteristic(UUID.fromString("feedaa02-c497-4476-a7ed-727de7648ab1"));
//                                               eqPre1.setValue("F1");
//                                               gattAno.writeCharacteristic(eqPre1);
//                                            }
//                                        }
//
//         );
//
//         //1565　PEQ02にアクセス
//         ImageButton phatBassBt = (ImageButton) findViewById(R.id.imageButton_phat_bass);
//         phatBassBt. setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                BluetoothGattCharacteristic eqPre2=
//                                                        gattAno.getService(UUID.fromString("feed0001-c497-4476-a7ed-727de7648ab1")).getCharacteristic(UUID.fromString("feedaa02-c497-4476-a7ed-727de7648ab1"));
//                                                eqPre2.setValue("F2");
//                                                gattAno.writeCharacteristic(eqPre2);
//                                            }
//                                        }
//
//
//         );
//
//         //1565　PEQ03にアクセス
//         ImageButton voBoostBt = (ImageButton) findViewById(R.id.imageButton_voBoost);
//         voBoostBt. setOnClickListener(new View.OnClickListener() {
//                                           @Override
//                                           public void onClick(View view) {
//                                               BluetoothGattCharacteristic eqPre3=
//                                                       gattAno.getService(UUID.fromString("feed0001-c497-4476-a7ed-727de7648ab1")).getCharacteristic(UUID.fromString("feedaa02-c497-4476-a7ed-727de7648ab1"));
//                                               eqPre3.setValue("F3");
//                                               gattAno.writeCharacteristic(eqPre3);
//                                           }
//                                       }
//
//
//         );
//
//     }
//
// }
