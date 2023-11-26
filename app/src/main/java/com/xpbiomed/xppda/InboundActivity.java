package com.xpbiomed.xppda;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InboundActivity extends AppCompatActivity {

    private EditText productionOrderEditText;
    private EditText barcodeEditText;
    private TextView currentQuantityTextView;
    private TextView remainingQuantityTextView;
    private TextView totalQuantityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbound);

        productionOrderEditText = findViewById(R.id.productionorderedittext);
        barcodeEditText = findViewById(R.id.barcodeedittext);
        currentQuantityTextView = findViewById(R.id.currentquantitytextview);
        remainingQuantityTextView = findViewById(R.id.remainingquantitytextview);
        totalQuantityTextView = findViewById(R.id.totalquantitytextview);
        // Add your code to handle the Inbound activity
    }
}
