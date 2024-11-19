package com.example.aula_19

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aula_19.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var cotacaoDolar: Double = 0.0
    val API_URL = "https://api.hgbrasil.com/finance"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Chama a função para buscar a cotação ao iniciar o app
        buscarCotacao()

        // Ação do botão de calcular
        binding.btnCalcular.setOnClickListener {
            calcular()
        }
    }

    // Função para buscar a cotação do dólar
    private fun buscarCotacao() {
        // Usando uma coroutine para buscar a cotação em segundo plano
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Fazendo a requisição HTTP para a API
                val resposta = URL(API_URL).readText()

                // Extraindo a cotação do dólar
                cotacaoDolar = JSONObject(resposta)
                    .getJSONObject("results")
                    .getJSONObject("currencies")
                    .getJSONObject("USD")
                    .getDouble("sell")

                // Formatando a cotação para exibir na UI
                val f = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                val cotacaoFormatada = f.format(cotacaoDolar)

                // Atualizando a UI no thread principal
                withContext(Dispatchers.Main) {
                    binding.txtCotacao.text = cotacaoFormatada
                }
            } catch (e: Exception) {
                // Em caso de erro na requisição ou JSON, mostra um Toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Erro ao buscar a cotação", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Função de cálculo (para implementar o que quiser aqui)
    fun calcular(){
        if(binding.txtValor.text.isEmpty()){
            binding.txtValor.error = "Preencha um valor"
            return

        }
        val valorDigitado = binding.txtValor.text.toString()
            .replace(",", ".").toDouble()


        val resultado = if (cotacaoDolar > 0) valorDigitado / cotacaoDolar
        else 0.0

        binding.txtQtdDolar.text = "%.8f".format(resultado)
    }
}
