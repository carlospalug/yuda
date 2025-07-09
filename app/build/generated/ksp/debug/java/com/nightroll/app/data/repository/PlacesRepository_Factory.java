package com.nightroll.app.data.repository;

import com.nightroll.app.data.api.PlacesApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class PlacesRepository_Factory implements Factory<PlacesRepository> {
  private final Provider<PlacesApiService> placesApiServiceProvider;

  public PlacesRepository_Factory(Provider<PlacesApiService> placesApiServiceProvider) {
    this.placesApiServiceProvider = placesApiServiceProvider;
  }

  @Override
  public PlacesRepository get() {
    return newInstance(placesApiServiceProvider.get());
  }

  public static PlacesRepository_Factory create(
      Provider<PlacesApiService> placesApiServiceProvider) {
    return new PlacesRepository_Factory(placesApiServiceProvider);
  }

  public static PlacesRepository newInstance(PlacesApiService placesApiService) {
    return new PlacesRepository(placesApiService);
  }
}
