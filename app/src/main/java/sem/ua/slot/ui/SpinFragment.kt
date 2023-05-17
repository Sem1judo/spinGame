package sem.ua.slot.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sem.ua.slot.R
import sem.ua.slot.data.AppDatabase
import sem.ua.slot.databinding.FragmentSpinBinding
import sem.ua.slot.model.User
import java.util.*

class SpinFragment : Fragment() {

    private lateinit var binding: FragmentSpinBinding
    private val sectors = intArrayOf(100, 200, 300, 400, 500, 600, 700, 800)
    private val sectorsDegrees = IntArray(sectors.size)
    private var randomSectorIndex = 0
    private var spinning = false
    private var earningsRecord = 0
    private var totalNameBalance = 0
    private var mediaPlayer: MediaPlayer? = null
    private val random = Random()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appDatabase = AppDatabase.getInstance(requireContext())
        val userDao = appDatabase.userDao()
        val inputValue = arguments?.getString("inputValue")
        val newUser = inputValue?.let { User(name = it, balance = totalNameBalance) }

        viewLifecycleOwner.lifecycleScope.launch {
            val user = inputValue?.let { userDao.getUserByName(it) }
            if (user != null) {
                user.balance += totalNameBalance
                withContext(Dispatchers.IO) {
                    userDao.update(user)
                }
            } else {
                withContext(Dispatchers.IO) {
                    if (newUser != null) {
                        userDao.insert(newUser)
                    }
                }
            }
        }

        binding.name.text = inputValue
        binding.nameBalance.text = getString(R.string.name_balance, totalNameBalance)
        setupViews()
    }
    private fun saveUserBalance(balance: Int) {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val userDao = appDatabase.userDao()
        val inputValue = arguments?.getString("inputValue")

        viewLifecycleOwner.lifecycleScope.launch {
            val user = inputValue?.let { userDao.getUserByName(it) }
            if (user != null) {
                user.balance = balance
                withContext(Dispatchers.IO) {
                    userDao.update(user)
                }
            }
        }
    }

    private fun setupViews( ) {
        binding.textBalance.text = "0"
        binding.wheel.setOnClickListener {
            startSpin()
        }
        binding.buttonReset.setOnClickListener {
            earningsRecord = 0
            binding.textBalance.text = earningsRecord.toString()
        }
        binding.buttonSpin.setOnClickListener {
            startSpin()
        }
    }

    private fun startSpin() {
        generateSectorDegrees()
        if (!spinning) {
            spin()
            spinning = true
        }
    }

    private fun spin() {
        randomSectorIndex = random.nextInt(sectors.size)
        val randomDegree = generateRandomDegreeToSpinTo()

        val rotationAnimation = RotateAnimation(
            0F,
            randomDegree.toFloat(),
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f
        )

        rotationAnimation.duration = 3650
        rotationAnimation.fillAfter = true
        rotationAnimation.interpolator = AccelerateDecelerateInterpolator()
        rotationAnimation.setAnimationListener(animationListener)

        binding.wheel.startAnimation(rotationAnimation)

        // Update the user balance
        totalNameBalance += sectors[sectors.size - randomSectorIndex - 1]
        binding.nameBalance.text = getString(R.string.name_balance, totalNameBalance)
        saveUserBalance(totalNameBalance)
    }

    private fun generateRandomDegreeToSpinTo(): Int {
        return (360 * sectors.size) + sectorsDegrees[randomSectorIndex]
    }

    private fun generateSectorDegrees() {
        val sectorDegree = 360 / sectors.size
        for (i in sectors.indices) {
            sectorsDegrees[i] = (i + 1) * sectorDegree
        }
    }

    private val animationListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.spin_sound)
            mediaPlayer?.start()
        }

        override fun onAnimationEnd(animation: Animation?) {
            val earnedCoins = sectors[sectors.size - randomSectorIndex - 1]
            saveEarnings(earnedCoins)

            showCustomToast("You have earned: $earnedCoins $")

            spinning = false
            mediaPlayer?.stop()
        }

        override fun onAnimationRepeat(animation: Animation?) {}
    }

    private fun saveEarnings(earnedCoins: Int) {
        earningsRecord += earnedCoins
        totalNameBalance += earnedCoins
        binding.nameBalance.text = getString(R.string.name_balance, totalNameBalance)
        binding.textBalance.text = earningsRecord.toString()
    }

    private fun showCustomToast(message: String) {
        val inflater = LayoutInflater.from(requireContext())
        val layout = inflater.inflate(
            R.layout.custom_toast,
            requireActivity().findViewById(R.id.custom_toast_container)
        )

        val text = layout.findViewById<TextView>(R.id.custom_toast_text)
        text.text = message

        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}