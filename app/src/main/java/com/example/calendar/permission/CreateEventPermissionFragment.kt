package com.example.calendar.permission

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.calendar.R
import com.example.calendar.helpers.ENTITY_ID
import com.example.calendar.helpers.ENTITY_TYPE
import com.example.calendar.helpers.PATTERN_ID
import com.example.calendar.inject.InjectApplication
import com.example.calendar.repository.server.model.EntityType
import com.example.calendar.repository.server.model.PermissionAction
import kotlinx.android.synthetic.main.fragment_create_permission.view.*

class CreateEventPermissionFragment : MvpAppCompatFragment(),
    PermissionView {

    companion object {
        fun newInstance(entity_id: Long,
                        pattern_id: Long): CreateEventPermissionFragment {
            val args = Bundle()
            args.run {
                putLong(ENTITY_ID, entity_id)
                putLong(PATTERN_ID, pattern_id)
                putString(ENTITY_TYPE, EntityType.EVENT.toString())
            }
            val f = CreateEventPermissionFragment()
            f.arguments = args
            return f
        }
    }

    @InjectPresenter
    lateinit var permissionPresenter: PermissionEventPresenter

    @ProvidePresenter
    fun providePermissionPresenter(): PermissionEventPresenter {
        var entity_id : Long? = null
        var pattern_id : Long? = null
        arguments?.let {
            entity_id = it.getLong(ENTITY_ID)
            pattern_id = it.getLong(PATTERN_ID)
        }

        return PermissionEventPresenter(
            entity_id,
            pattern_id,
            InjectApplication.inject.repository,
            InjectApplication.inject.router
        )
    }


    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        v = inflater.inflate(
            R.layout.fragment_create_permission,
            container, false
        )

        var text = "Предоставления доступа "
        if (!permissionPresenter.allEntity) {
            val id = permissionPresenter.event_id!!
            text = "$text для события \nid: $id"
        } else {
            text = "$text на событий"
        }
        v.tvTitleAccess.text = text

        v.bToken.setOnClickListener { onToken() }
        v.bGrant.setOnClickListener { onGrant() }

        return v
    }

    private fun getAction() : List<PermissionAction> {
        val action = arrayListOf<PermissionAction>(PermissionAction.READ)

        if (v.cbUpdate.isChecked) {
            action.add(PermissionAction.UPDATE)
        }

        if (v.cbUpdate.isChecked) {
            action.add(PermissionAction.UPDATE)
        }
        return action
    }

    private fun onToken() {
        permissionPresenter.getLink(getAction())
    }

    private fun onGrant() {
        permissionPresenter.getPermission(v.etUserEmail.text.toString(), getAction())
    }

    override fun showToast(mes: String) {
        Toast.makeText(context, mes, Toast.LENGTH_SHORT).show()
    }

    override fun addToClipboard(s: String) {
        val clipboard = activity!!.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", s)
        clipboard.primaryClip = clip
        showToast("В буфер добавлена ссылка")
    }

    override fun showEmailError() {
        v.etUserEmail.error = "Email not find"
    }
}