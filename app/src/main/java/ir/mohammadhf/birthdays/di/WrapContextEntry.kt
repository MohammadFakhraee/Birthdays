package ir.mohammadhf.birthdays.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mohammadhf.birthdays.utils.WrapContext

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WrapContextEntry {
    val wrapper: WrapContext
}