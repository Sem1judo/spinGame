package sem.ua.slot

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import sem.ua.slot.ui.LoadingFragment
import sem.ua.slot.ui.LoginFragment

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler.postDelayed({
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            navController.setGraph(R.navigation.nav_graph, intent.extras)
            navController.navigate(R.id.action_loadingFragment_to_loginFragment)
        }, 2000)

    }


}
