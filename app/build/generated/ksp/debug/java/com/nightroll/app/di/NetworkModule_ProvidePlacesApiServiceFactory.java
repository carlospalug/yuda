package com.nightroll.app.di;

import com.nightroll.app.data.api.PlacesApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class NetworkModule_ProvidePlacesApiServiceFactory implements Factory<PlacesApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvidePlacesApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public PlacesApiService get() {
    return providePlacesApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvidePlacesApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvidePlacesApiServiceFactory(retrofitProvider);
  }

  public static PlacesApiService providePlacesApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.providePlacesApiService(retrofit));
  }
}
