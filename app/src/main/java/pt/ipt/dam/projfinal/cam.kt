package pt.ipt.dam.projfinal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import pt.ipt.dam.projfinal.databinding.ActivityCamBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class cam : AppCompatActivity() {
    private lateinit var binding: ActivityCamBinding
    private lateinit var cameraExecutor: ExecutorService
    private var isScanning = true // Para evitar ler o mesmo código múltiplas vezes seguidas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.btnvoltar.setOnClickListener {
            finish()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // 1. Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            // 2. Image Analysis (O "Cérebro" para ler QR Codes)
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                // Ligamos o Preview e o ImageAnalyzer
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Erro ao ligar câmara", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && isScanning) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Configurar para ler apenas QR Codes (mais rápido)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(options)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        if (rawValue != null) {
                            isScanning = false // Para de ler
                            handleQRCodeResult(rawValue)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Erro ao processar QR Code", it)
                }
                .addOnCompleteListener {
                    imageProxy.close() // Fecha o frame para o próximo entrar
                }
        } else {
            imageProxy.close()
        }
    }

    private fun handleQRCodeResult(qrContent: String) {
        // 1. Dividir o conteúdo por quebras de linha
        // O qrContent é algo como:
        // HASH:hashsegura
        // qrcodesONLINE/salaI152.json
        val lines = qrContent.split("\n")

        if (lines.size >= 2) {
            // 2. Pegar apenas na segunda linha (índice 1)
            val fileName = lines[1].trim() // "qrcodesONLINE/salaI152.json"

            // 3. Se quiseres apenas o nome do ficheiro sem a pasta e sem o .json
            // para passar para a função loadSchedule que já adiciona o .json:


            runOnUiThread {
                Toast.makeText(this, "Sala detetada: $fileName", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, lerqrcode::class.java)
                // Passamos apenas "salaI152"
                intent.putExtra("url", fileName)
                startActivity(intent)
                finish()
            }
        } else {
            // Caso o QR Code não tenha o formato esperado
            runOnUiThread {
                Toast.makeText(this, "QR Code inválido", Toast.LENGTH_SHORT).show()
                isScanning = true // Permite tentar ler outro
            }
        }
    }

    // --- MÉTODOS DE PERMISSÃO ---
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            startCamera()
        } else {
            Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}