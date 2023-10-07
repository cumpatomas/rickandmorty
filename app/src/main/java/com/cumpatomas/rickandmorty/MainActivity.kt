package com.cumpatomas.rickandmorty

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cumpatomas.rickandmorty.domain.model.CharModel
import com.cumpatomas.rickandmorty.ui.theme.RickAndMortyTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel = viewModel<MainActivityViewModel>()
            val charList = viewModel.charList.collectAsState()
            val loading = viewModel.loading.collectAsState()
            val errorState = viewModel.errorOccurred.collectAsState()
            val webViewState = rememberSaveable { mutableStateOf(false) }


            RickAndMortyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel, charList, loading, errorState, webViewState)
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainActivityViewModel,
    charList: State<Set<CharModel>>,
    loading: State<Boolean>,
    errorState: State<Boolean>,
    webViewState: MutableState<Boolean>,
) {
    val searchText by viewModel.searchText.collectAsState()
    val state = rememberSaveable { mutableStateOf(false) }
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
            TextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                singleLine = true,
                placeholder = {
                    if (searchText.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.search_bar_hint),
                            color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
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
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(charList.value.distinct(), key = null) { char ->
                            Column(Modifier.animateItemPlacement()) {
                                Row(Modifier.animateItemPlacement()) {

                                    CharCard(char, listState, charList.value.indexOf(char))
                                }
                                if (char.webViewState.value) {
                                    Row(
                                        Modifier
                                            .animateItemPlacement()
                                            .fillParentMaxHeight()
                                    ) {
                                        ItemWebView(char)
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharCard(
    char: CharModel,
    listState: LazyListState,
    index: Int
) {
    val mutableText = rememberSaveable { mutableStateOf("+Info") }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
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
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Gender: ${char.gender}", maxLines = 1)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Species: ${char.species}", maxLines = 1)
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                if (char.id.toInt() in 1..5)
                    Text(text = mutableText.value,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF599C4D),
                        modifier = Modifier.clickable {
                            char.webViewState.value = !char.webViewState.value
                            when (char.webViewState.value) {
                                true -> mutableText.value = "close"
                                else -> mutableText.value = "+info"
                            }
                            coroutineScope.launch {
                                listState.animateScrollToItem(index = index, scrollOffset = 0)
                            }
                        })
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ItemWebView(char: CharModel) {
    val loadingState = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    Column() {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
        ) {
            AndroidView(
                modifier = Modifier.background(Color.White),
                factory = {
                    WebView(it).apply {
                        coroutineScope.launch {
                            val job = launch {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )

                                webViewClient = WebViewClient()
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = false
                                settings.allowContentAccess = true

                                when (char.id) {
                                    "1" -> loadUrl("https://rickandmortyshow.com/characters/rick-sanchez/")
                                    "2" -> loadUrl("https://rickandmortyshow.com/characters/morty-smith/")
                                    "3" -> loadUrl("https://rickandmortyshow.com/characters/summer-smith/")
                                    "4" -> loadUrl("https://rickandmortyshow.com/characters/beth-smith/")
                                    "5" -> loadUrl("https://rickandmortyshow.com/characters/jerry-smith/")
                                }
                            }

                            job.join()
                        }

                        loadingState.value = false
                    }
                },
            )
        }
    }
}


