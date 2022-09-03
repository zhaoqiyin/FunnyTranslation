package com.funny.translation.translate.ui.widget

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay

private const val TAG = "FrameAnimIcon"

data class FrameAnimationIconState(
    val frameIds: List<Int> = listOf(),
    var initialPlaying: Boolean = false,
    var duration: Long = 500
) {
    var currentFrameIdx by mutableStateOf(0)
    var isPlaying by mutableStateOf(initialPlaying)
    fun next(){
        currentFrameIdx++
        if(currentFrameIdx == frameIds.size) currentFrameIdx = 0
    }
    fun play(){
        isPlaying = true
    }
    fun pause(){
        isPlaying = false
    }
    fun reset(idx : Int = 0){
        currentFrameIdx = idx
        isPlaying = false
    }
}

@Composable
fun rememberFrameAnimIconState(
    frameIds: List<Int> = listOf(),
    initialPlaying: Boolean = false,
    duration: Long = 500
) = rememberSaveable(saver = listSaver(
    save = { listOf(it.isPlaying)},
    restore = { FrameAnimationIconState(frameIds, it[0], duration) }
)) {
    FrameAnimationIconState(frameIds, initialPlaying, duration)
}

@Composable
fun FrameAnimationIcon(
    modifier: Modifier = Modifier,
    state: FrameAnimationIconState = rememberFrameAnimIconState(),
    contentDescription: String = "",
    tint: Color = Color.Unspecified
) {
    val currentFrame by remember { derivedStateOf {
        state.currentFrameIdx
    }}

    LaunchedEffect(state.isPlaying){
//        Log.d(TAG, "FrameAnimationIcon: Launched $state")
        while (state.isPlaying && state.frameIds.isNotEmpty()){
            delay(state.duration / state.frameIds.size)
            state.next()
//            Log.d(TAG, "FrameAnimationIcon: 当前帧数：${state.currentFrameIdx}")
        }
    }

    Icon(painter = painterResource(id = state.frameIds[currentFrame]), contentDescription = contentDescription, tint = tint, modifier = modifier)
}