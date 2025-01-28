package com.example.djimsdkproject.pages

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.djimsdkproject.data.ControlInfo
import com.example.djimsdkproject.data.FLIGHT_CONTROL_PARAM
import com.example.djimsdkproject.data.GIMBAL_PARAM
import com.example.djimsdkproject.data.VIRTUAL_STICK_PARAM
import com.example.djimsdkproject.data.ZOOM_PARAM
import com.example.djimsdkproject.databinding.FragmentControlInputBinding
import com.example.djimsdkproject.viewModel.BasicController

class ControlInputFragment(private val command: String): DJIFragment() {
    private var _binding: FragmentControlInputBinding? = null
    private val binding: FragmentControlInputBinding
        get() = _binding!!

    private val editTextMap: MutableMap<String, EditText> = mutableMapOf()
    private val spinnerMap: MutableMap<String, Spinner> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentControlInputBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateInput()

        val fragmentManager: FragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.commit()


        binding.buttonDo.setOnClickListener {
            BasicController.runControlWithInfo(generateControlInfo())

            fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        binding.buttonPush.setOnClickListener {
            BasicController.addControl(generateControlInfo())

            fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(0).id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        binding.buttonCancel.setOnClickListener {
            fragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    private fun generateInput() {
        val param: Map<String, String> = mapOf(
            "flightControl" to FLIGHT_CONTROL_PARAM,
            "virtualStick" to VIRTUAL_STICK_PARAM,
            "gimbal" to GIMBAL_PARAM,
            "camera" to ZOOM_PARAM
        )[command] ?: return

        setInput(param)
    }

    private fun setInput(param: Map<String, String>) {
        setEditText(param)
        setSpinners(param)
    }

    private fun setEditText(param: Map<String, String>) {
        param.forEach {
            if(!it.value.matches("[+-]?\\d+(?:\\.\\d+)?".toRegex())) return@forEach

            val editText: EditText = EditText(context)
            editText.inputType = InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL + InputType.TYPE_NUMBER_FLAG_SIGNED
            editText.hint = it.key
            editText.setSelectAllOnFocus(true)
            editText.setText(it.value)

            editText.setOnEditorActionListener { _, actionId, keyEvent ->
                val isPushedEnter = actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN &&
                                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)

                if (isPushedEnter) {
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(
                        binding.root.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )

                    val parent = editText.parent as View
                    parent.isFocusable = true
                    parent.isFocusableInTouchMode = true
                    binding.root.requestFocus()

                    return@setOnEditorActionListener true
                } else {
                    return@setOnEditorActionListener false
                }
            }

            editTextMap[it.key] = editText

            binding.frameInput.addView(
                editText,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }

    private fun setSpinners(param: Map<String, String>) {
        param.forEach {
            if(it.value.matches("[+-]?\\d+(?:\\.\\d+)?".toRegex())) return@forEach

            val spinnerList: List<String> = it.value.split(",\\s*".toRegex())

            val spinner: Spinner = Spinner(context)
            val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerList.forEach { item ->
                adapter.add(item)
            }

            spinner.adapter = adapter

            spinnerMap[it.key] = spinner

            binding.frameInput.addView(
                spinner,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }

    private fun generateControlInfo(): ControlInfo {
        val command: String = command
        val runTime: Long =
            if((editTextMap["runTime"]?.text ?: "").toString().matches("\\d+".toRegex())) editTextMap["runTime"]?.text.toString().toLong()
            else 0
        val params: String = fun(): String {
            var text: String = "{ "

            editTextMap.forEach {
                if(it.key == "runTime") return@forEach

                text += "\"${it.key}\": ${it.value.text}, "
            }

            spinnerMap.forEach {
                text += if(it.key == "command") "\"${it.key}\": \"${it.value.selectedItem}\", "
                else "\"${it.key}\": ${it.value.selectedItem}, "
            }

            text = text.replace(",\\s*$".toRegex(), "")

            text += " }"

            return text
        }()

        return ControlInfo(command, params, runTime)
    }
}