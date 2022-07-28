package nathanmerrell.com.moneymanagement

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton


class SplashScreen : AppCompatActivity() {

    private lateinit var continueBtn: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureView()
        ModelController.openDatabase(this)
    }
    private fun configureView(){
        setContentView(R.layout.activity_splash_screen)

        continueBtn = findViewById(R.id.continueButton)
        continueBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
