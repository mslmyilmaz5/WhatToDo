package com.example.whattodo.ui.theme
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.whattodo.DayCompletedActivity
import com.example.whattodo.MainActivity
import com.example.whattodo.R
import com.example.whattodo.getFilesContainingString
import com.example.whattodo.getImageByName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun showAlbum() {

    val context = LocalContext.current

    val picturesToShow = context.getFilesContainingString("WhatToDo12_16")
    for (file in picturesToShow) {
        Log.d("XXXXXXXXASDASDAXXXXXXXXXXXXXX", "Eşleşen dosya adı: ${file.name}")
    }
    var currentImageIndex by remember { mutableStateOf(0) }
    val topAppBarShape = RoundedCornerShape(
        bottomStart = 20.dp,
        bottomEnd = 20.dp,
        topStart = 0.dp,
        topEnd = 0.dp
    )

    val topAppBarModifier = Modifier.clip(topAppBarShape)

    Scaffold(

        topBar = {
            CenterAlignedTopAppBar(
                modifier = topAppBarModifier,
                title = {
                    Text(
                        text = "WhatToDo Album",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4044C9),
                )
            )
        }
    )


    { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
            ,

            color = Color.Black
        ) {

            if (picturesToShow.isNotEmpty()) {

                ImageAndText(
                    imageFile = picturesToShow[currentImageIndex],
                    onImageClick = {
                        currentImageIndex = (currentImageIndex + 1) % picturesToShow.size
                    },
                    onSaveToGalleryClick = {
                        saveImageToGallery(picturesToShow[currentImageIndex], context)
                    }

                )

            }



        }
    }
}

@Composable
fun ImageAndText(
    imageFile: File?,
    onImageClick: () -> Unit,
    onSaveToGalleryClick: () -> Unit, // Yeni buton için tıklama işlevi
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ -> }



    Box(
        modifier = Modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {


            Button(
                onClick = onImageClick,
                shape = RoundedCornerShape(dimensionResource(R.dimen.button_corner_radius)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Image(
                    painter = rememberImagePainter(imageFile),
                    contentDescription = "",
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.button_image_width))
                        .height(dimensionResource(R.dimen.button_image_height))
                        .padding(dimensionResource(R.dimen.button_interior_padding))
                        .clip(RoundedCornerShape(21.dp)),
                        /*
                        .border(
                            BorderStroke(5.dp, Color.White),
                            RectangleShape
                        )

                         */
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Tap the screen to see the next image",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_vertical)))

            Row(modifier = Modifier
                .background(Color(0xFF4044C9))) {
                FloatingActionButton(
                    onClick = onSaveToGalleryClick
                    ,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(31.dp)
                        .weight(2f),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    containerColor =  Color(0xFF4044C9),
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save the image"
                    )
                }
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(context, MainActivity::class.java)
                        launcher.launch(intent)

                        }
                    ,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(31.dp)
                        .weight(2f)


                    ,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    containerColor = Color(0xFF4044C9),
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardReturn,
                        contentDescription = "Main Page"
                    )
                }

            }


            // Yeni eklenen buton

        }
    }
}


private fun saveImageToGallery(imageFile: File?, context: Context) {

    imageFile?.let {

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { imageUri ->
            contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                it.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
                outputStream.flush()
            }

            Toast.makeText(context, "Image saved to Gallery", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }
}

