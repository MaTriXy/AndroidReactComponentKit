package com.github.skyfe79.android.reactcomponentkit.component

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.skyfe79.android.reactcomponentkit.ComponentDispatchEvent
import com.github.skyfe79.android.reactcomponentkit.ComponentNewStateEvent
import com.github.skyfe79.android.reactcomponentkit.ReactComponent
import com.github.skyfe79.android.reactcomponentkit.eventbus.EventBus
import com.github.skyfe79.android.reactcomponentkit.eventbus.Token
import com.github.skyfe79.android.reactcomponentkit.redux.RCKViewModel
import com.github.skyfe79.android.reactcomponentkit.redux.State
import java.lang.ref.WeakReference

internal enum class FragmentComponentState {
    SHOW,
    HIDE,
    NONE
}

abstract class FragmentComponent: Fragment(), ReactComponent {

    companion object {
        inline fun <reified T: FragmentComponent> newInstance(token: Token): T {
            val args = Bundle()
            args.putParcelable("token", token)
            val component = T::class.java.newInstance()
            component.arguments = args
            return component
        }
    }

    override var token: Token = Token.empty
    internal var state: FragmentComponentState = FragmentComponentState.NONE

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (arguments != null) {
            arguments?.let {
                this@FragmentComponent.token = (it.getParcelable("token") as? Token) ?: Token.empty
            }
        }
    }

    abstract fun on(state: State)
}

inline fun <reified T: FragmentComponent> FragmentActivity.fragmentComponent(token: Token): T {
    return FragmentComponent.newInstance(token)
}

fun FragmentActivity.replaceFragment(component: FragmentComponent, containerViewId: Int, tag: String? = null) {
    component.state = FragmentComponentState.SHOW
    this.supportFragmentManager
        .beginTransaction()
        .replace(containerViewId, component, tag)
        .commit()
}

fun FragmentActivity.addFragment(component: FragmentComponent, containerViewId: Int, tag: String? = null) {
    component.state = FragmentComponentState.SHOW
    this.supportFragmentManager
        .beginTransaction()
        .add(containerViewId, component, tag)
        .commit()
}

fun FragmentActivity.hideFragment(component: FragmentComponent) {
    component.state = FragmentComponentState.HIDE

    this.supportFragmentManager
        .beginTransaction()
        .hide(component)
        .commit()
}

fun FragmentActivity.showFragment(component: FragmentComponent) {
    component.state = FragmentComponentState.SHOW

    this.supportFragmentManager
        .beginTransaction()
        .show(component)
        .commit()
}

fun FragmentActivity.removeFragment(component: FragmentComponent) {
    component.state = FragmentComponentState.HIDE
    this.supportFragmentManager
        .beginTransaction()
        .remove(component)
        .commit()
}

fun FragmentActivity.removeAllFragments() {
    this.supportFragmentManager
        .fragments
        .map { it as? FragmentComponent }
        .filterNotNull()
        .forEach { removeFragment(it) }
}