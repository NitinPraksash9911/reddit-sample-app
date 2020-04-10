package `in`.nitin.redditsample.di

import `in`.nitin.redditsample.ui.AuthenticationBottomSheet
import `in`.nitin.redditsample.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ViewModelModule::class]
)
interface ApplicationComponent {
    fun inject(mainActivity: MainActivity)

    fun inject(authenticationBottomSheet: AuthenticationBottomSheet)

}