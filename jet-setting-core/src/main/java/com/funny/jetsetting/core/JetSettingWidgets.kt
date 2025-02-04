package com.funny.jetsetting.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.funny.data_saver.core.rememberDataSaverState
import com.funny.jetsetting.core.ui.FunnyIcon
import com.funny.jetsetting.core.ui.IconWidget
import com.funny.jetsetting.core.ui.SettingBaseItem
import kotlinx.collections.immutable.ImmutableList

private val DefaultJetSettingModifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 24.dp, vertical = 12.dp)

private val EmptyAction = {}

@Composable
fun JetSettingSwitch(
    modifier: Modifier = Modifier,
    state: MutableState<Boolean>,
    imageVector: ImageVector? = null,
    resourceId: Int? = null,
    text: String,
    description: String? = null,
    interceptor: () -> Boolean = { true },
    onCheck: (Boolean) -> Unit = {}
) {
    SettingBaseItem(
        modifier = modifier,
        icon = {
            val icon = FunnyIcon(imageVector, resourceId)
            IconWidget(funnyIcon = icon, tintColor = MaterialTheme.colorScheme.onSurface)
        },
        title = {
            Text(text = text)
        },
        text = {
            description?.let {
                Text(text = it)
            }
        },
        action = {
            Switch(checked = state.value, onCheckedChange = {
                if (interceptor.invoke()) {
                    state.value = it
                    onCheck(it)
                }
            })
        },
        onClick = {
            if (interceptor.invoke()) {
                state.value = !state.value
                onCheck(state.value)
            }
        }
    )
}

@Composable
fun JetSettingSwitch(
    modifier: Modifier = Modifier,
    key: String,
    default: Boolean = false,
    imageVector: ImageVector? = null,
    resourceId: Int? = null,
    text: String,
    description: String? = null,
    interceptor: () -> Boolean = { true },
    onCheck: (Boolean) -> Unit
) {
    val state = rememberDataSaverState(key = key, default = default)
    JetSettingSwitch(
        modifier = modifier,
        state = state,
        imageVector = imageVector,
        resourceId = resourceId,
        text = text,
        description = description,
        interceptor = interceptor,
        onCheck = onCheck
    )
}

@Composable
fun JetSettingTile(
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    resourceId: Int? = null,
    text: String,
    interceptor: () -> Boolean = { true },
    onClick: () -> Unit
) {
    SettingBaseItem(
        modifier = Modifier.padding(vertical = 4.dp),
        title = {
            Text(text = text)
        },
        action = {
           Icon(Icons.Default.KeyboardArrowRight, "Goto", )
        },
        icon = {
            val icon = FunnyIcon(imageVector, resourceId)
            IconWidget(funnyIcon = icon, tintColor = MaterialTheme.colorScheme.onSurface)
        },
        onClick = {
            if (interceptor.invoke()) {
                onClick()
            }
        }
    )
}


@Composable
fun JetSettingDialog(
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    resourceId: Int? = null,
    text: String,
    dialogTitle: String = stringResource(id = R.string.hint),
    confirmButtonAction: () -> Unit? = EmptyAction,
    confirmButtonText: String = "确认",
    dismissButtonAction: () -> Unit? = EmptyAction,
    dismissButtonText: String = "取消",
    dialogContent: @Composable () -> Unit
) {
    var openDialogState by remember {
        mutableStateOf(false)
    }
    if (openDialogState) {
        AlertDialog(
            title = {
                Text(text = dialogTitle)
            },
            text = dialogContent,
            onDismissRequest = { openDialogState = false },
            confirmButton = {
                if (confirmButtonAction != EmptyAction)
                    Button(
                        onClick = {
                            openDialogState = false
                            confirmButtonAction()
                        }) {
                        Text(confirmButtonText)
                    }
            },
            dismissButton = {
                if (dismissButtonText.isNotEmpty())
                    Button(
                        onClick = {
                            openDialogState = false
                            dismissButtonAction()
                        }) {
                        Text(dismissButtonText)
                    }
            }
        )
    }

    JetSettingTile(
        modifier = modifier,
        text = text,
        imageVector = imageVector,
        resourceId = resourceId,
        onClick =  {
            openDialogState = true
        }
    )
}

@Composable
fun <E> JetSettingListDialog(
    modifier: Modifier = Modifier,
    list: ImmutableList<E>,
    text: String,
    imageVector: ImageVector? = null,
    resourceId: Int? = null,
    selected: E,
    updateSelected: (E) -> Unit,
    confirmButtonAction: () -> Unit? = EmptyAction,
    confirmButtonText: String = "确认",
    dismissButtonAction: () -> Unit? = EmptyAction,
    dismissButtonText: String = "取消",
) {
    JetSettingDialog(
        modifier = modifier,
        text = text,
        resourceId = resourceId,
        imageVector = imageVector,
        confirmButtonAction = confirmButtonAction,
        confirmButtonText = confirmButtonText,
        dismissButtonAction = dismissButtonAction,
        dismissButtonText = dismissButtonText,
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(list) { i, item ->
                SettingBaseItem(
                    modifier = Modifier.padding(vertical = 4.dp),
                    title = {
                        Text(text = item.toString())
                    },
                    action = {
                        RadioButton(selected = selected == item, onClick = { updateSelected(item) })
                    },
                    onClick = {
                        updateSelected(item)
                    }
                )
            }
        }
    }
}

