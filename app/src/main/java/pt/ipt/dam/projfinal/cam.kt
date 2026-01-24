package pt.ipt.dam.projfinal
// Imports necessários para permissões, intents, logs e funcionamento da CameraX
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
// Imports da CameraX
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
// Imports de compatibilidade
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
// ViewBinding (liga este ficheiro ao layout activity_cam.xml)
import pt.ipt.dam.projfinal.databinding.ActivityCamBinding
// Imports para criar o nome do ficheiro com data/hora
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import kotlin.collections.contains


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
        // Botão que tira uma fotografia
        binding.imageCaptureButton.setOnClickListener {
            takePhoto()
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
    } // fim onCreate()

    /**
     * Pede permissões necessárias ao utilizador
     */
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    /**
     * Verifica se todas as permissões necessárias foram dadas
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    /**
     * Launcher responsável por processar a resposta do utilizador às permissões:
     * Se forem aceites -> inicia a câmara
     * Se forem recusadas -> mostra mensagem de erro
     */
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Assume que todas as permissões estão OK até prova em contrário
            var permissionGranted = true
            // Verifica se todas as permissões necessárias foram aceites
            permissions.entries.forEach {
                // test for all types of permissions
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            // Se alguma permissão falhar ->aqui avisa o utilizador
            if (!permissionGranted) {
                // ver também 'SnackBar'
                Toast.makeText(
                    baseContext,
                    getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Permissões aceites -> inicia a câmara
                startCamera()
            }
        }


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
            // Criar o ImageCapture para tirar as fotografias
            imageCapture = ImageCapture.Builder().build()

            // Seleciona a câmara traseira como padrão
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Remove configurações antigas antes de ligar de novo
                cameraProvider.unbindAll()

                // Liga a câmara à Activity (lifecycle)
                // preview -> mostra imagem
                // imageCapture -> permite tirar foto
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                // Se falhar, regista o erro no log
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Função que tira fotografia e guarda no armazenamento do telemóvel
     */
    private fun takePhoto() {
        // Referência estável ao use case de captura
        val imageCapture = imageCapture ?: return

        //Criar nome do ficheiro com data/hora 2026-01-24-12-30-00-123
        // import java.text.SimpleDateFormat
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        // Definir dados da imagem e onde vai ser guardada
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            // Se a versão for Android 10 ou superior, define a pasta "Pictures/CameraX-Images"
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                // Android version is higher than 9 (Android 9 (Pie) --> SDK_INT = 28)
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Images")
            }
        }

        // Define opções de saída (onde a imagem vai ser guardada)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        // Tira fotografia e recebe callback quando terminar
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                // Se der erro ao tirar foto
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
                // Se guardar com sucesso
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = getString(R.string.after_take_photo, output.savedUri)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    Log.d(TAG, msg)
                }
            })
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