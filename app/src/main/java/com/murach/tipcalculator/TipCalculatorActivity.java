package com.murach.tipcalculator;

import java.text.NumberFormat;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TipCalculatorActivity extends Activity 
implements OnEditorActionListener, OnClickListener {

    // define variables for the widgets
    private EditText billAmountEditText;
    private TextView percentTextView;   
    private Button   percentUpButton;
    private Button   percentDownButton;
    private TextView tipTextView;
    private TextView totalTextView;
    private Button   applyButton;
    private TextView    textViewPerPersonAmt,
                        textViewPerPerson;


    //RadioButtons
    private RadioButton rbNone,
                        rbTip,
                        rbTotal;


    private ArrayAdapter<CharSequence> adapter;
    private Spinner sp;

    private float splitAmt,
                  splitWays;//Divisor.

    // define the SharedPreferences object
    private SharedPreferences savedValues;
    
    // define instance variables that should be saved
    private String billAmountString = "";
    private float tipPercent = .15f;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);
        
        // get references to the widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        applyButton = (Button) findViewById(R.id.applyButton);

        //Per-person split.
        textViewPerPersonAmt = (TextView)findViewById(R.id.textViewPerPersonAmt);

        textViewPerPerson  = (TextView)findViewById(R.id.textViewPerPerson);

        //RadioButton refs.
        rbNone      = (RadioButton)findViewById(R.id.radioButtonNone);
        rbTip       = (RadioButton)findViewById(R.id.radioButtonTip);
        rbTotal     = (RadioButton)findViewById(R.id.radioButtonTotal);

        //Setup the spinner with adapter.
        sp = (Spinner)findViewById(R.id.spinnerSplitBill);
        adapter = ArrayAdapter.createFromResource(this,R.array.split_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);
        applyButton.setOnClickListener(this);
        
        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);                
    }
    
    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = savedValues.edit();        
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();        

        super.onPause();      
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // get the instance variables
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);
        
        // calculate and display
        calculateAndDisplay();
    }
    
    public void calculateAndDisplay() {        

        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount; 
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }
        
        // calculate tip and total 
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;

        if(rbNone.isChecked())
        {

        }
        else if(rbTip.isChecked())
        {
            tipAmount       = StrictMath.round(billAmount * tipPercent);
            totalAmount     = billAmount + tipAmount;
        }
        else if(rbTotal.isChecked())
        {
            float tipNotRounded         = billAmount * tipPercent;
            totalAmount                 = StrictMath.round(billAmount + tipNotRounded);
            tipAmount                   = totalAmount - billAmount;
        }



        switch(sp.getSelectedItemPosition())
        {
            case 0://No split.
                splitWays=1;

                //Hide Per-person widgets.
                textViewPerPersonAmt.setVisibility(View.INVISIBLE);
                textViewPerPerson.setVisibility(View.INVISIBLE);
            break;

            case 1://2-way split.
                splitWays=2;

                //Show per-person widgets.
                textViewPerPersonAmt.setVisibility(View.VISIBLE);
                textViewPerPerson.setVisibility(View.VISIBLE);
            break;

            case 2://3-way split.
                splitWays=3;

                //Show per-person widgets.
                textViewPerPersonAmt.setVisibility(View.VISIBLE);
                textViewPerPerson.setVisibility(View.VISIBLE);
            break;

            case 3://4-way split.
                splitWays=4;

                //Show per-person widgets.
                textViewPerPersonAmt.setVisibility(View.VISIBLE);
                textViewPerPerson.setVisibility(View.VISIBLE);
            break;

            default:
                splitWays=1;

                //Hide Per-person widgets.
                textViewPerPersonAmt.setVisibility(View.INVISIBLE);
                textViewPerPerson.setVisibility(View.INVISIBLE);
        }

        splitAmt = totalAmount / splitWays;

        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));


        textViewPerPersonAmt.setText(currency.format(splitAmt));


        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));

    }
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
    		actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }        
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.percentDownButton:
            tipPercent = tipPercent - .01f;

            rbNone.setChecked(true);

            calculateAndDisplay();
            break;
        case R.id.percentUpButton:
            tipPercent = tipPercent + .01f;

            rbNone.setChecked(true);

            calculateAndDisplay();
            break;
        case R.id.applyButton:
            calculateAndDisplay();
            break;
        }
    }
}