package com.sample.audio.recorder

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sample.audio.recorder.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setBinding()
        setSTTOnClick()
        checkPermission()
        collectMainState()
    }

    private fun setBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun setSTTOnClick() {
        binding.btnMainGroq.setOnClickListener {
            lifecycleScope.launch {
                viewModel.create()
                delay(300)
                viewModel.start()
            }
        }
    }

    private fun checkPermission() {
        requestPermission(
            "권한이 거절 되었습니다.",
        ).launch(
            getDeniedPermissions(
                RequirePermissions.entries.map { it.manifestPermission }.toTypedArray(),
            ),
        )
    }

    private fun collectMainState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mainState.collect { state ->
                    when (state) {
                        MainState.Idle -> Unit
                        is MainState.Success -> {
                            binding.tvMainText.text = state.text
                        }

                        is MainState.TimeOut -> {
                            binding.tvMainText.text = "시간이 초과되었습니다."
                        }

                        is MainState.Error -> {
                            binding.tvMainText.text = state.message
                        }
                    }
                }
            }
        }
    }

    private fun checkDeniedPermissions(permissions: Array<String>) =
        permissions
            .map { type -> getPermissionStatus(type) to type }
            .filter { !it.first }
            .map { it.first }.toTypedArray()

    private fun getDeniedPermissions(permissions: Array<String>) =
        permissions
            .map { type -> getPermissionStatus(type) to type }
            .filter { !it.first }
            .map { it.second }.toTypedArray()

    private fun requestPermission(deniedPermissionMessage: String) =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val hasDeniedPermission =
                checkDeniedPermissions(permissions.entries.map { it.key }.toTypedArray()).any { !it }
            if (hasDeniedPermission)
                Toast.makeText(this, deniedPermissionMessage, Toast.LENGTH_SHORT).show()
        }


    private fun getPermissionStatus(permissionType: String): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            permissionType,
        ) == PackageManager.PERMISSION_GRANTED

    override fun onPause() {
        super.onPause()
        viewModel.cancel()
    }
}
