package co.edu.unal.reto10.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unal.reto10.model.BancoSangre


@Composable
fun BancoSangreItem(bancoSangre: BancoSangre) {

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(15.dp, 8.dp)
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Surface {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(IntrinsicSize.Max)
            ) {
                Text(
                    bancoSangre.bancodesangre,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .padding(10.dp, 10.dp)
                )
                Row {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.65f)
                    ) {
                        Text(
                            "Departamento: " + bancoSangre.departamento,
                            textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Ciudad: " + bancoSangre.ciudad,
                            textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Medium
                        )

                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.95f)
                    ) {
                        Text(
                            text = bancoSangre.codigonacionaldesangre,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier
                                .background(
                                    Color(0XFF9999FF)
                                )
                                .padding(16.dp, 4.dp)

                        )
                        Text(
                            text = "Categoría: " + bancoSangre.categoria,
                            style = MaterialTheme.typography.caption
                        )
                        Text(
                            text = "Estado: " + bancoSangre.estado,
                            style = MaterialTheme.typography.caption
                        )

                    }
                }
            }


        }
    }
}