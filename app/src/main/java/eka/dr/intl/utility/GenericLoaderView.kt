package eka.dr.intl.utility

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import coil.load
import eka.dr.intl.icons.R
import eka.dr.intl.databinding.GenericLoaderViewBinding
import androidx.constraintlayout.widget.ConstraintLayout

class GenericLoaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var binding: GenericLoaderViewBinding =
        GenericLoaderViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.imageLoader.visibility = VISIBLE
        binding.imageLoader.load(R.drawable.eka_loader) {
            crossfade(true)
        }
    }
    fun setLoaderMessage(message: String) {
        binding.tvLoaderMsg.text = message
    }

}