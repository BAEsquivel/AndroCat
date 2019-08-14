package mustafaozhan.github.com.androcat.base

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import de.mateware.snacky.Snacky
import mustafaozhan.github.com.androcat.R

/**
 * Created by Mustafa Ozhan on 2018-07-22.
 */
@Suppress("TooManyFunctions")
abstract class BaseActivity : AppCompatActivity() {

    @LayoutRes
    protected abstract fun getLayoutResId(): Int?

    @IdRes
    open var containerId: Int = R.id.content

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getLayoutResId()?.let {
            setContentView(it)
            getDefaultFragment()?.let { defaultFragment ->
                replaceFragment(defaultFragment, false)
            }
        }
    }

    open fun getDefaultFragment(): BaseFragment? = null

    private fun addFragment(containerViewId: Int, fragment: BaseFragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.add(containerViewId, fragment, fragment.fragmentTag)
        ft.commit()
    }

    protected fun addFragment(fragment: BaseFragment) {
        addFragment(containerId, fragment)
    }

    fun replaceFragment(fragment: BaseFragment, withBackStack: Boolean) {
        if (withBackStack) {
            replaceFragmentWithBackStack(containerId, fragment)
        } else {
            replaceFragment(containerId, fragment)
        }
    }

    private fun replaceFragmentWithBackStack(containerViewId: Int, fragment: BaseFragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        ft.replace(containerViewId, fragment, fragment.fragmentTag)
        ft.addToBackStack(null)
        ft.commit()
    }

    private fun replaceFragment(containerViewId: Int, fragment: BaseFragment) {
        val ft = supportFragmentManager.beginTransaction()
        if (supportFragmentManager.backStackEntryCount != 0) {
            ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
        }
        ft.replace(containerViewId, fragment, fragment.fragmentTag)
        ft.commit()
    }

    fun clearBackStack() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun snacky(text: String, actionText: String = "", action: () -> Unit = {}) {
        Snacky.builder()
            .setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setText(text)
            .setIcon(R.mipmap.ic_launcher)
            .setActivity(this)
            .setDuration(Snacky.LENGTH_SHORT)
            .setActionText(actionText.toUpperCase())
            .setActionTextColor(ContextCompat.getColor(this, R.color.blue_gray_50))
            .setActionTextTypefaceStyle(Typeface.BOLD)
            .setActionClickListener {
                action()
            }
            .build()
            .show()
    }

    fun showDialog(
        title: String,
        description: String,
        positiveButton: String,
        cancelable: Boolean = true,
        function: () -> Unit = {}
    ) {
        if (!isFinishing) {
            val builder = AlertDialog
                .Builder(this, R.style.AlertDialogCustom)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(positiveButton) { _, _ ->
                    function()
                }
                .setCancelable(cancelable)

            if (cancelable) {
                builder.setNegativeButton(getString(R.string.cancel), null)
            }

            builder.show()
        }
    }
}
