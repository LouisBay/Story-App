package com.louis.bpaaisubmission.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.louis.bpaaisubmission.R

class CustomPasswordEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.insert_password)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        typeface = ResourcesCompat.getFont(context, R.font.poppins_light)
    }

    private fun init() {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing.
            }

            override fun afterTextChanged(s: Editable?) {
                checkPassword(s)
            }

        })
    }

    private fun checkPassword(s: Editable?) {
        val password = s.toString()

        if (password.length < 6 && password.isNotEmpty() ) {
            error = resources.getString(R.string.invalid_password)
        }

    }
}