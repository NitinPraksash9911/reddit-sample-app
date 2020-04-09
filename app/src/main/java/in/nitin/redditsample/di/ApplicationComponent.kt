package `in`.nitin.redditsample.di

import `in`.nitin.redditsample.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ViewModelFactoryModule::class, ViewModelModule::class]
)
interface ApplicationComponent {
    fun inject(mainActivity: MainActivity)

}