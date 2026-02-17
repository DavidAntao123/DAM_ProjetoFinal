package pt.ipt.dam.projfinal

// Permissões Android
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
// Componentes AndroidX
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

// CameraX
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
// Coroutines
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
// ML Kit para leitura de QR Codes
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
// ViewBinding
import pt.ipt.dam.projfinal.databinding.ActivityCamBinding

// Execução em background
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Cam
 *
 * Activity responsável pela leitura de QR Codes utilizando:
 * CameraX (acesso à câmara)
 * ML Kit (deteção de QR Codes)
 *
 * O QR Code contém apenas o identificador da sala (ex: "I153").
 * Após leitura, a aplicação consulta a API e carrega o horário correspondente.
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
            finish()
        }

        // Verifica permissões da câmara
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
    }

    /**
     * Inicializa a câmara com CameraX e começa a analisar frames
     */
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            // Preview em tempo real
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            // Analyzer para processar imagens e detetar QR Codes
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            // Usa câmara traseira
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Processa cada frame da câmara e tenta detetar QR Codes
     */
    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image

        if (mediaImage != null && isScanning) {

            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            // Configura scanner apenas para QR Codes
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(options)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->

                    for (barcode in barcodes) {

                        barcode.rawValue?.let { salaId ->
                            Log.d(TAG, getString(R.string.cam_sala, salaId))
                            isScanning = false
                            handleQRCodeResult(salaId)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, getString(R.string.cam_erroQR), e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }

        } else {
            imageProxy.close()
        }
    }

    /**
     * Trata o conteúdo do QR Code
     * O QR Code contém apenas o código da sala (ex: "I153")
     */
    private fun handleQRCodeResult(salaId: String) {

        Log.d(TAG, getString(R.string.cam_codigoSala, salaId))

        // Validação básica - não pode ser vazio
        if (salaId.isNotBlank()) {
            // Mostra feedback ao utilizador
            runOnUiThread {
                Toast.makeText(
                    this,
                    getString(R.string.cam_sala, salaId),
                    Toast.LENGTH_SHORT
                ).show()
            }

            fetchHorarioBySala(salaId)

        } else {
            Log.e(TAG, getString(R.string.cam_qrInvalido))
            runOnUiThread {
                Toast.makeText(
                    this,
                    getString(R.string.cam_QRInvaldio),
                    Toast.LENGTH_SHORT
                ).show()
                isScanning = true
            }
        }
    }


    /**
     * Consulta a API para obter o horário da sala
     * utilizando Retrofit + Coroutines
     */
    private fun fetchHorarioBySala(sala: String) {

        lifecycleScope.launch {
            try {

                val horarioResponse = RetrofitClient.horarioApi.getHorarioBySala(sala)

                val intent = Intent(this@cam, horarios::class.java)

                // Envia dados para a próxima Activity
                intent.putExtra("horario_data", horarioResponse.horario)
                intent.putExtra("turma", horarioResponse.turma)
                intent.putExtra("curso", horarioResponse.curso)
                intent.putExtra("ano", horarioResponse.ano)
                intent.putExtra("sala", horarioResponse.sala)


                // Inicia a activity e fecha a câmara
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                e.printStackTrace()

                runOnUiThread {
                    Toast.makeText(
                        this@cam,
                        getString(R.string.cam_erroSala, sala, e.message),
                        Toast.LENGTH_LONG
                    ).show()

                    // Reativa a leitura para tentar novamente
                    isScanning = true
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