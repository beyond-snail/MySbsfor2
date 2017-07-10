
package com.zfsbs.activity.liandong;

/**
 * Created by zf on 2017/3/16.
 */


import android.content.Context;
        import android.graphics.drawable.GradientDrawable;
        import android.text.Editable;
        import android.text.InputFilter;
        import android.text.InputType;
        import android.text.TextWatcher;
        import android.util.AttributeSet;
        import android.view.Gravity;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;


public class IPEdit extends LinearLayout {

    private EditText mIpAddrEdt1 = null;
    private EditText  mIpAddrEdt2 = null;
    private EditText  mIpAddrEdt3 = null;
    private EditText  mIpAddrEdt4 = null;

    private TextView mPointTv1 = null;
    private TextView mPointTv2 = null;
    private TextView mPointTv3 = null;

    private TextWatcher mTextWatcher = null;

    private Context mParentContext = null;

    public IPEdit(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();

    }

    public IPEdit(Context context)
    {
        super(context);
        init();
    }

    public String getText()
    {
        StringBuilder  strBuild = new StringBuilder();
        String wsIp1 = mIpAddrEdt1.getText().toString();
        String wsIp2 = mIpAddrEdt2.getText().toString();
        String wsIp3 = mIpAddrEdt3.getText().toString();
        String wsIp4 = mIpAddrEdt4.getText().toString();
        String wsPoint = ".";
        strBuild.append(wsIp1);
        strBuild.append(wsPoint);
        strBuild.append(wsIp2);
        strBuild.append(wsPoint);
        strBuild.append(wsIp3);
        strBuild.append(wsPoint);
        strBuild.append(wsIp4);
        return strBuild.toString();
    }


    private void init()
    {
        mParentContext = this.getContext();
        mIpAddrEdt1 = new EditText(mParentContext);
        mIpAddrEdt2 = new EditText(mParentContext);
        mIpAddrEdt3 = new EditText(mParentContext);
        mIpAddrEdt4 = new EditText(mParentContext);
        initEditText(mIpAddrEdt1);
        initEditText(mIpAddrEdt2);
        initEditText(mIpAddrEdt3);
        initEditText(mIpAddrEdt4);
        mPointTv1 = new TextView(mParentContext);
        mPointTv2 = new TextView(mParentContext);
        mPointTv3 = new TextView(mParentContext);
        initTextView(mPointTv1);
        initTextView(mPointTv2);
        initTextView(mPointTv3);
        drawFrame();
        seftLayout();

        mTextWatcher = new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s)
            {
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after)
            {
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count)
            {
                String wsText = null;
                EditText focusedEdt = getFocuseEdt();
                if(focusedEdt == null)
                    return;
                if (s != null && s.length() > 0) {
                    if (s.length() > 2 || s.toString().trim().contains(".")) {
                        if (s.toString().trim().contains(".")) {
                            wsText = s.toString().substring(0, s.length() - 1);
                            focusedEdt.setText(wsText);
                        }
                        else {
                            wsText = s.toString().trim();
                        }
                        if (Integer.parseInt(wsText) > 255) {
                            Toast.makeText(mParentContext, "请输入合法的ip地址",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        EditText nextFocuseEdt = getNextFocuseEdt();
                        if(nextFocuseEdt != null)
                        {
                            nextFocuseEdt.setFocusable(true);
                            nextFocuseEdt.requestFocus();
                        }else
                        {
                            return;
                        }
                    }
                    else if(s.length() >= 2 && (s.toString().startsWith("0")) == true)
                    {
                        wsText = s.toString().substring(1, s.length());
                        focusedEdt.setText(wsText);
                        focusedEdt.setSelection(wsText.length());
                    }
                }
                if (start == 0 && s.length() == 0) {
                    EditText prevEdt = getPrevFocuseEdt();
                    if(prevEdt != null)
                    {
                        prevEdt.setFocusable(true);
                        prevEdt.requestFocus();
                    }
                }
            }
        };
        addTextChangeListener();
    }

    private EditText getFocuseEdt()
    {
        if(mIpAddrEdt1.isFocused())
            return mIpAddrEdt1;
        if(mIpAddrEdt2.isFocused())
            return mIpAddrEdt2;
        if(mIpAddrEdt3.isFocused())
            return mIpAddrEdt3;
        if(mIpAddrEdt4.isFocused())
            return mIpAddrEdt4;
        return null;
    }

    private EditText getNextFocuseEdt()
    {
        EditText focusedEdt = getFocuseEdt();
        if(focusedEdt == mIpAddrEdt1)
            return mIpAddrEdt2;
        if(focusedEdt == mIpAddrEdt2)
            return mIpAddrEdt3;
        if(focusedEdt == mIpAddrEdt3)
            return mIpAddrEdt4;
        return null;
    }

    private EditText getPrevFocuseEdt()
    {
        EditText focusedEdt = getFocuseEdt();
        if(focusedEdt == mIpAddrEdt4)
            return mIpAddrEdt3;
        if(focusedEdt == mIpAddrEdt3)
            return mIpAddrEdt2;
        if(focusedEdt == mIpAddrEdt2)
            return mIpAddrEdt1;
        return null;

    }


    private void drawFrame()
    {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(1, 0xFFC8C7C7);  //
        drawable.setColor(0xFFFFFFFF);
        this.setBackground(drawable);
    }

    private void initEditText(EditText edtText)
    {
        edtText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        edtText.setMaxLines(1);
        edtText.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtText.setBackground(null);
        edtText.setGravity(Gravity.CENTER);
    }

    private void initTextView(TextView textView)
    {
        textView.setBackground(null);
        textView.setText(".");
    }

    private void seftLayout()
    {
        this.setOrientation(LinearLayout.HORIZONTAL);
        edtTextLayout(mIpAddrEdt1);
        textViewLayout(mPointTv1);
        edtTextLayout(mIpAddrEdt2);
        textViewLayout(mPointTv2);
        edtTextLayout(mIpAddrEdt3);
        textViewLayout(mPointTv3);
        edtTextLayout(mIpAddrEdt4);
    }

    private void edtTextLayout(EditText edt)
    {
        int lHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        int lWidth = LinearLayout.LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams  lp = new LinearLayout.LayoutParams(lHeight, lWidth);
        lp.weight = 1;
        this.addView(edt, lp);
    }

    private void textViewLayout(TextView txtView)
    {
        int lHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        int lWidth = LinearLayout.LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams  lp = new LinearLayout.LayoutParams(lHeight, lWidth);
        this.addView(txtView, lp);
    }

    private void addTextChangeListener()
    {
        mIpAddrEdt1.addTextChangedListener(mTextWatcher);
        mIpAddrEdt2.addTextChangedListener(mTextWatcher);
        mIpAddrEdt3.addTextChangedListener(mTextWatcher);
        mIpAddrEdt4.addTextChangedListener(mTextWatcher);
    }
}

