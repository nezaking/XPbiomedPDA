package com.xpbiomed.xppda;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OutboundActivity extends AppCompatActivity {

    private EditText salesOrderEditText;
    private EditText barcodeEditText;
    private TextView currentQuantityTextView;
    private TextView remainingQuantityTextView;
    private TextView totalQuantityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outbound);

        salesOrderEditText = findViewById(R.id.salesorderedittext);
        barcodeEditText = findViewById(R.id.outbarcodeedittext);
        currentQuantityTextView = findViewById(R.id.outcurrentquantitytextview);
        remainingQuantityTextView = findViewById(R.id.outremainingquantitytextview);
        totalQuantityTextView = findViewById(R.id.outbarcodeedittext);
        // Add your code to handle the Outbound activity
    }
}
