package com.seidor.myapplication.ui.users.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seidor.domain.model.User

@Composable
fun UserItemComposable(
    user: User,
    onClick: (Int) -> Unit
) {
    ListItem(
        headlineContent = { Text(user.name) },
        supportingContent = { Text(user.email) },
        overlineContent = { Text(user.address.city) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(user.id) }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
    HorizontalDivider()
}
