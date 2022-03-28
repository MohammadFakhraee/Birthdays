package ir.mohammadhf.birthdays.feature.create

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import ir.mohammadhf.birthdays.MainActivity
import ir.mohammadhf.birthdays.R
import ir.mohammadhf.birthdays.core.bases.BaseFragment
import ir.mohammadhf.birthdays.databinding.FragmentCreatePersonBinding
import ir.mohammadhf.birthdays.feature.create.adapters.AvatarListAdapter
import ir.mohammadhf.birthdays.feature.create.adapters.CreatePersonGroupAdapter
import ir.mohammadhf.birthdays.feature.setting.groups.CreateGroupDialog
import ir.mohammadhf.birthdays.utils.DateManager
import ir.mohammadhf.birthdays.utils.ImageLoader
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class CreatePersonFragment : BaseFragment<FragmentCreatePersonBinding>() {
    private val createPersonViewModel: CreatePersonViewModel by viewModels()

    lateinit var avatarListAdapter: AvatarListAdapter
    lateinit var createPersonGroupAdapter: CreatePersonGroupAdapter

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var dateManager: DateManager

    private var isFirstLoading = true

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreatePersonBinding =
        FragmentCreatePersonBinding.inflate(inflater, container, false)

    override fun initial() {
        val args: CreatePersonFragmentArgs by navArgs()
        val personId = args.personId

        createPersonViewModel.getDefValue(personId)
        createPersonViewModel.getGroups()
        createPersonViewModel.getAvatars()

        avatarListAdapter = AvatarListAdapter()
        createPersonGroupAdapter = CreatePersonGroupAdapter()

        requireBinding {
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            avatarsRv.adapter = avatarListAdapter

            groupListRv.adapter = createPersonGroupAdapter

            personNameEt.addTextChangedListener {
                if (!isFirstLoading)
                    createPersonViewModel.changeName(it.toString())
            }

            chooseBirthdayTv.setOnClickListener {
                val bottomSheet = ChooseBirthdayDialog()
                bottomSheet.show(childFragmentManager, "CHOOSE_BIRTH_BOTTOM_SHEET")

                val dis = bottomSheet.selectedDateBehaveSub.subscribe {
                    createPersonViewModel.changeBirthday(it[2]!!, it[1]!!, it[0])
                    bottomSheet.dismiss()
                }
            }

            addGroupFab.setOnClickListener {
                val bottomSheet = CreateGroupDialog()
                bottomSheet.isCancelable = false
                bottomSheet.show(childFragmentManager, "CREATE_GROUP_BOTTOM_SHEET")

                val dis = bottomSheet.createdGroupBehaveSub.subscribe {
                    createPersonViewModel.saveNewGroup(it)
                    bottomSheet.dismiss()
                }
            }

            createBtn.setOnClickListener {
                if (selectedIv.tag is String)
                    createPersonViewModel.savePerson(
                        (selectedIv.drawable as BitmapDrawable).bitmap,
                        File(
                            requireContext().getDir(
                                MainActivity.AVATAR_FOLDER_NAME,
                                Context.MODE_PRIVATE
                            ),
                            selectedIv.tag as String
                        )
                    )
            }

            selectedIv.setOnClickListener {
                enterCropMode()
            }

            choosePhotoIv.setOnClickListener {
                enterCropMode()
            }

            // views used in cropping mode
            acceptCropFab.setOnClickListener {
                selectedIv.setImageBitmap(imageCropperIv.croppedBitmap)
                selectedIv.tag = IMAGE_TAG + createPersonViewModel.getTagId()
                quitCropMode()
            }

            cancelCropFab.setOnClickListener { quitCropMode() }
        }
    }

    override fun subscribe() {
        compositeDisposable.add(
            createPersonViewModel.personBehaveSub
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    requireBinding {
                        if (isFirstLoading) {
                            personNameEt.setText(it.name)

                            if (it.avatarPath.isNotEmpty())
                                imageLoader.load(
                                    requireContext(), it.avatarPath, selectedIv
                                )

                            createPersonGroupAdapter.selectGroupById(it.groupId)

                            createBtn.text =
                                if (it.id > 0) getString(R.string.update)
                                else getString(R.string.create)

                            isFirstLoading = false
                        }

                        chooseBirthdayTv.text =
                            if (it.birthdayDay > 0 && it.birthdayMonth > 0) {
                                dateManager
                                    .stringFormattedDate(
                                        intArrayOf(it.birthdayYear?.apply { } ?: 0,
                                            it.birthdayMonth,
                                            it.birthdayDay
                                        )
                                    )
                            } else
                                getString(R.string.tap_to_choose)
                    }
                }
        )

        compositeDisposable.add(
            createPersonViewModel.groupListBehaveSub
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { createPersonGroupAdapter.submitList(it) }
        )

        compositeDisposable.add(
            createPersonViewModel.avatarListBehaveSub
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { avatarListAdapter.submitList(it) }
        )

        compositeDisposable.add(
            createPersonViewModel.saveBehaveSub
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { if (it) findNavController().popBackStack() }
        )

        compositeDisposable.add(
            createPersonViewModel.loadingBehaveSub
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    requireBinding {
                        createBtn.isEnabled = !it
                        loadingPb.visibility = if (it) VISIBLE else GONE
                    }
                }
        )

        compositeDisposable.add(
            createPersonViewModel.formCompleteBehaveSub
                .subscribe {
                    requireBinding {
                        createBtn.isEnabled = it
                    }
                }
        )

        compositeDisposable.add(
            createPersonGroupAdapter.selectedGroupBehaveSub
                .subscribe { createPersonViewModel.changeGroup(it) }
        )

        compositeDisposable.add(
            avatarListAdapter.selectedAvatarBehaveSub
                .subscribe {
                    requireBinding {
                        selectedIv.setImageResource(it.resId)
                        selectedIv.tag = IMAGE_TAG + it.name
                    }
                }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
            val imageUri = data?.data
            requireBinding {
                imageUri?.let {
                    imageCropperIv.load(it).executeAsCompletable().subscribe()

                }
            }
        } else quitCropMode()
    }

    private fun enterCropMode() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_REQUEST_CODE)

        requireBinding {
            imageCropperIv.visibility = VISIBLE
            acceptCropFab.visibility = VISIBLE
            cancelCropFab.visibility = VISIBLE

            createBtn.isEnabled = false
        }
    }

    private fun quitCropMode() {
        requireBinding {
            imageCropperIv.visibility = GONE
            acceptCropFab.visibility = GONE
            cancelCropFab.visibility = GONE

            createPersonViewModel.changeFormComplete()
        }
    }

    override fun isBottomNavShown(): Boolean = true

    companion object {
        const val IMAGE_REQUEST_CODE = 2001
        const val IMAGE_TAG = "img_user_"
    }
}