package co.edu.unal.reto10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unal.reto10.data.Departments
import co.edu.unal.reto10.model.BancoSangre
import co.edu.unal.reto10.ui.theme.Reto10Theme
import co.edu.unal.reto10.ui.theme.Teal200
import co.edu.unal.reto10.view.BancoSangreItem
import co.edu.unal.reto10.viewModel.BancoSangreViewModel
import java.util.*

class MainActivity : ComponentActivity() {

    private val bancoSangreViewModel by viewModels<BancoSangreViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Reto10Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeScreen(
                        bancoSangreViewModel = bancoSangreViewModel,
                        modifier = Modifier
                            .background(Color.Gray)
                    )
                }
            }
        }
    }
}

@Composable
fun BancoSangreList(
    bancoSangreViewModel: BancoSangreViewModel,
    state: MutableState<TextFieldValue>
) {
    var filteredBancoSangre: List<BancoSangre>
    LazyColumn {
        val search = bancoSangreViewModel.selectedFilter
        filteredBancoSangre = if (search == "") {
            bancoSangreViewModel.getBancosSangreList()
            val bancosSangre = bancoSangreViewModel.bancoSangreListResponse
            bancosSangre
        } else {
            bancoSangreViewModel.getBancosSangreListByDepartment(search)
            val resultList = bancoSangreViewModel.bancoSangreListResponse
            resultList
        }
        itemsIndexed(filteredBancoSangre) { _, filteredBancoSangre ->
            BancoSangreItem(bancoSangre = filteredBancoSangre)
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier, bancoSangreViewModel: BancoSangreViewModel) {
    Column(modifier.padding(vertical = 16.dp).fillMaxWidth().background(Color(0xFFdddddd))) {
        Text(
            "Bancos de sangre en Colombia",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF000000),
        )
        val textState = remember { mutableStateOf(TextFieldValue("")) }
        DropdownTipo(bancoSangreViewModel)
        bancoSangreViewModel.getBancosSangreList()
        BancoSangreList(bancoSangreViewModel, textState)
    }
}


@Composable
fun DropdownTipo(bancoSangreViewModel: BancoSangreViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val items = Departments

    var selectedIndex by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .height(50.dp)
            .padding(6.dp)
            .wrapContentSize(Alignment.TopEnd)
            .background(
                Color(0XFF99FF99)
            )
    ) {
        Text(
            items[selectedIndex],
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 7.dp)
                .clickable(onClick = { expanded = true })

        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White
                )
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    var search = ""
                    if (s == Departments[0]) {
                        search = ""
                    } else {
                        search = s
                    }
                    bancoSangreViewModel.selectedFilter = search
                }) {
                    Text(
                        text = s,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Reto10Theme {
        Column {
            Text(
                "Bancos de sangre en Colombia",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000),

                )
            //DropDownDepartments(Modifier.padding(16.dp), companyViewModel)
            val bancoSangre = BancoSangre(
                "HOSPITAL CENTRAL POLICÍA NACIONAL",
                "11-001-21",
                "A",
                "BOGOTÁ",
                "BOGOTÁ",
                "ABIERTO"
            )
            BancoSangreItem(bancoSangre = bancoSangre)
        }
    }
}