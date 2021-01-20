package pl.patrykgoworowski.liftchart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import pl.patrykgoworowski.liftchart.databinding.ActivityMainBinding
import pl.patrykgoworowski.liftchart.showcase.ShowcaseFragmentAdapter

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            viewPager.adapter = ShowcaseFragmentAdapter(this@MainActivity)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position) {
                    0 -> tab.setText(R.string.showcase_compose_title)
                    1 -> tab.setText(R.string.showcase_view_title)
                }
            }.attach()
        }
    }

}