package com.warrantyshelf.notes.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.warrantyshelf.notes.core.theme.ShelfLine
import com.warrantyshelf.notes.core.theme.ShelfSurface

@Composable
fun FolderCard(
    modifier: Modifier = Modifier,
    innerPadding: Dp = 18.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 14.dp,
            bottomEnd = 14.dp,
            bottomStart = 14.dp
        ),
        color = ShelfSurface,
        border = BorderStroke(1.dp, ShelfLine),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(innerPadding),
            content = content
        )
    }
}
