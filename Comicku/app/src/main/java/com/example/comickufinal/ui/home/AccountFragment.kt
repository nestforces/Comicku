package com.example.comickufinal.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.comickufinal.ui.AuthActivity
import com.example.comickufinal.R
import com.example.comickufinal.ui.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_account.view.*

class AccountFragment : Fragment(), View.OnClickListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // view model
        val homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        val firebaseAuth = homeViewModel.firebaseAuth

        // text view
        view.accountFrag_tvEmail.text = firebaseAuth.currentUser?.email
        view.accountFrag_tvName.text = firebaseAuth.currentUser?.displayName

        // button listener
        view.accountFrag_btnGithub.setOnClickListener(this)
        view.accountFrag_btnInstagram.setOnClickListener(this)
        view.accountFrag_btnFacebook.setOnClickListener(this)

        view.accountFrag_btnSignOut.setOnClickListener {
            firebaseAuth.signOut()

            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onClick(view: View?) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = when (view?.id) {
            R.id.accountFrag_btnGithub -> {
                Uri.parse(requireContext().getString(R.string.github_link))
            }
            R.id.accountFrag_btnInstagram -> {
                Uri.parse(requireContext().getString(R.string.instagram_link))
            }
            R.id.accountFrag_btnFacebook -> {
                Uri.parse(requireContext().getString(R.string.facebook_link))
            }
            else -> Uri.parse("")
        }

        intent.data = uri
        startActivity(intent)
    }
}