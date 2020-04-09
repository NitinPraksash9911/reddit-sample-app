package `in`.nitin.redditsample.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ViewModelFactoryModule::class, ViewModelModule::class]
)
interface ApplicationComponent {

}