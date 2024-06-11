package com.example.blackbank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.blackbank.ui.theme.BlackBankTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackBankTheme {
                ConteudoApp()
            }
        }
    }
}

@Composable
fun ConteudoApp() {
    var estaLogado by remember { mutableStateOf(false) }
    var estaNaTelaDeSaque by remember { mutableStateOf(false) }
    var estaNaTelaDeDeposito by remember { mutableStateOf(false) }
    var estaNaTelaDeTransferencia by remember { mutableStateOf(false) }
    var saldo by remember { mutableStateOf(1000.0) }  // Saldo inicial fictício

    when {
        estaLogado && estaNaTelaDeSaque -> {
            TelaDeSaque(
                saldo = saldo,
                onSaque = { valor ->
                    saldo -= valor
                    estaNaTelaDeSaque = false
                },
                onCancelamento = {
                    estaNaTelaDeSaque = false
                }
            )
        }
        estaLogado && estaNaTelaDeDeposito -> {
            TelaDeDeposito(
                onDeposito = { valor ->
                    saldo += valor
                    estaNaTelaDeDeposito = false
                },
                onCancelamento = {
                    estaNaTelaDeDeposito = false
                }
            )
        }
        estaLogado && estaNaTelaDeTransferencia -> {
            TelaDeTransferencia(
                saldo = saldo,
                onTransferencia = { chavePix, valor ->
                    saldo -= valor
                    estaNaTelaDeTransferencia = false
                },
                onCancelamento = {
                    estaNaTelaDeTransferencia = false
                }
            )
        }
        estaLogado -> {
            TelaBancaria(
                saldo = saldo,
                onCliqueSaque = { estaNaTelaDeSaque = true },
                onCliqueDeposito = { estaNaTelaDeDeposito = true },
                onCliqueTransferencia = { estaNaTelaDeTransferencia = true }
            )
        }
        else -> {
            TelaLogin { estaLogado = true }
        }
    }
}

@Composable
fun TelaLogin(onSucessoLogin: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.img), contentDescription =null )
        Text(text = "Login", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuário") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onSucessoLogin()  }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
            Text("Login")
        }
    }
}

@Composable
fun TelaBancaria(
    saldo: Double,
    onCliqueSaque: () -> Unit,
    onCliqueDeposito: () -> Unit,
    onCliqueTransferencia: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Saldo: R$ $saldo", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { onCliqueSaque() },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                Text("Saque")
            }
            Button(onClick = { onCliqueDeposito() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                Text("Depósito")
            }
            Button(onClick = { onCliqueTransferencia() },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                Text("Transferência (PIX)")
            }
        }
    }
}

@Composable
fun TelaDeSaque(saldo: Double, onSaque: (Double) -> Unit, onCancelamento: () -> Unit) {
    var valor by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Digite o valor para saque", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { input ->
                if (input.toDoubleOrNull() != null) {
                    valor = input
                    mensagemErro = ""
                } else {
                    mensagemErro = "Valor inválido!"
                }
            },
            label = { Text("Valor") },
            modifier = Modifier.fillMaxWidth()
        )

        if (mensagemErro.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = mensagemErro, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                val valorSaque = valor.toDoubleOrNull() ?: 0.0
                if (valorSaque <= 0 || valorSaque > saldo) {
                    mensagemErro = "Valor inválido!"
                } else {
                    onSaque(valorSaque)
                    mensagemErro = ""
                }
            },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                Text("Confirmar")
            }
            Button(onClick = { onCancelamento() },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
fun TelaDeDeposito(onDeposito: (Double) -> Unit, onCancelamento: () -> Unit) {
    var valor by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Digite o valor para depósito", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { input ->
                if (input.toDoubleOrNull() != null) {
                    valor = input
                    mensagemErro = ""
                } else {
                    mensagemErro = "Valor inválido!"
                }
            },
            label = { Text("Valor") },
            modifier = Modifier.fillMaxWidth()
        )

        if (mensagemErro.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = mensagemErro, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                val valorDeposito = valor.toDoubleOrNull() ?: 0.0
                if (valorDeposito <= 0) {
                    mensagemErro = "Valor inválido!"
                } else {
                    onDeposito(valorDeposito)
                    mensagemErro = ""
                }
            },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                Text("Confirmar")
            }
            Button(onClick = { onCancelamento() },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
fun TelaDeTransferencia(
    saldo: Double,
    onTransferencia: (String, Double) -> Unit,
    onCancelamento: () -> Unit
) {
    var chavePix by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf("") }
    var transferenciaRealizada by remember { mutableStateOf(false) }

    if (transferenciaRealizada) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Transferência realizada com sucesso!", style = MaterialTheme.typography.headlineSmall)
           Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { transferenciaRealizada = false },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                Text("Voltar")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Digite a chave PIX e o valor", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = chavePix,
                onValueChange = { chavePix = it },
                label = { Text("Chave PIX") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = valor,
                onValueChange = { input ->
                    if (input.toDoubleOrNull() != null) {
                        valor = input
                        mensagemErro = ""
                    } else {
                        mensagemErro = "Valor inválido!"
                    }
                },
                label = { Text("Valor") },
                modifier = Modifier.fillMaxWidth()
            )

            if (mensagemErro.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = mensagemErro, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    val valorTransferencia = valor.toDoubleOrNull() ?: 0.0
                    if (valorTransferencia <= 0 || valorTransferencia > saldo) {
                        mensagemErro = "Valor inválido!"
                    } else {
                        onTransferencia(chavePix, valorTransferencia)
                        mensagemErro = ""
                        transferenciaRealizada = true
                    }
                },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                    Text("Enviar")
                }
                Button(onClick = { onCancelamento() },colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                    Text("Cancelar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaLoginPreview() {
    BlackBankTheme {
        TelaLogin(onSucessoLogin = {})
    }
}
