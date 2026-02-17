package pt.ipt.dam.projfinal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import pt.ipt.dam.projfinal.databinding.ActivityCamBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Activity responsável pela leitura de QR Codes utilizando CameraX e ML Kit.
 * O QR Code contém apenas o código da sala (ex: "I153")
 */
class cam : AppCompatActivity() {

    // ViewBinding do layout activity_cam.xml
    private lateinit var binding: ActivityCamBinding

    // Executor para tarefas da câmara fora da UI Thread
    private lateinit var cameraExecutor: ExecutorService

    // Flag para evitar leituras repetidas do mesmo QR Code
    private var isScanning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG, "onCreate: Iniciando activity de câmera")

        // Inicializa ViewBinding
        binding = ActivityCamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa executor da câmara
        cameraExecutor = Executors.newSingleThreadExecutor()

        /**
         * Botão Voltar
         * Fecha esta Activity e regressa ao menu principal
         */
        binding.btnvoltar.setOnClickListener {
            Log.d(TAG, "Botão voltar pressionado")
            finish()
        }

        // Verifica permissões da câmara
        if (allPermissionsGranted()) {
            Log.d(TAG, "Permissões já concedidas, iniciando câmera")
            startCamera()
        } else {
            Log.d(TAG, "Solicitando permissões")
            requestPermissions()
        }
    }

    /**
     * Inicializa a câmara com CameraX e começa a analisar frames
     */
    private fun startCamera() {
        Log.d(TAG, "startCamera: Iniciando câmera")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()
            Log.d(TAG, "CameraProvider obtido com sucesso")

            // Preview em tempo real
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                Log.d(TAG, "Preview configurado")
            }

            // Analyzer para processar imagens e detetar QR Codes
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        Log.d(TAG, "Frame recebido para análise")
                        processImageProxy(imageProxy)
                    }
                    Log.d(TAG, "ImageAnalyzer configurado")
                }

            // Usa câmara traseira
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            Log.d(TAG, "Usando câmera traseira")

            try {
                cameraProvider.unbindAll()
                Log.d(TAG, "Unbind all completed")

                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
                Log.d(TAG, "Câmera ligada ao lifecycle com sucesso")

            } catch (exc: Exception) {
                Log.e(TAG, "Erro ao ligar a câmara", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Processa cada frame da câmara e tenta detetar QR Codes
     */
    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        Log.d(TAG, "processImageProxy: Processando frame")

        val mediaImage = imageProxy.image

        if (mediaImage != null && isScanning) {
            Log.d(TAG, "Frame tem imagem e scanning está ativo")

            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            // Configura scanner apenas para QR Codes
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(options)
            Log.d(TAG, "Scanner ML Kit configurado")

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    Log.d(TAG, "Scanner processado com sucesso. Barcodes encontrados: ${barcodes.size}")

                    for (barcode in barcodes) {
                        Log.d(TAG, "Barcode rawValue: ${barcode.rawValue}")

                        barcode.rawValue?.let { salaId ->
                            Log.d(TAG, "QR Code detectado - Código da sala: $salaId")
                            isScanning = false
                            handleQRCodeResult(salaId)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Erro ao processar QR Code", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                    Log.d(TAG, "ImageProxy fechado")
                }

        } else {
            Log.d(TAG, "Frame ignorado - mediaImage null: ${mediaImage == null}, isScanning: $isScanning")
            imageProxy.close()
        }
    }

    /**
     * Trata o conteúdo do QR Code
     * O QR Code contém apenas o código da sala (ex: "I153")
     */
    private fun handleQRCodeResult(salaId: String) {

        Log.d(TAG, "=== HANDLE QR CODE RESULT ===")
        Log.d(TAG, "Código da sala lido: $salaId")

        // Validação básica - não pode ser vazio
        if (salaId.isNotBlank()) {
            // Mostra feedback ao utilizador
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Sala detectada: $salaId",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "Toast mostrado para sala: $salaId")
            }

            // Faz a requisição à API usando o Retrofit
            Log.d(TAG, "Chamando fetchHorarioBySala para sala: $salaId")
            fetchHorarioBySala(salaId)

        } else {
            Log.e(TAG, "Código da sala vazio ou inválido")
            runOnUiThread {
                Toast.makeText(
                    this,
                    "QR Code inválido. Código da sala não reconhecido.",
                    Toast.LENGTH_SHORT
                ).show()
                isScanning = true
                Log.d(TAG, "Scanning reativado")
            }
        }
    }

    /**
     * Busca o horário da sala usando o Retrofit
     */
    private fun fetchHorarioBySala(sala: String) {
        Log.d(TAG, "=== FETCH HORARIO BY SALA ===")
        Log.d(TAG, "Buscando horário para sala: $sala")

        lifecycleScope.launch {
            try {
                Log.d(TAG, "Fazendo chamada Retrofit para sala: $sala")

                // Chama a API usando o Retrofit
                val horarioResponse = RetrofitClient.instance.getHorarioBySala(sala)

                Log.d(TAG, "Resposta recebida da API")
                Log.d(TAG, "HorarioResponse: $horarioResponse")
                Log.d(TAG, "Sala no response: ${horarioResponse.sala}")
                Log.d(TAG, "Turma no response: ${horarioResponse.turma}")
                Log.d(TAG, "Curso no response: ${horarioResponse.curso}")
                Log.d(TAG, "Ano no response: ${horarioResponse.ano}")

                // Cria Intent para abrir a activity de horários
                Log.d(TAG, "Criando Intent para horarios activity")
                val intent = Intent(this@cam, horarios::class.java)

                // Passa todos os dados necessários
                intent.putExtra("horario_data", horarioResponse.horario)
                intent.putExtra("turma", horarioResponse.turma)
                intent.putExtra("curso", horarioResponse.curso)
                intent.putExtra("ano", horarioResponse.ano)
                intent.putExtra("sala", horarioResponse.sala)

                Log.d(TAG, "Extras adicionados à Intent")

                // Inicia a activity e fecha a câmara
                Log.d(TAG, "Iniciando horarios activity")
                startActivity(intent)
                Log.d(TAG, "startActivity chamado - deve mudar de tela")

                Log.d(TAG, "Finalizando cam activity")
                finish()
                Log.d(TAG, "finish chamado - cam activity fechada")

            } catch (e: Exception) {
                Log.e(TAG, "ERRO ao carregar horário: ${e.message}", e)
                e.printStackTrace()

                runOnUiThread {
                    Toast.makeText(
                        this@cam,
                        "Erro ao carregar horário da sala $sala: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Reativa a leitura para tentar novamente
                    isScanning = true
                    Log.d(TAG, "Scanning reativado após erro")
                }
            }
        }
    }

    // ---------------- PERMISSÕES ----------------

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun requestPermissions() {
        Log.d(TAG, "Solicitando permissões")
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val allGranted = permissions.entries.all { it.value }
            Log.d(TAG, "Resultado permissões - allGranted: $allGranted")

            if (allGranted) {
                Log.d(TAG, "Permissões concedidas, iniciando câmera")
                startCamera()
            } else {
                Log.e(TAG, "Permissão da câmara negada")
                Toast.makeText(
                    this,
                    "Permissão da câmara negada. Não é possível ler QR Codes.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Fechando executor")
        cameraExecutor.shutdown()
    }

    companion object {

        private const val TAG = "CameraXApp"

        // Permissões necessárias
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}