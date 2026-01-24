package pt.ipt.dam.projfinal
// Imports necessários para permissões, intents, logs e funcionamento da CameraX
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

/**
 * Esta Activity é responsável por:
 * - pedir permissões da câmara
 * - mostrar a pré-visualização da câmara (com o Preview)
 * - tirar fotografia e guardar no dispositivo
 */
class cam : AppCompatActivity() {
    // Variável do ViewBinding do layout activity_cam.xml
    private lateinit var binding: ActivityCamBinding
    // Objeto que permite capturar fotos usando CameraX
    private lateinit var imageCapture: ImageCapture
    // Executor que executa tarefas da câmara fora da UI Thread
    private lateinit var cameraExecutor: ExecutorService
    private var isScanning = true // Para evitar ler o mesmo código múltiplas vezes seguidas

    /**
     * Este método é chamado quando a Activity é criada ou seja quando o ecra da câmara abre
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Faz o ecrã ocupar a área total
        enableEdgeToEdge()
        // Inicializa o ViewBinding e liga esta Activity ao layout activity_cam.xml
        binding = ActivityCamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // setContentView(R.layout.activity_main)
        // Ajusta o layout para não ficar escondido por trás da barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // permissão para usar a câmara
        //Verifica permissões necessárias para usar a câmara
        // Se já tiver permissões -> inicia a câmara
        // Se não -> pede permissões
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }


        // configura acesso à câmara
        // utiliza o padrão 'Singleton'
        // Executor para tarefas relacionadas com a câmara thread separada
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Botão voltar - aqui vai para o menu principal
        val btnVoltar = findViewById<Button>(R.id.btnvoltar)

        btnVoltar.setOnClickListener {
            // Intent para abrir a MainActivity
            val intent = Intent(this, MainActivity::class.java)

            // Começar a activity
            startActivity(intent)

        }


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.btnvoltar.setOnClickListener {
            finish()
        }
    } // fim onCreate()

    /**
     * Inicia a câmara com CameraX e mostra a pré-visualização no PreviewView
     */
    private fun startCamera() {
        // Provider que controla o tempo da câmara
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Obter o provider da câmara
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Criar o Preview (imagem em tempo real)
            val preview = Preview.Builder()
                .build()
                .also {
                    // Define onde a pré-visualização vai aparecer PreviewView do layout
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }


            // analisa os frames da câmara em tempo real para detetar dados (QR Codes)
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            // Seleciona a câmara traseira como padrão
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA




            try {
                // Remove configurações antigas antes de ligar de novo
                cameraProvider.unbindAll()
                // Liga a câmara à Activity (lifecycle)
                // preview -> mostra imagem
                // imageCapture -> permite tirar foto
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                // Se falhar, regista o erro no log
                Log.e(TAG, "Erro ao ligar câmara", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        //imagem em tempo real
        val mediaImage = imageProxy.image
        //verifica se a imagem existe  e se ainda esta a dar "scan"
        if (mediaImage != null && isScanning) {
            //converte a imagem
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            //Configura o Reader (ao dizer so para procurar o qr code)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
            //incializa
            val scanner = BarcodeScanning.getClient(options)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        //se encontrar o qr code
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
                    imageProxy.close() //
                }
        } else {
            imageProxy.close()
        }
    }

    private fun handleQRCodeResult(qrContent: String) {
        // Dividir o qrcode por quebras de linha
        // Exemplo do QRCode:
        // HASH:hashsegura
        // qrcodesONLINE/salaI152.json
        val lines = qrContent.split("\n")

        if (lines.size >= 2) {
            // Guarda apenas a segunda linha (índice 1)
            val fileName = lines[1].trim() // "qrcodesONLINE/salaI152.json"

            //verifica a segurança do qr code
            val qrcodeSecure = lines[0].trim()
            //caso o hash seja diferente , nao ira ler o qrcode
            if(qrcodeSecure != "HASH:hashsegura")
            {
                runOnUiThread {
                    Toast.makeText(this, "QR Code inválido", Toast.LENGTH_SHORT).show()
                    isScanning = true // Permite tentar ler outro
                }
            }

            runOnUiThread {
                Toast.makeText(this, "Sala detetada: $fileName", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, lerqrcode::class.java)
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

    /**
     * Chamado quando a Activity é fechada
     * Aqui é importante fechar o executor para não ficar a gastar os recursos
     */
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    /**
     * Guarda constantes e lista de permissões necessárias
     */
    companion object {
        private const val TAG = "CameraXApp"
        // Formato do nome do ficheiro da foto
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        // Lista de permissões obrigatórias para funcionar
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                // import android.Manifest
                // sempre necessário
                Manifest.permission.CAMERA
            ).apply {
                // Apenas Android 9 (API 28) ou inferior precisa de WRITE_EXTERNAL_STORAGE
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

}