package ir.mohammadhf.birthdays.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ir.mohammadhf.birthdays.utils.WrapContext

@EntryPoint
@InstallIn(ApplicationComponent::class)
interface WrapContextEntry {
    val wrapper: WrapContext
}