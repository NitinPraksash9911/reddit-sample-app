package `in`.nitin.redditsample.di

import `in`.nitin.redditsample.ui.AuthenticationBottomSheet
import `in`.nitin.redditsample.viewmodel.RedditViewModel
import `in`.nitin.redditsample.viewmodel.ViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(RedditViewModel::class)
    abstract fun bindWebViewModel(myViewModel: RedditViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}

