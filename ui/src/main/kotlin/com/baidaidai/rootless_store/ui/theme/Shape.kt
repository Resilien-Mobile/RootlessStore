package com.baidaidai.rootless_store.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * 用于获取：非外包裹任意 Container 的 ListItem 列表 Shape 信息
 *
 * 也就是自组性 ListItems，会基于数组长度和目前 Index 返回四种 Shape 信息
 *
 * @since 2026-10-6
 */
@Composable
fun getExpressiveListItemShape(
    index: Int,
    list: List<*>
): RoundedCornerShape {
    return when {
        list.size == 1 -> RoundedCornerShape(16.dp)

        index == 0 -> RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 4.dp,
            bottomEnd = 4.dp
        )

        index == list.lastIndex -> RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 4.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )

        else -> RoundedCornerShape(4.dp)
    }
}