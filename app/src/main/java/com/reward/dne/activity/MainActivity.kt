package com.reward.dne.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.reward.dne.R
import com.reward.dne.fragment.*
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : BaseActivity() {

    companion object {
        var mainBack: Int = 0
        var tvAmount: TextView? = null
        var tvMenuAmount: TextView? = null
    }

    private var doubleBackToExitPressedOnce: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = intent
        if (intent != null) {
            val earning = intent.getIntExtra("rewardNotification", 0)
            if (earning == 1) {
                push(EarningsFragment())
            } else {
                push(HomeFragment())
            }

        } else {
            push(HomeFragment())
        }

        tvAmount = findViewById(R.id.textAmount)
        tvMenuAmount = findViewById(R.id.tv_menuAmount)

        UtilsDefault.updateSharedPreferenceString(Constants.SESSION_STATUS, "Yes")

        setOnClicks()
    }

    private val mRunnable = Runnable {
        doubleBackToExitPressedOnce = false
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun setOnClicks() {
        imgMenu.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        menuHome.setOnClickListener {
            textEditProfile.visibility = View.GONE
            relativeWalletAmount.visibility = View.GONE
            push(HomeFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        menuHotApps.setOnClickListener {
            textEditProfile.visibility = View.GONE
            relativeWalletAmount.visibility = View.GONE
            push(HotAppsFragment())

            drawerLayout.closeDrawer(GravityCompat.START)
        }
        menuEarnings.setOnClickListener {
            textEditProfile.visibility = View.GONE
            relativeWalletAmount.visibility = View.GONE
            push(EarningsFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        menuEarningHistory.setOnClickListener {
            textEditProfile.visibility = View.GONE
            relativeWalletAmount.visibility = View.GONE
            push(EarningHistoryFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        menuProfile.setOnClickListener {
            push(MyProfileFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        /*menuProMember.setOnClickListener {
            textEditProfile.visibility = View.GONE
            relativeWalletAmount.visibility = View.GONE
            push(ProMembershipFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }*/
        menuInvite.setOnClickListener {
            textEditProfile.visibility = View.GONE
            relativeWalletAmount.visibility = View.GONE
            push(InviteFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        menuRate.setOnClickListener {
            textEditProfile.visibility = View.GONE
            relativeWalletAmount.visibility = View.GONE
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.reward.dne&reviewId")))
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        menuHelp.setOnClickListener {
            textEditProfile.visibility = View.GONE
            relativeWalletAmount.visibility = View.GONE
            push(HelpFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        menuFAQ.setOnClickListener {
            textEditProfile.visibility = View.GONE
            relativeWalletAmount.visibility = View.GONE
            push(FAQFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        menuRedeem.setOnClickListener {
            textEditProfile.visibility = View.GONE
            relativeWalletAmount.visibility = View.GONE
            push(RedeemFragment())
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        menuLogout.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val builder = AlertDialog.Builder(this@MainActivity)

            builder.setTitle(R.string.app_name)
            builder.setMessage("Are you sure want to Exit the App?")

            builder.setPositiveButton("Yes") { dialog, which ->
               finish()
               /* UtilsDefault.clearSession()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finishAffinity()*/

            }
            builder.setNegativeButton("No") { dialog, which ->

                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()

            dialog.show()


        }
    }

    fun popBackStackImmediate() {
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack()
        fragmentManager.executePendingTransactions()
    }

    fun popAllBackstack() {
        val fragmentManager = supportFragmentManager
        val backCount = fragmentManager.backStackEntryCount
        if (backCount > 0) {
            fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).id,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun push(fragment: Fragment, title: String?) {
        val fragmentManager = supportFragmentManager
        val tag = fragment.javaClass.canonicalName

        if (title != null) {
            try {
                fragmentManager.beginTransaction()
                        .replace(R.id.mainFrameContainer, fragment, tag)
                        .addToBackStack(title)
                        .commit()
            } catch (ile: IllegalStateException) {
                fragmentManager.beginTransaction()
                        .replace(R.id.mainFrameContainer, fragment, tag)
                        .addToBackStack(title)
                        .commitAllowingStateLoss()
            }

        } else {
            try {
                fragmentManager.beginTransaction()
                        .replace(R.id.mainFrameContainer, fragment, tag).commit()
            } catch (ile: IllegalStateException) {
                fragmentManager.beginTransaction()
                        .replace(R.id.mainFrameContainer, fragment, tag).commitAllowingStateLoss()
                //  .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )

            }

        }

    }


    fun push(fragment: Fragment?) {
        if (fragment == null) {
            return
        }
        push(fragment, null)
    }

    override fun onBackPressed() {
        if (mainBack == 1) {
            push(HomeFragment())
        } else {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                val toast = Toast.makeText(this, getString(R.string.tap_again), Toast.LENGTH_SHORT)
                if (doubleBackToExitPressedOnce) {
                    toast.cancel()
                    super.onBackPressed()
                    return
                }
                this.doubleBackToExitPressedOnce = true
                toast.show()
                mHandler.postDelayed(mRunnable, 2000)
            }

        }


    }
}
