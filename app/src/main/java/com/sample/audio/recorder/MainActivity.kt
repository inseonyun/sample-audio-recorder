package com.sample.audio.recorder

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sample.audio.recorder.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
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
        collectMainState()
    }

    private fun setBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
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

    override fun onPause() {
        super.onPause()
        viewModel.cancel()
    }
}
