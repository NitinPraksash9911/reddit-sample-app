package `in`.nitin.redditsample.di

import `in`.nitin.redditsample.viewmodel.RedditViewModel
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(RedditViewModel::class)
    abstract fun bindWebViewModel(myViewModel: RedditViewModel): ViewModel
}