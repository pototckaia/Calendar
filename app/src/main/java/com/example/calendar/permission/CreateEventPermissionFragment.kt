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

        fun newInstance(): CreateEventPermissionFragment {
            val args = Bundle()
            args.run {
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
        val arg = arguments!!
        val entity_type = EntityType.valueOf(arg.getString(ENTITY_TYPE)!!)
        return PermissionEventPresenter(
            arg.getLong(ENTITY_ID),
            arg.getLong(PATTERN_ID),
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

        var text = "Предоставления доступа для "
        if (!permissionPresenter.allEntity) {
            val id = permissionPresenter.event_id!!
            text = "$text события \nid: $id"
        } else {
            text = "$text событий"
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
        showToast("Не работает")
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
}