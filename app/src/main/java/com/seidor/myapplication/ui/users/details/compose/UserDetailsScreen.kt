package com.seidor.myapplication.ui.users.details.compose

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.seidor.domain.model.User
import com.seidor.myapplication.R

@Composable
fun UserDetailsScreen(
    user: User?,
    onBack: () -> Unit
) {
    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0,0,0,0),
        topBar = {
            NoInsetTopBar(
                title = user.name,
                onBack = onBack
            )
        }
    ) { padding ->

    Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .animateContentSize()
        ) {

            // HEADER com gradiente + avatar
            HeaderSection(user)

            Spacer(Modifier.height(16.dp))

            SectionCard(title = "Informações Pessoais") {
                InfoRow("Usuário:", user.username)
                InfoRow("Email:", user.email)
                InfoRow("Telefone:", user.phone)
                InfoRow("Website:", user.website)
            }

            SectionCard(title = "Endereço") {
                InfoRow("Rua:", user.address.street)
                InfoRow("Número:", user.address.suite)
                InfoRow("Cidade:", user.address.city)
                InfoRow("CEP:", user.address.zipcode)
                InfoRow(
                    "Coordenadas:",
                    "Lat ${user.address.geo.lat}, Lng ${user.address.geo.lng}"
                )
            }

            SectionCard(title = "Empresa") {
                InfoRow("Nome:", user.company.name)
                InfoRow("Frase:", user.company.catchPhrase)
                InfoRow("BS:", user.company.bs)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun HeaderSection(user: User) {
    val avatarUrl = remember {
        "https://ui-avatars.com/api/?name=${user.name.replace(" ", "+")}&background=random&size=256"
    }
    Log.d("avatar", avatarUrl)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground)
            )


            Spacer(Modifier.height(12.dp))

            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun NoInsetTopBar(
    title: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

