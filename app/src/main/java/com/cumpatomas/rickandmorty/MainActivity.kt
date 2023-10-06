package com.cumpatomas.rickandmorty

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cumpatomas.rickandmorty.domain.model.CharModel
import com.cumpatomas.rickandmorty.ui.theme.RickAndMortyTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel = viewModel<MainActivityViewModel>()
            val charList = viewModel.charList.collectAsState()
            val loading = viewModel.loading.collectAsState()
            val errorState = viewModel.errorOccurred.collectAsState()

            RickAndMortyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel, charList, loading, errorState)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainActivityViewModel,
    charList: State<List<CharModel>>,
    loading: State<Boolean>,
    errorState: State<Boolean>
) {
    Image(
        painter = painterResource(id = R.drawable.rickandmortybackground),
        null,
        contentScale = ContentScale.FillHeight,
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.2f)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row() {
            OutlinedTextField(
                value = viewModel.searchText.value,
                onValueChange = {
                    viewModel.searchText.value = it
                    viewModel.searchInList(it)
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_bar_hint),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                shape = RoundedCornerShape(8.dp),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
            if (loading.value) {
                CircularProgressIndicator()
            } else {
                if (errorState.value) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Image(
                            painterResource(id = R.drawable.error_crying),
                            null,
                            modifier = Modifier.clip(
                                RoundedCornerShape(12.dp)
                            )
                        )
                    }
                } else {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(charList.value, key = null) { char ->
                            Row(Modifier.animateItemPlacement()) {
                                CharCard(char)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharCard(char: CharModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row {
            Column {
                AsyncImage(model = char.image, contentDescription = null)
            }
            Column(
                Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    char.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(
                        Font(R.font.get_schwifty)
                    ),
                    maxLines = 2,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Gender: ${char.gender}", maxLines = 1)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Species: ${char.species}", maxLines = 1)
            }
        }
    }
}
