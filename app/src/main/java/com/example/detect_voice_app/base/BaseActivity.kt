package com.example.detect_voice_app.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.detect_voice_app.BR

abstract class BaseActivity<ViewBinding : ViewDataBinding, ViewModel : BaseViewModel> :
        AppCompatActivity() {

    protected lateinit var viewBinding: ViewBinding

    protected abstract val viewModel: ViewModel

    private var forceUpdateDialog: AlertDialog? = null

    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(this, layoutId)
        viewBinding.apply {
            setVariable(BR.viewModel, viewModel)
            root.isClickable = true
            lifecycleOwner = this@BaseActivity
            executePendingBindings()
        }
    }


    fun findFragment(TAG: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(TAG)
    }


    fun addFragment(
            fragment: BaseFragment<*, *>,
            containerViewId: Int,
            addToBackStack: Boolean = true,
            transit: Int = -1
    ) {
        supportFragmentManager.beginTransaction()
                .apply {
                    if (addToBackStack) addToBackStack(fragment::class.simpleName)
                    if (transit != FragmentTransaction.TRANSIT_NONE) setTransition(
                            FragmentTransaction.TRANSIT_NONE)
                }
                .add(containerViewId, fragment, fragment::class.simpleName)
                .commit()
    }

    fun replaceFragment(
            fragment: BaseFragment<*, *>,
            containerViewId: Int,
            addToBackStack: Boolean = true,
            transit: Int = -1
    ) {
        supportFragmentManager.beginTransaction()
                .apply {
                    if (addToBackStack) addToBackStack(fragment::class.simpleName)
                    if (transit != FragmentTransaction.TRANSIT_NONE) setTransition(
                            FragmentTransaction.TRANSIT_NONE)
                }
                .replace(containerViewId, fragment, fragment::class.simpleName)
                .commit()
    }

    fun backToPreviousScreen() {
        supportFragmentManager.popBackStack()
    }


    fun showDialogFragment(
            dialogFragment: DialogFragment,
            addToBackStack: Boolean = false, transit: Int = FragmentTransaction.TRANSIT_NONE
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        if (addToBackStack) transaction.addToBackStack(dialogFragment::class.simpleName)
        if (transit != FragmentTransaction.TRANSIT_NONE) transaction.setTransition(transit)
        if (transaction != null) {
            dialogFragment.show(transaction, dialogFragment::class.simpleName)
        }
    }

    fun clearBackStack() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

}
