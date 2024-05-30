package com.nicolocurioni.samplesmb

import android.os.Build
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.nicolocurioni.samplesmb.shared.Secrets
import com.nicolocurioni.samplesmb.ui.theme.SampleSMBTheme
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()
        setupSambaAccess()

        enableEdgeToEdge()
        setContent {
            SampleSMBTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        }
    }


    // SMB stuff
    private fun setupSambaAccess() {
        val username = Secrets.sambaUsername.value
        val password = Secrets.sambaPassword.value
        val domain = ""
        val host = Secrets.sambaHost.value
        val share = Secrets.sambaShare.value

        val smbURL = URL("$host")

        val auth = NtlmPasswordAuthenticator(username, password)
        val smbDirectory = SmbFile(smbURL)

        try {
            if (smbDirectory.exists() && smbDirectory.isDirectory) {
                val files = smbDirectory.listFiles()

                files.forEach { file ->
                    println("File: ${file.name}")

                    if (file.isFile) {
                        //downloadFile(file, Environment.DIRECTORY_DOCUMENTS + "sample-smb")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun downloadFile(file: SmbFile, localPath: String) {
        try {
            SmbFileInputStream(file).use { fileInput ->
                FileOutputStream(File(localPath)).use { fileOutput ->
                    val buffer = ByteArray(4096)
                    var byteRead: Int

                    while (fileInput.read(buffer).also { byteRead = it } != -1) {
                        fileOutput.write(buffer, 0, byteRead)
                    }

                    fileOutput.flush()
                    println("File downloaded: $localPath")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SampleSMBTheme {
        Greeting("Android")
    }
}