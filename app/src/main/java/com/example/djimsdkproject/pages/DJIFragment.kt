package com.example.djimsdkproject.pages

import android.app.AlertDialog
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.example.djimsdkproject.data.MAIN_FILE_DETAILS_STR
import com.example.djimsdkproject.viewModel.MSDKInfoVm
import com.example.djimsdkproject.R
import dji.v5.utils.common.StringUtils

open class DJIFragment: Fragment() {
    protected var mainHandler: Handler = Handler(Looper.getMainLooper())
    protected var indexChosen: IntArray = intArrayOf(-1, -1, -1)
    protected val msdkInfoVm: MSDKInfoVm by activityViewModels()

    open fun updateTitle() {
        arguments?.let {
            val title = it.getInt(MAIN_FILE_DETAILS_STR, R.string.testing_tools)
            msdkInfoVm.mainTitle.value = StringUtils.getResStr(context, title)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateTitle()
    }

    fun resetIndex() {
        indexChosen = intArrayOf(-1, -1, -1)
    }

    fun isFragmentShow(): Boolean {
        return lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)
    }

    fun openInputDialog(text: String, title: String, onStrInput: (str: String) -> Unit) {
        val inputServer = EditText(context)
        inputServer.setText(text)
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title).setView(inputServer).setNegativeButton("CANCEL", null)
        builder.setPositiveButton("OK") { _, _ ->
            onStrInput(inputServer.text.toString())
            builder.show()
        }
    }
}