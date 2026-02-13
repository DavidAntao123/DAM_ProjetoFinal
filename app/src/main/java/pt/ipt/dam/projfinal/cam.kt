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
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import pt.ipt.dam.projfinal.databinding.ActivityCamBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Activity responsável pela leitura de QR Codes utilizando CameraX e ML Kit.
 * Permite:
 * - pedir permissões da câmara
 * - mostrar a pré-visualização da câmara
 * - ler QR Codes em tempo real
 * - validar o conteúdo do QR Code
 * - abrir o horário correspondente
 */
class cam : AppCompatActivity() {

    // ViewBinding do layout activity_cam.xml
    private lateinit var binding: ActivityCamBinding

    // Executor para tarefas da câmara fora da UI Thread
    private lateinit var cameraExecutor: ExecutorService

    // Flag para evitar leituras repetidas do mesmo QR Code
    private var isScanning = true

    /**
     * Método chamado quando a Activity é criada.
     * Inicializa a câmara, permissões e listeners.
     */
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
                Log.e(TAG, "Erro ao ligar a câmara", exc)
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
                        barcode.rawValue?.let {
                            isScanning = false
                            handleQRCodeResult(it)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Erro ao processar QR Code", it)
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
     * Valida o hash e extrai o ficheiro JSON do horário
     */
    private fun handleQRCodeResult(qrContent: String) {

        val lines = qrContent.split("\n")

        if (lines.size >= 2) {

            val securityHash = lines[0].trim()
            val fileName = lines[1].trim()

            // Verifica hash de segurança
            if (securityHash != "HASH:hashsegura") {

                runOnUiThread {
                    Toast.makeText(this, getString(R.string.qr_invalido), Toast.LENGTH_SHORT).show()
                    isScanning = true
                }
                return
            }

            runOnUiThread {

                Toast.makeText(
                    this,
                    getString(R.string.sala_detectada, fileName),
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, lerqrcode::class.java)
                intent.putExtra("url", fileName)
                startActivity(intent)
                finish()
            }

        } else {

            runOnUiThread {
                Toast.makeText(this, getString(R.string.qr_invalido), Toast.LENGTH_SHORT).show()
                isScanning = true
            }
        }
    }

    // ---------------- PERMISSÕES ----------------

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            if (permissions.all { it.value }) {
                startCamera()
            } else {
                Toast.makeText(this, getString(R.string.permissao_negada), Toast.LENGTH_SHORT)
                    .show()
            }
        }

    /**
     * Fecha o executor ao destruir a Activity
     */
    override fun onDestroy() {
        super.onDestroy()
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
