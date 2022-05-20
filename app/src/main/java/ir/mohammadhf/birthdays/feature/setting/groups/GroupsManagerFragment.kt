package ir.mohammadhf.birthdays.feature.setting.groups

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.data.model.Group
import ir.mohammadhf.birthdays.databinding.FragmentGroupsManagerBinding
import javax.inject.Inject

@AndroidEntryPoint
class GroupsManagerFragment : BaseFragment<FragmentGroupsManagerBinding>() {
    private val groupsManagerViewModel: GroupsManagerViewModel by viewModels()

    @Inject
    lateinit var groupManagerAdapter: GroupManagerAdapter

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGroupsManagerBinding =
        FragmentGroupsManagerBinding.inflate(inflater, container, false)

    override fun initial() {
        groupsManagerViewModel.getGroups()

        requireBinding {
            groupsRv.adapter = groupManagerAdapter

            addGroupFab.setOnClickListener {
                val bottomSheet = CreateGroupDialog()
                bottomSheet.show(childFragmentManager, "CREATE_GROUP_BOTTOM_SHEET")

                compositeDisposable.add(
                    bottomSheet.createdGroupBehaveSub.subscribe {
                        groupsManagerViewModel.addGroup(it)
                        bottomSheet.dismiss()
                    }
                )
            }

            backIb.setOnClickListener { findNavController().popBackStack() }
        }
    }

    override fun subscribe() {
        compositeDisposable.add(
            groupsManagerViewModel.groupListBS
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { groupManagerAdapter.submitList(it) }
        )

        compositeDisposable.add(
            groupManagerAdapter.deleteGroupSelectBS.subscribe {
                AlertDialog.Builder(requireContext())
                    .setTitle(
                        getString(R.string.delete_title, it.name)
                    )
                    .setMessage(getString(R.string.delete_message))
                    .setPositiveButton(getString(R.string.delete_accept)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        groupsManagerViewModel.removeGroup(it)
                    }
                    .setNegativeButton(getString(R.string.delete_reject)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .show()
            }
        )

        compositeDisposable.add(
            groupManagerAdapter.editGroupSelectBS.subscribe { selectedGroup ->
                val bottomSheet = CreateGroupDialog().apply {
                    setDefaults(selectedGroup.color, selectedGroup.name)
                }
                bottomSheet.show(childFragmentManager, "CREATE_GROUP_BOTTOM_SHEET")

                compositeDisposable.add(
                    bottomSheet.createdGroupBehaveSub.subscribe {
                        groupsManagerViewModel.changeGroups(Group(selectedGroup.id, it.name, it.color))
                        bottomSheet.dismiss()
                    }
                )
            }
        )
    }

    override fun isBottomNavShown(): Boolean = false
}